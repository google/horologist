/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.data.apphelper

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.PowerManager
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.WearableListenerService
import com.google.android.horologist.data.ActivityConfig
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.CompanionConfig
import com.google.android.horologist.data.LaunchRequest
import kotlinx.coroutines.runBlocking

/** Base service to respond to incoming requests from the partnering app on the connected device. */
public abstract class DataLayerAppHelperService : WearableListenerService() {
  public abstract val appHelper: DataLayerAppHelper

  override fun onRequest(nodeId: String, path: String, byteArray: ByteArray): Task<ByteArray> {
    if (path != DataLayerAppHelper.LAUNCH_APP) {
      return Tasks.forResult(
        byteArrayForResultCode(AppHelperResultCode.APP_HELPER_RESULT_UNKNOWN_REQUEST)
      )
    }

    val request = LaunchRequest.parseFrom(byteArray)
    val result =
      when {
        request.hasOwnApp() -> launchOwnApp()
        request.hasActivity() -> launchActivity(request.activity)
        request.hasCompanion() -> launchCompanion(request.companion)
        else -> AppHelperResultCode.APP_HELPER_RESULT_UNKNOWN_REQUEST
      }
    return Tasks.forResult(byteArrayForResultCode(result))
  }

  private fun launchOwnApp(): AppHelperResultCode {
    try {
      val intent =
        this.packageManager.getLaunchIntentForPackage(packageName)
          ?: return AppHelperResultCode.APP_HELPER_RESULT_ACTIVITY_NOT_FOUND

      wakeDeviceAndStartActivity(intent)
    } catch (e: ActivityNotFoundException) {
      Log.w(TAG, "Launch activity not found for : $packageName")
      return AppHelperResultCode.APP_HELPER_RESULT_ACTIVITY_NOT_FOUND
    }
    return AppHelperResultCode.APP_HELPER_RESULT_SUCCESS
  }

  /**
   * Attempts to launch an activity, which belongs to the same app (same package name), on this
   * device.
   *
   * The class name is supplied by the paired device, so it is untrusted. Only activities that are
   * exported, or that explicitly opt in with the [REMOTE_LAUNCH_ALLOWED_META_DATA] meta-data, can
   * be launched.
   */
  private fun launchActivity(activityConfig: ActivityConfig): AppHelperResultCode {
    val component = ComponentName(packageName, activityConfig.classFullName)
    if (!isRemoteLaunchAllowed(packageManager, component)) {
      Log.w(TAG, "Activity not allowed for remote launch: $activityConfig")
      return AppHelperResultCode.APP_HELPER_RESULT_ACTIVITY_NOT_FOUND
    }

    try {
      val intent =
        Intent().apply {
          setComponent(component)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
      wakeDeviceAndStartActivity(intent)
    } catch (e: ActivityNotFoundException) {
      Log.w(TAG, "Activity not found: $activityConfig")
      return AppHelperResultCode.APP_HELPER_RESULT_ACTIVITY_NOT_FOUND
    }
    return AppHelperResultCode.APP_HELPER_RESULT_SUCCESS
  }

  /** Attempts to launch the companion app on this device. */
  private fun launchCompanion(companionConfig: CompanionConfig): AppHelperResultCode {
    return runBlocking { appHelper.startCompanion(companionConfig.sourceNode) }
  }

  /** Ensures device is woken (e.g. screen turns on) before Activity launched. */
  private fun wakeDeviceAndStartActivity(intent: Intent) {
    wakeDevice()
    startActivity(intent)
  }

  /** Wakes the device, screen turns on. */
  private fun wakeDevice() {
    val powerManager = getSystemService(POWER_SERVICE) as PowerManager

    // FULL_WAKE_LOCK and ACQUIRE_CAUSES_WAKEUP are deprecated, but they remain in use as the
    // approach for achieving screen wakeup across mainstream apps, so are the approach to use
    // for now.
    @Suppress("DEPRECATION")
    val wakeLock =
      powerManager.newWakeLock(
        PowerManager.FULL_WAKE_LOCK or
          PowerManager.ACQUIRE_CAUSES_WAKEUP or
          PowerManager.ON_AFTER_RELEASE,
        wakeLockTag,
      )

    // Wakelock timeout should not be required as it is being immediately released but
    // linting guidance recommends one so setting it nonetheless.
    wakeLock.acquire(wakeLockTimeoutMs)
    wakeLock.release()
  }

  companion object {
    // Tag format as per recommendations:
    // https://developer.android.com/reference/android/os/PowerManager#newWakeLock(int,%20java.lang.String)
    private const val wakeLockTag = "horologist:apphelper"
    private const val wakeLockTimeoutMs = 1000L
  }
}

/**
 * Meta-data key that an activity can declare, with a value of `true`, to allow it to be launched
 * from the paired device via [DataLayerAppHelper.startRemoteActivity] even if it is not exported.
 *
 * ```xml
 * <activity android:name=".MyActivity" android:exported="false">
 *   <meta-data
 *     android:name="com.google.android.horologist.datalayer.REMOTE_LAUNCH_ALLOWED"
 *     android:value="true" />
 * </activity>
 * ```
 */
internal const val REMOTE_LAUNCH_ALLOWED_META_DATA: String =
  "com.google.android.horologist.datalayer.REMOTE_LAUNCH_ALLOWED"

/**
 * Returns whether [component] may be launched on behalf of a request from the paired device.
 *
 * The component must be an activity in this app that is either exported (and therefore already
 * reachable by other apps) or explicitly opts in with [REMOTE_LAUNCH_ALLOWED_META_DATA].
 */
internal fun isRemoteLaunchAllowed(
  packageManager: PackageManager,
  component: ComponentName,
): Boolean {
  val activityInfo =
    try {
      @Suppress("DEPRECATION")
      packageManager.getActivityInfo(component, PackageManager.GET_META_DATA)
    } catch (e: PackageManager.NameNotFoundException) {
      return false
    }

  return activityInfo.exported ||
    activityInfo.metaData?.getBoolean(REMOTE_LAUNCH_ALLOWED_META_DATA, false) == true
}

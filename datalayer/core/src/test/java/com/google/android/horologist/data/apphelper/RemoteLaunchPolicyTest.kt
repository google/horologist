/*
 * Copyright 2026 The Android Open Source Project
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

import android.app.Application
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class RemoteLaunchPolicyTest {
  private val context = ApplicationProvider.getApplicationContext<Application>()
  private val packageManager = context.packageManager

  private fun addActivity(name: String, exported: Boolean, metaData: Bundle? = null) {
    val activityInfo =
      ActivityInfo().apply {
        this.name = name
        packageName = context.packageName
        this.exported = exported
        this.metaData = metaData
      }
    shadowOf(packageManager).addOrUpdateActivity(activityInfo)
  }

  @Test
  fun exportedActivityIsAllowed() {
    addActivity("com.example.Exported", exported = true)

    assertThat(
        isRemoteLaunchAllowed(
          packageManager,
          ComponentName(context.packageName, "com.example.Exported"),
        )
      )
      .isTrue()
  }

  @Test
  fun nonExportedActivityIsRejected() {
    addActivity("com.example.Internal", exported = false)

    assertThat(
        isRemoteLaunchAllowed(
          packageManager,
          ComponentName(context.packageName, "com.example.Internal"),
        )
      )
      .isFalse()
  }

  @Test
  fun nonExportedActivityWithOptInIsAllowed() {
    addActivity(
      "com.example.OptedIn",
      exported = false,
      metaData = Bundle().apply { putBoolean(REMOTE_LAUNCH_ALLOWED_META_DATA, true) },
    )

    assertThat(
        isRemoteLaunchAllowed(
          packageManager,
          ComponentName(context.packageName, "com.example.OptedIn"),
        )
      )
      .isTrue()
  }

  @Test
  fun nonExportedActivityWithOptOutValueIsRejected() {
    addActivity(
      "com.example.OptedOut",
      exported = false,
      metaData = Bundle().apply { putBoolean(REMOTE_LAUNCH_ALLOWED_META_DATA, false) },
    )

    assertThat(
        isRemoteLaunchAllowed(
          packageManager,
          ComponentName(context.packageName, "com.example.OptedOut"),
        )
      )
      .isFalse()
  }

  @Test
  fun unknownActivityIsRejected() {
    assertThat(
        isRemoteLaunchAllowed(
          packageManager,
          ComponentName(context.packageName, "com.example.DoesNotExist"),
        )
      )
      .isFalse()
  }

  @Test
  fun nonExportedActivityIsAllowedWhenAppAllowsAll() {
    setApplicationMetaData(
      Bundle().apply { putBoolean(ALLOW_ALL_REMOTE_ACTIVITY_LAUNCHES_META_DATA, true) }
    )
    addActivity("com.example.Internal", exported = false)

    assertThat(
        isRemoteLaunchAllowed(
          packageManager,
          ComponentName(context.packageName, "com.example.Internal"),
        )
      )
      .isTrue()
  }

  @Test
  fun nonExportedActivityIsRejectedWhenAppAllowAllIsFalse() {
    setApplicationMetaData(
      Bundle().apply { putBoolean(ALLOW_ALL_REMOTE_ACTIVITY_LAUNCHES_META_DATA, false) }
    )
    addActivity("com.example.Internal", exported = false)

    assertThat(
        isRemoteLaunchAllowed(
          packageManager,
          ComponentName(context.packageName, "com.example.Internal"),
        )
      )
      .isFalse()
  }

  private fun setApplicationMetaData(metaData: Bundle) {
    shadowOf(packageManager)
      .getInternalMutablePackageInfo(context.packageName)
      .applicationInfo!!
      .metaData = metaData
  }
}

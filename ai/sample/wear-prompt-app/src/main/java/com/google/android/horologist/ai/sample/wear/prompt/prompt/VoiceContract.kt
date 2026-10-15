/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.ai.sample.wear.prompt.prompt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.result.contract.ActivityResultContract

class VoiceContract : ActivityResultContract<Intent, VoiceContract.Result>() {
  override fun createIntent(context: Context, input: Intent): Intent =
    input.withSystemHandler(context.packageManager)

  override fun parseResult(resultCode: Int, intent: Intent?): Result {
    if (resultCode != Activity.RESULT_OK) {
      return Result.Empty
    }
    val res = intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
    val enteredPrompt = res?.firstOrNull()
    return if (!enteredPrompt.isNullOrBlank()) {
      Result.EnteredPrompt(enteredPrompt)
    } else {
      Result.Empty
    }
  }

  sealed class Result {
    data class EnteredPrompt(val prompt: String) : Result()

    data object Empty : Result()
  }
}

/**
 * Targets the intent at a system app that handles it, if there is one, so that another installed
 * app that declares the same intent filter can't handle it instead (and, for speech recognition,
 * return a prompt the user never said).
 */
internal fun Intent.withSystemHandler(packageManager: PackageManager): Intent {
  if (component != null || `package` != null) {
    return this
  }
  val systemHandler =
    packageManager.queryIntentActivities(this, 0).firstOrNull {
      it.activityInfo.applicationInfo.flags and
        (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    } ?: return this
  return Intent(this)
    .setClassName(systemHandler.activityInfo.packageName, systemHandler.activityInfo.name)
}

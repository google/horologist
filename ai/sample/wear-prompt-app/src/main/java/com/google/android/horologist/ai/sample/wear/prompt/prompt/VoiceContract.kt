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

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.contract.ActivityResultContract

class VoiceContract : ActivityResultContract<Intent, VoiceContract.Result>() {
    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): Result {
        val res = intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val enteredPrompt = res?.get(0)
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

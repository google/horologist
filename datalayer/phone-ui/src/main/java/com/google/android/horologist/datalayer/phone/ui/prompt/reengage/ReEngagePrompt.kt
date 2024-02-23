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

package com.google.android.horologist.datalayer.phone.ui.prompt.reengage

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import kotlinx.coroutines.CoroutineScope

public class ReEngagePrompt(coroutineScope: CoroutineScope) {

    init {
        CoroutineScopeHolder.coroutineScope = coroutineScope
    }

    /**
     * Returns the [Intent] to display a re-engage prompt to the user.
     *
     * This can be used in Compose with [rememberLauncherForActivityResult] and
     * [ActivityResultLauncher.launch]:
     *
     * ```
     * val launcher = rememberLauncherForActivityResult(
     *     ActivityResultContracts.StartActivityForResult()
     * ) { result ->
     *     if (result.resultCode == RESULT_OK) {
     *         // user pushed the positive button!
     *     }
     * }
     *
     * launcher.launch(getIntent(/*params*/))
     * ```
     *
     * It can also be used directly in an [ComponentActivity] with
     * [ComponentActivity.registerForActivityResult]:
     * ```
     *  val launcher = registerForActivityResult(
     *      ActivityResultContracts.StartActivityForResult()
     *  ) { result ->
     *      if (result.resultCode == RESULT_OK) {
     *          // user pushed the positive button!
     *      }
     *  }
     *
     * launcher.launch(getIntent(/*params*/))
     * ```
     */
    public fun getIntent(
        context: Context,
        nodeId: String,
        @DrawableRes image: Int,
        topMessage: String,
        bottomMessage: String,
        positiveButtonLabel: String? = null,
        negativeButtonLabel: String? = null,
    ): Intent = ReEngageBottomSheetActivity.getIntent(
        context = context,
        nodeId = nodeId,
        image = image,
        topMessage = topMessage,
        bottomMessage = bottomMessage,
        positiveButtonLabel = positiveButtonLabel,
        negativeButtonLabel = negativeButtonLabel,
    )
}

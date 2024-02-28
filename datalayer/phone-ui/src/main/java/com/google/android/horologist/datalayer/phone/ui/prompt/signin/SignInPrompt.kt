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

package com.google.android.horologist.datalayer.phone.ui.prompt.signin

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.UsageStatus
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import kotlinx.coroutines.CoroutineScope

/**
 * Functions to display a sign-in prompt.
 *
 * @param coroutineScope [CoroutineScope] used to make the call to launch the app on the watch.
 */
@ExperimentalHorologistApi
public class SignInPrompt(
    coroutineScope: CoroutineScope,
    private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper,
) {

    init {
        CoroutineScopeHolder.coroutineScope = coroutineScope
    }

    /**
     * Returns a [AppHelperNodeStatus] that meets the criteria to show this prompt, otherwise
     * returns null.
     */
    public suspend fun shouldDisplayPrompt(): AppHelperNodeStatus? =
        phoneDataLayerAppHelper.connectedNodes().firstOrNull {
            when (it.surfacesInfo.usageInfo.usageStatus) {
                UsageStatus.UNRECOGNIZED,
                UsageStatus.USAGE_STATUS_UNSPECIFIED,
                UsageStatus.USAGE_STATUS_LAUNCHED_ONCE,
                null,
                -> true

                UsageStatus.USAGE_STATUS_SETUP_COMPLETE -> false
            }
        }

    /**
     * Returns the [Intent] to display a sign-in prompt to the user.
     *
     * This can be used in Compose with [rememberLauncherForActivityResult] and
     * [ActivityResultLauncher.launch]:
     *
     * ```
     * val launcher = rememberLauncherForActivityResult(
     *     ActivityResultContracts.StartActivityForResult()
     * ) { result ->
     *     if (result.resultCode == RESULT_OK) {
     *         // user pushed sign-in!
     *     }
     * }
     *
     * launcher.launch(signInPrompt.getIntent(/*params*/))
     * ```
     *
     * It can also be used directly in an [ComponentActivity] with
     * [ComponentActivity.registerForActivityResult]:
     * ```
     *  val launcher = registerForActivityResult(
     *      ActivityResultContracts.StartActivityForResult()
     *  ) { result ->
     *      if (result.resultCode == RESULT_OK) {
     *          // user pushed sign-in!
     *      }
     *  }
     *
     * launcher.launch(signInPrompt.getIntent(/*params*/))
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
    ): Intent = SignInBottomSheetActivity.getIntent(
        context = context,
        nodeId = nodeId,
        image = image,
        topMessage = topMessage,
        bottomMessage = bottomMessage,
        positiveButtonLabel = positiveButtonLabel,
        negativeButtonLabel = negativeButtonLabel,
    )

    /**
     * Performs the same action taken by the prompt when the user taps on the positive button.
     */
    public suspend fun performAction(nodeId: String) {
        SignInPromptAction.run(
            phoneDataLayerAppHelper = phoneDataLayerAppHelper,
            nodeId = nodeId,
        )
    }
}

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

package com.google.android.horologist.datalayer.phone.ui.prompt.installapp

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.data.apphelper.appInstalled
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper

@ExperimentalHorologistApi
public class InstallAppPrompt(private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper) {
    /**
     * Returns a [AppHelperNodeStatus] that meets the criteria to show this prompt, otherwise
     * returns null.
     *
     * @param predicate augments the criteria applying a [filter][List.filter] with this predicate.
     */
    public suspend fun shouldDisplayPrompt(
        predicate: ((AppHelperNodeStatus) -> Boolean)? = null,
    ): AppHelperNodeStatus? =
        phoneDataLayerAppHelper.connectedNodes()
            .filter { !it.appInstalled }
            .let {
                if (predicate != null) {
                    it.filter(predicate)
                } else {
                    it
                }
            }
            .firstOrNull()

    /**
     * Returns the [Intent] to display an install app prompt to the user.
     *
     * This can be used in Compose with [rememberLauncherForActivityResult] and
     * [ActivityResultLauncher.launch]:
     *
     * ```
     * val launcher = rememberLauncherForActivityResult(
     *     ActivityResultContracts.StartActivityForResult()
     * ) { result ->
     *     if (result.resultCode == RESULT_OK) {
     *         // user pushed install!
     *     }
     * }
     *
     * launcher.launch(installAppPrompt.getIntent(/*params*/))
     * ```
     *
     * It can also be used directly in an [ComponentActivity] with
     * [ComponentActivity.registerForActivityResult]:
     * ```
     *  val launcher = registerForActivityResult(
     *      ActivityResultContracts.StartActivityForResult()
     *  ) { result ->
     *      if (result.resultCode == RESULT_OK) {
     *          // user pushed install!
     *      }
     *  }
     *
     * launcher.launch(installAppPrompt.getIntent(/*params*/))
     * ```
     */
    public fun getIntent(
        context: Context,
        appPackageName: String,
        @DrawableRes image: Int,
        topMessage: String,
        bottomMessage: String,
    ): Intent = InstallAppBottomSheetActivity.getIntent(
        context = context,
        appPackageName = appPackageName,
        image = image,
        topMessage = topMessage,
        bottomMessage = bottomMessage,
    )

    /**
     * Performs the same action taken by the prompt when the user taps on "install".
     */
    public fun performAction(context: Context, appPackageName: String): Unit =
        InstallAppPromptAction.run(context = context, appPackageName = appPackageName)
}

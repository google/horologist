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

package com.google.android.horologist.datalayer.phone.ui.prompt.installtile

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.AppHelperResultCode
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper

@ExperimentalHorologistApi
public class InstallTilePrompt(private val phoneDataLayerAppHelper: PhoneDataLayerAppHelper) {
    /**
     * Returns a [AppHelperNodeStatus] that meets the criteria to show this prompt, otherwise
     * returns null.
     */
    private val tileName = "com.example.MediaPlayerTile"
    public suspend fun shouldDisplayPrompt(): AppHelperNodeStatus? {
        if (phoneDataLayerAppHelper.checkCompanionVersionSupportTileEditing()
                ?.equals(AppHelperResultCode.APP_HELPER_RESULT_SUCCESS) == true
        ) {
            val node = phoneDataLayerAppHelper.findWatchToInstallTile()
            val tileInstalled = node?.surfacesInfo?.tilesList?.any { it.name == tileName } ?: false
            if (!tileInstalled) {
                return node
            }
        }
        return null
    }

    /**
     * Returns the [Intent] to display an install tile prompt to the user.
     *
     * This can be used in Compose with [rememberLauncherForActivityResult] and
     * [ActivityResultLauncher.launch]:
     *
     * ```
     * val launcher = rememberLauncherForActivityResult(
     *     ActivityResultContracts.StartActivityForResult()
     * ) { result ->
     *     if (result.resultCode == RESULT_OK) {
     *         // user pushed add Tile!
     *     }
     * }
     *
     * launcher.launch(installTilePrompt.getIntent(/*params*/))
     * ```
     *
     * It can also be used directly in an [ComponentActivity] with
     * [ComponentActivity.registerForActivityResult]:
     * ```
     *  val launcher = registerForActivityResult(
     *      ActivityResultContracts.StartActivityForResult()
     *  ) { result ->
     *      if (result.resultCode == RESULT_OK) {
     *          // user pushed add tile!
     *      }
     *  }
     *
     * launcher.launch(installTilePrompt.getIntent(/*params*/))
     * ```
     */
    public fun getIntent(
        context: Context,
        appPackageName: String,
        @DrawableRes image: Int,
        topMessage: String,
        bottomMessage: String,
    ): Intent = InstallTileBottomSheetActivity.getIntent(
        context = context,
        appPackageName = appPackageName,
        image = image,
        topMessage = topMessage,
        bottomMessage = bottomMessage,
    )
}

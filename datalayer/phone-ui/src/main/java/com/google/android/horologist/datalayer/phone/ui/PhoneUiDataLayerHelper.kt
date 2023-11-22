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

package com.google.android.horologist.datalayer.phone.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.datalayer.phone.ui.prompt.InstallAppDialogActivity

private const val NO_RESULT_REQUESTED_REQUEST_CODE = -1

/**
 * Data layer related UI helper features, for use on phones.
 */
@ExperimentalHorologistApi
public class PhoneUiDataLayerHelper {

    /**
     * Display an install app prompt to the user.
     *
     * Use [callback] as an option to check if the dialog was dismissed
     * ([Activity.RESULT_CANCELED]).
     */
    public fun showInstallAppPrompt(
        activity: ComponentActivity,
        appName: String,
        watchName: String,
        message: String,
        @DrawableRes image: Int,
        callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback { },
    ) {
        val launcher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            callback,
        )

        val intent = getInstallPromptIntent(
            context = activity,
            appName = appName,
            watchName = watchName,
            message = message,
            image = image,
        )

        launcher.launch(intent)
    }

    /**
     * Alternative to [showInstallAppPrompt] for activities that are not [ComponentActivity].
     *
     * Use [requestCode] as an option to check in [Activity.onActivityResult] if the dialog was
     * dismissed ([Activity.RESULT_CANCELED]).
     */
    public fun showInstallAppPrompt(
        activity: Activity,
        appName: String,
        watchName: String,
        message: String,
        @DrawableRes image: Int,
        requestCode: Int = NO_RESULT_REQUESTED_REQUEST_CODE,
    ) {
        val intent = getInstallPromptIntent(
            context = activity,
            appName = appName,
            watchName = watchName,
            message = message,
            image = image,
        )
        activity.startActivityForResult(
            intent,
            requestCode,
        )
    }

    /**
     * Returns the [Intent] to display an install prompt to the user.
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
     * launcher.launch(getInstallPromptIntent(/*params*/))
     * ```
     */
    public fun getInstallPromptIntent(
        context: Context,
        appName: String,
        watchName: String,
        message: String,
        @DrawableRes image: Int,
    ): Intent = InstallAppDialogActivity.getIntent(
        context = context,
        appName = appName,
        watchName = watchName,
        message = message,
        image = image,
    )
}

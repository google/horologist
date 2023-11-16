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

package com.google.android.horologist.datalayer.sample.play

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat

private const val PLAY_STORE_APP_URI_PREFIX = "market://details?id="
private const val PLAY_STORE_WEB_URL_PREFIX = "https://play.google.com/store/apps/details?id="

/**
 * Launch Google Play app, requesting to display app with specified [package name][packageName].
 */
fun Context.launchPlay(packageName: String) {
    try {
        ContextCompat.startActivity(
            this,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(PLAY_STORE_APP_URI_PREFIX + packageName),
            ),
            Bundle(),
        )
    } catch (anfe: ActivityNotFoundException) {
        // Handle scenario where Google Play app is not installed
        ContextCompat.startActivity(
            this,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(PLAY_STORE_WEB_URL_PREFIX + packageName),
            ),
            Bundle(),
        )
    }
}

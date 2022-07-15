/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.compose.tools

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Round Preview, typical useful when using previews without explicit round device support
 * such as paparazzi.
 */
@Composable
public fun RoundPreview(round: Boolean = true, content: @Composable () -> Unit) {
    if (round) {
        val configuration =
            LocalConfiguration.current.let {
                Configuration(it).apply {
                    screenLayout = (screenLayout or Configuration.SCREENLAYOUT_ROUND_YES)
                }
            }

        CompositionLocalProvider(LocalConfiguration provides configuration) {
            content()
        }
    } else {
        content()
    }
}

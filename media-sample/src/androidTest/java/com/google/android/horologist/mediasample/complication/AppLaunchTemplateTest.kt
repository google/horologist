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

package com.google.android.horologist.mediasample.complication

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.android.horologist.compose.tools.ComplicationRendererPreview
import com.google.android.horologist.media3.complication.AppLaunchTemplate
import com.google.android.horologist.mediasample.R
import org.junit.Rule
import org.junit.Test

class AppLaunchTemplateTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDraw() {
        composeTestRule.setContent {
            val context = LocalContext.current
            val renderer = AppLaunchTemplate(context)

            ComplicationRendererPreview(
                complicationRenderer = renderer,
                data = AppLaunchTemplate.Data(
                    appName = R.string.app_name,
                    appIcon = R.drawable.ic_baseline_queue_music_24,
                    appImage = R.drawable.ic_uamp,
                    launchIntent = null
                )
            )
        }
    }
}

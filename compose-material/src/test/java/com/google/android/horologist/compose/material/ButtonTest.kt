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

package com.google.android.horologist.compose.material

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.google.android.horologist.screenshots.ScreenshotBaseTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters

@RunWith(ParameterizedRobolectricTestRunner::class)
internal class ButtonTest(
    @Suppress("unused") // it's used by junit to display the test name
    private val description: String,
    private val buttonType: ButtonType,
    private val buttonSize: ButtonSize,
    private val enabled: Boolean
) : ScreenshotBaseTest() {

    @Test
    fun variants() {
        screenshotTestRule.setContent(isComponent = true, takeScreenshot = true) {
            Button(
                imageVector = Icons.Default.Check,
                contentDescription = "contentDescription",
                onClick = { },
                buttonType = buttonType,
                buttonSize = buttonSize,
                enabled = enabled
            )
        }
    }

    internal companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun params() = listOf(
            arrayOf(
                "Primary Default",
                ButtonType.Primary,
                ButtonSize.Default,
                true
            ),
            arrayOf(
                "Primary Large",
                ButtonType.Primary,
                ButtonSize.Large,
                true
            ),
            arrayOf(
                "Primary Small",
                ButtonType.Primary,
                ButtonSize.Small,
                true
            ),
            arrayOf(
                "Primary ExtraSmall",
                ButtonType.Primary,
                ButtonSize.ExtraSmall,
                true
            ),
            arrayOf(
                "Primary Default Disabled",
                ButtonType.Primary,
                ButtonSize.Default,
                false
            ),
            arrayOf(
                "Secondary Default",
                ButtonType.Secondary,
                ButtonSize.Default,
                true
            ),
            arrayOf(
                "Secondary Large",
                ButtonType.Secondary,
                ButtonSize.Large,
                true
            ),
            arrayOf(
                "Secondary Small",
                ButtonType.Secondary,
                ButtonSize.Small,
                true
            ),
            arrayOf(
                "Secondary Extra Small",
                ButtonType.Secondary,
                ButtonSize.ExtraSmall,
                true
            ),
            arrayOf(
                "Secondary Default Disabled",
                ButtonType.Secondary,
                ButtonSize.Default,
                false
            ),
            arrayOf(
                "Icon only Default",
                ButtonType.IconOnly,
                ButtonSize.Default,
                true
            ),
            arrayOf(
                "Icon only Large",
                ButtonType.IconOnly,
                ButtonSize.Large,
                true
            ),
            arrayOf(
                "Icon only Small",
                ButtonType.IconOnly,
                ButtonSize.Small,
                true
            ),
            arrayOf(
                "Icon only Extra Small",
                ButtonType.IconOnly,
                ButtonSize.ExtraSmall,
                true
            ),
            arrayOf(
                "Icon only Default Disabled",
                ButtonType.IconOnly,
                ButtonSize.Default,
                false
            )
        )
    }
}

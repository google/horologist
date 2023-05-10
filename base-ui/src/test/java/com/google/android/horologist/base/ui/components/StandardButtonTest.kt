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

package com.google.android.horologist.base.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [30],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound
)
internal class StandardButtonTest(
    @Suppress("unused") // it's used by junit to display the test name
    private val description: String,
    private val buttonType: StandardButtonType,
    private val buttonSize: StandardButtonSize,
    private val enabled: Boolean
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val roborazziRule = RoborazziRule(
        composeRule = composeTestRule,
        captureRoot = composeTestRule.onRoot(),
        options = RoborazziRule.Options(
            captureType = RoborazziRule.CaptureType.LastImage,
            outputDirectoryPath = "src/test/snapshots/images",
            roborazziOptions = RoborazziOptions(
                recordOptions = RoborazziOptions.RecordOptions(
                    pixelBitConfig = RoborazziOptions.PixelBitConfig.Argb8888
                )
            )
        )
    )

    @Test
    fun variants() {
        composeTestRule.setContent {
            StandardButton(
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
                StandardButtonType.Primary,
                StandardButtonSize.Default,
                true
            ),
            arrayOf(
                "Primary Large",
                StandardButtonType.Primary,
                StandardButtonSize.Large,
                true
            ),
            arrayOf(
                "Primary Small",
                StandardButtonType.Primary,
                StandardButtonSize.Small,
                true
            ),
            arrayOf(
                "Primary ExtraSmall",
                StandardButtonType.Primary,
                StandardButtonSize.ExtraSmall,
                true
            ),
            arrayOf(
                "Primary Default Disabled",
                StandardButtonType.Primary,
                StandardButtonSize.Default,
                false
            ),
            arrayOf(
                "Secondary Default",
                StandardButtonType.Secondary,
                StandardButtonSize.Default,
                true
            ),
            arrayOf(
                "Secondary Large",
                StandardButtonType.Secondary,
                StandardButtonSize.Large,
                true
            ),
            arrayOf(
                "Secondary Small",
                StandardButtonType.Secondary,
                StandardButtonSize.Small,
                true
            ),
            arrayOf(
                "Secondary Extra Small",
                StandardButtonType.Secondary,
                StandardButtonSize.ExtraSmall,
                true
            ),
            arrayOf(
                "Secondary Default Disabled",
                StandardButtonType.Secondary,
                StandardButtonSize.Default,
                false
            ),
            arrayOf(
                "Icon only Default",
                StandardButtonType.IconOnly,
                StandardButtonSize.Default,
                true
            ),
            arrayOf(
                "Icon only Large",
                StandardButtonType.IconOnly,
                StandardButtonSize.Large,
                true
            ),
            arrayOf(
                "Icon only Small",
                StandardButtonType.IconOnly,
                StandardButtonSize.Small,
                true
            ),
            arrayOf(
                "Icon only Extra Small",
                StandardButtonType.IconOnly,
                StandardButtonSize.ExtraSmall,
                true
            ),
            arrayOf(
                "Icon only Default Disabled",
                StandardButtonType.IconOnly,
                StandardButtonSize.Default,
                false
            )
        )
    }
}

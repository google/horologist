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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [30],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound
)
class TitleComposeTest {
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
    fun givenHeadingModifierIsNOTOverridden_thenHeadingModifierIsPresent() {
        // given
        composeTestRule.setContent {
            Title(text = "text")
        }

        // then
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
            .assertExists()
    }

    @Test
    fun givenHeadingModifierIsOverridden_thenHeadingModifierIsNOTPresent() {
        // given
        composeTestRule.setContent {
            Title(
                text = "text",
                modifier = Modifier.clearAndSetSemantics {}
            )
        }

        // then
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
            .assertDoesNotExist()
    }
}

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
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [30],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound
)
class StandardChipIconWithProgressTest {
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
    fun default() {
        composeTestRule.setContent {
            StandardChipIconWithProgress(progress = 75f)
        }
    }

    @Test
    fun withProgressSmallIcon() {
        composeTestRule.setContent {
            StandardChipIconWithProgress(progress = 75f, icon = Icon12dp)
        }
    }

    @Test
    fun withProgressMediumIcon() {
        composeTestRule.setContent {
            StandardChipIconWithProgress(progress = 75f, icon = Icon32dp)
        }
    }

    @Test
    fun withProgressLargeIcon() {
        composeTestRule.setContent {
            StandardChipIconWithProgress(
                progress = 75f,
                icon = Icon48dp,
                largeIcon = true
            )
        }
    }

    companion object {
        private val Icon12dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Small",
                defaultWidth = 12f.dp,
                defaultHeight = 12f.dp,
                viewportWidth = 12f,
                viewportHeight = 12f
            ).materialPath {
                horizontalLineToRelative(12.0f)
                verticalLineToRelative(12.0f)
                horizontalLineTo(0.0f)
                close()
            }.build()

        private val Icon32dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Large",
                defaultWidth = 32f.dp,
                defaultHeight = 32f.dp,
                viewportWidth = 32f,
                viewportHeight = 32f
            ).materialPath {
                horizontalLineToRelative(32.0f)
                verticalLineToRelative(32.0f)
                horizontalLineTo(0.0f)
                close()
            }.build()

        private val Icon48dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Extra Large",
                defaultWidth = 48f.dp,
                defaultHeight = 48f.dp,
                viewportWidth = 48f,
                viewportHeight = 48f
            ).materialPath {
                horizontalLineToRelative(48.0f)
                verticalLineToRelative(48.0f)
                horizontalLineTo(0.0f)
                close()
            }.build()
    }
}

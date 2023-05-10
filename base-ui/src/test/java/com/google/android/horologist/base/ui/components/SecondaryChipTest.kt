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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.google.android.horologist.base.ui.util.rememberVectorPainter
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
class SecondaryChipTest {
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
            StandardChip(
                label = "Primary label",
                onClick = { },
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabel() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withLargeIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = Icon32dp,
                largeIcon = true,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabelAndIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLargeIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icon32dp,
                largeIcon = true,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun disabled() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                secondaryLabel = "Secondary label",
                icon = Icons.Default.Image,
                chipType = StandardChipType.Secondary,
                enabled = false
            )
        }
    }

    @Test
    fun withLongText() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label very very very very very very very very very very very very very very very very very long text",
                onClick = { },
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label very very very very very very very very long text",
                onClick = { },
                secondaryLabel = "Secondary label very very very very very very very very very long text",
                icon = Icons.Default.Image,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun usingSmallIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = Icon12dp,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withLargeIconUsingSmallIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = Icon12dp,
                largeIcon = true,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun usingExtraLargeIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withLargeIconUsingExtraLargeIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = Icon48dp,
                largeIcon = true,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withPlaceholderIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = Icons.Default.Image,
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withProgressIcon() {
        composeTestRule.setContent {
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = {
                    StandardChipIconWithProgress(
                        progress = 75f,
                        icon = Icon48dp,
                        largeIcon = true
                    )
                },
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun withSquareIcon() {
        composeTestRule.setContent {
            // This was made to showcase that the icon can be any composable in this version of
            // StandardChip.
            StandardChip(
                label = "Primary label",
                onClick = { },
                icon = {
                    Box(
                        Modifier
                            .size(32.dp)
                            .background(Color.White)
                    )
                },
                chipType = StandardChipType.Secondary
            )
        }
    }

    @Test
    fun disabledWithIconPlaceholder() {
        composeTestRule.setContent {
            // In inspection mode will jump to placeholder
            CompositionLocalProvider(LocalInspectionMode.provides(true)) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = "iconUri",
                    placeholder = rememberVectorPainter(
                        image = Icons.Default.Image,
                        tintColor = Color.Black
                    ),
                    chipType = StandardChipType.Secondary,
                    enabled = false
                )
            }
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
            )
                .materialPath {
                    horizontalLineToRelative(12.0f)
                    verticalLineToRelative(12.0f)
                    horizontalLineTo(0.0f)
                    close()
                }
                .build()

        private val Icon32dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Large",
                defaultWidth = 32f.dp,
                defaultHeight = 32f.dp,
                viewportWidth = 32f,
                viewportHeight = 32f
            )
                .materialPath {
                    horizontalLineToRelative(32.0f)
                    verticalLineToRelative(32.0f)
                    horizontalLineTo(0.0f)
                    close()
                }
                .build()

        private val Icon48dp: ImageVector
            get() = ImageVector.Builder(
                name = "Icon Extra Large",
                defaultWidth = 48f.dp,
                defaultHeight = 48f.dp,
                viewportWidth = 48f,
                viewportHeight = 48f
            )
                .materialPath {
                    horizontalLineToRelative(48.0f)
                    verticalLineToRelative(48.0f)
                    horizontalLineTo(0.0f)
                    close()
                }
                .build()
    }
}

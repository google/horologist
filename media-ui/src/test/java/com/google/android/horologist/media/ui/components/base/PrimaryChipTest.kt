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

package com.google.android.horologist.media.ui.components.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.media.ui.utils.rememberVectorPainter
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class PrimaryChipTest {

    private val maxPercentDifference = 0.1

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = maxPercentDifference,
        snapshotHandler = WearSnapshotHandler(determineHandler(maxPercentDifference))
    )

    @Test
    fun default() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { }
                )
            }
        }
    }

    @Test
    fun withSecondaryLabel() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label"
                )
            }
        }
    }

    @Test
    fun withIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icons.Default.Image
                )
            }
        }
    }

    @Test
    fun withLargeIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icon32dp,
                    largeIcon = true
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = Icons.Default.Image
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLargeIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = Icon32dp,
                    largeIcon = true
                )
            }
        }
    }

    @Test
    fun disabled() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = Icons.Default.Image,
                    enabled = false
                )
            }
        }
    }

    @Test
    fun withLongText() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label very very very very very very very very very very very very very very very very very long text",
                    onClick = { }
                )
            }
        }
    }

    @Test
    fun withSecondaryLabelAndLongText() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label very very very very very very very very long text",
                    onClick = { },
                    secondaryLabel = "Secondary label very very very very very very very very very long text",
                    icon = Icons.Default.Image
                )
            }
        }
    }

    @Test
    fun usingSmallIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icon12dp
                )
            }
        }
    }

    @Test
    fun withLargeIconUsingSmallIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icon12dp,
                    largeIcon = true
                )
            }
        }
    }

    @Test
    fun usingExtraLargeIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icon48dp
                )
            }
        }
    }

    @Test
    fun withLargeIconUsingExtraLargeIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icon48dp,
                    largeIcon = true
                )
            }
        }
    }

    @Test
    fun withPlaceholderIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    icon = Icons.Default.Image
                )
            }
        }
    }

    @Test
    fun disabledWithIconPlaceholder() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = "iconUri",
                    placeholder = rememberVectorPainter(
                        image = Icons.Default.Image,
                        tintColor = Color.Black
                    ),
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

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

@file:OptIn(ExperimentalHorologistPaparazziApi::class)

package com.google.android.horologist.base.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test

class StandardChipIconWithProgressTest {

    @get:Rule
    val paparazzi = WearPaparazzi(
        maxPercentDifference = 0.1
    )

    @Test
    fun default() {
        paparazzi.snapshot {
            FakeImageLoader.NotFound.override {
                Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                    StandardChipIconWithProgress(progress = 75f)
                }
            }
        }
    }

    @Test
    fun withProgressSmallIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChipIconWithProgress(progress = 75f, icon = Icon12dp)
            }
        }
    }

    @Test
    fun withProgressMediumIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChipIconWithProgress(progress = 75f, icon = Icon32dp)
            }
        }
    }

    @Test
    fun withProgressLargeIcon() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                StandardChipIconWithProgress(
                    progress = 75f,
                    icon = Icon48dp,
                    largeIcon = true
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

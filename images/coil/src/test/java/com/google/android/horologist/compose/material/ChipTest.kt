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
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class ChipTest : WearLegacyComponentTest() {
    @Test
    fun withPlaceholderIcon() {
        runComponentTest {
            // In inspection mode will jump to placeholder
            CompositionLocalProvider(LocalInspectionMode.provides(true)) {
                Chip(
                    label = "Primary label",
                    onClick = { },
                    icon = CoilPaintable(
                        "iconUri",
                        placeholder = rememberVectorPainter(
                            image = Icons.Default.Image,
                            tintColor = Color.Black,
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun disabledWithIconPlaceholder() {
        runComponentTest {
            // In inspection mode will jump to placeholder
            CompositionLocalProvider(LocalInspectionMode.provides(true)) {
                Chip(
                    label = "Primary label",
                    onClick = { },
                    secondaryLabel = "Secondary label",
                    icon = CoilPaintable(
                        "iconUri",
                        placeholder = rememberVectorPainter(
                            image = Icons.Default.Image,
                            tintColor = Color.Black,
                        ),
                    ),
                    enabled = false,
                )
            }
        }
    }
}

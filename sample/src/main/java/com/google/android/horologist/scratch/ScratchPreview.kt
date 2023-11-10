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

package com.google.android.horologist.scratch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.wear.compose.material.Icon
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.accompanist.testharness.TestHarness
import com.google.android.horologist.compose.material.Icon
import com.google.android.horologist.compose.material.IconRtlMode

@WearPreviewLargeRound
@Composable
fun WearPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Icon(
                Icons.AutoMirrored.Filled.DirectionsBike,
                contentDescription = null,
            )
            Icon(
                Icons.AutoMirrored.Filled.DirectionsBike,
                contentDescription = null,
                rtlMode = IconRtlMode.Mirrored
            )
        }

        Row {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Icon(
                    Icons.AutoMirrored.Filled.DirectionsBike,
                    contentDescription = null,
                )
                Icon(
                    Icons.AutoMirrored.Filled.DirectionsBike,
                    contentDescription = null,
                    rtlMode = IconRtlMode.Mirrored
                )
            }
        }

        Row {
            Icon(
                imageVector = Icons.Filled.DirectionsBike,
                contentDescription = null,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                contentDescription = null,
            )
        }

        Row {
            TestHarness(layoutDirection = LayoutDirection.Rtl) {
                Icon(
                    imageVector = Icons.Filled.DirectionsBike,
                    contentDescription = null,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                    contentDescription = null,
                )
            }
        }
    }
}
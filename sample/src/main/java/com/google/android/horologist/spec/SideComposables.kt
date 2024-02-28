/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.spec

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState

// @WearCustomPreviews
@Composable
fun SideMixed() {
    SampleMenu(
        columnState = rememberResponsiveColumnState(
            contentPadding = ScalingLazyColumnDefaults.padding(
                first = ItemType.Chip,
                last = ItemType.Text,
            ),
        ),
        borders = {
            side(0.052f)
        },
    ) {
        item {
            SoundToggleChip()
        }
        item {
            ListHeader {
                Text("Media volume")
            }
        }
        item {
            VolumeSlider()
        }
    }
}

// @WearCustomPreviews
@Composable
fun SideChips() {
    SampleMenu(
        rememberResponsiveColumnState(
            contentPadding = ScalingLazyColumnDefaults.padding(
                first = ItemType.Chip,
                last = ItemType.Chip,
            ),
        ),
        borders = {
            side(0.052f)
        },
    )
}

// @WearCustomPreviews
@Composable
fun SideCards() {
    SampleMenu(
        rememberResponsiveColumnState(
            contentPadding = ScalingLazyColumnDefaults.padding(
                first = ItemType.Card,
                last = ItemType.Card,
            ),
        ),
        borders = {
            side(0.052f)
        },
    ) {
        item {
            MessagesCard()
        }
        item {
            BofACard()
        }
    }
}

// @WearCustomPreviews
@Composable
fun SideText() {
    SampleMenu(
        rememberResponsiveColumnState(
            contentPadding = ScalingLazyColumnDefaults.padding(
                first = ItemType.Text,
                last = ItemType.Text,
            ),
        ),
        borders = {
            side(0.052f)
            side(0.052f, 0.052f, Color.Yellow)
        },
    ) {
        warningTextItems()
    }
}

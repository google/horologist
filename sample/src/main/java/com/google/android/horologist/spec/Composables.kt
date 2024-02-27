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

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState

@WearCustomPreviews
@Composable
fun Sample1Line() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text
        )
    ),
        before = {
            Title(lines = 1)
        },
        borders = {
            top(0.1644f)
        },
    )
}

@WearCustomPreviews
@Composable
fun Sample2Lines() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text
        )
    ),
        before = {
            Title(lines = 2)
        },
        borders = {
            top(0.1644f)
        },
    )
}

@WearCustomPreviews
@Composable
fun Sample3Lines() {
    SampleMenu(
        columnState = rememberResponsiveColumnState(
            contentPadding = ScalingLazyColumnDefaults.padding(
                first = ScalingLazyColumnDefaults.ItemType.Text
            )
        ),
        before = {
            Title(lines = 3)
        },
        borders = {
            top(0.1644f)
        },
    )
}

@WearCustomPreviews
@Composable
fun Sample1Button() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.SingleButton
        )
    ),
        before = {
            AddCircleButton()
        },
        borders = {
            top(0.1248f)
        },
    )
}

@WearCustomPreviews
@Composable
fun Sample2Buttons() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.MultiButton
        )
    ),
        before = {
            Row {
                AddCircleButton()
                LocationOnButton()
            }
        },
        borders = {
            top(0.2188f)
        },
    )
}

@WearCustomPreviews
@Composable
fun SampleCompactChip() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.CompactChip
        )
    ),
        before = {
            DoneCompactChip()
        },
        borders = {
            top(0.1248f)
        },
    )
}

@WearCustomPreviews
@Composable
fun SampleOtherChips() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Chip
        )
    ),borders = {
        top(0.2188f)
    })
}

@WearCustomPreviews
@Composable
fun SampleOtherCards() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Card
        )
    ),
        borders = {
            top(0.2188f)
        }
    ) {
        item {
            MessagesCard()
        }
        item {
            BofACard()
        }
    }
}

@WearCustomPreviews
@Composable
fun SampleOtherText() {
    SampleMenu(columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text
        )
    ),
        borders = {
            top(0.2188f)
        }
    ) {
        warningTextItems()
    }
}
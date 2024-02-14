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


@WearCustomPreviews
@Composable
fun Bottom1Button() {
    SampleMenu(atBottom = true, borders = {
        bottom(0.2083f)
    },
        before = {
            AddCircleButton()
        }
    )
}

@WearCustomPreviews
@Composable
fun Bottom2Buttons() {
    SampleMenu(
        before = {
            Row {
                AddCircleButton()
                LocationOnButton()
            }
        },
        borders = {
            bottom(0.2083f)
        },
        atBottom = true
    )
}

@WearCustomPreviews
@Composable
fun Bottom3Buttons() {
    SampleMenu(
        before = {
            Row {
                AddCircleButton()
                LocationOnButton()
            }
        },
        borders = {
            bottom(0.2083f)
        },
        atBottom = true
    )
}

@WearCustomPreviews
@Composable
fun BottomOtherChips() {
    SampleMenu(borders = {
        bottom(0.3646f)
    }, atBottom = true)
}

@WearCustomPreviews
@Composable
fun BottomOtherCards() {
    SampleMenu(atBottom = true,
        borders = {
            bottom(0.3646f)
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
fun BottomOtherText() {
    SampleMenu(atBottom = true,
        borders = {
            bottom(0.3646f)
        }
    ) {
        item {
            WarningText()
        }
    }
}
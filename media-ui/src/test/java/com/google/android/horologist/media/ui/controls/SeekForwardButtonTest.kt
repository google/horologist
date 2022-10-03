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

@file:OptIn(
    ExperimentalHorologistPaparazziApi::class,
    ExperimentalHorologistMediaUiApi::class
)

package com.google.android.horologist.media.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.components.controls.SeekForwardButton
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class SeekForwardButtonTest {

    private val maxPercentDifference = 0.1

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = maxPercentDifference,
        snapshotHandler = WearSnapshotHandler(determineHandler(maxPercentDifference))
    )

    @Test
    fun givenIncrementIsFive_thenIconIsFive() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                SeekForwardButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Five
                )
            }
        }
    }

    @Test
    fun givenIncrementIsTen_thenIconIsTen() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                SeekForwardButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Ten
                )
            }
        }
    }

    @Test
    fun givenIncrementIsThirty_thenIconIsThirty() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                SeekForwardButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Thirty
                )
            }
        }
    }

    @Test
    fun givenIncrementIsOtherValue_thenIconIsDefault() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                SeekForwardButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Other(15)
                )
            }
        }
    }

    @Test
    fun givenIncrementIsUnknown_thenIconIsDefault() {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
                SeekForwardButton(
                    onClick = {},
                    seekButtonIncrement = SeekButtonIncrement.Unknown
                )
            }
        }
    }
}

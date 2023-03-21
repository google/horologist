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
    ExperimentalHorologistApi::class
)

package com.google.android.horologist.media.ui.controls

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.a11y.ComposeA11yExtension
import com.google.android.horologist.compose.tools.snapshotInABox
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
import com.google.android.horologist.media.ui.components.controls.SeekForwardButton
import com.google.android.horologist.paparazzi.RoundNonFullScreenDevice
import com.google.android.horologist.paparazzi.WearPaparazzi
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test

class SeekForwardButtonA11yTest {

    private val maxPercentDifference = 1.0

    private val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = WearPaparazzi(
        deviceConfig = RoundNonFullScreenDevice,
        maxPercentDifference = maxPercentDifference,
        renderExtensions = setOf(composeA11yExtension),
        snapshotHandler = A11ySnapshotHandler(
            delegate = determineHandler(
                maxPercentDifference = maxPercentDifference
            ),
            accessibilityStateFn = { composeA11yExtension.accessibilityState }
        )
    )

    @Test
    fun incrementIsFive() {
        paparazzi.snapshotInABox {
            SeekForwardButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Five
            )
        }
    }

    @Test
    fun incrementIsTen() {
        paparazzi.snapshotInABox {
            SeekForwardButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Ten
            )
        }
    }

    @Test
    fun incrementIsThirty() {
        paparazzi.snapshotInABox {
            SeekForwardButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Thirty
            )
        }
    }

    @Test
    fun incrementIsOther() {
        paparazzi.snapshotInABox {
            SeekForwardButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Known(15)
            )
        }
    }

    @Test
    fun incrementIsUnknown() {
        paparazzi.snapshotInABox {
            SeekForwardButton(
                onClick = {},
                seekButtonIncrement = SeekButtonIncrement.Unknown
            )
        }
    }
}

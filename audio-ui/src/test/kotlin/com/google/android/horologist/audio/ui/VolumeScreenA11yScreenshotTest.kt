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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.audio.ui

import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.screenshots.ScreenshotTest
import com.google.android.horologist.screenshots.a11y.A11ySnapshotTransformer
import org.junit.Test

class VolumeScreenA11yScreenshotTest : ScreenshotTest() {
    init {
        snapshotTransformer = A11ySnapshotTransformer()
        tolerance = 3.0f
        screenTimeText = {}
    }

    @Test
    fun volumeScreenAtMinimums() {
        val volumeState = VolumeState(
            current = 0,
            max = 100
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        takeScreenshot(timeText = {}) {
            VolumeScreenTestCase(
                colors = MaterialTheme.colors,
                volumeState = volumeState,
                audioOutput = audioOutput
            )
        }
    }
}

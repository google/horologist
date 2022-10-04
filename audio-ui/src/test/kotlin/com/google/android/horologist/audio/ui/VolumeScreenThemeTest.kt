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
    ExperimentalHorologistAudioApi::class,
    ExperimentalHorologistComposeToolsApi::class,
    ExperimentalHorologistPaparazziApi::class
)

package com.google.android.horologist.audio.ui

import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.ThemeValues
import com.google.android.horologist.compose.tools.themeValues
import com.google.android.horologist.paparazzi.ExperimentalHorologistPaparazziApi
import com.google.android.horologist.paparazzi.WearPaparazzi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class VolumeScreenThemeTest(
    private val themeValue: ThemeValues
) {
    @get:Rule
    val paparazzi = WearPaparazzi()

    @Test
    fun volumeScreenThemes() {
        val volumeState = VolumeState(
            current = 50,
            max = 100
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        paparazzi.snapshot(name = themeValue.safeName) {
            VolumeScreenTestCase(
                colors = themeValue.colors,
                volumeState = volumeState,
                audioOutput = audioOutput
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun colours() = themeValues
    }
}

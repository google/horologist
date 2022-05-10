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

@file:OptIn(ExperimentalHorologistAudioUiApi::class, ExperimentalHorologistAudioApi::class)

package com.google.android.horologist.audioui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import app.cash.paparazzi.HtmlReportWriter
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.SnapshotHandler
import app.cash.paparazzi.SnapshotVerifier
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class VolumeScreenTest(
    private val themeValue: ThemeValues
) {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = 0.0,
        snapshotHandler = WearSnapshotHandler(determineHandler(0.1))
    )

    @Test
    fun volumeScreenThemes() {
        val volumeState = VolumeState(
            current = 50,
            max = 100,
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

    @Composable
    private fun VolumeScreenTestCase(
        colors: Colors,
        volumeState: VolumeState,
        audioOutput: AudioOutput.BluetoothHeadset
    ) {
        MaterialTheme(colors = colors) {
            RoundPreview {
                Scaffold(
                    positionIndicator = {
                        VolumePositionIndicator(
                            volumeState = { volumeState },
                            autoHide = false
                        )
                    }
                ) {
                    VolumeScreen(
                        volume = { volumeState },
                        audioOutput = audioOutput,
                        increaseVolume = { },
                        decreaseVolume = { },
                        onAudioOutputClick = { },
                        showVolumeIndicator = false,
                    )
                }
            }
        }
    }

    @Test
    fun volumeScreenAtMinimum() {
        assumeTrue(themeValue.index == 0)

        val volumeState = VolumeState(
            current = 0,
            max = 100,
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        paparazzi.snapshot {
            VolumeScreenTestCase(
                colors = themeValue.colors,
                volumeState = volumeState,
                audioOutput = audioOutput
            )
        }
    }

    @Test
    fun volumeScreenAtMaximum() {
        assumeTrue(themeValue.index == 0)

        val volumeState = VolumeState(
            current = 100,
            max = 100,
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds")

        paparazzi.snapshot {
            VolumeScreenTestCase(
                colors = themeValue.colors,
                volumeState = volumeState,
                audioOutput = audioOutput
            )
        }
    }

    @Test
    fun volumeScreenWithGuides() {
        assumeTrue(themeValue.index == 0)

        val volumeState = VolumeState(
            current = 50,
            max = 100,
        )
        val audioOutput = AudioOutput.BluetoothHeadset("id", "Sennheiser Momentum Wireless")

        paparazzi.snapshot {
            Box(modifier = Modifier.fillMaxSize()) {
                VolumeScreenTestCase(
                    colors = themeValue.colors,
                    volumeState = volumeState,
                    audioOutput = audioOutput
                )
                VolumeScreenUxGuide()
            }
        }
    }

    @Composable
    private fun RoundPreview(content: @Composable () -> Unit) {
        val configuration =
            LocalConfiguration.current.let {
                Configuration(it).apply {
                    screenLayout = (screenLayout or Configuration.SCREENLAYOUT_ROUND_YES)
                }
            }

        CompositionLocalProvider(LocalConfiguration provides configuration) {
            content()
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun colours() = themeValues

        private val isVerifying: Boolean =
            System.getProperty("paparazzi.test.verify")?.toBoolean() == true

        private fun determineHandler(maxPercentDifference: Double): SnapshotHandler {
            return if (isVerifying) {
                SnapshotVerifier(maxPercentDifference)
            } else {
                HtmlReportWriter()
            }
        }
    }
}

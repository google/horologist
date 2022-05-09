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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import app.cash.paparazzi.HtmlReportWriter
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.SnapshotHandler
import app.cash.paparazzi.SnapshotVerifier
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.ExperimentalHorologistAudioApi
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
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
    fun compose() {
        paparazzi.snapshot(name = "VolumeScreen_${themeValue.safeName}") {
            MaterialTheme(colors = themeValue.colors) {
                RoundPreview {
                    Scaffold(
                        timeText = {
                            TimeText(
                                timeSource = object : TimeSource {
                                    override val currentTime: String
                                        @Composable get() = "1:03 pm"
                                }
                            )
                        },
                        positionIndicator = {
                            VolumePositionIndicator(
                                volumeState = { VolumeState(5, 10) },
                                autoHide = false
                            )
                        }
                    ) {
                        VolumeScreen(
                            volume = {
                                VolumeState(
                                    current = 50,
                                    max = 100,
                                )
                            },
                            audioOutput = AudioOutput.BluetoothHeadset("id", "Pixelbuds"),
                            increaseVolume = { },
                            decreaseVolume = { },
                            onAudioOutputClick = { }
                        )
                    }
                }
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

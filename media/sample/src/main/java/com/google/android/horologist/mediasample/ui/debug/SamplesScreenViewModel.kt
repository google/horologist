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

package com.google.android.horologist.mediasample.ui.debug

import androidx.lifecycle.ViewModel
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SamplesScreenViewModel
    @Inject
    constructor(
        private val playerRepository: PlayerRepository,
    ) : ViewModel() {
        val uiState: StateFlow<UiState> = MutableStateFlow(
            UiState(
                samples = listOf(
                    fraunhoferGapless,
                    gapless,
                    gaplessStripped,
                ),
            ),
        )

        fun playSamples(id: Int): Boolean {
            val mediaItems = uiState.value.samples.find {
                it.id == id
            }?.mediaItems

            if (mediaItems != null) {
                playerRepository.setMediaList(mediaItems)
                playerRepository.play()
                return true
            }

            return false
        }

        data class Sample(
            val id: Int,
            val name: String,
            val mediaItems: List<Media>,
        )

        data class UiState(
            val samples: List<Sample>,
        )

        companion object {
            val fraunhoferGapless =
                Sample(
                    1,
                    "Fraunhofer Gapless",
                    listOf(
                        "https://www2.iis.fraunhofer.de/AAC/gapless-sweep_part1_iis.m4a?delay=1600&padding=106",
                        "https://www2.iis.fraunhofer.de/AAC/gapless-sweep_part2_iis.m4a?delay=110&padding=1024",
                    ).mapIndexed { i, it ->
                        Media(
                            id = i.toString(),
                            uri = it,
                            title = "Fraunhofer Gapless $i",
                            artist = "fraunhofer",
                            artworkUri = "https://www2.iis.fraunhofer.de/AAC/logo-fraunhofer.gif",
                        )
                    },
                )

            val gapless =
                Sample(
                    2,
                    "Gapless",
                    @Suppress("ktlint:standard:max-line-length")
                    listOf(
                        "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-10.m4a",
                        "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-11.m4a",
                    ).mapIndexed { i, it ->
                        Media(
                            id = i.toString(),
                            uri = it,
                            title = "Gapless $i",
                            artist = "unknown",
                        )
                    },
                )

            val gaplessStripped =
                Sample(
                    3,
                    "Gapless Stripped",
                    @Suppress("ktlint:standard:max-line-length")
                    listOf(
                        "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-10-stripped.m4a",
                        "https://storage.googleapis.com/exoplayer-test-media-internal-63834241aced7884c2544af1a3452e01/m4a/gapless-asot-11-stripped.m4a",
                    ).mapIndexed { i, it ->
                        Media(
                            id = i.toString(),
                            uri = it,
                            title = "Gapless (stripped) $i",
                            artist = "unknown",
                        )
                    },
                )
        }
    }

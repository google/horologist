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

package com.google.android.horologist.sectionedlist.stateful

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SectionedListStatefulScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            recommendationSectionState = RecommendationSectionState.Loading,
            trendingSectionState = TrendingSectionState.Loading
        )
    )
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadRecommendations()
        loadTrending()
    }

    data class UiState(
        val recommendationSectionState: RecommendationSectionState,
        val trendingSectionState: TrendingSectionState
    )

    sealed class RecommendationSectionState {
        object Loading : RecommendationSectionState()
        data class Loaded(val list: List<Recommendation>) : RecommendationSectionState()
        object Failed : RecommendationSectionState()
    }

    data class Recommendation(
        val playlistName: String,
        val icon: ImageVector
    )

    sealed class TrendingSectionState {
        object Loading : TrendingSectionState()
        data class Loaded(val list: List<Trending>) : TrendingSectionState()
        object Failed : TrendingSectionState()
    }

    data class Trending(
        val name: String,
        val artist: String
    )

    fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                recommendationSectionState = RecommendationSectionState.Loading
            )

            // pretend it is fetching date over remote
            delay(getRandomTimeInMillis())

            _uiState.value = _uiState.value.copy(
                recommendationSectionState = if (shouldFailToLoad()) {
                    RecommendationSectionState.Failed
                } else {
                    RecommendationSectionState.Loaded(
                        list = listOf(
                            Recommendation("Running playlist", Icons.Default.DirectionsRun),
                            Recommendation("Focus", Icons.Default.SelfImprovement)
                        )
                    )
                }
            )
        }
    }

    fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                trendingSectionState = TrendingSectionState.Loading
            )

            // pretend it is fetching date over remote
            delay(getRandomTimeInMillis())

            _uiState.value = _uiState.value.copy(
                trendingSectionState = if (shouldFailToLoad()) {
                    TrendingSectionState.Failed
                } else {
                    TrendingSectionState.Loaded(
                        list = listOf(
                            Trending("There'd Better Be A Mirrorball", "Arctic Monkeys"),
                            Trending("180 Hours", "Dudu Kanegae")
                        )
                    )
                }
            )
        }
    }

    private fun shouldFailToLoad(): Boolean {
        return Random.nextInt(1, 3) == 1
    }

    private fun getRandomTimeInMillis(): Long {
        return Random.nextLong(3000, 7000)
    }
}

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

package com.google.android.horologist.mediasample.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    data class UiState(
        val podcastControls: Boolean = false,
        val loadItemsAtStartup: Boolean = true
    )

    fun setPodcastControls(enabled: Boolean) {
        _uiState.value = uiState.value.copy(podcastControls = enabled)
    }

    fun setLoadItemsAtStartup(enabled: Boolean) {
        _uiState.value = uiState.value.copy(loadItemsAtStartup = enabled)
    }

    fun logout() {
        // TODO login and logout functionality
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                SettingsScreenViewModel()
            }
        }
    }
}

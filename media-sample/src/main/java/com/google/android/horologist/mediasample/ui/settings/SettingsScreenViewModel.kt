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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.mediasample.di.MediaApplicationContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val uiState: StateFlow<UiState> = dataStore.data.map {
        UiState(
            settings = Settings(it)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    data class UiState(
        val settings: Settings? = null,
    )

    fun setPodcastControls(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit {
                it[Settings.PodcastControls] = enabled
            }
        }
    }

    fun setLoadItemsAtStartup(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit {
                it[Settings.LoadItemsAtStartup] = enabled
            }
        }
    }

    fun logout() {
        // TODO login and logout functionality
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                SettingsScreenViewModel(
                    dataStore = this[MediaApplicationContainer.DataStoreKey]!!,
                )
            }
        }
    }
}

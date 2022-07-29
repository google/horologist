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

package com.google.android.horologist.mediasample.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.google.android.horologist.mediasample.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun edit(transform: suspend (MutablePreferences) -> Unit) {
        dataStore.edit {
            transform(it)
        }
    }

    suspend fun writePodcastControls(enabled: Boolean) {
        edit {
            it[PodcastControls] = enabled
        }
    }

    suspend fun writeLoadItemsAtStartup(enabled: Boolean) {
        edit {
            it[LoadItemsAtStartup] = enabled
        }
    }

    suspend fun writeShowTimeTextInfo(enabled: Boolean) {
        edit {
            it[ShowTimeTextInfo] = enabled
        }
    }

    suspend fun writeAnimated(enabled: Boolean) {
        edit {
            it[Animated] = enabled
        }
    }

    suspend fun writeDebugOffload(enabled: Boolean) {
        edit {
            it[DebugOffload] = enabled
        }
    }

    val settingsFlow: Flow<Settings> = dataStore.data.map {
        it.toSettings()
    }

    companion object {
        val ShowTimeTextInfo = booleanPreferencesKey("show_time_text_info")
        val PodcastControls = booleanPreferencesKey("podcast_controls")
        val LoadItemsAtStartup = booleanPreferencesKey("load_items_at_startup")
        val Animated = booleanPreferencesKey("animated")
        val DebugOffload = booleanPreferencesKey("debug_offload")

        fun Preferences.toSettings() = Settings(
            showTimeTextInfo = this[ShowTimeTextInfo] ?: false,
            podcastControls = this[PodcastControls] ?: false,
            loadItemsAtStartup = this[LoadItemsAtStartup] ?: true,
            animated = this[Animated] ?: true,
            debugOffload = this[DebugOffload] ?: false
        )
    }
}

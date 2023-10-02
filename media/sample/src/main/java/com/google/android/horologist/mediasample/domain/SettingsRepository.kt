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
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences
import androidx.media3.common.util.UnstableApi
import com.google.android.horologist.mediasample.domain.proto.SettingsProto
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.Settings
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val dataStore: DataStore<Settings>,
) {

    suspend fun edit(transform: suspend (Settings) -> Settings) {
        dataStore.updateData {
            transform(it)
        }
    }

    val settingsFlow: Flow<Settings> = dataStore.data
}

val SettingsProto.OffloadMode.strategy: AudioOffloadPreferences
    @UnstableApi
    get() = when (this) {
        SettingsProto.OffloadMode.OFFLOAD_MODE_ALWAYS -> AudioOffloadPreferences.Builder()
            .setAudioOffloadMode(
                AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_REQUIRED
            )
            .build()

        SettingsProto.OffloadMode.OFFLOAD_MODE_NEVER -> AudioOffloadPreferences.Builder()
            .setAudioOffloadMode(
                AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_DISABLED
            )
            .build()

        SettingsProto.OffloadMode.OFFLOAD_MODE_IF_SUPPORTED -> AudioOffloadPreferences.Builder()
            .setAudioOffloadMode(
                AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_ENABLED
            )
            .setIsGaplessSupportRequired(true)
            .setIsSpeedChangeSupportRequired(false)
            .build()

        else -> AudioOffloadPreferences.DEFAULT
    }

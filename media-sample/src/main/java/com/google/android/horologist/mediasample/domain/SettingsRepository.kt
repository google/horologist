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
import com.google.android.horologist.media3.offload.AudioOffloadStrategy
import com.google.android.horologist.media3.offload.BackgroundAudioOffloadStrategy
import com.google.android.horologist.mediasample.domain.proto.SettingsProto
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.Settings
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val dataStore: DataStore<Settings>
) {

    suspend fun edit(transform: suspend (Settings) -> Settings) {
        dataStore.updateData {
            transform(it)
        }
    }

    val settingsFlow: Flow<Settings> = dataStore.data
}

val SettingsProto.OffloadMode.strategy: AudioOffloadStrategy
    get() = when (this) {
        SettingsProto.OffloadMode.BACKGROUND -> BackgroundAudioOffloadStrategy
        SettingsProto.OffloadMode.ALWAYS -> AudioOffloadStrategy.Always
        SettingsProto.OffloadMode.NEVER -> AudioOffloadStrategy.Never
        SettingsProto.OffloadMode.UNRECOGNIZED -> AudioOffloadStrategy.Never
    }

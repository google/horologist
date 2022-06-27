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

package com.google.android.horologist.mediasample.di

import com.google.android.horologist.audio.SystemAudioRepository
import com.google.android.horologist.media3.audio.AudioOutputSelector
import com.google.android.horologist.media3.audio.BluetoothSettingsOutputSelector
import java.io.Closeable

/**
 * Simple DI implementation - to be replaced by hilt.
 */
class AudioContainer(
    private val mediaApplicationContainer: MediaApplicationContainer
) : Closeable {
    val audioOutputSelector: AudioOutputSelector by lazy {
        BluetoothSettingsOutputSelector(systemAudioRepository)
    }

    // Must be on Main thread
    val systemAudioRepository: SystemAudioRepository =
        SystemAudioRepository.fromContext(mediaApplicationContainer.application)

    override fun close() {
        systemAudioRepository.close()
    }
}

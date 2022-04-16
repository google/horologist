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

package com.google.android.horologist.audioui

import com.google.android.horologist.audio.ExperimentalAudioApi
import com.google.android.horologist.audio.VolumeRepository
import com.google.android.horologist.audio.VolumeState
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalAudioApi::class)
class FakeVolumeRepository @OptIn(ExperimentalAudioApi::class) constructor(initial: VolumeState) : VolumeRepository {
    override val volumeState: MutableStateFlow<VolumeState> = MutableStateFlow(initial)

    override fun increaseVolume() {
        val current = volumeState.value
        volumeState.value = current.copy(current = (current.current + 1).coerceAtMost(current.max))
    }

    override fun decreaseVolume() {
        val current = volumeState.value
        volumeState.value = current.copy(current = (current.current - 1).coerceAtLeast(0))
    }

    override fun close() {
    }
}

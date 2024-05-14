/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.test.toolbox.testdoubles

import com.google.android.horologist.audio.VolumeRepository
import com.google.android.horologist.audio.VolumeState
import kotlinx.coroutines.flow.MutableStateFlow

class FakeVolumeRepository(initial: VolumeState) : VolumeRepository {
    override val volumeState: MutableStateFlow<VolumeState> = MutableStateFlow(initial)

    override fun increaseVolume() {
        val current = volumeState.value
        volumeState.value = current.copy(current = (current.current + 1).coerceAtMost(current.max))
    }

    override fun decreaseVolume() {
        val current = volumeState.value
        volumeState.value = current.copy(current = (current.current - 1).coerceAtLeast(current.min))
    }

    override fun setVolume(volume: Int) {
        val current = volumeState.value
        volumeState.value = current.copy(current = volume.coerceIn(current.min, current.max))
    }

    override fun close() {
    }
}

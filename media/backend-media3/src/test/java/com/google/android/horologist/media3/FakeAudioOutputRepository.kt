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

package com.google.android.horologist.media3

import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.AudioOutputRepository
import kotlinx.coroutines.flow.MutableStateFlow

open class FakeAudioOutputRepository : AudioOutputRepository {
    override val audioOutput: MutableStateFlow<AudioOutput> = MutableStateFlow(AudioOutput.None)
    override val available: MutableStateFlow<List<AudioOutput>> = MutableStateFlow(listOf())

    override fun launchOutputSelection(closeOnConnect: Boolean) {
    }

    override fun close() {
    }
}

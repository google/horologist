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

package com.google.android.horologist.media3.offload

import android.media.AudioTrack
import androidx.media3.exoplayer.audio.DefaultAudioSink

internal val DefaultAudioSink.audioTrack: AudioTrack?
    get() {
        return audioTrackField.get(this) as AudioTrack?
    }

private val audioTrackField by lazy {
    DefaultAudioSink::class.java.getDeclaredField("audioTrack").apply {
        this.isAccessible = true
    }
}

private val offloadModeField by lazy {
    DefaultAudioSink::class.java.getDeclaredField("offloadMode").apply {
        this.isAccessible = true
    }
}

internal var DefaultAudioSink.offloadMode: Int
    get() = offloadModeField.get(this) as Int
    set(value) = offloadModeField.set(this, value)
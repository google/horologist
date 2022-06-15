/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.android.horologist.media3.rules

import androidx.media3.common.MediaItem
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi

/**
 * Configuration rules for how restrictive or permissive the app should
 * be for actions like playing live streams.
 */
@ExperimentalHorologistMedia3BackendApi
public interface PlaybackRules {
    /**
     * Can the given item be played with it's given state.
     */
    public suspend fun canPlayItem(mediaItem: MediaItem): Boolean

    /**
     * Can Media be played with the given audio target.
     */
    public fun canPlayWithOutput(audioOutput: AudioOutput): Boolean

    @ExperimentalHorologistMedia3BackendApi
    public object Normal : PlaybackRules {
        override suspend fun canPlayItem(mediaItem: MediaItem): Boolean = true

        override fun canPlayWithOutput(audioOutput: AudioOutput): Boolean =
            audioOutput is AudioOutput.BluetoothHeadset
    }

    @ExperimentalHorologistMedia3BackendApi
    public object Emulator : PlaybackRules {
        override suspend fun canPlayItem(mediaItem: MediaItem): Boolean = true

        override fun canPlayWithOutput(audioOutput: AudioOutput): Boolean = true
    }
}

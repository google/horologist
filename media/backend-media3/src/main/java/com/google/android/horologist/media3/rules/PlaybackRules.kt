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
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.AudioOutput

/**
 * Configuration rules for how restrictive or permissive the app should
 * be for actions like playing live streams.
 */
@ExperimentalHorologistApi
public interface PlaybackRules {
    /**
     * Can the given item be played with it's given state.
     */
    public suspend fun canPlayItem(mediaItem: MediaItem): Boolean

    @ExperimentalHorologistApi
    public object Normal : PlaybackRules {
        override suspend fun canPlayItem(mediaItem: MediaItem): Boolean = true
    }

    @ExperimentalHorologistApi
    public object SpeakerAllowed : PlaybackRules {
        override suspend fun canPlayItem(mediaItem: MediaItem): Boolean = true
    }
}

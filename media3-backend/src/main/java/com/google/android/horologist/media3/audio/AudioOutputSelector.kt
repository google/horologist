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

package com.google.android.horologist.media3.audio

import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi

/**
 * Strategy interface for different audio output switchers.
 */
@ExperimentalHorologistMedia3BackendApi
public interface AudioOutputSelector {
    /**
     * Change from the current audio output, according to some sensible logic,
     * and return when either the user has selected a new audio output or returning null
     * if timed out.
     */
    public suspend fun selectNewOutput(currentAudioOutput: AudioOutput): AudioOutput?

    fun launchSelector()
}

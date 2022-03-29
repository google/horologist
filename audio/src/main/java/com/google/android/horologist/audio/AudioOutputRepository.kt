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

package com.google.android.horologist.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Audio Output Repository for identifying available audio devices in a simple manner.
 */
@ExperimentalAudioApi
public interface AudioOutputRepository : AutoCloseable {
    /**
     * The current audio output.
     */
    public val audioOutput: StateFlow<AudioOutput>

    /**
     * The list of available audio output devices.
     */
    public val available: StateFlow<List<AudioOutput>>

    /**
     * Action to launch output selection by the user.
     */
    public fun launchOutputSelection(closeOnConnect: Boolean)
}

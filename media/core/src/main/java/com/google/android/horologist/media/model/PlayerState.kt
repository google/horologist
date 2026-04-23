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

package com.google.android.horologist.media.model

import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * Represents the state of the player.
 */
@ExperimentalHorologistApi
public enum class PlayerState {

    /**
     * Initial empty state. The player hasn't finished loading or doesn't have any media added.
     */
    Idle,

    /**
     * Playback is requested, but the player is not yet ready and is working towards playback.
     * The player is expected to eventually reach [Playing] from this state without further user
     * action required.
     */
    Loading,

    /**
     * The player is able to continue or attempt playback. Sending the play command will move from
     * this state to either [Loading] or [Playing].
     */
    Stopped,

    /**
     * The player is playing and position is advancing.
     */
    Playing,
}

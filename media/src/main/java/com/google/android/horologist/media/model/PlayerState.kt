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

/**
 * Represents the state of the player.
 */
public enum class PlayerState {

    /**
     * The player is idle, meaning it holds only limited resources. The player must be prepared
     * before it will play the media.
     */
    Idle,

    /**
     * The player is not able to immediately play the media, but is doing work toward being able to
     * do so. This state typically occurs when the player needs to buffer more data before playback
     * can start.
     */
    Loading,

    /**
     * The player is able to immediately play from its current position.
     */
    Ready,

    /**
     * The player is playing.
     */
    Playing,

    /**
     * The player has finished playing the media.
     */
    Ended
}

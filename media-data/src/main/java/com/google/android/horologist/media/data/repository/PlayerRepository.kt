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

package com.google.android.horologist.media.data.repository

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.android.horologist.media.data.ExperimentalMediaDataApi
import com.google.android.horologist.media.data.model.TrackPosition
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for the current [Player].
 */
@ExperimentalMediaDataApi
public interface PlayerRepository {

    /**
     * The list of available commands at this moment in time. All UI
     * controls that affect the Player should be enabled/disabled based on
     * the available commands.
     */
    public val availableCommands: StateFlow<Player.Commands>

    /**
     * Is the player already playing or set to start playing as soon
     * as the media has buffered?
     */
    public val isPlaying: StateFlow<Boolean>

    /**
     * The current media item playing, or that would play when user hit play.
     */
    public val currentMediaItem: StateFlow<MediaItem?>

    /**
     * The current track position of the player.  This is not updated automatically
     * so [updatePosition] should be called within a ViewModel coroutineScope regularly
     * to update the UI while the activity is in the foreground.
     */
    public val trackPosition: StateFlow<TrackPosition>

    /**
     * The current value for shuffling of media items mode.
     */
    public val shuffleEnabled: StateFlow<Boolean>

    /**
     * Plays the mediaItem after preparing (buffering).
     */
    public fun prepareAndPlay(
        mediaItem: MediaItem,
        play: Boolean = true,
    )

    /**
     * Plays the mediaItems after preparing (buffering).
     * If mediaItems is null, the existing mediaItems and position
     * will be kept.
     */
    public fun prepareAndPlay(
        mediaItems: List<MediaItem>? = null,
        startIndex: Int = 0,
        play: Boolean = true,
    )

    /**
     * Go to the previous track, see [Player.seekToPreviousMediaItem].
     */
    public fun seekToPreviousMediaItem()

    /**
     * Next track, see [Player.seekToNextMediaItem]
     */
    public fun seekToNextMediaItem()

    /**
     * Returns the [seekBack] increment.
     *
     * @return The seek back increment, in milliseconds, or null if this info is not available.
     */
    public fun getSeekBackIncrement(): Long?

    /**
     * Seek back by the default amount, see [Player.seekForward]
     */
    public fun seekBack()

    /**
     * Returns the [seekForward] increment.
     *
     * @return The seek forward increment, in milliseconds, or null if this info is not available.
     */
    public fun getSeekForwardIncrement(): Long?

    /**
     * Seek forward by the default amount, see [Player.seekForward]
     */
    public fun seekForward()

    /**
     * Pause the player, see [Player.pause]
     */
    public fun pause()

    /**
     * Sets whether shuffling of media items is enabled, see [Player.setShuffleModeEnabled]
     */
    public fun toggleShuffle()

    /**
     * Update the position to show track progress correctly on screen.
     * Updating roughly once a second while activity is foregrounded is appropriate.
     */
    public fun updatePosition()
}

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

package com.google.android.horologist.media.repository

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.android.horologist.media.model.PlayerState
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

@ExperimentalHorologistMediaApi
public interface PlayerRepository {

    /**
     * Returns whether the repository is currently connected to a working Player.
     */
    public val connected: StateFlow<Boolean>

    /**
     * Returns the player's currently available [commands][Command].
     */
    public val availableCommands: StateFlow<Set<Command>>

    /**
     * Returns the player's current [state][PlayerState].
     */
    public val currentState: StateFlow<PlayerState>

    /**
     * Returns the current [media item][MediaItem] playing, or that would play when player starts playing.
     */
    public val currentMediaItem: StateFlow<MediaItem?>

    /**
     * Returns the current [media item position][MediaItemPosition] of the player.
     */
    public val mediaItemPosition: StateFlow<MediaItemPosition?>

    /**
     * Returns the current value for shuffling of [media items][MediaItem] mode.
     */
    public val shuffleModeEnabled: StateFlow<Boolean>

    /**
     * Prepares the player. E.g. player will start acquiring all the required resources to play.
     */
    public fun prepare()

    /**
     * Resumes playback as soon as player is ready.
     */
    public fun play()

    /**
     * Play [media item][MediaItem] at given index as soon as player is ready.
     */
    public fun play(mediaItemIndex: Int)

    /**
     * Pauses playback.
     */
    public fun pause()

    /**
     * Returns whether a previous [media item][MediaItem] exists.
     */
    public fun hasPreviousMediaItem(): Boolean

    /**
     * Skips to the default position of previous [media item][MediaItem].
     */
    public fun skipToPreviousMediaItem()

    /**
     * Returns whether a next [media item][MediaItem] exists.
     */
    public fun hasNextMediaItem(): Boolean

    /**
     * Skips to the default position of next [media item][MediaItem].
     */
    public fun skipToNextMediaItem()

    /**
     * Returns the [seekBack] increment.
     */
    public fun getSeekBackIncrement(): Duration

    /**
     * Seeks back in the [current media item][currentMediaItem] by [seek back increment][getSeekBackIncrement].
     */
    public fun seekBack()

    /**
     * Returns the [seekForward] increment.
     */
    public fun getSeekForwardIncrement(): Duration

    /**
     * Seek forward in the [current media item][currentMediaItem] by [seek forward increment][getSeekForwardIncrement].
     */
    public fun seekForward()

    /**
     * Sets whether shuffling of [media items][MediaItem] is enabled.
     */
    public fun setShuffleModeEnabled(shuffleModeEnabled: Boolean)

    /**
     * Clears the playlist, adds the specified [media item][MediaItem] and resets the position to
     * the default position.
     */
    public fun setMediaItem(mediaItem: MediaItem)

    /**
     * Clears the playlist, adds the specified [media items][MediaItem] and resets the position to
     * the default position.
     *
     * @param mediaItems The new [media item][MediaItem].
     */
    public fun setMediaItems(mediaItems: List<MediaItem>)

    /**
     * Adds a [media item][MediaItem] to the end of the playlist.
     */
    public fun addMediaItem(mediaItem: MediaItem)

    /**
     * Adds a [media item][MediaItem] at the given index of the playlist.
     *
     * @param index The index at which to add the [media item][MediaItem]. If the index is larger than the size
     * of the playlist, the media item is added to the end of the playlist.
     * @param mediaItem The [media item][MediaItem] to add.
     */
    public fun addMediaItem(index: Int, mediaItem: MediaItem)

    /**
     * Removes the [media item][MediaItem] at the given index of the playlist.
     *
     * @param index The index at which to remove the [media item][MediaItem].
     */
    public fun removeMediaItem(index: Int)

    /**
     * Clears the playlist.
     */
    public fun clearMediaItems()

    /**
     * Returns the number of [media items][MediaItem] in the playlist.
     * */
    public fun getMediaItemCount(): Int

    /**
     * Returns the [media item][MediaItem] at the given index.
     */
    public fun getMediaItemAt(index: Int): MediaItem?

    /**
     * Returns the index of the current [media item][MediaItem].
     */
    public fun getCurrentMediaItemIndex(): Int

    /**
     * Releases the player. This method must be called when the player is no longer required. The
     * player must not be used after calling this method.
     */
    public fun release()
}

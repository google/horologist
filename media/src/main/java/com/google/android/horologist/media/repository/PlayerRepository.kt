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
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaPosition
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
     * Returns the current [Media] playing, or that would play when player starts playing.
     */
    public val currentMedia: StateFlow<Media?>

    /**
     * Returns the current [media position][MediaPosition] of the player.
     */
    public val mediaPosition: StateFlow<MediaPosition?>

    /**
     * Returns the current value for shuffling of [Media] mode.
     */
    public val shuffleModeEnabled: StateFlow<Boolean>

    /**
     * Returns the [seekBack] increment.
     */
    public val seekBackIncrement: StateFlow<Duration?>

    /**
     * Returns the [seekForward] increment.
     */
    public val seekForwardIncrement: StateFlow<Duration?>

    /**
     * Prepares the player. E.g. player will start acquiring all the required resources to play.
     */
    public fun prepare()

    /**
     * Resumes playback as soon as player is ready.
     */
    public fun play()

    /**
     * Seeks to the default position associated with the specified Media.
     */
    public fun seekToDefaultPosition(mediaIndex: Int)

    /**
     * Pauses playback.
     */
    public fun pause()

    /**
     * Returns whether a previous [Media] exists.
     */
    public fun hasPreviousMedia(): Boolean

    /**
     * Skips to the default position of previous [Media].
     */
    public fun skipToPreviousMedia()

    /**
     * Returns whether a next [Media] exists.
     */
    public fun hasNextMedia(): Boolean

    /**
     * Skips to the default position of next [Media].
     */
    public fun skipToNextMedia()

    /**
     * Seeks back in the [current media][currentMedia] by [seek back increment][getSeekBackIncrement].
     */
    public fun seekBack()

    /**
     * Seek forward in the [current media][currentMedia] by [seek forward increment][getSeekForwardIncrement].
     */
    public fun seekForward()

    /**
     * Sets whether shuffling of [Media] is enabled.
     */
    public fun setShuffleModeEnabled(shuffleModeEnabled: Boolean)

    /**
     * Clears the playlist, adds the specified [Media] and resets the position to
     * the default position.
     */
    public fun setMedia(media: Media)

    /**
     * Clears the playlist, adds the specified [Media] list and resets the position to
     * the default position.
     *
     * @param mediaList The new [Media] list.
     */
    public fun setMediaList(mediaList: List<Media>)

    /**
     * Clears the playlist, adds the specified [Media] list and plays the [Media] at the position of
     * the index passed as param.
     *
     * @param mediaList The new [Media] list.
     * @param index The new position.
     */
    public fun setMediaListAndPlay(mediaList: List<Media>, index: Int)

    /**
     * Adds a [Media] to the end of the playlist.
     */
    public fun addMedia(media: Media)

    /**
     * Adds a [Media] at the given index of the playlist.
     *
     * @param index The index at which to add the [Media]. If the index is larger than the size
     * of the playlist, the media is added to the end of the playlist.
     * @param media The [Media] to add.
     */
    public fun addMedia(index: Int, media: Media)

    /**
     * Removes the [Media] at the given index of the playlist.
     *
     * @param index The index at which to remove the [Media].
     */
    public fun removeMedia(index: Int)

    /**
     * Clears the playlist.
     */
    public fun clearMediaList()

    /**
     * Returns the number of [Media] in the playlist.
     */
    public fun getMediaCount(): Int

    /**
     * Returns the [Media] at the given index.
     */
    public fun getMediaAt(index: Int): Media?

    /**
     * Returns the index of the current [Media].
     */
    public fun getCurrentMediaIndex(): Int

    /**
     * Releases the player. This method must be called when the player is no longer required. The
     * player must not be used after calling this method.
     */
    public fun release()
}

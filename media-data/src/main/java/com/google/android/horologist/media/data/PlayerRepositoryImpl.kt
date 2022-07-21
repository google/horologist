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

package com.google.android.horologist.media.data

import android.util.Log
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.MediaItem
import com.google.android.horologist.media.model.MediaItemPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Repository for the current Media3 Player for a Player Activity.
 *
 * The current implementation is available as soon as the ListenableFuture
 * to connect to the MediaSession completes.
 */
public class PlayerRepositoryImpl : PlayerRepository, Closeable {

    private var onClose: (() -> Unit)? = null
    private var closed = false

    private var _player = MutableStateFlow<Player?>(null)

    /**
     * The active player, or null if no active player is currently available.
     */
    public val player: StateFlow<Player?>
        get() = _player

    private val _connected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean> = _connected

    private val _availableCommands = MutableStateFlow(emptySet<Command>())

    override val availableCommands: StateFlow<Set<Command>> = _availableCommands

    private val _currentState = MutableStateFlow(PlayerState.Idle)

    override val currentState: StateFlow<PlayerState> = _currentState

    private var _currentMediaItem = MutableStateFlow<MediaItem?>(null)

    /**
     * The current media item playing, or that would play when user hit play.
     */
    override val currentMediaItem: StateFlow<MediaItem?>
        get() = _currentMediaItem

    private var _mediaItemPosition = MutableStateFlow<MediaItemPosition?>(null)

    override val mediaItemPosition: StateFlow<MediaItemPosition?> = _mediaItemPosition

    private var _shuffleModeEnabled = MutableStateFlow(false)

    override val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled

    private var _playbackSpeed = MutableStateFlow(1f)

    /**
     * The current playback speed relative to 1.0.
     */
    public val playbackSpeed: StateFlow<Float>
        get() = _playbackSpeed

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updateAvailableCommands(player)
            }

            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                updateCurrentMediaItem(player)
                updatePosition()
            }

            // Reason for handling these events here, instead of using individual callbacks
            // (onIsLoadingChanged, onIsPlayingChanged, onPlaybackStateChanged, etc):
            // - The listener intends to use multiple state values that are reported through
            //   separate callbacks together, or in combination with Player getter methods
            // Reference:
            // https://exoplayer.dev/listening-to-player-events.html#individual-callbacks-vs-onevents
            if (PlayerStateMapper.affectsState(events)) {
                updateState(player)
            }
        }

        // Not firing on onEvents
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            player.value?.let {
                updateShuffleMode(it)
            }
        }

        // Not firing on onEvents
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            player.value?.let {
                updatePlaybackSpeed(it)
            }
        }
    }

    private fun updatePlaybackSpeed(player: Player) {
        _playbackSpeed.value = player.playbackParameters.speed
    }

    private fun updateShuffleMode(player: Player) {
        _shuffleModeEnabled.value = player.shuffleModeEnabled
    }

    private fun updateCurrentMediaItem(player: Player) {
        _currentMediaItem.value = player.currentMediaItem?.let(MediaItemMapper::map)
    }

    /**
     * Update the state based on [Player.isPlaying], [Player.isLoading],
     * [Player.getPlaybackState] and [Player.getPlayWhenReady] properties.
     */
    private fun updateState(player: Player) {
        _currentState.value = PlayerStateMapper.map(player)

        Log.d(TAG, "Player state changed to ${_currentState.value}")
    }

    private fun updateAvailableCommands(player: Player) {
        player.availableCommands.let {
            _availableCommands.value = SetCommandMapper.map(it)
        }
    }

    /**
     * Connect this repository to the player including listening to events.
     */
    public fun connect(player: Player, onClose: () -> Unit) {
        // TODO support a cycle of changing players

        checkNotClosed()

        if (this.onClose != null) {
            throw IllegalStateException("previously connected")
        }

        _player.value = player
        _connected.value = true
        player.addListener(listener)

        updateCurrentMediaItem(player)
        updateAvailableCommands(player)
        updateShuffleMode(player)
        updateState(player)
        updatePlaybackSpeed(player)

        this.onClose = onClose
    }

    /**
     * Close this repository and release the listener from the player.
     */
    override fun close() {
        closed = true

        // TODO consider ordering for UI updates purposes
        _player.value?.removeListener(listener)
        onClose?.invoke()
        _connected.value = false
    }

    override fun prepare() {
        checkNotClosed()

        _player.value?.prepare()
    }

    override fun play() {
        checkNotClosed()

        _player.value?.let {
            it.play()
            updatePosition()
        }
    }

    override fun play(mediaItemIndex: Int) {
        checkNotClosed()

        player.value?.let {
            it.seekToDefaultPosition(mediaItemIndex)
            it.play()
            updatePosition()
        }
    }

    override fun pause() {
        checkNotClosed()

        player.value?.let {
            it.pause()
            updatePosition()
        }
    }

    override fun hasPreviousMediaItem(): Boolean {
        checkNotClosed()

        return player.value?.hasPreviousMediaItem() ?: false
    }

    override fun skipToPreviousMediaItem() {
        checkNotClosed()

        player.value?.let {
            it.seekToPreviousMediaItem()
            updatePosition()
        }
    }

    override fun hasNextMediaItem(): Boolean {
        checkNotClosed()

        return player.value?.hasNextMediaItem() ?: false
    }

    override fun skipToNextMediaItem() {
        checkNotClosed()

        player.value?.let {
            it.seekToNextMediaItem()
            updatePosition()
        }
    }

    override fun getSeekBackIncrement(): Duration {
        checkNotClosed()

        return player.value?.seekBackIncrement?.toDuration(DurationUnit.MILLISECONDS) ?: 0.seconds
    }

    override fun seekBack() {
        checkNotClosed()

        player.value?.let {
            it.seekBack()
            updatePosition()
        }
    }

    override fun getSeekForwardIncrement(): Duration {
        checkNotClosed()

        return player.value?.seekForwardIncrement?.toDuration(DurationUnit.MILLISECONDS)
            ?: 0.seconds
    }

    override fun seekForward() {
        checkNotClosed()

        player.value?.let {
            it.seekForward()
            updatePosition()
        }
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        checkNotClosed()

        player.value?.shuffleModeEnabled = shuffleModeEnabled
    }

    /**
     * This operation will stop the current MediaItem that is playing, if there is one, as per
     * [Player.setMediaItem].
     */
    override fun setMediaItem(mediaItem: MediaItem) {
        checkNotClosed()

        player.value?.let {
            it.setMediaItem(Media3MediaItemMapper.map(mediaItem))
            updatePosition()
        }
    }

    /**
     * This operation will stop the current MediaItem that is playing, if there is one, as per
     * [Player.setMediaItems].
     */
    override fun setMediaItems(mediaItems: List<MediaItem>) {
        checkNotClosed()

        player.value?.let {
            it.setMediaItems(Media3MediaItemMapper.map(mediaItems))
            updatePosition()
        }
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        checkNotClosed()

        player.value?.addMediaItem(Media3MediaItemMapper.map(mediaItem))
    }

    override fun addMediaItem(index: Int, mediaItem: MediaItem) {
        checkNotClosed()

        player.value?.addMediaItem(index, Media3MediaItemMapper.map(mediaItem))
    }

    override fun removeMediaItem(index: Int) {
        checkNotClosed()

        player.value?.removeMediaItem(index)
    }

    override fun clearMediaItems() {
        checkNotClosed()

        player.value?.clearMediaItems()
    }

    override fun getMediaItemCount(): Int {
        checkNotClosed()

        return player.value?.mediaItemCount ?: 0
    }

    override fun getMediaItemAt(index: Int): MediaItem? {
        checkNotClosed()

        return player.value?.getMediaItemAt(index)?.let(MediaItemMapper::map)
    }

    override fun getCurrentMediaItemIndex(): Int {
        checkNotClosed()

        return player.value?.currentMediaItemIndex ?: 0
    }

    override fun release() {
        checkNotClosed()

        player.value?.release()
    }

    /**
     * Update the position to show track progress correctly on screen.
     * Updating roughly once a second while activity is foregrounded is appropriate.
     */
    public fun updatePosition() {
        _mediaItemPosition.value = MediaItemPositionMapper.map(player.value)
    }

    public fun setPlaybackSpeed(speed: Float) {
        player.value?.setPlaybackSpeed(speed)
    }

    private fun checkNotClosed() {
        check(!closed) { "Player is already closed." }
    }

    private companion object {
        private val TAG = PlayerRepositoryImpl::class.java.simpleName
    }
}

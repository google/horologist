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

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.mapper.MediaExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemMapper
import com.google.android.horologist.media.data.mapper.MediaMapper
import com.google.android.horologist.media.data.mapper.MediaPositionMapper
import com.google.android.horologist.media.data.mapper.PlayerStateMapper
import com.google.android.horologist.media.data.mapper.SetCommandMapper
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.MediaPosition
import com.google.android.horologist.media.model.PlayerState
import com.google.android.horologist.media.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Repository for the current Media3 Player for a Player Activity.
 *
 * The current implementation is available as soon as the ListenableFuture
 * to connect to the MediaSession completes.
 */
@ExperimentalHorologistMediaDataApi
public class PlayerRepositoryImpl(
    private val mediaMapper: MediaMapper = MediaMapper(MediaExtrasMapperNoopImpl),
    private val mediaItemMapper: MediaItemMapper = MediaItemMapper(MediaItemExtrasMapperNoopImpl)
) : PlayerRepository, Closeable {

    private var onClose: (() -> Unit)? = null
    private var closed = false

    /**
     * The active player, or null if no active player is currently available.
     */
    private var _player = MutableStateFlow<Player?>(null)
    public val player: StateFlow<Player?> get() = _player

    private val _connected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean> get() = _connected

    private val _availableCommands = MutableStateFlow(emptySet<Command>())
    override val availableCommands: StateFlow<Set<Command>> get() = _availableCommands

    private val _currentState = MutableStateFlow(PlayerState.Idle)
    override val currentState: StateFlow<PlayerState> get() = _currentState

    /**
     * The current media playing, or that would play when user hit play.
     */
    private var _currentMedia = MutableStateFlow<Media?>(null)
    override val currentMedia: StateFlow<Media?> get() = _currentMedia

    private var _mediaPosition = MutableStateFlow<MediaPosition?>(null)
    override val mediaPosition: StateFlow<MediaPosition?> get() = _mediaPosition

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> get() = _shuffleModeEnabled

    /**
     * The current playback speed relative to 1.0.
     */
    private var _playbackSpeed = MutableStateFlow(1f)
    public val playbackSpeed: StateFlow<Float> get() = _playbackSpeed

    private var _seekBackIncrement = MutableStateFlow<Duration?>(null)
    override val seekBackIncrement: StateFlow<Duration?> get() = _seekBackIncrement

    private var _seekForwardIncrement = MutableStateFlow<Duration?>(null)
    override val seekForwardIncrement: StateFlow<Duration?> get() = _seekForwardIncrement

    // https://github.com/google/horologist/issues/496
    private var mediaIndexToSeekTo: Int? = null

    private val listener = object : Player.Listener {
        private val eventHandlers = mapOf(
            Player.EVENT_AVAILABLE_COMMANDS_CHANGED to ::updateAvailableCommands,
            Player.EVENT_MEDIA_ITEM_TRANSITION to ::updateCurrentMedia,
            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED to ::updateShuffleMode,
            Player.EVENT_PLAYBACK_PARAMETERS_CHANGED to ::updatePlaybackSpeed,
            Player.EVENT_SEEK_BACK_INCREMENT_CHANGED to ::updateSeekBackIncrement,
            Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED to ::updateSeekForwardIncrement,
            Player.EVENT_MEDIA_METADATA_CHANGED to ::updateCurrentMedia,
            // Moved below until https://github.com/google/horologist/issues/496 solved
//            Player.EVENT_TIMELINE_CHANGED to ::updateTimeline,

            // Reason for handling these events here, instead of using individual callbacks
            // (onIsLoadingChanged, onIsPlayingChanged, onPlaybackStateChanged, etc):
            // - The listener intends to use multiple state values that are reported through
            //   separate callbacks together, or in combination with Player getter methods
            // Reference:
            // https://exoplayer.dev/listening-to-player-events.html#individual-callbacks-vs-onevents
            Player.EVENT_IS_LOADING_CHANGED to ::updateState,
            Player.EVENT_IS_PLAYING_CHANGED to ::updateState,
            Player.EVENT_PLAYBACK_STATE_CHANGED to ::updateState,
            Player.EVENT_PLAY_WHEN_READY_CHANGED to ::updateState
        )

        override fun onEvents(player: Player, events: Player.Events) {
            val called = mutableSetOf<(Player) -> Unit>()
            for ((event, handler) in eventHandlers) {
                if (events.contains(event) && !called.contains(handler)) {
                    handler.invoke(player)
                    called.add(handler)
                }
            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateTimeline(player.value!!)
        }
    }

    private fun updatePlaybackSpeed(player: Player) {
        _playbackSpeed.value = player.playbackParameters.speed
    }

    private fun updateShuffleMode(player: Player) {
        _shuffleModeEnabled.value = player.shuffleModeEnabled
    }

    private fun updateCurrentMedia(player: Player) {
        _currentMedia.value = player.currentMediaItem?.let { mediaMapper.map(it, player.mediaMetadata) }
        updatePosition()
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

    private fun updateSeekBackIncrement(player: Player) {
        _seekBackIncrement.value = player.seekBackIncrement.toDuration(DurationUnit.MILLISECONDS)
    }

    private fun updateSeekForwardIncrement(player: Player) {
        _seekForwardIncrement.value = player.seekForwardIncrement.toDuration(DurationUnit.MILLISECONDS)
    }

    // TODO https://github.com/google/horologist/issues/496
    private fun updateTimeline(player: Player) {
        mediaIndexToSeekTo?.let { index ->
            player.seekTo(index, 0)
            player.prepare()
            player.play()
            mediaIndexToSeekTo = null
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

        updateCurrentMedia(player)
        updateAvailableCommands(player)
        updateShuffleMode(player)
        updateState(player)
        updatePlaybackSpeed(player)
        updateSeekBackIncrement(player)
        updateSeekForwardIncrement(player)

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

    override fun seekToDefaultPosition(mediaIndex: Int) {
        checkNotClosed()

        _player.value?.seekToDefaultPosition(mediaIndex)
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

    override fun pause() {
        checkNotClosed()

        player.value?.let {
            it.pause()
            updatePosition()
        }
    }

    override fun hasPreviousMedia(): Boolean {
        checkNotClosed()

        return player.value?.hasPreviousMediaItem() ?: false
    }

    override fun skipToPreviousMedia() {
        checkNotClosed()

        player.value?.let {
            it.seekToPreviousMediaItem()
            updatePosition()
        }
    }

    override fun hasNextMedia(): Boolean {
        checkNotClosed()

        return player.value?.hasNextMediaItem() ?: false
    }

    override fun skipToNextMedia() {
        checkNotClosed()

        player.value?.let {
            it.seekToNextMediaItem()
            updatePosition()
        }
    }

    override fun seekBack() {
        checkNotClosed()

        player.value?.let {
            it.seekBack()
            updatePosition()
        }
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
    override fun setMedia(media: Media) {
        checkNotClosed()

        player.value?.let {
            it.setMediaItem(mediaItemMapper.map(media))
            updatePosition()
        }
    }

    /**
     * This operation will stop the current [MediaItem] that is playing, if there is one, as per
     * [Player.setMediaItems].
     */
    override fun setMediaList(mediaList: List<Media>) {
        checkNotClosed()

        player.value?.let {
            it.setMediaItems(mediaList.map(mediaItemMapper::map))
            updatePosition()
        }
    }

    override fun setMediaListAndPlay(mediaList: List<Media>, index: Int) {
        checkNotClosed()

        player.value?.let {
            it.setMediaItems(mediaList.map(mediaItemMapper::map))

            mediaIndexToSeekTo = index
        }
    }

    override fun addMedia(media: Media) {
        checkNotClosed()

        player.value?.addMediaItem(mediaItemMapper.map(media))
    }

    override fun addMedia(index: Int, media: Media) {
        checkNotClosed()

        player.value?.addMediaItem(index, mediaItemMapper.map(media))
    }

    override fun removeMedia(index: Int) {
        checkNotClosed()

        player.value?.removeMediaItem(index)
    }

    override fun clearMediaList() {
        checkNotClosed()

        player.value?.clearMediaItems()
    }

    override fun getMediaCount(): Int {
        checkNotClosed()

        return player.value?.mediaItemCount ?: 0
    }

    override fun getMediaAt(index: Int): Media? {
        checkNotClosed()

        return player.value?.getMediaItemAt(index)?.let { mediaMapper.map(it, null) }
    }

    override fun getCurrentMediaIndex(): Int {
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
        _mediaPosition.value = MediaPositionMapper.map(player.value)
    }

    override fun setPlaybackSpeed(speed: Float) {
        player.value?.setPlaybackSpeed(speed)
    }

    private fun checkNotClosed() {
        check(!closed) { "Player is already closed." }
    }

    private companion object {
        private val TAG = PlayerRepositoryImpl::class.java.simpleName
    }
}

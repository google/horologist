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

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.data.mapper.MediaExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemExtrasMapperNoopImpl
import com.google.android.horologist.media.data.mapper.MediaItemMapper
import com.google.android.horologist.media.data.mapper.MediaMapper
import com.google.android.horologist.media.data.mapper.PlaybackStateMapper
import com.google.android.horologist.media.data.mapper.SetCommandMapper
import com.google.android.horologist.media.model.Command
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.PlaybackStateEvent
import com.google.android.horologist.media.model.PlaybackStateEvent.Cause
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
@ExperimentalHorologistApi
public class PlayerRepositoryImpl(
    private val mediaMapper: MediaMapper = MediaMapper(MediaExtrasMapperNoopImpl),
    private val mediaItemMapper: MediaItemMapper = MediaItemMapper(MediaItemExtrasMapperNoopImpl),
    private val playbackStateMapper: PlaybackStateMapper = PlaybackStateMapper()
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

    /**
     * The current media playing, or that would play when user hit play.
     */
    private var _currentMedia = MutableStateFlow<Media?>(null)
    override val currentMedia: StateFlow<Media?> get() = _currentMedia

    private var _latestPlaybackState = MutableStateFlow(PlaybackStateEvent.INITIAL)
    override val latestPlaybackState: StateFlow<PlaybackStateEvent> get() = _latestPlaybackState

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> get() = _shuffleModeEnabled

    private var _seekBackIncrement = MutableStateFlow<Duration?>(null)
    override val seekBackIncrement: StateFlow<Duration?> get() = _seekBackIncrement

    private var _seekForwardIncrement = MutableStateFlow<Duration?>(null)
    override val seekForwardIncrement: StateFlow<Duration?> get() = _seekForwardIncrement

    private val listener = object : Player.Listener {
        private val eventHandlers = mapOf(
            Player.EVENT_AVAILABLE_COMMANDS_CHANGED to ::updateAvailableCommands,
            Player.EVENT_MEDIA_ITEM_TRANSITION to ::updateCurrentMedia,
            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED to ::updateShuffleMode,
            Player.EVENT_PLAYBACK_PARAMETERS_CHANGED to { updatePlaybackState(it, Cause.ParametersChanged) },
            Player.EVENT_POSITION_DISCONTINUITY to { updatePlaybackState(it, Cause.PositionDiscontinuity) },
            Player.EVENT_SEEK_BACK_INCREMENT_CHANGED to ::updateSeekBackIncrement,
            Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED to ::updateSeekForwardIncrement,
            Player.EVENT_MEDIA_METADATA_CHANGED to ::updateCurrentMedia,

            // Reason for handling these events here, instead of using individual callbacks
            // (onIsLoadingChanged, onIsPlayingChanged, onPlaybackStateChanged, etc):
            // - The listener intends to use multiple state values that are reported through
            //   separate callbacks together, or in combination with Player getter methods
            // Reference:
            // https://exoplayer.dev/listening-to-player-events.html#individual-callbacks-vs-onevents
            Player.EVENT_IS_PLAYING_CHANGED to ::updateState,
            Player.EVENT_PLAYBACK_STATE_CHANGED to ::updateState,
            Player.EVENT_PLAY_WHEN_READY_CHANGED to ::updateState,
            Player.EVENT_TIMELINE_CHANGED to ::updateState
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
    }

    private fun updateShuffleMode(player: Player) {
        _shuffleModeEnabled.value = player.shuffleModeEnabled
    }

    private fun updateCurrentMedia(player: Player) {
        _currentMedia.value = player.currentMediaItem?.let { mediaMapper.map(it, player.mediaMetadata) }
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

    private fun updateState(player: Player) {
        updatePlaybackState(player, Cause.PlayerStateChanged)
    }

    private fun updatePlaybackState(player: Player, cause: Cause = Cause.Other) {
        _latestPlaybackState.value = playbackStateMapper.createEvent(player, cause)
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
        updateSeekBackIncrement(player)
        updateSeekForwardIncrement(player)
        updatePlaybackState(player, Cause.Initial)

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
        _player.value?.release()
        _connected.value = false
    }

    override fun seekToDefaultPosition(mediaIndex: Int) {
        checkNotClosed()

        _player.value?.seekToDefaultPosition(mediaIndex)
    }

    override fun play() {
        checkNotClosed()
        _player.value?.let {
            when (it.playbackState) {
                Player.STATE_IDLE -> it.prepare()
                Player.STATE_ENDED -> it.seekTo(it.currentMediaItemIndex, C.TIME_UNSET)
                Player.STATE_BUFFERING, Player.STATE_READY -> {}
            }
            it.play()
        }
    }

    override fun pause() {
        checkNotClosed()

        player.value?.let {
            it.currentPosition // hack to make sure position is not stale
            it.pause()
        }
    }

    override fun hasPreviousMedia(): Boolean {
        checkNotClosed()

        return player.value?.hasPreviousMediaItem() ?: false
    }

    override fun skipToPreviousMedia() {
        checkNotClosed()

        player.value?.seekToPreviousMediaItem()
    }

    override fun hasNextMedia(): Boolean {
        checkNotClosed()

        return player.value?.hasNextMediaItem() ?: false
    }

    override fun skipToNextMedia() {
        checkNotClosed()

        player.value?.seekToNextMediaItem()
    }

    override fun seekBack() {
        checkNotClosed()

        player.value?.seekBack()
    }

    override fun seekForward() {
        checkNotClosed()

        player.value?.seekForward()
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

        player.value?.setMediaItem(mediaItemMapper.map(media))
    }

    /**
     * This operation will stop the current [MediaItem] that is playing, if there is one, as per
     * [Player.setMediaItems].
     */
    override fun setMediaList(mediaList: List<Media>) {
        checkNotClosed()

        player.value?.setMediaItems(mediaList.map(mediaItemMapper::map))
    }

    /**
     * This operation will stop the current [MediaItem] that is playing, if there is one, as per
     * [Player.setMediaItems] and set the starting position to the position passed as parameter.
     */
    override fun setMediaList(mediaList: List<Media>, index: Int, position: Duration?) {
        checkNotClosed()

        player.value?.setMediaItems(mediaList.map(mediaItemMapper::map), index, position?.inWholeMilliseconds ?: C.TIME_UNSET)
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

        return player.value?.getMediaItemAt(index)?.let { mediaMapper.map(it, it.mediaMetadata) }
    }

    override fun getCurrentMediaIndex(): Int {
        checkNotClosed()

        return player.value?.currentMediaItemIndex ?: 0
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

/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.media3.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.listen
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.components.display.NothingPlayingDisplay
import com.google.android.horologist.media.ui.components.display.TrackMediaDisplay
import com.google.android.horologist.media.ui.state.model.MediaUiModel

@ExperimentalHorologistApi
@Composable
fun MediaInfoDisplay(player: Player, modifier: Modifier = Modifier) {
    val state = rememberMediaInfoDisplayState(player)
    when (val mediaUiModel = state.mediaUiModel) {
        MediaUiModel.Loading -> LoadingMediaDisplay(modifier)
        is MediaUiModel.Ready -> TrackMediaDisplay(mediaUiModel, modifier)
        else -> NothingPlayingDisplay(modifier)
    }
}

@Composable
fun rememberMediaInfoDisplayState(player: Player): MediaInfoDisplayState {
    val mediaInfoDisplayState = remember(player) { MediaInfoDisplayState(player) }
    LaunchedEffect(player) { mediaInfoDisplayState.observe() }
    return mediaInfoDisplayState
}

class MediaInfoDisplayState(private val player: Player) {
    var mediaUiModel by mutableStateOf(getMediaUiModel(player))
        private set

    suspend fun observe(): Nothing =
        player.listen { events ->
            if (
                // TODO: Find the right triggers
                events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                    Player.EVENT_AVAILABLE_COMMANDS_CHANGED,
                )
            ) {
                mediaUiModel = getMediaUiModel(this)
            }
        }

    private fun getMediaUiModel(player: Player): MediaUiModel {
        val mediaItem = player.currentMediaItem
        val mediaMetadata = mediaItem?.mediaMetadata

        return if (mediaItem != null && mediaMetadata != null) {
            MediaUiModel.Ready(
                id = mediaItem.mediaId,
                title = mediaMetadata.title?.toString() ?: "",
                subtitle = mediaMetadata.artist?.toString()
                    ?: mediaMetadata.albumArtist?.toString()
                    ?: mediaMetadata.subtitle?.toString()
                    ?: "",
                artwork = mediaMetadata.artworkUri?.let { CoilPaintable(it) },
            )
        } else {
            MediaUiModel.Loading
        }
    }
}

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

package com.google.android.horologist.media.ui.screens.entity

import android.text.format.Formatter
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.base.SecondaryPlaceholderChip
import com.google.android.horologist.media.ui.components.base.StandardButton
import com.google.android.horologist.media.ui.components.base.StandardButtonType
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

/**
 * An implementation of [EntityScreen] using [PlaylistUiModel] and [DownloadMediaUiModel] as
 * models.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PlaylistDownloadScreen(
    playlistName: String,
    playlistDownloadScreenState: PlaylistDownloadScreenState<PlaylistUiModel, DownloadMediaUiModel>,
    onDownloadClick: (PlaylistUiModel) -> Unit,
    onDownloadItemClick: (DownloadMediaUiModel) -> Unit,
    onShuffleClick: (PlaylistUiModel) -> Unit,
    onPlayClick: (PlaylistUiModel) -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    defaultMediaTitle: String = "",
    downloadItemArtworkPlaceholder: Painter? = null
) {
    val entityScreenState: EntityScreenState<DownloadMediaUiModel> =
        when (playlistDownloadScreenState) {
            is PlaylistDownloadScreenState.Loading -> EntityScreenState.Loading()
            is PlaylistDownloadScreenState.Loaded -> EntityScreenState.Loaded(
                playlistDownloadScreenState.mediaList
            )
        }

    EntityScreen(
        entityScreenState = entityScreenState,
        headerContent = { DefaultEntityScreenHeader(title = playlistName) },
        loadingContent = { items(count = 2) { SecondaryPlaceholderChip() } },
        mediaContent = { downloadMediaUiModel ->

            val secondaryLabel = when (downloadMediaUiModel) {
                is DownloadMediaUiModel.Downloading -> {
                    when (downloadMediaUiModel.size) {
                        is DownloadMediaUiModel.Size.Known -> {
                            val size = Formatter.formatShortFileSize(
                                LocalContext.current,
                                downloadMediaUiModel.size.sizeInBytes
                            )
                            stringResource(
                                id = R.string.horologist_playlist_download_download_progress_known_size,
                                downloadMediaUiModel.progress,
                                size
                            )
                        }
                        DownloadMediaUiModel.Size.Unknown -> stringResource(
                            id = R.string.horologist_playlist_download_download_progress_unknown_size,
                            downloadMediaUiModel.progress
                        )
                    }
                }
                is DownloadMediaUiModel.Downloaded -> downloadMediaUiModel.artist
                is DownloadMediaUiModel.NotDownloaded -> downloadMediaUiModel.artist
            }

            StandardChip(
                label = downloadMediaUiModel.title ?: defaultMediaTitle,
                onClick = { onDownloadItemClick(downloadMediaUiModel) },
                secondaryLabel = secondaryLabel,
                icon = downloadMediaUiModel.artworkUri,
                largeIcon = true,
                placeholder = downloadItemArtworkPlaceholder,
                chipType = StandardChipType.Secondary,
                enabled = downloadMediaUiModel !is DownloadMediaUiModel.NotDownloaded
            )
        },
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        buttonsContent = {
            ButtonsContent(
                state = playlistDownloadScreenState,
                onDownloadClick = onDownloadClick,
                onShuffleClick = onShuffleClick,
                onPlayClick = onPlayClick
            )
        }
    )
}

@ExperimentalHorologistMediaUiApi
@Composable
private fun ButtonsContent(
    state: PlaylistDownloadScreenState<PlaylistUiModel, DownloadMediaUiModel>,
    onDownloadClick: (PlaylistUiModel) -> Unit,
    onShuffleClick: (PlaylistUiModel) -> Unit,
    onPlayClick: (PlaylistUiModel) -> Unit
) {
    when (state) {
        is PlaylistDownloadScreenState.Loading -> {
            StandardChip(
                label = stringResource(id = R.string.horologist_entity_button_download),
                onClick = { },
                modifier = Modifier.padding(bottom = 16.dp),
                icon = Icons.Default.Download,
                enabled = false
            )
        }

        is PlaylistDownloadScreenState.Loaded -> {
            if (state.downloadsState == PlaylistDownloadScreenState.Loaded.DownloadMediaListState.None) {
                if (state.downloading) {
                    StandardChip(
                        label = stringResource(id = R.string.horologist_entity_button_downloading),
                        onClick = { },
                        modifier = Modifier.padding(bottom = 16.dp),
                        icon = Icons.Default.Download
                    )
                } else {
                    StandardChip(
                        label = stringResource(id = R.string.horologist_entity_button_download),
                        onClick = { onDownloadClick(state.collectionModel) },
                        modifier = Modifier.padding(bottom = 16.dp),
                        icon = Icons.Default.Download
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FirstButton(
                        downloadMediaListState = state.downloadsState,
                        downloading = state.downloading,
                        collectionModel = state.collectionModel,
                        onDownloadClick = onDownloadClick,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(weight = 0.3F, fill = false)
                    )

                    StandardButton(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = stringResource(id = R.string.horologist_entity_button_shuffle_content_description),
                        onClick = { onShuffleClick(state.collectionModel) },
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(weight = 0.3F, fill = false)
                    )

                    StandardButton(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(id = R.string.horologist_entity_button_play_content_description),
                        onClick = { onPlayClick(state.collectionModel) },
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(weight = 0.3F, fill = false)
                    )
                }
            }
        }
    }
}

@ExperimentalHorologistMediaUiApi
@Composable
private fun <Collection> FirstButton(
    downloadMediaListState: PlaylistDownloadScreenState.Loaded.DownloadMediaListState,
    downloading: Boolean,
    collectionModel: Collection,
    onDownloadClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, contentDescription) = when (downloadMediaListState) {
        PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Partially -> {
            if (downloading) {
                Pair(
                    Icons.Default.Downloading,
                    R.string.horologist_entity_button_downloading_content_description
                )
            } else {
                Pair(
                    Icons.Default.Download,
                    R.string.horologist_entity_button_download_content_description
                )
            }
        }
        PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Fully -> {
            Pair(
                Icons.Default.DownloadDone,
                R.string.horologist_entity_button_download_done_content_description
            )
        }
        else -> {
            error("Invalid state to be used with this button")
        }
    }

    StandardButton(
        imageVector = icon,
        contentDescription = stringResource(id = contentDescription),
        onClick = { onDownloadClick(collectionModel) },
        modifier = modifier,
        buttonType = StandardButtonType.Secondary
    )
}

/**
 * Represents the state of [PlaylistDownloadScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class PlaylistDownloadScreenState<Collection, Media> {

    public class Loading<Collection, Media> : PlaylistDownloadScreenState<Collection, Media>()

    public data class Loaded<Collection, Media>(
        val collectionModel: Collection,
        val mediaList: List<Media>,
        val downloadsState: DownloadMediaListState,
        val downloading: Boolean = false
    ) : PlaylistDownloadScreenState<Collection, Media>() {

        /**
         * Represents the state of the list of [Media] when [PlaylistDownloadScreenState] is [Loaded].
         */
        public enum class DownloadMediaListState {
            None,
            Partially,
            Fully
        }
    }
}

/**
 * A helper function to build a [EntityScreenState.Loaded] with [PlaylistUiModel] and
 * [DownloadMediaUiModel], calculating the value of [PlaylistDownloadScreenState.Loaded.downloadsState].
 */
@ExperimentalHorologistMediaUiApi
public fun createPlaylistDownloadScreenStateLoaded(
    playlistModel: PlaylistUiModel,
    downloadMediaList: List<DownloadMediaUiModel>,
    downloading: Boolean = false
): PlaylistDownloadScreenState.Loaded<PlaylistUiModel, DownloadMediaUiModel> {
    val downloadsState = if (downloadMediaList.isEmpty()) {
        PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Fully
    } else {
        var none = true
        var fully = true

        downloadMediaList.forEach {
            if (it is DownloadMediaUiModel.Downloaded) none = false
            if (it is DownloadMediaUiModel.NotDownloaded) fully = false
        }

        when {
            !none && !fully -> PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Partially
            none -> PlaylistDownloadScreenState.Loaded.DownloadMediaListState.None
            else -> PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Fully
        }
    }

    return PlaylistDownloadScreenState.Loaded(
        collectionModel = playlistModel,
        mediaList = downloadMediaList,
        downloadsState = downloadsState,
        downloading = downloading
    )
}

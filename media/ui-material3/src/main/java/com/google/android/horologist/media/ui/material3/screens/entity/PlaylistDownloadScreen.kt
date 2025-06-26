/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.screens.entity

import android.text.format.Formatter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.FilledTonalIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.material3.composables.PlaceholderButton
import com.google.android.horologist.media.ui.material3.screens.entity.PlaylistDownloadScreenState.Loaded.DownloadsProgress
import com.google.android.horologist.media.ui.material3.util.ifNan
import com.google.android.horologist.media.ui.model.R
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

/**
 * An implementation of [EntityScreen] using [PlaylistUiModel] and [DownloadMediaUiModel] as
 * models.
 */
@ExperimentalHorologistApi
@Composable
public fun PlaylistDownloadScreen(
    playlistName: String,
    playlistDownloadScreenState: PlaylistDownloadScreenState<PlaylistUiModel, DownloadMediaUiModel>,
    onDownloadButtonClick: (PlaylistUiModel) -> Unit,
    onCancelDownloadButtonClick: (PlaylistUiModel) -> Unit,
    onDownloadItemClick: (DownloadMediaUiModel) -> Unit,
    onDownloadItemInProgressClick: (DownloadMediaUiModel) -> Unit,
    onShuffleButtonClick: (PlaylistUiModel) -> Unit,
    onPlayButtonClick: (PlaylistUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onDownloadCompletedButtonClick: ((PlaylistUiModel) -> Unit)? = null,
    defaultMediaTitle: String = "",
    downloadItemArtworkPlaceholder: Painter? = null,
    onDownloadItemInProgressClickActionLabel: String? = null,
) {
    val entityScreenState: EntityScreenState<DownloadMediaUiModel> =
        when (playlistDownloadScreenState) {
            PlaylistDownloadScreenState.Loading -> EntityScreenState.Loading
            is PlaylistDownloadScreenState.Loaded -> EntityScreenState.Loaded(
                playlistDownloadScreenState.mediaList,
            )

            PlaylistDownloadScreenState.Failed -> EntityScreenState.Failed
        }

    EntityScreen(
        entityScreenState = entityScreenState,
        headerContent = { DefaultEntityScreenHeader(title = playlistName) },
        loadingContent = {
            items(count = 2) {
                PlaceholderButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(),
                )
            }
        },
        mediaContent = { downloadMediaUiModel ->
            MediaContent(
                downloadMediaUiModel = downloadMediaUiModel,
                onDownloadItemClick = onDownloadItemClick,
                onDownloadItemInProgressClick = onDownloadItemInProgressClick,
                defaultMediaTitle = defaultMediaTitle,
                downloadItemArtworkPlaceholder = downloadItemArtworkPlaceholder,
                onDownloadItemInProgressClickActionLabel = onDownloadItemInProgressClickActionLabel,
            )
        },
        modifier = modifier,
        buttonsContent = {
            ButtonsContent(
                state = playlistDownloadScreenState,
                onDownloadButtonClick = onDownloadButtonClick,
                onCancelDownloadButtonClick = onCancelDownloadButtonClick,
                onDownloadCompletedButtonClick = onDownloadCompletedButtonClick
                    ?: { /* do nothing */ },
                onShuffleButtonClick = onShuffleButtonClick,
                onPlayButtonClick = onPlayButtonClick,
            )
        },
    )
}

@Composable
private fun MediaContent(
    downloadMediaUiModel: DownloadMediaUiModel,
    onDownloadItemClick: (DownloadMediaUiModel) -> Unit,
    onDownloadItemInProgressClick: (DownloadMediaUiModel) -> Unit,
    defaultMediaTitle: String = "",
    downloadItemArtworkPlaceholder: Painter?,
    onDownloadItemInProgressClickActionLabel: String?,
) {
    val mediaTitle = downloadMediaUiModel.title ?: defaultMediaTitle

    val secondaryLabel = when (downloadMediaUiModel) {
        is DownloadMediaUiModel.Downloading -> {
            when (downloadMediaUiModel.progress) {
                is DownloadMediaUiModel.Progress.Waiting -> stringResource(
                    id = R.string.horologist_playlist_download_download_progress_waiting,
                )

                is DownloadMediaUiModel.Progress.InProgress -> when (downloadMediaUiModel.size) {
                    is DownloadMediaUiModel.Size.Known -> {
                        val size = Formatter.formatShortFileSize(
                            LocalContext.current,
                            (downloadMediaUiModel.size as DownloadMediaUiModel.Size.Known).sizeInBytes,
                        )
                        stringResource(
                            id = R.string.horologist_playlist_download_download_progress_known_size,
                            (downloadMediaUiModel.progress as DownloadMediaUiModel.Progress.InProgress).progress,
                            size,
                        )
                    }

                    DownloadMediaUiModel.Size.Unknown -> stringResource(
                        id = R.string.horologist_playlist_download_download_progress_unknown_size,
                        (downloadMediaUiModel.progress as DownloadMediaUiModel.Progress.InProgress).progress,
                    )
                }
            }
        }

        is DownloadMediaUiModel.Downloaded -> downloadMediaUiModel.artist
        is DownloadMediaUiModel.NotDownloaded -> downloadMediaUiModel.artist
    }

    when (downloadMediaUiModel) {
        is DownloadMediaUiModel.Downloaded,
        is DownloadMediaUiModel.NotDownloaded,
        -> {
            FilledTonalButton(
                label = { Text(mediaTitle) },
                onClick = { onDownloadItemClick(downloadMediaUiModel) },
                secondaryLabel = secondaryLabel?.let { { Text(secondaryLabel) } },
                icon = {
                    Icon(
                        painter = CoilPaintable(
                            downloadMediaUiModel.artworkUri,
                            downloadItemArtworkPlaceholder,
                        ).rememberPainter(),
                        contentDescription = null,
                    )
                },
                enabled = downloadMediaUiModel !is DownloadMediaUiModel.NotDownloaded,
            )
        }

        is DownloadMediaUiModel.Downloading -> {
            val icon: @Composable BoxScope.() -> Unit =
                when (downloadMediaUiModel.progress) {
                    is DownloadMediaUiModel.Progress.InProgress -> {
                        {
                            val progress by animateFloatAsState(
                                targetValue = (downloadMediaUiModel.progress as DownloadMediaUiModel.Progress.InProgress).progress,
                                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                            )
                            Icon(
                                painter = CoilPaintable(
                                    downloadMediaUiModel.artworkUri,
                                    downloadItemArtworkPlaceholder,
                                ).rememberPainter(),
                                modifier = Modifier.size(ButtonDefaults.LargeIconSize),
                                contentDescription = null,
                            )
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(ButtonDefaults.LargeIconSize),
                            )
                        }
                    }

                    is DownloadMediaUiModel.Progress.Waiting -> {
                        {
                            Icon(
                                painter = CoilPaintable(
                                    downloadMediaUiModel.artworkUri,
                                    downloadItemArtworkPlaceholder,
                                ).rememberPainter(),
                                modifier = Modifier.size(ButtonDefaults.LargeIconSize),
                                contentDescription = null,
                            )
                            CircularProgressIndicator(
                                modifier = Modifier.size(ButtonDefaults.LargeIconSize),
                            )
                        }
                    }
                }

            val customModifier = onDownloadItemInProgressClickActionLabel?.let {
                Modifier.semantics {
                    onClick(
                        label = onDownloadItemInProgressClickActionLabel,
                        action = null,
                    )
                }
            } ?: Modifier

            FilledTonalButton(
                label = { Text(mediaTitle) },
                onClick = { onDownloadItemInProgressClick(downloadMediaUiModel) },
                secondaryLabel = secondaryLabel?.let { { Text(secondaryLabel) } },
                modifier = customModifier,
                icon = icon,
            )
        }
    }
}

@Composable
private fun ButtonsContent(
    state: PlaylistDownloadScreenState<PlaylistUiModel, DownloadMediaUiModel>,
    onDownloadButtonClick: (PlaylistUiModel) -> Unit,
    onCancelDownloadButtonClick: (PlaylistUiModel) -> Unit,
    onDownloadCompletedButtonClick: (PlaylistUiModel) -> Unit,
    onShuffleButtonClick: (PlaylistUiModel) -> Unit,
    onPlayButtonClick: (PlaylistUiModel) -> Unit,
) {
    when (state) {
        PlaylistDownloadScreenState.Failed,
        PlaylistDownloadScreenState.Loading,
        -> {
            Button(
                label = { Text(stringResource(id = R.string.horologist_playlist_download_button_download)) },
                onClick = {},
                modifier = Modifier.padding(bottom = 16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                    )
                },
                enabled = false,
            )
        }

        is PlaylistDownloadScreenState.Loaded -> {
            if (state.downloadMediaListState == PlaylistDownloadScreenState.Loaded.DownloadMediaListState.None) {
                if (state.downloadsProgress is DownloadsProgress.InProgress) {
                    Button(
                        label = { Text(stringResource(id = R.string.horologist_playlist_download_button_cancel)) },
                        onClick = { onCancelDownloadButtonClick(state.collectionModel) },
                        modifier = Modifier.padding(bottom = 16.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                            )
                        },
                    )
                } else {
                    Button(
                        label = { Text(stringResource(id = R.string.horologist_playlist_download_button_download)) },
                        onClick = { onDownloadButtonClick(state.collectionModel) },
                        modifier = Modifier.padding(bottom = 16.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                            )
                        },
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .height(52.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp, CenterHorizontally),
                ) {
                    FirstButton(
                        downloadMediaListState = state.downloadMediaListState,
                        downloadsProgress = state.downloadsProgress,
                        collectionModel = state.collectionModel,
                        onDownloadButtonClick = onDownloadButtonClick,
                        onCancelDownloadButtonClick = onCancelDownloadButtonClick,
                        onDownloadCompletedButtonClick = onDownloadCompletedButtonClick,
                        modifier = Modifier
                            .weight(weight = 0.3F, fill = false),
                    )

                    IconButton(
                        onClick = { onShuffleButtonClick(state.collectionModel) },
                        modifier = Modifier
                            .weight(weight = 0.3F, fill = false),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = stringResource(
                                id = R.string.horologist_playlist_download_button_shuffle_content_description,
                            ),
                        )
                    }

                    IconButton(
                        onClick = { onPlayButtonClick(state.collectionModel) },
                        modifier = Modifier
                            .weight(weight = 0.3F, fill = false),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(id = R.string.horologist_playlist_download_button_play_content_description),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <Collection> FirstButton(
    downloadMediaListState: PlaylistDownloadScreenState.Loaded.DownloadMediaListState,
    downloadsProgress: DownloadsProgress,
    collectionModel: Collection,
    onDownloadButtonClick: (Collection) -> Unit,
    onCancelDownloadButtonClick: (Collection) -> Unit,
    onDownloadCompletedButtonClick: (Collection) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (downloadsProgress is DownloadsProgress.InProgress) {
        val progress by animateFloatAsState(
            targetValue = downloadsProgress.progress.ifNan(0f),
            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
        )
        val label =
            stringResource(id = R.string.horologist_playlist_download_progress_button_cancel_action_label)

        IconButton(
            onClick = { onCancelDownloadButtonClick(collectionModel) },
            modifier = modifier
                .semantics(mergeDescendants = true) {
                    onClick(label = label, action = null)
                },
            enabled = true,
            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Transparent),
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .clearAndSetSemantics { }
                    .fillMaxSize()
                    .background(
                        IconButtonDefaults
                            .filledTonalIconButtonColors()
                            .containerColor,
                    ),
                progress = { progress },
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(id = R.string.horologist_playlist_download_progress_button_cancel_content_description),
                modifier = Modifier
                    .size(IconButtonDefaults.DefaultIconSize)
                    .align(Alignment.Center),
            )
        }
    } else if (downloadMediaListState == PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Partially) {
        FilledTonalIconButton(
            onClick = { onDownloadButtonClick(collectionModel) },
            modifier = modifier,
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = stringResource(id = R.string.horologist_playlist_download_button_download_content_description),
            )
        }
    } else if (downloadMediaListState == PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Fully) {
        val label =
            stringResource(id = R.string.horologist_playlist_download_button_remove_download_action_label)
        IconButton(
            onClick = { onDownloadCompletedButtonClick(collectionModel) },
            modifier = modifier.semantics {
                onClick(
                    label = label,
                    action = null,
                )
            },
        ) {
            Icon(
                imageVector = Icons.Default.DownloadDone,
                contentDescription = stringResource(id = R.string.horologist_playlist_download_button_download_done_content_description),
            )
        }
    } else {
        error("Invalid state to be used with this button")
    }
}

/**
 * Represents the state of [PlaylistDownloadScreen].
 */
@ExperimentalHorologistApi
public sealed class PlaylistDownloadScreenState<out Collection, out Media> {

    public object Loading : PlaylistDownloadScreenState<Nothing, Nothing>()

    public data class Loaded<Collection, Media>(
        val collectionModel: Collection,
        val mediaList: List<Media>,
        val downloadMediaListState: DownloadMediaListState,
        val downloadsProgress: DownloadsProgress = DownloadsProgress.Idle,
    ) : PlaylistDownloadScreenState<Collection, Media>() {

        /**
         * Represents the state of the list of [Loaded.mediaList] when [PlaylistDownloadScreenState]
         * is [Loaded].
         */
        public enum class DownloadMediaListState {
            None,
            Partially,
            Fully,
        }

        /**
         * Represents the status of the downloads when [PlaylistDownloadScreenState] is [Loaded].
         */
        public sealed class DownloadsProgress {

            public object Idle : DownloadsProgress()

            public data class InProgress(val progress: Float) : DownloadsProgress()
        }
    }

    public object Failed : PlaylistDownloadScreenState<Nothing, Nothing>()
}

/**
 * A helper function to build a [EntityScreenState.Loaded] with [PlaylistUiModel] and
 * [DownloadMediaUiModel], calculating the value of [PlaylistDownloadScreenState.Loaded.downloadMediaListState].
 */
@ExperimentalHorologistApi
public fun createPlaylistDownloadScreenStateLoaded(
    playlistModel: PlaylistUiModel,
    downloadMediaList: List<DownloadMediaUiModel>,
): PlaylistDownloadScreenState.Loaded<PlaylistUiModel, DownloadMediaUiModel> {
    var downloadsProgress: DownloadsProgress = DownloadsProgress.Idle

    val downloadsState = if (downloadMediaList.isEmpty()) {
        PlaylistDownloadScreenState.Loaded.DownloadMediaListState.Fully
    } else {
        var none = true
        var fully = true
        var downloading = false

        var downloadedCount = 0
        for (it in downloadMediaList) {
            if (it is DownloadMediaUiModel.Downloaded) {
                downloadedCount++
                none = false
            }
            if (it is DownloadMediaUiModel.NotDownloaded) fully = false
            if (it is DownloadMediaUiModel.Downloading) {
                fully = false
                downloading = true
            }
        }

        if (downloading) {
            downloadsProgress =
                DownloadsProgress.InProgress(downloadedCount.toFloat() / downloadMediaList.size)
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
        downloadMediaListState = downloadsState,
        downloadsProgress = downloadsProgress,
    )
}

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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.base.SecondaryPlaceholderChip
import com.google.android.horologist.media.ui.components.base.StandardButton
import com.google.android.horologist.media.ui.components.base.StandardButtonType
import com.google.android.horologist.media.ui.components.base.StandardChip
import com.google.android.horologist.media.ui.components.base.StandardChipType
import com.google.android.horologist.media.ui.components.base.Title
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

@ExperimentalHorologistMediaUiApi
@Composable
public fun EntityScreen(
    entityScreenState: EntityScreenState,
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
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(focusRequester, scalingLazyListState),
        state = scalingLazyListState
    ) {
        when (entityScreenState) {
            is EntityScreenState.Loading -> {
                item {
                    Title(
                        text = entityScreenState.playlistName,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                item {
                    StandardChip(
                        label = stringResource(id = R.string.horologist_entity_button_download),
                        onClick = { },
                        modifier = Modifier.padding(bottom = 16.dp),
                        icon = Icons.Default.Download,
                        enabled = false
                    )
                }

                items(count = 2) {
                    SecondaryPlaceholderChip()
                }
            }

            is EntityScreenState.Loaded -> {
                item {
                    Title(
                        text = entityScreenState.playlistUiModel.title,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (entityScreenState.downloadsState == EntityScreenState.Loaded.DownloadsState.None) {
                    if (entityScreenState.downloading) {
                        item {
                            StandardChip(
                                label = stringResource(id = R.string.horologist_entity_button_downloading),
                                onClick = { onDownloadClick(entityScreenState.playlistUiModel) },
                                modifier = Modifier.padding(bottom = 16.dp),
                                icon = Icons.Default.Download
                            )
                        }
                    } else {
                        item {
                            StandardChip(
                                label = stringResource(id = R.string.horologist_entity_button_download),
                                onClick = { onDownloadClick(entityScreenState.playlistUiModel) },
                                modifier = Modifier.padding(bottom = 16.dp),
                                icon = Icons.Default.Download
                            )
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .height(52.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FirstButton(
                                downloadsState = entityScreenState.downloadsState,
                                downloading = entityScreenState.downloading,
                                playlistUiModel = entityScreenState.playlistUiModel,
                                onDownloadClick = onDownloadClick,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .weight(weight = 0.3F, fill = false)
                            )

                            StandardButton(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = stringResource(id = R.string.horologist_entity_button_shuffle_content_description),
                                onClick = { onShuffleClick(entityScreenState.playlistUiModel) },
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .weight(weight = 0.3F, fill = false)
                            )

                            StandardButton(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = stringResource(id = R.string.horologist_entity_button_play_content_description),
                                onClick = { onPlayClick(entityScreenState.playlistUiModel) },
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .weight(weight = 0.3F, fill = false)
                            )
                        }
                    }
                }

                items(count = entityScreenState.downloadList.size) { index ->
                    val downloadMediaUiModel = entityScreenState.downloadList[index]
                    val mediaUiModel = downloadMediaUiModel.mediaUiModel

                    StandardChip(
                        label = mediaUiModel.title ?: defaultMediaTitle,
                        onClick = { onDownloadItemClick(downloadMediaUiModel) },
                        secondaryLabel = mediaUiModel.artist,
                        icon = mediaUiModel.artworkUri,
                        largeIcon = true,
                        placeholder = downloadItemArtworkPlaceholder,
                        chipType = StandardChipType.Secondary,
                        enabled = downloadMediaUiModel is DownloadMediaUiModel.Available
                    )
                }
            }
        }
    }
}

@ExperimentalHorologistMediaUiApi
@Composable
private fun FirstButton(
    downloadsState: EntityScreenState.Loaded.DownloadsState,
    downloading: Boolean,
    playlistUiModel: PlaylistUiModel,
    onDownloadClick: (PlaylistUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, contentDescription) = when (downloadsState) {
        EntityScreenState.Loaded.DownloadsState.Partially -> {
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
        EntityScreenState.Loaded.DownloadsState.Fully -> {
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
        onClick = { onDownloadClick(playlistUiModel) },
        modifier = modifier,
        buttonType = StandardButtonType.Secondary
    )
}

/**
 * Represents the state of [EntityScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class EntityScreenState {

    public data class Loading(
        val playlistName: String
    ) : EntityScreenState()

    public data class Loaded(
        val playlistUiModel: PlaylistUiModel,
        val downloadList: List<DownloadMediaUiModel>,
        val downloading: Boolean = false
    ) : EntityScreenState() {

        internal val downloadsState: DownloadsState

        init {
            downloadsState = if (downloadList.isEmpty()) {
                DownloadsState.Fully
            } else {
                var none = true
                var fully = true

                downloadList.forEach {
                    if (it is DownloadMediaUiModel.Available) none = false
                    if (it is DownloadMediaUiModel.Unavailable) fully = false
                }

                when {
                    !none && !fully -> DownloadsState.Partially
                    none -> DownloadsState.None
                    else -> DownloadsState.Fully
                }
            }
        }

        /**
         * Represents the state of the downloads.
         */
        internal enum class DownloadsState {
            None,
            Partially,
            Fully
        }
    }
}

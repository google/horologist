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

@file:OptIn(ExperimentalHorologistMediaUiApi::class, ExperimentalHorologistComposablesApi::class)

package com.google.android.horologist.media.ui.screens.entity

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.ScalingParams
import com.google.android.horologist.base.ui.components.StandardButton
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel

/**
 * An implementation of [EntityScreen] using [PlaylistUiModel] and [DownloadMediaUiModel] as
 * models.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun PlaylistStreamingScreen(
    playlistName: String,
    playlistDownloadScreenState: PlaylistDownloadScreenState<PlaylistUiModel, DownloadMediaUiModel>,
    onShuffleButtonClick: () -> Unit,
    onPlayButtonClick: () -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    onPlayItemClick: (DownloadMediaUiModel) -> Unit,
    modifier: Modifier = Modifier,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    autoCentering: AutoCenteringParams? = AutoCenteringParams(),
    defaultMediaTitle: String = ""
) {
    val entityScreenState: EntityScreenState<DownloadMediaUiModel> =
        when (playlistDownloadScreenState) {
            is PlaylistDownloadScreenState.Loading -> EntityScreenState.Loading()
            is PlaylistDownloadScreenState.Loaded -> EntityScreenState.Loaded(
                playlistDownloadScreenState.mediaList
            )

            is PlaylistDownloadScreenState.Failed -> EntityScreenState.Failed()
        }

    EntityScreen(
        entityScreenState = entityScreenState,
        headerContent = { DefaultEntityScreenHeader(title = playlistName) },
        loadingContent = { items(count = 2) { PlaceholderChip(colors = ChipDefaults.secondaryChipColors()) } },
        mediaContent = { mediaUiModel ->
            val mediaTitle = mediaUiModel.title ?: defaultMediaTitle
            StandardChip(
                label = mediaTitle,
                onClick = { onPlayItemClick(mediaUiModel) },
                icon = mediaUiModel.artworkUri,
                largeIcon = true,
                chipType = StandardChipType.Secondary
            )
        },
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        scalingParams = scalingParams,
        autoCentering = autoCentering,
        buttonsContent = {
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StandardButton(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = stringResource(id = R.string.horologist_playlist_download_button_shuffle_content_description),
                    onClick = { onShuffleButtonClick() },
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(weight = 0.3F, fill = false)
                )

                StandardButton(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(id = R.string.horologist_playlist_download_button_play_content_description),
                    onClick = { onPlayButtonClick() },
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(weight = 0.3F, fill = false)
                )
            }
        }
    )
}

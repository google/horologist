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

package com.google.android.horologist.media.ui.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.state.PlayerViewModel
import com.google.android.horologist.media.ui.utils.StateUtils.rememberStateWithLifecycle

@ExperimentalMediaUiApi
@Composable
public fun PlayButton(
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
) {
    val playerUiState by rememberStateWithLifecycle(flow = playerViewModel.playerUiState)

    PlayButton(
        onClick = { playerViewModel.prepareAndPlay() },
        modifier = modifier,
        enabled = playerUiState.playEnabled,
        colors = colors
    )
}

@ExperimentalMediaUiApi
@Composable
public fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
) {
    MediaButton(
        onClick = onClick,
        icon = Icons.Default.PlayArrow,
        contentDescription = stringResource(id = R.string.play_button_content_description),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
    )
}

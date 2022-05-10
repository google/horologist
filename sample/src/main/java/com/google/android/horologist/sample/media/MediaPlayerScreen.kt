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

package com.google.android.horologist.sample.media

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.audioui.VolumePositionIndicator
import com.google.android.horologist.audioui.VolumeViewModel
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import com.google.android.horologist.compose.pager.FocusOnResume
import com.google.android.horologist.media.ui.screens.PlayerScreen
import com.google.android.horologist.utils.rememberStateWithLifecycle

@Composable
fun MediaPlayerScreen(
    mediaPlayerScreenViewModel: MediaPlayerScreenViewModel,
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    onOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playerFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(
                focusRequester = playerFocusRequester,
                volumeViewModel.volumeScrollableState
            ),
        positionIndicator = {
            val volumeState by rememberStateWithLifecycle(flow = volumeViewModel.volumeState)
            VolumePositionIndicator(volumeState = { volumeState })
        },
        timeText = { TimeText() }
    ) {
        PlayerScreen(
            playerViewModel = mediaPlayerScreenViewModel,
            buttons = {
                SettingsButtons(
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = onVolumeClick,
                    onOutputClick = onOutputClick
                )
            }
        )
    }

    FocusOnResume(playerFocusRequester)
}

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

package com.google.android.horologist.scratch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.material.scrollAway
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import com.google.android.horologist.media.ui.screens.player.PlayerScreen
import com.google.android.horologist.media.ui.state.PlayerViewModel

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val volumeViewModel = viewModel<VolumeViewModel>(factory = VolumeViewModel.Factory)
    val playerViewModel = viewModel() {
        PlayerViewModel(FakePlayerRepository())
    }

    val volumeState by volumeViewModel.volumeState.collectAsStateWithLifecycle()
    val onVolumeChangeByScroll = volumeViewModel::onVolumeChangeByScroll

    val playerFocusRequester = rememberActiveFocusRequester()

    Scaffold(
            modifier = Modifier
                    .fillMaxSize()
                    .onRotaryInputAccumulated(onValueChange = onVolumeChangeByScroll)
                    .focusRequester(playerFocusRequester)
                    .focusable(),
            positionIndicator = { VolumePositionIndicator(volumeState = { volumeState }) },
            timeText = { TimeText() }
    ) {
        PlayerScreen(
                playerViewModel = playerViewModel,
                volumeViewModel = volumeViewModel,
                buttons = {
                }
        )
    }
}
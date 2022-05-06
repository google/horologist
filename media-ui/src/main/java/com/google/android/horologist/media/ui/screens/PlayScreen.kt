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

package com.google.android.horologist.media.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.media.ui.ExperimentalMediaUiApi
import com.google.android.horologist.media.ui.state.PlayerViewModel

@ExperimentalMediaUiApi
@Composable
public fun PlayScreen(
    playerViewModel: PlayerViewModel,
    mediaDisplay: @Composable ColumnScope.(PlayerViewModel) -> Unit,
    controlButtons: @Composable RowScope.(PlayerViewModel) -> Unit,
    buttons: @Composable RowScope.(PlayerViewModel) -> Unit,
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.(PlayerViewModel) -> Unit = {}
) {
    PlayScreen(
        mediaDisplay = { mediaDisplay(playerViewModel) },
        controlButtons = { controlButtons(playerViewModel) },
        buttons = { buttons(playerViewModel) },
        modifier = modifier,
        background = { background(playerViewModel) },
    )
}

@ExperimentalMediaUiApi
@Composable
public fun PlayScreen(
    mediaDisplay: @Composable ColumnScope.() -> Unit,
    controlButtons: @Composable RowScope.() -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        background()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(26.dp))

                mediaDisplay()
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                controlButtons()
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                buttons()
            }
        }
    }
}

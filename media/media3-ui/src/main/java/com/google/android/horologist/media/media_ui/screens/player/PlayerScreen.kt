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

package com.google.android.horologist.media.media_ui.screens.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.media_ui.components.MediaControlButtons
import com.google.android.horologist.media.media_ui.components.MediaInfoDisplay
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.screens.player.PlayerScreen

@Composable
fun PlayerScreen(
    player: Player?,
    modifier: Modifier = Modifier,
    mediaInfoDisplay: @Composable (player: Player) -> Unit = { MediaInfoDisplay(it) },
    controlButtons: @Composable (player: Player) -> Unit = { MediaControlButtons(it) },
    background: @Composable (player: Player) -> Unit = { DefaultBackground(it) }
) {
    // TODO: I've been ignoring the animated versions for now
    if (player != null) {
        PlayerScreen(
            mediaDisplay = { mediaInfoDisplay(player) },
            controlButtons = { controlButtons(player) },
            buttons = {},
            modifier = modifier,
            background = { background(player) },
        )
    }
}

@Composable
private fun DefaultBackground(player: Player) {
    // TODO: This should probably not be directly accessed
    val artwork = player.mediaMetadata.artworkUri?.let { CoilPaintable(it) }
    ArtworkColorBackground(
        paintable = artwork,
        defaultColor = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxSize(),
    )
}
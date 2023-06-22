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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip

@WearPreviewDevices
@Composable
fun EntityScreenPreview() {
    EntityScreen(
        columnState = belowTimeTextPreview(),
        headerContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                (Color.Green).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("Playlist name")
            }
        },
        buttonsContent = {
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    onClick = { },
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(weight = 0.3F, fill = false)
                )

                Button(
                    imageVector = Icons.Default.PlaylistPlay,
                    contentDescription = "Play",
                    onClick = { },
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(weight = 0.3F, fill = false)
                )
            }
        },
        content = {
            item { Chip(label = "Song 1", onClick = { }) }
            item { Chip(label = "Song 2", onClick = { }) }
        }
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoadedState() {
    EntityScreen(
        columnState = belowTimeTextPreview(),
        entityScreenState = EntityScreenState.Loaded(listOf("Song 1", "Song 2")),
        headerContent = { DefaultEntityScreenHeader(title = "Playlist name") },
        loadingContent = { },
        mediaContent = { song -> Chip(label = song, onClick = { }) },
        buttonsContent = { ButtonContentForStatePreview() }
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewLoadingState() {
    EntityScreen(
        columnState = belowTimeTextPreview(),
        entityScreenState = EntityScreenState.Loading,
        headerContent = { DefaultEntityScreenHeader(title = "Playlist name") },
        loadingContent = { items(count = 2) { PlaceholderChip(colors = ChipDefaults.secondaryChipColors()) } },
        mediaContent = { },
        buttonsContent = { ButtonContentForStatePreview() }
    )
}

@WearPreviewDevices
@Composable
fun EntityScreenPreviewFailedState() {
    EntityScreen(
        columnState = belowTimeTextPreview(),
        entityScreenState = EntityScreenState.Failed,
        headerContent = { DefaultEntityScreenHeader(title = "Playlist name") },
        loadingContent = { },
        mediaContent = { },
        buttonsContent = { ButtonContentForStatePreview() },
        failedContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                )
                Text(
                    text = "Could not retrieve the playlist.",
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Composable
private fun ButtonContentForStatePreview() {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            imageVector = Icons.Default.Download,
            contentDescription = "Download",
            onClick = { },
            modifier = Modifier
                .padding(start = 6.dp)
                .weight(weight = 0.3F, fill = false)
        )

        Button(
            imageVector = Icons.Default.Shuffle,
            contentDescription = "Shuffle",
            onClick = { },
            modifier = Modifier
                .padding(start = 6.dp)
                .weight(weight = 0.3F, fill = false)
        )

        Button(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            onClick = { },
            modifier = Modifier
                .padding(start = 6.dp)
                .weight(weight = 0.3F, fill = false)
        )
    }
}

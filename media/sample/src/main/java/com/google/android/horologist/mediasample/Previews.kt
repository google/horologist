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

package com.google.android.horologist.mediasample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumePositionIndicator
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.composables.MarqueeText
import com.google.android.horologist.media.ui.components.animated.AnimatedPlayPauseButton
import com.google.android.horologist.media.ui.components.animated.AnimatedSeekToNextButton
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.components.background.ColorBackground
import com.google.android.horologist.media.ui.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.components.display.LoadingMediaDisplay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
@WearPreviewLargeRound
fun MarqueePreview() {
    val testText = "In-A-Gadda-Da-Vida (Cover) - Miley Cyrus & Jack Black"
    Box(modifier = Modifier.padding(35.dp)
        .fillMaxSize().border(width = 1.dp, color= Color.White),
        contentAlignment = Alignment.Center) {
        MarqueeText(text= testText, modifier = Modifier.fillMaxWidth(), followGap = 40.dp)
    }
}

@Composable
@WearPreviewLargeRound
fun AnimatedSeekToNextButtonPreview() {
    Box(modifier = Modifier.padding(35.dp)
        .fillMaxSize()
        .border(width = 1.dp, color= Color.White),
        contentAlignment = Alignment.Center,
        ) {
        AnimatedSeekToNextButton(
            modifier = Modifier.fillMaxSize(),
            onClick = { } ,
            enabled = true,
            colors = MediaButtonDefaults.mediaButtonDefaultColors,
        )
    }
}

@Composable
@WearPreviewLargeRound
fun AnimatedPlayPauseButtonPreview() {
    Box(modifier = Modifier.padding(35.dp)
        .fillMaxSize()
        .border(width = 1.dp, color= Color.White),
        contentAlignment = Alignment.Center,
    ) {
        var playing by remember { mutableStateOf(false) }
        var progress by remember { mutableStateOf(0.1f) } // Start at 30%

        // Simulate progress increase while playing
        LaunchedEffect(playing) {
            if (playing) {
                while (progress < 1.0f) {
                    delay(100)
                    progress += 0.01f
                }
            }
        }

        AnimatedPlayPauseButton(
            modifier = Modifier.padding(30.dp).fillMaxSize(),
            onPlayClick = { playing = true },
            onPauseClick = { playing = false },
            playing = playing,
            enabled = true,
            colors = MediaButtonDefaults.mediaButtonDefaultColors,
            progress = {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(0.8f), // Adjust size as needed
                    strokeWidth = 4.dp,
                )
            }
        )
    }
}

@Composable
@WearPreviewLargeRound
fun LoadingMediaDisplayPreview() {
    Box(modifier = Modifier.padding(35.dp)
        .fillMaxSize()
        .border(width = 1.dp, color= Color.White),
        contentAlignment = Alignment.Center,
    ) {
        LoadingMediaDisplay()
    }
}

@Composable
@WearPreviewLargeRound
fun ButtonGroupPreview() {
    val interactionSources = remember { Array(3) { MutableInteractionSource() } }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ButtonGroup(modifier = Modifier.fillMaxWidth()) {
            repeat(3) { index ->
                buttonGroupItem(interactionSource = interactionSources[index]) {
                    Button(onClick = {}, interactionSource = interactionSources[index]) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(listOf("A", "B", "C")[index])
                        }
                    }
                }
            }

        }
    }
}

@Composable
@WearPreviewLargeRound
fun RadialBackgroundPreview() {
    var color by remember { mutableStateOf(Color.Red) }

    LaunchedEffect(key1 = Unit) { // Trigger once on composition
        while (true) {
            delay(2000) // Change color every 2 seconds
            color = when (color) {
                Color.Red -> Color.Green
                Color.Green -> Color.Blue
                else -> Color.Red
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Directly using ColorBackground
        ColorBackground(
            color = color,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
@WearPreviewLargeRound
fun VolumePositionIndicatorPreview() {
    val volume = remember { mutableStateOf(VolumeState(0, 10)) } // Make volume mutable
    val volumeUiState = VolumeUiStateMapper.map(volumeState = volume.value)
    val displayIndicatorChannel = remember { Channel<Unit>(Channel.CONFLATED) }

    LaunchedEffect(Unit) {
        delay(3000)
        displayIndicatorChannel.send(Unit)
        while(volume.value.current < volume.value.max) {
            delay(500)
            val current = volume.value.current
            volume.value = volume.value.copy(current+1)
        }
        while(volume.value.max >= volume.value.current && volume.value.current > 0) {
            delay(500)
            val current = volume.value.current
            volume.value = volume.value.copy(current-1)
        }
        delay(5000)
        displayIndicatorChannel.send(Unit)
    }

    Box(modifier = Modifier.padding(35.dp)
        .fillMaxSize()
        .border(width = 1.dp, color= Color.White),
        contentAlignment = Alignment.Center,
    ) {
        VolumePositionIndicator(
            volumeUiState = { volumeUiState },
            displayIndicatorEvents = displayIndicatorChannel.receiveAsFlow()
        )
    }
}


/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.material3.VolumeLevelIndicator
import com.google.android.horologist.audio.ui.material3.VolumeScreen
import com.google.android.horologist.audio.ui.material3.components.AudioOutputUi

/**
 * Audio — `:media:audio-ui-material3`.
 *
 * The volume screen and its indicator, at the three volume positions whose rendering actually
 * differs: mid-range, muted (decrease disabled), and maxed (increase disabled).
 */
private val Headphones =
  AudioOutputUi(
    displayName = "Pixel Buds Pro",
    imageVector = Icons.Default.Phone,
    isConnected = true,
  )

private val NotConnected =
  AudioOutputUi(displayName = "Watch speaker", imageVector = Icons.Default.Phone, isConnected = false)

@Composable
private fun Volume(state: VolumeUiState, output: AudioOutputUi = Headphones) {
  CatalogWearTheme {
    VolumeScreen(
      volume = { state },
      audioOutputUi = output,
      increaseVolume = {},
      decreaseVolume = {},
      onAudioOutputClick = {},
    )
  }
}

@AudioCatalog
@Composable
internal fun AudioVolumeScreen() {
  Volume(VolumeUiState(current = 5, max = 10))
}

@AudioCatalog
@Composable
internal fun AudioVolumeScreenMuted() {
  Volume(VolumeUiState(current = 0, max = 10))
}

@AudioCatalog
@Composable
internal fun AudioVolumeScreenMax() {
  Volume(VolumeUiState(current = 10, max = 10))
}

@AudioCatalog
@Composable
internal fun AudioVolumeScreenNotConnected() {
  Volume(VolumeUiState(current = 5, max = 10), output = NotConnected)
}

@AudioCatalog
@Composable
internal fun AudioVolumeLevelIndicator() {
  CatalogWearTheme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      VolumeLevelIndicator(volumeUiState = { VolumeUiState(current = 7, max = 10) })
    }
  }
}

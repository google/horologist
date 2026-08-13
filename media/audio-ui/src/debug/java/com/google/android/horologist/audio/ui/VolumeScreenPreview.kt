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

package com.google.android.horologist.audio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.audio.AudioOutput
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.components.toAudioOutputUi
import com.google.android.horologist.audio.ui.mapper.VolumeUiStateMapper
import com.google.android.horologist.compose.tools.ThemeValues
import com.google.android.horologist.compose.tools.WearPreviewThemes

@WearPreviewDevices
@Composable
fun VolumeScreenPreview() {
  val volumeUiState = VolumeUiStateMapper.map(volumeState = VolumeState(5, 10))
  Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
    Scaffold(positionIndicator = { VolumePositionIndicator(volumeUiState = { volumeUiState }) }) {
      VolumeScreen(
        volume = { volumeUiState },
        audioOutputUi = AudioOutput.BluetoothHeadset("1", "PixelBuds Pro").toAudioOutputUi(),
        increaseVolume = {},
        decreaseVolume = {},
        onAudioOutputClick = {},
      )
    }
  }
}

@WearPreviewFontScales
@Composable
fun VolumeScreenLongLabelPreview() {
  val volumeUiState = VolumeUiStateMapper.map(volumeState = VolumeState(5, 10))
  Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
    Scaffold(positionIndicator = { VolumePositionIndicator(volumeUiState = { volumeUiState }) }) {
      VolumeScreen(
        volume = { volumeUiState },
        audioOutputUi =
          AudioOutput.BluetoothHeadset("2", "Sennheiser Momentum Wireless Plus").toAudioOutputUi(),
        increaseVolume = {},
        decreaseVolume = {},
        onAudioOutputClick = {},
      )
    }
  }
}

@WearPreviewLargeRound
@Composable
fun VolumeScreenTheme(@PreviewParameter(WearPreviewThemes::class) themeValues: ThemeValues) {
  val volume = VolumeState(5, 10)
  val volumeUiState = VolumeUiStateMapper.map(volumeState = volume)

  MaterialTheme(themeValues.colors) {
    Box(modifier = Modifier.fillMaxSize()) {
      Scaffold(positionIndicator = { VolumePositionIndicator(volumeUiState = { volumeUiState }) }) {
        VolumeScreen(
          volume = { volumeUiState },
          audioOutputUi =
            AudioOutput.BluetoothHeadset(id = "1", name = "PixelBuds").toAudioOutputUi(),
          increaseVolume = {},
          decreaseVolume = {},
          onAudioOutputClick = {},
        )
      }
    }
  }
}

@WearPreviewDevices
@Composable
fun VolumeScreenWithLabel() {
  val volume = VolumeState(5, 10)
  val volumeUiState = VolumeUiStateMapper.map(volumeState = volume)

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(positionIndicator = { VolumePositionIndicator(volumeUiState = { volumeUiState }) }) {
      VolumeWithLabelScreen(volume = { volumeUiState }, increaseVolume = {}, decreaseVolume = {})
    }
  }
}

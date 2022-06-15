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

@file:OptIn(ExperimentalHorologistAudioUiApi::class, ExperimentalHorologistComposeToolsApi::class)

package com.google.android.horologist.audio.ui.components.animated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.wear.compose.material.Stepper
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.VolumeScreenDefaults.DecreaseIcon
import com.google.android.horologist.audio.ui.VolumeScreenDefaults.IncreaseIcon
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.WearSmallRoundDevicePreview

@WearSmallRoundDevicePreview
@Composable
fun AnimatedSetVolumeButtonPreview() {
    var volumeState by remember { mutableStateOf(VolumeState(3, 5)) }

    InteractivePreviewAware {
        Stepper(
            value = volumeState.current.toFloat(),
            onValueChange = { volumeState = volumeState.copy(current = it.toInt()) },
            steps = volumeState.max - 1,
            valueRange = (0f..volumeState.max.toFloat()),
            increaseIcon = {
                IncreaseIcon()
            },
            decreaseIcon = {
                DecreaseIcon()
            },
        ) {
            AnimatedSetVolumeButton(onVolumeClick = { }, volumeState = volumeState)
        }
    }
}

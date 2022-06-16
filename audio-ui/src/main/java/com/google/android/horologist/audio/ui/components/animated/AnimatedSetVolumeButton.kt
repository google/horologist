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

package com.google.android.horologist.audio.ui.components.animated

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton

/**
 * Button to launch a screen to control the system volume.
 *
 * See [VolumeState]
 */
@ExperimentalHorologistAudioUiApi
@Composable
public fun AnimatedSetVolumeButton(
    onVolumeClick: () -> Unit,
    volumeState: VolumeState,
    modifier: Modifier = Modifier,
) {
    if (LocalStaticPreview.current) {
        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeState = volumeState,
            modifier = modifier
        )
    } else {
        val volumeUp by rememberLottieComposition(
            spec = LottieCompositionSpec.Asset("lottie/VolumeUp.json"),
        )
        val volumeDown by rememberLottieComposition(
            spec = LottieCompositionSpec.Asset("lottie/VolumeDown.json"),
        )
        val lottieAnimatable = rememberLottieAnimatable()

        var lastVolume by remember { mutableStateOf(volumeState.current) }

        LaunchedEffect(volumeState) {
            val lastVolumeBefore = lastVolume
            lastVolume = volumeState.current
            if (volumeState.current > lastVolumeBefore) {
                lottieAnimatable.animate(
                    iterations = 1,
                    composition = volumeUp,
                )
            } else {
                lottieAnimatable.animate(
                    iterations = 1,
                    composition = volumeDown,
                )
            }
        }

        Button(
            modifier = modifier.size(ButtonDefaults.SmallButtonSize),
            onClick = onVolumeClick,
            colors = ButtonDefaults.iconButtonColors(),
        ) {
            LottieAnimation(
                composition = volumeDown,
                modifier = Modifier.size(24.dp),
                progress = { lottieAnimatable.progress }
            )
        }
    }
}

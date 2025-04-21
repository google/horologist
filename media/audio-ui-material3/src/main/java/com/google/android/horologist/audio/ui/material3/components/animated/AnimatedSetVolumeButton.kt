/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.material3.components.animated

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.touchTargetAwareSize
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.horologist.audio.VolumeState
import com.google.android.horologist.audio.ui.VolumeUiState

/**
 * Button to launch a screen to control the system volume.
 *
 * See [VolumeState]
 */
@Composable
public fun AnimatedSetVolumeButton(
    onVolumeClick: () -> Unit,
    volumeUiState: VolumeUiState,
    modifier: Modifier = Modifier,
) {
    val volumeUp by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset("lottie/VolumeUp.json"),
    )
    val volumeDown by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset("lottie/VolumeDown.json"),
    )
    val lottieAnimatable = rememberLottieAnimatable()

    var lastVolume by remember { mutableStateOf(volumeUiState.current) }

    LaunchedEffect(volumeUiState) {
        val lastVolumeBefore = lastVolume
        lastVolume = volumeUiState.current
        if (volumeUiState.current > lastVolumeBefore) {
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

    IconButton(
        modifier = modifier.touchTargetAwareSize(IconButtonDefaults.SmallButtonSize),
        onClick = onVolumeClick,
    ) {
        LottieAnimation(
            composition = volumeDown,
            modifier = Modifier.size(
                IconButtonDefaults.iconSizeFor(IconButtonDefaults.SmallButtonSize),
            ),
            progress = { lottieAnimatable.progress },
        )
    }
}

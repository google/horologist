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

package com.google.android.horologist.media.ui.components.animated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.horologist.audio.ui.components.animated.LocalStaticPreview
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.components.controls.SeekToNextButton

@Composable
public fun AnimatedSeekToNextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongRepeatableClick: (() -> Unit)? = null,
    onLongRepeatableClickEnd: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(48.dp, 60.dp),
) {
    if (LocalStaticPreview.current) {
        SeekToNextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = colors,
        )
    } else {
        val compositionResult = rememberLottieComposition(
            spec = LottieCompositionSpec.Asset("lottie/Next.json"),
        )

        AnimatedMediaButton(
            modifier = modifier,
            onClick = onClick,
            contentDescription = stringResource(id = R.string.horologist_seek_to_next_button_content_description),
            enabled = enabled,
            colors = colors,
            iconSize = iconSize,
            tapTargetSize = tapTargetSize,
            compositionResult = compositionResult,
            iconAlign = Alignment.End,
            onLongRepeatableClick = onLongRepeatableClick,
            onLongRepeatableClickEnd = onLongRepeatableClickEnd,
        )
    }
}

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

package com.google.android.horologist.media.ui.material3.components.animated

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.horologist.media.ui.material3.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.model.R

@Composable
public fun AnimatedSeekToPreviousButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRepeatableClick: (() -> Unit)? = null,
    onRepeatableClickEnd: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    buttonPadding: PaddingValues = PaddingValues(0.dp),
    colors: IconButtonColors = MediaButtonDefaults.mediaButtonDefaultColors(),
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
) {
    val compositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.Asset("lottie/M3Next.json"),
    )
    // Reverse the button padding as the icon is flipped horizontally.
    val reversedButtonPadding = PaddingValues.Absolute(
        left = buttonPadding.calculateRightPadding(LocalLayoutDirection.current),
        right = buttonPadding.calculateLeftPadding(LocalLayoutDirection.current),
        top = buttonPadding.calculateTopPadding(),
        bottom = buttonPadding.calculateBottomPadding(),
    )

    AnimatedMediaButton(
        modifier = modifier.graphicsLayer(scaleX = -1f),
        onClick = onClick,
        contentDescription = stringResource(id = R.string.horologist_seek_to_previous_button_content_description),
        enabled = enabled,
        colors = colors,
        iconSize = iconSize,
        buttonPadding = reversedButtonPadding,
        compositionResult = compositionResult,
        onRepeatableClick = onRepeatableClick,
        onRepeatableClickEnd = onRepeatableClickEnd,
        interactionSource = interactionSource,
    )
}

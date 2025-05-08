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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material3.IconButtonColors
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.LocalContentColor
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.google.android.horologist.media.ui.material3.components.controls.MediaButtonDefaults
import com.google.android.horologist.media.ui.material3.composables.RepeatableClickableButton
import com.google.android.horologist.media.ui.material3.composables.UnboundedRippleIconButton
import com.google.android.horologist.media.ui.material3.util.LOTTIE_DYNAMIC_PROPERTY_KEY_PATH
import kotlinx.coroutines.launch

/**
 * A button that animates a Lottie composition when clicked.
 *
 * @param onClick The callback to invoke when the button is clicked.
 * @param compositionResult The Lottie composition to animate.
 * @param contentDescription The content description for the button.
 * @param modifier The modifier to apply to the button.
 * @param onRepeatableClick The callback to invoke when the button is repeatedly clicked (optional).
 * @param onRepeatableClickEnd The callback to invoke when the button is no longer repeatedly
 *     clicked (optional).
 * @param enabled Whether the button is enabled (optional).
 * @param colors The colors to apply to the button (optional).
 * @param interactionSource The interaction source to apply to the button (optional).
 * @param buttonPadding The padding to be applied around the button. Defafults to Zero (optional).
 * @param iconSize The size of the icon (optional).
 * @param iconAlign The alignment of the icon (optional).
 */
@Composable
public fun AnimatedMediaButton(
    onClick: () -> Unit,
    compositionResult: LottieCompositionResult,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onRepeatableClick: (() -> Unit)? = null,
    onRepeatableClickEnd: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: IconButtonColors = MediaButtonDefaults.mediaButtonDefaultColors(),
    interactionSource: MutableInteractionSource? = null,
    buttonPadding: PaddingValues = PaddingValues(0.dp),
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    iconAlign: Alignment = Alignment.Center,
) {
    val scope = rememberCoroutineScope()
    val lottieAnimatable = rememberLottieAnimatable()
    if (onRepeatableClick == null) {
        UnboundedRippleIconButton(
            onClick = {
                scope.launch { lottieAnimatable.animate(composition = compositionResult.value) }
                onClick()
            },
            modifier = modifier,
            enabled = enabled,
            colors = colors,
            rippleRadius = null,
            buttonPadding = buttonPadding,
            interactionSource = interactionSource,
        ) {
            MediaButtonContent(
                compositionResult = compositionResult,
                contentDescription = contentDescription,
                iconSize = iconSize,
                iconAlign = iconAlign,
                lottieAnimatable = lottieAnimatable,
            )
        }
    } else {
        RepeatableClickableButton(
            onClick = {
                scope.launch { lottieAnimatable.animate(composition = compositionResult.value) }
                onClick()
            },
            onRepeatableClick = onRepeatableClick,
            onRepeatableClickEnd = onRepeatableClickEnd ?: {},
            interactionSource = interactionSource,
            indication = null,
            buttonPadding = buttonPadding,
            modifier = modifier,
            enabled = enabled,
            colors = colors,
        ) {
            MediaButtonContent(
                compositionResult = compositionResult,
                contentDescription = contentDescription,
                iconSize = iconSize,
                iconAlign = iconAlign,
                lottieAnimatable = lottieAnimatable,
            )
        }
    }
}

@Composable
private fun BoxScope.MediaButtonContent(
    compositionResult: LottieCompositionResult,
    contentDescription: String,
    iconSize: Dp = IconButtonDefaults.LargeIconSize,
    dynamicProperties: LottieDynamicProperties? = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = LocalContentColor.current.toArgb(),
            keyPath = LOTTIE_DYNAMIC_PROPERTY_KEY_PATH,
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = LocalContentColor.current.toArgb(),
            keyPath = LOTTIE_DYNAMIC_PROPERTY_KEY_PATH,
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.OPACITY,
            value = LocalContentColor.current.opacityForLottieAnimation(),
            keyPath = LOTTIE_DYNAMIC_PROPERTY_KEY_PATH,
        ),
    ),
    iconAlign: Alignment = Alignment.Center,
    lottieAnimatable: LottieAnimatable,
) {
    val contentModifier = Modifier
        .size(iconSize)
        .align(iconAlign)

    LottieAnimationWithPlaceholder(
        lottieCompositionResult = compositionResult,
        progress = { lottieAnimatable.progress },
        placeholder = LottiePlaceholders.Next,
        contentDescription = contentDescription,
        modifier = contentModifier,
        dynamicProperties = dynamicProperties,
    )
}

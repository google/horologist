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

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.LocalContentAlpha
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import kotlinx.coroutines.launch
import androidx.compose.ui.semantics.contentDescription as contentDescriptionProperty

/**
 * A base button for media controls.
 */
@ExperimentalHorologistApi
@Composable
public fun AnimatedMediaButton(
    onClick: () -> Unit,
    compositionResult: LottieCompositionResult,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    dynamicProperties: LottieDynamicProperties? = null,
    iconSize: Dp = 30.dp,
    tapTargetSize: DpSize = DpSize(48.dp, 60.dp),
    iconAlign: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    val scope = rememberCoroutineScope()
    val composition = compositionResult.value
    val lottieAnimatable = rememberLottieAnimatable()

    Button(
        onClick = {
            scope.launch {
                android.util.Log.d("lottie2", "animate")
                lottieAnimatable.animate(composition = composition)
            }
            onClick()
        },
        modifier = modifier.size(tapTargetSize),
        enabled = enabled,
        colors = colors
    ) {
        val contentModifier = Modifier
            .size(iconSize)
            .run {
                when (iconAlign) {
                    Alignment.Start -> {
                        offset(x = -7.5.dp)
                    }

                    Alignment.End -> {
                        offset(x = 7.5.dp)
                    }

                    else -> {
                        this
                    }
                }
            }
            .align(Alignment.Center)
            .graphicsLayer(alpha = LocalContentAlpha.current)
            .semantics { contentDescriptionProperty = contentDescription }

        LottieAnimationWithPlaceholder(
            lottieCompositionResult = compositionResult,
            lottieAnimatable = lottieAnimatable,
            placeholder = LottiePlaceholders.Next,
            contentDescription = contentDescription,
            modifier = contentModifier,
            lottieComposition = compositionResult.value,
            dynamicProperties = dynamicProperties
        )
    }
}

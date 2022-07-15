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

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.LocalContentAlpha
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import kotlinx.coroutines.launch
import androidx.compose.ui.semantics.contentDescription as contentDescriptionProperty

/**
 * A base button for media controls.
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun AnimatedMediaButton(
    onClick: () -> Unit,
    composition: LottieComposition?,
    lottieAnimatable: LottieAnimatable,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.iconButtonColors(),
    dynamicProperties: LottieDynamicProperties? = null,
    iconSize: Dp = 30.dp,
    tapTargetSize: Dp = 60.dp,
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                lottieAnimatable.animate(composition = composition)
            }
            onClick()
        },
        modifier = modifier.size(tapTargetSize),
        enabled = enabled,
        colors = colors,
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer(alpha = LocalContentAlpha.current)
                .semantics { contentDescriptionProperty = contentDescription },
            composition = composition,
            progress = { lottieAnimatable.progress },
            dynamicProperties = dynamicProperties,
        )
    }
}

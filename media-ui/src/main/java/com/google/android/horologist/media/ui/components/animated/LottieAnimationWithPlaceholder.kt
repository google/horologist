/*
 * Copyright 2023 The Android Open Source Project
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

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.wear.compose.material.Icon
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieDynamicProperties

/**
 * A lottie animation with placeholder.
 */
@Composable
public fun LottieAnimationWithPlaceholder(
    lottieCompositionResult: LottieCompositionResult,
    lottieAnimatable: LottieAnimatable,
    placeholder: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    lottieComposition: LottieComposition?,
    dynamicProperties: LottieDynamicProperties? = null
) {
    val isCompositionReady by produceState(initialValue = false, producer = {
        lottieCompositionResult.await()
        value = true
    })

    if (isCompositionReady) {
        LottieAnimation(
            modifier = modifier,
            composition = lottieComposition,
            progress = {
                lottieAnimatable.progress
            },
            dynamicProperties = dynamicProperties
        )
    } else {
        Icon(
            modifier = modifier,
            imageVector = placeholder,
            contentDescription = contentDescription
        )
    }
}

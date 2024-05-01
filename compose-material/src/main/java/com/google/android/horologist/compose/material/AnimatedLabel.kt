/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.compose.material

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * This composable provides an animated label that smoothly transitions between different
 * text values. It uses animations for appearance, disappearance, and content changes.
 *
 * @param label The text to display in the label. If `null`, the label is hidden.
 * @param content The composable function to render the animated content within the label.
 * This function receives the current `targetState` (the text being displayed) as a parameter.
 */
@Composable
public fun AnimatedLabel(
    label: String?,
    content: @Composable AnimatedContentScope.(targetState: String) -> Unit,
) {
    var previousLabel by remember {
        mutableStateOf(label ?: "")
    }
    label?.also { previousLabel = it }

    AnimatedVisibility(
        visible = label != null,
        enter = fadeIn(tween(250, delayMillis = 50)) +
            expandVertically(tween(200)),
        exit = fadeOut(tween(250)) +
            shrinkVertically(tween(200, delayMillis = 50)),
    ) {
        AnimatedContent(
            targetState = previousLabel,
            transitionSpec = {
                fadeIn(tween(durationMillis = 300, delayMillis = 150))
                    .togetherWith(fadeOut(tween(durationMillis = 150), targetAlpha = 0.1f))
            },
            label = "AnimatedLabel",
            content = content,
        )
    }
}

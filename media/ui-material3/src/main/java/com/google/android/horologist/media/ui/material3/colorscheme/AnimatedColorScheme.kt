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

package com.google.android.horologist.media.ui.material3.colorscheme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.wear.compose.material3.ColorScheme

/**
 * An animated color scheme.
 *
 * This class is used to create a smooth transition between different color schemes.
 *
 * @param initialColorScheme The initial color scheme.
 */
public class AnimatedColorScheme(initialColorScheme: ColorScheme) {
    private var currentScheme: ColorScheme = initialColorScheme
    private var targetScheme: ColorScheme = initialColorScheme
    private val progress = Animatable(0f)

    public val primary: Color
        get() = lerp(currentScheme.primary, targetScheme.primary, progress.value)

    public val primaryDim: Color
        get() = lerp(currentScheme.primaryDim, targetScheme.primaryDim, progress.value)

    public val primaryContainer: Color
        get() = lerp(currentScheme.primaryContainer, targetScheme.primaryContainer, progress.value)

    public val onPrimary: Color
        get() = lerp(currentScheme.onPrimary, targetScheme.onPrimary, progress.value)

    public val onPrimaryContainer: Color
        get() = lerp(currentScheme.onPrimaryContainer, targetScheme.onPrimaryContainer, progress.value)

    public val secondary: Color
        get() = lerp(currentScheme.secondary, targetScheme.secondary, progress.value)

    public val secondaryDim: Color
        get() = lerp(currentScheme.secondaryDim, targetScheme.secondaryDim, progress.value)

    public val secondaryContainer: Color
        get() = lerp(currentScheme.secondaryContainer, targetScheme.secondaryContainer, progress.value)

    public val onSecondary: Color
        get() = lerp(currentScheme.onSecondary, targetScheme.onSecondary, progress.value)

    public val onSecondaryContainer: Color
        get() = lerp(currentScheme.onSecondaryContainer, targetScheme.onSecondaryContainer, progress.value)

    public val tertiary: Color
        get() = lerp(currentScheme.tertiary, targetScheme.tertiary, progress.value)

    public val tertiaryDim: Color
        get() = lerp(currentScheme.tertiaryDim, targetScheme.tertiaryDim, progress.value)

    public val tertiaryContainer: Color
        get() = lerp(currentScheme.tertiaryContainer, targetScheme.tertiaryContainer, progress.value)

    public val onTertiary: Color
        get() = lerp(currentScheme.onTertiary, targetScheme.onTertiary, progress.value)

    public val onTertiaryContainer: Color
        get() = lerp(currentScheme.onTertiaryContainer, targetScheme.onTertiaryContainer, progress.value)

    public val surfaceContainerLow: Color
        get() = lerp(currentScheme.surfaceContainerLow, targetScheme.surfaceContainerLow, progress.value)

    public val surfaceContainer: Color
        get() = lerp(currentScheme.surfaceContainer, targetScheme.surfaceContainer, progress.value)

    public val surfaceContainerHigh: Color
        get() = lerp(currentScheme.surfaceContainerHigh, targetScheme.surfaceContainerHigh, progress.value)

    public val onSurface: Color
        get() = lerp(currentScheme.onSurface, targetScheme.onSurface, progress.value)

    public val onSurfaceVariant: Color
        get() = lerp(currentScheme.onSurfaceVariant, targetScheme.onSurfaceVariant, progress.value)

    public val outline: Color
        get() = lerp(currentScheme.outline, targetScheme.outline, progress.value)

    public val outlineVariant: Color
        get() = lerp(currentScheme.outlineVariant, targetScheme.outlineVariant, progress.value)

    public val background: Color
        get() = lerp(currentScheme.background, targetScheme.background, progress.value)

    public val onBackground: Color
        get() = lerp(currentScheme.onBackground, targetScheme.onBackground, progress.value)

    public val error: Color
        get() = lerp(currentScheme.error, targetScheme.error, progress.value)

    public val onError: Color
        get() = lerp(currentScheme.onError, targetScheme.onError, progress.value)

    public val errorContainer: Color
        get() = lerp(currentScheme.errorContainer, targetScheme.errorContainer, progress.value)

    public val onErrorContainer: Color
        get() = lerp(currentScheme.onErrorContainer, targetScheme.onErrorContainer, progress.value)

    /**
     * Animates to the target color scheme.
     *
     * @param targetColorScheme The target color scheme.
     * @param animationSpec The animation spec to animate the color updates.
     */
    public suspend fun animateTo(targetColorScheme: ColorScheme, animationSpec: AnimationSpec<Float>) {
        this.targetScheme = targetColorScheme
        if (progress.targetValue != 1f) {
            progress.animateTo(1f, animationSpec)
        }
        this.currentScheme = targetScheme
        if (progress.targetValue != 0f) {
            progress.snapTo(0f)
        }
    }

    /** Indicates whether the animation is running. */
    public fun isRunning(): Boolean = progress.isRunning
}

package com.google.android.horologist.media.ui.material3.colorscheme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.graphics.lerp
import androidx.wear.compose.material3.ColorScheme

/**
 * An animated color scheme.
 *
 * This class is used to create a smooth transition between different color schemes.
 *
 * @param initialColorScheme The initial color scheme.
 */
class AnimatedColorScheme(initialColorScheme: ColorScheme) {
    private var currentScheme: ColorScheme = initialColorScheme
    private var targetScheme: ColorScheme = initialColorScheme
    private val progress = Animatable(0f)

    val primary
        get() = lerp(currentScheme.primary, targetScheme.primary, progress.value)

    val primaryDim
        get() = lerp(currentScheme.primaryDim, targetScheme.primaryDim, progress.value)

    val primaryContainer
        get() = lerp(currentScheme.primaryContainer, targetScheme.primaryContainer, progress.value)

    val onPrimary
        get() = lerp(currentScheme.onPrimary, targetScheme.onPrimary, progress.value)

    val onPrimaryContainer
        get() = lerp(currentScheme.onPrimaryContainer, targetScheme.onPrimaryContainer, progress.value)

    val secondary
        get() = lerp(currentScheme.secondary, targetScheme.secondary, progress.value)

    val secondaryDim
        get() = lerp(currentScheme.secondaryDim, targetScheme.secondaryDim, progress.value)

    val secondaryContainer
        get() = lerp(currentScheme.secondaryContainer, targetScheme.secondaryContainer, progress.value)

    val onSecondary
        get() = lerp(currentScheme.onSecondary, targetScheme.onSecondary, progress.value)

    val onSecondaryContainer
        get() =
            lerp(currentScheme.onSecondaryContainer, targetScheme.onSecondaryContainer, progress.value)

    val tertiary
        get() = lerp(currentScheme.tertiary, targetScheme.tertiary, progress.value)

    val tertiaryDim
        get() = lerp(currentScheme.tertiaryDim, targetScheme.tertiaryDim, progress.value)

    val tertiaryContainer
        get() = lerp(currentScheme.tertiaryContainer, targetScheme.tertiaryContainer, progress.value)

    val onTertiary
        get() = lerp(currentScheme.onTertiary, targetScheme.onTertiary, progress.value)

    val onTertiaryContainer
        get() =
            lerp(currentScheme.onTertiaryContainer, targetScheme.onTertiaryContainer, progress.value)

    val surfaceContainerLow
        get() =
            lerp(currentScheme.surfaceContainerLow, targetScheme.surfaceContainerLow, progress.value)

    val surfaceContainer
        get() = lerp(currentScheme.surfaceContainer, targetScheme.surfaceContainer, progress.value)

    val surfaceContainerHigh
        get() =
            lerp(currentScheme.surfaceContainerHigh, targetScheme.surfaceContainerHigh, progress.value)

    val onSurface
        get() = lerp(currentScheme.onSurface, targetScheme.onSurface, progress.value)

    val onSurfaceVariant
        get() = lerp(currentScheme.onSurfaceVariant, targetScheme.onSurfaceVariant, progress.value)

    val outline
        get() = lerp(currentScheme.outline, targetScheme.outline, progress.value)

    val outlineVariant
        get() = lerp(currentScheme.outlineVariant, targetScheme.outlineVariant, progress.value)

    val background
        get() = lerp(currentScheme.background, targetScheme.background, progress.value)

    val onBackground
        get() = lerp(currentScheme.onBackground, targetScheme.onBackground, progress.value)

    val error
        get() = lerp(currentScheme.error, targetScheme.error, progress.value)

    val onError
        get() = lerp(currentScheme.onError, targetScheme.onError, progress.value)

    val errorContainer
        get() = lerp(currentScheme.errorContainer, targetScheme.errorContainer, progress.value)

    val onErrorContainer
        get() = lerp(currentScheme.onErrorContainer, targetScheme.onErrorContainer, progress.value)

    /**
     * Animates to the target color scheme.
     *
     * @param targetColorScheme The target color scheme.
     * @param animationSpec The animation spec to animate the color updates.
     */
    suspend fun animateTo(targetColorScheme: ColorScheme, animationSpec: AnimationSpec<Float>) {
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
    fun isRunning() = progress.isRunning
}
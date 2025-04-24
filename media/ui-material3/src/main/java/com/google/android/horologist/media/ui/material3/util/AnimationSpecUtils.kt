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

package com.google.android.horologist.media.ui.material3.util

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector3D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
import androidx.compose.runtime.withFrameMillis

/**
 * Code modified from https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/compose/compose-material3/src/main/java/androidx/wear/compose/material3/AnimationSpecUtils.kt.
 *
 * Returns a new [FiniteAnimationSpec] that is a faster version of this one.
 *
 * @param speedupPct How much to speed up the animation, as a percentage of the current speed. 0f
 *   being no change, 100f being double, speed and so on.
 */
internal fun <T> FiniteAnimationSpec<T>.faster(
    @FloatRange(from = 0.0) speedupPct: Float,
): FiniteAnimationSpec<T> {
    require(speedupPct >= 0f) { "speedupPct has to be positive. Was: $speedupPct" }
    return speedFactor(1 + speedupPct / 100)
}

/**
 * Returns a new [FiniteAnimationSpec] that is a slower version of this one.
 *
 * @param slowdownPct How much to slow down the animation, as a percentage of the current speed. 0f
 *   being no change, 50f being half the speed.
 */
internal fun <T> FiniteAnimationSpec<T>.slower(
    @FloatRange(from = 0.0, to = 100.0, toInclusive = false) slowdownPct: Float,
): FiniteAnimationSpec<T> {
    require(slowdownPct >= 0f && slowdownPct < 100f) {
        "slowdownPct has to be between 0 and 100. Was: $slowdownPct"
    }
    return speedFactor(1 - slowdownPct / 100)
}

/**
 * Returns a new [FiniteAnimationSpec] that is a slower or faster version of this one.
 *
 * @param factor How much to speed or slow the animation. 0f -> runs forever, zero speed (not
 *   allowed) 0.5f -> half speed 1f -> current speed 2f -> double speed
 */
internal fun <T> FiniteAnimationSpec<T>.speedFactor(
    @FloatRange(from = 0.0, fromInclusive = false) factor: Float,
): FiniteAnimationSpec<T> {
    require(factor > 0f) { "factor has to be positive. Was: $factor" }
    return when (this) {
        is SpringSpec -> SpringSpec(dampingRatio, stiffness * factor * factor, visibilityThreshold)
        else -> WrappedAnimationSpec(this, factor)
    }
}

private class WrappedAnimationSpec<T>(
    val wrapped: FiniteAnimationSpec<T>,
    val speedupFactor: Float,
) : FiniteAnimationSpec<T> {
    override fun <V : AnimationVector> vectorize(
        converter: TwoWayConverter<T, V>,
    ): VectorizedFiniteAnimationSpec<V> =
        WrappedVectorizedAnimationSpec(wrapped.vectorize(converter), speedupFactor)
}

private class WrappedVectorizedAnimationSpec<V : AnimationVector>(
    val wrapped: VectorizedFiniteAnimationSpec<V>,
    val speedupFactor: Float,
) : VectorizedFiniteAnimationSpec<V> {
    override val isInfinite: Boolean
        get() = wrapped.isInfinite

    override fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V,
    ): V = wrapped.getValueFromNanos(
        (playTimeNanos * speedupFactor).toLong(),
        initialValue,
        targetValue,
        initialVelocity,
    )

    override fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V,
    ): V = wrapped.getVelocityFromNanos(
        (playTimeNanos * speedupFactor).toLong(),
        initialValue,
        targetValue,
        initialVelocity,
    ) * speedupFactor

    @Suppress("MethodNameUnits")
    override fun getDurationNanos(initialValue: V, targetValue: V, initialVelocity: V): Long =
        (wrapped.getDurationNanos(initialValue, targetValue, initialVelocity) / speedupFactor)
            .toLong()
}

@Suppress("UNCHECKED_CAST")
internal operator fun <T : AnimationVector> T.times(k: Float): T {
    val t = this as AnimationVector
    return when (t) {
        is AnimationVector1D -> AnimationVector1D(t.value * k)
        is AnimationVector2D -> AnimationVector2D(t.v1 * k, t.v2 * k)
        is AnimationVector3D -> AnimationVector3D(t.v1 * k, t.v2 * k, t.v3 * k)
        is AnimationVector4D -> AnimationVector4D(t.v1 * k, t.v2 * k, t.v3 * k, t.v4 * k)
    } as T
}

internal suspend fun waitUntil(condition: () -> Boolean) {
    val initialTimeMillis = withFrameMillis { it }
    while (!condition()) {
        val timeMillis = withFrameMillis { it }
        if (timeMillis - initialTimeMillis > MAX_WAIT_TIME_MILLIS) return
    }
    return
}

private const val MAX_WAIT_TIME_MILLIS = 1_000L

/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.remotecompose.lottie.renderer

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.cubicEasing
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import com.google.android.horologist.remotecompose.lottie.format.ScalarKeyframeEasing

internal data class AnimationSegment(val startFrame: Float, val value: RemoteFloat)

/** A 2D point represented with RemoteFloats. */
internal data class Point(val x: RemoteFloat, val y: RemoteFloat)

internal val scalarLinearEasingOut = ScalarKeyframeEasing(x = 0f, 0f)
internal val scalarLinearEasingIn = ScalarKeyframeEasing(1f, 1f)

internal fun <T, U> List<List<T>>.innerMap(f: (T) -> U): List<List<U>> = this.map { it.map(f) }

@SuppressLint("RestrictedApi")
internal fun evaluateKeyframeProgress(
  startFrame: Float,
  endFrame: Float,
  outTangent: ScalarKeyframeEasing?,
  inTangent: ScalarKeyframeEasing?,
  currentFrame: RemoteFloat,
): RemoteFloat {
  val duration = endFrame - startFrame
  val frameInAnimation = currentFrame - startFrame
  val outT = outTangent ?: scalarLinearEasingOut
  val inT = inTangent ?: scalarLinearEasingIn

  val clampedFrame = clamp(value = frameInAnimation, min = 0.rf, max = duration.rf)
  val progress = if (duration == 0f) 0.rf else clampedFrame / duration.rf
  return cubicEasing(outT.x.rf, outT.y.rf, inT.x.rf, inT.y.rf, progress)
}

/**
 * Support keyframed animations (and delayed start animations) by chaining multiple animations
 * together.
 *
 * This recursively builds up a chain of IFELSE operations to select the correct RemoteFloat
 * representing the current segment of the animation.
 */
@SuppressLint("RestrictedApi")
internal fun chainAnimation(segments: List<AnimationSegment>, frame: RemoteFloat): RemoteFloat {
  if (segments.size == 1) {
    return segments[0].value
  }

  return selectIfLt(
    frame,
    segments[1].startFrame.rf,
    segments[0].value,
    chainAnimation(segments.subList(1, segments.size), frame),
  )
}

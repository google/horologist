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
import androidx.compose.remote.creation.compose.state.cubicEasing
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.AnimatedPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.BaseBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.BezierValue
import com.google.android.horologist.remotecompose.lottie.format.ScalarKeyframeEasing
import com.google.android.horologist.remotecompose.lottie.format.StaticBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticVectorProperty

internal data class AnimationSegment(val startFrame: Float, val value: RemoteFloat)

/** A 2D point represented with RemoteFloats. */
internal data class Point(val x: RemoteFloat, val y: RemoteFloat)

/**
 * Animates a position property.
 *
 * Take a BasePositionProperty (either animated or static) and convert it to a [Point] of
 * RemoteFloats (x, y). If the position is animated, the RemoteFloats will change based on the
 * animation specified in the Lottie Position Property.
 */
@SuppressLint("RestrictedApi")
internal fun animatePosition(
  position: BasePositionProperty,
  animationSettings: LottieSettings,
): Point {
  return when (position) {
    // Static constant position: directly wrap the [x, y] coordinates into RemoteFloats.
    is StaticPositionProperty -> {
      Point(x = position.value.getOrElse(0) { 0f }.rf, y = position.value.getOrElse(1) { 0f }.rf)
    }
    // Keyframed animated position: interpolate [x, y] across keyframes using Bézier easing curves.
    is AnimatedPositionProperty -> {
      if (position.keyframes.isEmpty()) {
        return Point(0f.rf, 0f.rf)
      }
      // Single keyframe: hold static position at that single value.
      if (position.keyframes.size == 1) {
        return Point(
          x = position.keyframes[0].value.getOrElse(0) { 0f }.rf,
          y = position.keyframes[0].value.getOrElse(1) { 0f }.rf,
        )
      }

      val animationSegments = mutableListOf<List<AnimationSegment>>()

      // If the first keyframe starts after frame 0, prepend an initial static segment
      // holding the first keyframe's value from frame 0 until the first keyframe.
      val firstKeyframe = position.keyframes[0]
      if (firstKeyframe.frame != 0f) {
        animationSegments.add(firstKeyframe.value.map { AnimationSegment(0f, it.rf) })
      }

      // Build interpolation segments between adjacent keyframe pairs.
      for (i in 0 until position.keyframes.size - 1) {
        val startKeyframe = position.keyframes[i]
        val endKeyframe = position.keyframes[i + 1]
        val duration = endKeyframe.frame - startKeyframe.frame
        val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame

        // Control point tangents for the cubic Bézier curve, defaulting to linear easing if
        // omitted.
        val outTangent = startKeyframe.outTangent ?: scalarLinearEasingOut
        val inTangent = startKeyframe.inTangent ?: scalarLinearEasingIn

        // Evaluate the cubic Bézier curve to obtain the normalized interpolation factor [0.0, 1.0].
        val currentBezierValue =
          lookupValueInBezier(
            outTangent.x,
            outTangent.y,
            inTangent.x,
            inTangent.y,
            duration,
            frameInAnimation,
          )

        // Linearly interpolate each coordinate (x, y) between the start and end keyframe values.
        val segment =
          startKeyframe.value.mapIndexed { index, value ->
            AnimationSegment(
              startKeyframe.frame,
              lerp(value.rf, endKeyframe.value[index].rf, currentBezierValue),
            )
          }

        animationSegments.add(segment)
      }

      // Chain individual segments together into conditional expressions that resolve
      // the appropriate interpolated value for X and Y based on currentFrame.
      val chainedX = chainAnimation(animationSegments.map { it[0] }, animationSettings.currentFrame)
      val chainedY = chainAnimation(animationSegments.map { it[1] }, animationSettings.currentFrame)

      Point(x = chainedX, y = chainedY)
    }
  }
}

/**
 * Animates a vector property.
 *
 * Take a BaseVectorProperty (either animated or static) and convert it to a List of RemoteFloats.
 * If the vector is animated, the RemoteFloat will change based on the animation specified in the
 * Lottie Vector Property.
 */
@SuppressLint("RestrictedApi")
internal fun animateVector(
  vector: BaseVectorProperty,
  animationSettings: LottieSettings,
): List<RemoteFloat> {
  return when (vector) {
    is StaticVectorProperty -> vector.value.map { it.rf }
    is AnimatedVectorProperty -> {
      if (vector.keyframes.size == 1) {
        return vector.keyframes[0].value.map { it.rf }
      }

      val animationSegments = mutableListOf<List<AnimationSegment>>()

      val firstKeyframe = vector.keyframes[0]
      if (firstKeyframe.frame != 0f) {
        animationSegments.add(firstKeyframe.value.map { AnimationSegment(0f, it.rf) })
      }

      for (i in 0 until vector.keyframes.size - 1) {
        val startKeyframe = vector.keyframes[i]
        val endKeyframe = vector.keyframes[i + 1]
        val duration = endKeyframe.frame - startKeyframe.frame
        val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame
        val outTangent = startKeyframe.outTangent ?: scalarLinearEasingOut
        val inTangent = startKeyframe.inTangent ?: scalarLinearEasingIn
        val currentBezierValue =
          lookupValueInBezier(
            outTangent.x,
            outTangent.y,
            inTangent.x,
            inTangent.y,
            duration,
            frameInAnimation,
          )

        val segment =
          startKeyframe.value.mapIndexed { index, value ->
            AnimationSegment(
              startKeyframe.frame,
              lerp(value.rf, endKeyframe.value[index].rf, currentBezierValue),
            )
          }

        animationSegments.add(segment)
      }

      val vectorSize = animationSegments[0].size
      return (0..<vectorSize).map { index ->
        chainAnimation(animationSegments.map { it[index] }, animationSettings.currentFrame)
      }
    }
  }
}

internal data class RemoteBezierValue(
  val closed: Boolean,
  val inTangents: List<List<Float>>,
  val outTangents: List<List<Float>>,
  val vertices: List<List<Float>>,
)

/**
 * Animates a bezier property.
 *
 * Take a BaseBezierProperty (either animated or static) and convert it to a RemoteBezierValue. If
 * the bezier is animated, the RemoteBezierValue will change based on the animation specified in the
 * Lottie Bezier Property.
 *
 * This is used for path morphing, where either the vertices or control points of beziers used to
 * draw a shape are animated.
 */
@SuppressLint("RestrictedApi")
internal fun animateBezier(
  path: BaseBezierProperty,
  animationSettings: LottieSettings,
): RemoteBezierValue {
  return when (val p = path) {
    is StaticBezierProperty -> {
      return RemoteBezierValue(
        p.value.closed,
        p.value.inTangents.innerMap { it },
        p.value.outTangents.innerMap { it },
        p.value.vertices.innerMap { it },
      )
    }
    is AnimatedBezierProperty -> {
      // TODO: Support delayed start & chained animations for bezier curves
      if (p.keyframes.size == 1) {
        return RemoteBezierValue(
          p.keyframes[0].value[0].closed,
          p.keyframes[0].value[0].inTangents.innerMap { it },
          p.keyframes[0].value[0].outTangents.innerMap { it },
          p.keyframes[0].value[0].vertices.innerMap { it },
        )
      }

      val startKeyFrame = p.keyframes[0]
      val endKeyFrame = p.keyframes[1]

      if (startKeyFrame.frame != 0f) {
        return RemoteBezierValue(
          p.keyframes[0].value[0].closed,
          p.keyframes[0].value[0].inTangents.innerMap { it },
          p.keyframes[0].value[0].outTangents.innerMap { it },
          p.keyframes[0].value[0].vertices.innerMap { it },
        )
      }

      val duration = endKeyFrame.frame - startKeyFrame.frame
      val frameInAnimation = animationSettings.currentFrame - startKeyFrame.frame

      val outTangent = startKeyFrame.outTangent ?: scalarLinearEasingOut
      val inTangent = startKeyFrame.inTangent ?: scalarLinearEasingIn

      val currentBezierValue =
        lookupValueInBezier(
          outTangent.x,
          outTangent.y,
          inTangent.x,
          inTangent.y,
          duration,
          frameInAnimation,
        )

      // TODO: b/442404202 - Support multiple spline segments within a bezier (i.e.
      // startKeyFrame.value.size > 1)
      return RemoteBezierValue(
        startKeyFrame.value[0].closed,
        animateNestedFloatArray(
          startKeyFrame.value[0].inTangents,
          endKeyFrame.value[0].inTangents,
          currentBezierValue,
          duration,
          frameInAnimation,
        ),
        animateNestedFloatArray(
          startKeyFrame.value[0].outTangents,
          endKeyFrame.value[0].outTangents,
          currentBezierValue,
          duration,
          frameInAnimation,
        ),
        animateNestedFloatArray(
          startKeyFrame.value[0].vertices,
          endKeyFrame.value[0].vertices,
          currentBezierValue,
          duration,
          frameInAnimation,
        ),
      )
    }
  }
}

private fun animateNestedFloatArray(
  from: List<List<Float>>,
  to: List<List<Float>>,
  bezierValue: RemoteFloat,
  duration: Float,
  currentFrame: RemoteFloat,
): List<List<Float>> {
  return from.mapIndexed { _outerIndex, outer ->
    outer.mapIndexed { _innerIndex, inner ->
      // TODO: b/442404202 - Actually animate the path!
      // calculateAnimationValue(inner, to[outerIndex][innerIndex], bezierValue)
      inner
    }
  }
}

/**
 * Support keyframed animations (and delayed start animations) by chaining multiple animations
 * together.
 *
 * This recursively builds up a chain of IFELSE operations to select the correct RemoteFloat
 * representing the current segment of the animation.
 */
@SuppressLint("RestrictedApi")
private fun chainAnimation(segments: List<AnimationSegment>, frame: RemoteFloat): RemoteFloat {
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

@SuppressLint("RestrictedApi")
private fun lookupValueInBezier(
  a: Float,
  b: Float,
  c: Float,
  d: Float,
  duration: Float,
  frame: RemoteFloat,
): RemoteFloat {
  val clampedFrame = clamp(value = frame, min = 0.rf, max = duration.rf)
  val progress = if (duration == 0f) 0.rf else clampedFrame / duration.rf
  return cubicEasing(a.rf, b.rf, c.rf, d.rf, progress)
}

private fun BezierValue.toRemote(): RemoteBezierValue {
  return RemoteBezierValue(
    this.closed,
    this.inTangents.innerMap { it },
    this.outTangents.innerMap { it },
    this.vertices.innerMap { it },
  )
}

private fun <T, U> List<List<T>>.innerMap(f: (T) -> U): List<List<U>> = this.map { it.map(f) }

internal val scalarLinearEasingOut = ScalarKeyframeEasing(x = 0f, 0f)
internal val scalarLinearEasingIn = ScalarKeyframeEasing(1f, 1f)

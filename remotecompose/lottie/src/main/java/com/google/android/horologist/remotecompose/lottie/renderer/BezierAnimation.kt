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
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.BaseBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.BezierKeyframe
import com.google.android.horologist.remotecompose.lottie.format.BezierValue
import com.google.android.horologist.remotecompose.lottie.format.StaticBezierProperty

internal data class RemoteBezierValue(
  val closed: Boolean,
  val inTangents: List<List<RemoteFloat>>,
  val outTangents: List<List<RemoteFloat>>,
  val vertices: List<List<RemoteFloat>>,
)

internal data class BezierAnimationSegment(val startFrame: Float, val value: RemoteBezierValue)

/**
 * Animates a bezier property.
 *
 * Take a BaseBezierProperty (either animated or static) and convert it to a List of
 * RemoteBezierValue. If the bezier is animated, the resulting values change based on the animation
 * keyframes. This is used for path morphing.
 */
@SuppressLint("RestrictedApi")
internal fun animateBezier(
  path: BaseBezierProperty,
  animationSettings: LottieSettings,
): List<RemoteBezierValue> {
  return when (path) {
    is StaticBezierProperty -> listOf(path.value.toRemote())
    is AnimatedBezierProperty -> evaluateAnimatedBezier(path, animationSettings)
  }
}

@SuppressLint("RestrictedApi")
private fun evaluateAnimatedBezier(
  path: AnimatedBezierProperty,
  animationSettings: LottieSettings,
): List<RemoteBezierValue> {
  if (path.keyframes.size == 1) {
    return path.keyframes[0].value.map { it.toRemote() }
  }

  val numberOfSplines = path.keyframes[0].value.size
  return (0 until numberOfSplines).map { splineIndex ->
    val segments = buildSplineSegments(path.keyframes, splineIndex, animationSettings)
    chainBezierAnimation(segments, animationSettings.currentFrame)
  }
}

@SuppressLint("RestrictedApi")
private fun buildSplineSegments(
  keyframes: List<BezierKeyframe>,
  splineIndex: Int,
  animationSettings: LottieSettings,
): List<BezierAnimationSegment> {
  val segments = mutableListOf<BezierAnimationSegment>()
  addInitialBezierSegment(keyframes[0], splineIndex, segments)

  for (i in 0 until keyframes.size - 1) {
    val start = keyframes[i]
    val end = keyframes[i + 1]

    val progress =
      evaluateKeyframeProgress(
        start.frame,
        end.frame,
        start.outTangent,
        start.inTangent,
        animationSettings.currentFrame,
      )

    val segmentValue =
      createBezierSegmentValue(start.value[splineIndex], end.value[splineIndex], progress)
    segments.add(BezierAnimationSegment(start.frame, segmentValue))
  }
  return segments
}

private fun addInitialBezierSegment(
  firstKeyframe: BezierKeyframe,
  splineIndex: Int,
  segments: MutableList<BezierAnimationSegment>,
) {
  if (firstKeyframe.frame != 0f) {
    segments.add(BezierAnimationSegment(0f, firstKeyframe.value[splineIndex].toRemote()))
  }
}

private fun createBezierSegmentValue(
  start: BezierValue,
  end: BezierValue,
  progress: RemoteFloat,
): RemoteBezierValue {
  return RemoteBezierValue(
    start.closed,
    animateNestedFloatArray(start.inTangents, end.inTangents, progress),
    animateNestedFloatArray(start.outTangents, end.outTangents, progress),
    animateNestedFloatArray(start.vertices, end.vertices, progress),
  )
}

private fun animateNestedFloatArray(
  from: List<List<Float>>,
  to: List<List<Float>>,
  bezierValue: RemoteFloat,
): List<List<RemoteFloat>> {
  return from.mapIndexed { outerIndex, outer ->
    outer.mapIndexed { innerIndex, inner ->
      lerp(inner.rf, to[outerIndex][innerIndex].rf, bezierValue)
    }
  }
}

/**
 * Support keyframed animations for bezier curves by chaining multiple segments resolving the
 * correct RemoteFloat matrices for the current segment frame.
 */
@SuppressLint("RestrictedApi")
private fun chainBezierAnimation(
  segments: List<BezierAnimationSegment>,
  frame: RemoteFloat,
): RemoteBezierValue {
  if (segments.size == 1) return segments[0].value
  val first = segments[0]
  val threshold = segments[1].startFrame.rf
  val rest = chainBezierAnimation(segments.subList(1, segments.size), frame)

  return RemoteBezierValue(
    first.value.closed,
    first.value.inTangents.innerMapIndexed { o, i, inner ->
      selectIfLt(frame, threshold, inner, rest.inTangents[o][i])
    },
    first.value.outTangents.innerMapIndexed { o, i, inner ->
      selectIfLt(frame, threshold, inner, rest.outTangents[o][i])
    },
    first.value.vertices.innerMapIndexed { o, i, inner ->
      selectIfLt(frame, threshold, inner, rest.vertices[o][i])
    },
  )
}

private fun BezierValue.toRemote(): RemoteBezierValue {
  return RemoteBezierValue(
    this.closed,
    this.inTangents.innerMap { it.rf },
    this.outTangents.innerMap { it.rf },
    this.vertices.innerMap { it.rf },
  )
}

private inline fun <T, U> List<List<T>>.innerMapIndexed(
  f: (outerIdx: Int, innerIdx: Int, item: T) -> U
): List<List<U>> = this.mapIndexed { o, outer -> outer.mapIndexed { i, inner -> f(o, i, inner) } }

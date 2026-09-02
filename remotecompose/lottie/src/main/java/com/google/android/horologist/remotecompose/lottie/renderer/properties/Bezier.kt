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

package com.google.android.horologist.remotecompose.lottie.renderer.properties

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.format.values.Point as FormatPoint
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut

internal data class RemoteBezierValue(
  val closed: Boolean,
  val inTangents: List<Point>,
  val outTangents: List<Point>,
  val vertices: List<Point>,
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
      return p.value.toRemote()
    }
    is AnimatedBezierProperty -> {
      // TODO: Support delayed start & chained animations for bezier curves
      if (p.keyframes.size == 1) {
        return p.keyframes[0].value[0].toRemote()
      }

      val startKeyFrame = p.keyframes[0]
      val endKeyFrame = p.keyframes[1]

      if (startKeyFrame.frame != 0f) {
        return p.keyframes[0].value[0].toRemote()
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
        animatePoints(
          startKeyFrame.value[0].inTangents.map { it.toRemote() },
          endKeyFrame.value[0].inTangents.map { it.toRemote() },
          currentBezierValue,
          duration,
          frameInAnimation,
        ),
        animatePoints(
          startKeyFrame.value[0].outTangents.map { it.toRemote() },
          endKeyFrame.value[0].outTangents.map { it.toRemote() },
          currentBezierValue,
          duration,
          frameInAnimation,
        ),
        animatePoints(
          startKeyFrame.value[0].vertices.map { it.toRemote() },
          endKeyFrame.value[0].vertices.map { it.toRemote() },
          currentBezierValue,
          duration,
          frameInAnimation,
        ),
      )
    }
  }
}

private fun animatePoints(
  from: List<Point>,
  to: List<Point>,
  bezierValue: RemoteFloat,
  duration: Float,
  currentFrame: RemoteFloat,
): List<Point> {
  return from.mapIndexed { _index, point ->
    // TODO: b/442404202 - Actually animate the path!
    // calculateAnimationValue(point, to[index], bezierValue)
    point
  }
}

internal fun BezierValue.toRemote(): RemoteBezierValue {
  return RemoteBezierValue(
    this.closed,
    this.inTangents.map { it.toRemote() },
    this.outTangents.map { it.toRemote() },
    this.vertices.map { it.toRemote() },
  )
}

internal fun FormatPoint.toRemote(): Point = Point(this.x.rf, this.y.rf)

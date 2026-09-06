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
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut

/**
 * Animates a bezier property.
 *
 * Take a [BaseBezierProperty] (either animated or static) and convert it to a [BezierValue]. If the
 * bezier is animated, the [BezierValue] will change based on the animation specified in the Lottie
 * Bezier Property.
 *
 * This is used for path morphing, where either the vertices or control points of beziers used to
 * draw a shape are animated.
 */
@SuppressLint("RestrictedApi")
internal fun animateBezier(
  path: BaseBezierProperty,
  animationSettings: LottieSettings,
): BezierValue {
  return when (path) {
    is StaticBezierProperty -> path.value
    is AnimatedBezierProperty -> {
      val keyframes = path.keyframes
      if (keyframes.isEmpty()) {
        return BezierValue(
          closed = false.rb,
          inTangents = emptyList(),
          outTangents = emptyList(),
          vertices = emptyList(),
        )
      }
      if (keyframes.size == 1) {
        return keyframes[0].value[0]
      }

      val firstKeyframe = keyframes[0]
      val firstValue = firstKeyframe.value[0]

      val animationSegments = mutableListOf<Pair<Float, BezierValue>>()

      if (firstKeyframe.frame.constantValue > 0f) {
        animationSegments.add(0f to firstValue)
      }

      for (i in 0 until keyframes.size - 1) {
        val startKeyframe = keyframes[i]
        val endKeyframe = keyframes[i + 1]
        val duration = endKeyframe.frame.constantValue - startKeyframe.frame.constantValue
        val startValue = startKeyframe.value[0]
        val endValue = endKeyframe.value[0]

        val segmentValue =
          if (startKeyframe.hold.constantValue || duration <= 0f) {
            startValue
          } else {
            val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame
            val currentBezierValue =
              if (startKeyframe.outTangent != null || startKeyframe.inTangent != null) {
                val outTangent = startKeyframe.outTangent ?: scalarLinearEasingOut
                val inTangent = startKeyframe.inTangent ?: scalarLinearEasingIn
                lookupValueInBezier(
                  outTangent.x,
                  outTangent.y,
                  inTangent.x,
                  inTangent.y,
                  duration,
                  frameInAnimation,
                )
              } else {
                clamp(frameInAnimation / duration.rf, 0f.rf, 1f.rf)
              }

            BezierValue(
              closed = startValue.closed,
              inTangents =
                interpolatePoints(startValue.inTangents, endValue.inTangents, currentBezierValue),
              outTangents =
                interpolatePoints(startValue.outTangents, endValue.outTangents, currentBezierValue),
              vertices =
                interpolatePoints(startValue.vertices, endValue.vertices, currentBezierValue),
            )
          }

        animationSegments.add(startKeyframe.frame.constantValue to segmentValue)
      }

      val lastKeyframe = keyframes.last()
      animationSegments.add(lastKeyframe.frame.constantValue to lastKeyframe.value[0])

      val chainedVertices =
        chainPoints(
          animationSegments.map { it.first to it.second.vertices },
          animationSettings.currentFrame,
        )
      val chainedInTangents =
        chainPoints(
          animationSegments.map { it.first to it.second.inTangents },
          animationSettings.currentFrame,
        )
      val chainedOutTangents =
        chainPoints(
          animationSegments.map { it.first to it.second.outTangents },
          animationSettings.currentFrame,
        )

      BezierValue(
        closed = firstValue.closed,
        inTangents = chainedInTangents,
        outTangents = chainedOutTangents,
        vertices = chainedVertices,
      )
    }
  }
}

private fun chainPoints(segments: List<Pair<Float, List<Point>>>, frame: RemoteFloat): List<Point> {
  if (segments.isEmpty()) return emptyList()
  val numPoints = segments[0].second.size
  return (0 until numPoints).map { pointIndex ->
    val xSegments = segments.map { (startFrame, points) ->
      AnimationSegment(startFrame, points.getOrElse(pointIndex) { Point(0f.rf, 0f.rf) }.x)
    }
    val ySegments = segments.map { (startFrame, points) ->
      AnimationSegment(startFrame, points.getOrElse(pointIndex) { Point(0f.rf, 0f.rf) }.y)
    }
    Point(x = chainAnimation(xSegments, frame), y = chainAnimation(ySegments, frame))
  }
}

private fun interpolatePoints(
  from: List<Point>,
  to: List<Point>,
  progress: RemoteFloat,
): List<Point> {
  return from.mapIndexed { index, point ->
    val toPoint = to.getOrElse(index) { point }
    Point(x = lerp(point.x, toPoint.x, progress), y = lerp(point.y, toPoint.y, progress))
  }
}

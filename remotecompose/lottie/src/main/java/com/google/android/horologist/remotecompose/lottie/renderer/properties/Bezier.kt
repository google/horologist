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
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingIn
import com.google.android.horologist.remotecompose.lottie.renderer.scalarLinearEasingOut

internal data class RemoteBezierValue(
  val closed: Boolean,
  val inTangents: List<List<RemoteFloat>>,
  val outTangents: List<List<RemoteFloat>>,
  val vertices: List<List<RemoteFloat>>,
  val topology: RemoteBezierTopology? = null,
  /** Binary contour presence, independent of collapsed recording capacity or paint opacity. */
  val visibility: RemoteFloat = 1f.rf,
)

/**
 * Logical vertices and edges within a fixed-capacity path. A collapsed recording slot is not an
 * authored corner: centroid-based modifiers must not count it, or introduce controls on its empty
 * edge. Segment j runs from vertex j to j+1 (wrapping for a closed path).
 */
internal data class RemoteBezierTopology(
  val vertices: List<RemoteFloat>,
  val segments: List<RemoteFloat>,
)

internal data class BezierAnimationSegment(val startFrame: Float, val value: RemoteBezierValue)

internal fun BezierValue.toRemote(): RemoteBezierValue {
  return RemoteBezierValue(
    closed = closed.constantValue,
    inTangents = inTangents.map { point -> listOf(point.x, point.y) },
    outTangents = outTangents.map { point -> listOf(point.x, point.y) },
    vertices = vertices.map { point -> listOf(point.x, point.y) },
  )
}

/**
 * Animates a bezier (shape) property.
 *
 * Takes a [BaseBezierProperty] (either static or animated) and resolves it to a list of
 * [RemoteBezierValue] subpaths. Supports keyframed transitions with cubic Bézier easing curves,
 * hold keyframes, and delayed starts.
 */
@SuppressLint("RestrictedApi")
internal fun animateBezier(
  path: BaseBezierProperty,
  animationSettings: LottieSettings,
): List<RemoteBezierValue> {
  return when (path) {
    is StaticBezierProperty -> listOf(path.value.toRemote())
    is AnimatedBezierProperty -> {
      if (path.keyframes.isEmpty()) {
        return emptyList()
      }
      if (path.keyframes.size == 1) {
        return path.keyframes[0].value.map { it.toRemote() }
      }

      val firstKeyframe = path.keyframes[0]
      val subpathCount = firstKeyframe.value.size
      if (subpathCount == 0) {
        return emptyList()
      }

      (0 until subpathCount).map { subpathIndex ->
        val animationSegments = mutableListOf<BezierAnimationSegment>()
        val firstSubpath = firstKeyframe.value[subpathIndex]

        if (firstKeyframe.frame.constantValue != 0f) {
          animationSegments.add(BezierAnimationSegment(0f, firstSubpath.toRemote()))
        }

        for (i in 0 until path.keyframes.size - 1) {
          val startKeyframe = path.keyframes[i]
          val endKeyframe = path.keyframes[i + 1]
          val duration = endKeyframe.frame.constantValue - startKeyframe.frame.constantValue
          val frameInAnimation = animationSettings.currentFrame - startKeyframe.frame

          val startSubpath = startKeyframe.value.getOrElse(subpathIndex) { firstSubpath }
          val endSubpath = endKeyframe.value.getOrElse(subpathIndex) { startSubpath }

          val segmentValue =
            if (startKeyframe.hold.constantValue) {
              RemoteBezierValue(
                closed = startSubpath.closed.constantValue,
                inTangents =
                  startSubpath.inTangents.mapIndexed { v, point ->
                    listOf(point.x, point.y).mapIndexed { c, startCoord ->
                      val endCoord = endSubpath.inTangents.getOrNull(v)?.getOrNull(c) ?: startCoord
                      if (sameConstantCoordinate(startCoord, endCoord)) startCoord
                      else selectIfLt(frameInAnimation, duration.rf, startCoord, endCoord)
                    }
                  },
                outTangents =
                  startSubpath.outTangents.mapIndexed { v, point ->
                    listOf(point.x, point.y).mapIndexed { c, startCoord ->
                      val endCoord = endSubpath.outTangents.getOrNull(v)?.getOrNull(c) ?: startCoord
                      if (sameConstantCoordinate(startCoord, endCoord)) startCoord
                      else selectIfLt(frameInAnimation, duration.rf, startCoord, endCoord)
                    }
                  },
                vertices =
                  startSubpath.vertices.mapIndexed { v, point ->
                    listOf(point.x, point.y).mapIndexed { c, startCoord ->
                      val endCoord = endSubpath.vertices.getOrNull(v)?.getOrNull(c) ?: startCoord
                      if (sameConstantCoordinate(startCoord, endCoord)) startCoord
                      else selectIfLt(frameInAnimation, duration.rf, startCoord, endCoord)
                    }
                  },
              )
            } else {
              val outTangent = startKeyframe.outTangent ?: scalarLinearEasingOut
              val inTangent = startKeyframe.inTangent ?: scalarLinearEasingIn

              val currentBezierValue =
                lookupValueInBezier(
                  outTangent.x.constantValue,
                  outTangent.y.constantValue,
                  inTangent.x.constantValue,
                  inTangent.y.constantValue,
                  duration,
                  frameInAnimation,
                )

              RemoteBezierValue(
                closed = startSubpath.closed.constantValue,
                inTangents =
                  startSubpath.inTangents.mapIndexed { v, point ->
                    listOf(point.x, point.y).mapIndexed { c, startCoord ->
                      val endCoord = endSubpath.inTangents.getOrNull(v)?.getOrNull(c) ?: startCoord
                      if (sameConstantCoordinate(startCoord, endCoord)) startCoord
                      else lerp(startCoord, endCoord, currentBezierValue)
                    }
                  },
                outTangents =
                  startSubpath.outTangents.mapIndexed { v, point ->
                    listOf(point.x, point.y).mapIndexed { c, startCoord ->
                      val endCoord = endSubpath.outTangents.getOrNull(v)?.getOrNull(c) ?: startCoord
                      if (sameConstantCoordinate(startCoord, endCoord)) startCoord
                      else lerp(startCoord, endCoord, currentBezierValue)
                    }
                  },
                vertices =
                  startSubpath.vertices.mapIndexed { v, point ->
                    listOf(point.x, point.y).mapIndexed { c, startCoord ->
                      val endCoord = endSubpath.vertices.getOrNull(v)?.getOrNull(c) ?: startCoord
                      if (sameConstantCoordinate(startCoord, endCoord)) startCoord
                      else lerp(startCoord, endCoord, currentBezierValue)
                    }
                  },
              )
            }

          animationSegments.add(
            BezierAnimationSegment(startKeyframe.frame.constantValue, segmentValue)
          )
        }

        chainBezierAnimation(animationSegments, animationSettings.currentFrame)
      }
    }
  }
}

/**
 * Support keyframed bezier animations (and delayed start animations) by chaining multiple animation
 * segments together across timeline thresholds.
 */
@SuppressLint("RestrictedApi")
private fun chainBezierAnimation(
  segments: List<BezierAnimationSegment>,
  frame: RemoteFloat,
): RemoteBezierValue {
  if (segments.size == 1) {
    return segments[0].value
  }

  val firstSegment = segments[0]
  val remainingChained = chainBezierAnimation(segments.subList(1, segments.size), frame)
  val nextStartFrame = segments[1].startFrame.rf

  return RemoteBezierValue(
    closed = firstSegment.value.closed,
    inTangents =
      firstSegment.value.inTangents.mapIndexed { v, point ->
        point.mapIndexed { c, coordVal ->
          val remainingVal = remainingChained.inTangents.getOrNull(v)?.getOrNull(c) ?: coordVal
          if (sameConstantCoordinate(coordVal, remainingVal)) coordVal
          else selectIfLt(frame, nextStartFrame, coordVal, remainingVal)
        }
      },
    outTangents =
      firstSegment.value.outTangents.mapIndexed { v, point ->
        point.mapIndexed { c, coordVal ->
          val remainingVal = remainingChained.outTangents.getOrNull(v)?.getOrNull(c) ?: coordVal
          if (sameConstantCoordinate(coordVal, remainingVal)) coordVal
          else selectIfLt(frame, nextStartFrame, coordVal, remainingVal)
        }
      },
    vertices =
      firstSegment.value.vertices.mapIndexed { v, point ->
        point.mapIndexed { c, coordVal ->
          val remainingVal = remainingChained.vertices.getOrNull(v)?.getOrNull(c) ?: coordVal
          if (sameConstantCoordinate(coordVal, remainingVal)) coordVal
          else selectIfLt(frame, nextStartFrame, coordVal, remainingVal)
        }
      },
  )
}

/** Keep invariant controls recognizable to topology-dependent geometry modifiers. */
@SuppressLint("RestrictedApi")
private fun sameConstantCoordinate(a: RemoteFloat, b: RemoteFloat): Boolean =
  a.constantValueOrNull?.let { it == b.constantValueOrNull } == true

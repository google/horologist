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
import androidx.compose.remote.creation.compose.state.RemoteFloatArray
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.CubicSegment
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.Point as CurvePoint

private const val SPATIAL_SAMPLES = 256

/**
 * Traverses a constant spatial cubic by eased arc-length progress. Both tangents belong to the
 * starting keyframe: outgoing is relative to [start], incoming is relative to [end]. The bounded
 * length/position tables approximate the curve while remaining live through [progress]. Overshoot
 * extends along the endpoint tangent; a zero-length path stays at its start without division.
 */
@SuppressLint("RestrictedApi")
internal fun sampleSpatialPosition(
  start: Point,
  end: Point,
  outgoing: Point?,
  incoming: Point?,
  progress: RemoteFloat,
): Point {
  val p0 = CurvePoint(start.x.constantValue, start.y.constantValue)
  val p3 = CurvePoint(end.x.constantValue, end.y.constantValue)
  val p1 = p0 + CurvePoint(outgoing?.x?.constantValue ?: 0f, outgoing?.y?.constantValue ?: 0f)
  val p2 = p3 + CurvePoint(incoming?.x?.constantValue ?: 0f, incoming?.y?.constantValue ?: 0f)
  val curve = CubicSegment(p0, p1, p2, p3)
  val lengths = curve.computeLengthTable(SPATIAL_SAMPLES)
  val length = lengths.last()
  if (length == 0f) return start

  val points =
    (0..SPATIAL_SAMPLES).map { index ->
      curve.pointAt(curve.tAtDistance(length * index / SPATIAL_SAMPLES, lengths))
    }
  val sampleIndex = clamp(progress, 0f.rf, 1f.rf) * SPATIAL_SAMPLES.rf
  val lower = floor(sampleIndex)
  val upper = min(lower + 1f, SPATIAL_SAMPLES.rf)
  fun direction(vararg candidates: CurvePoint): CurvePoint {
    val delta = candidates.firstOrNull { it.x != 0f || it.y != 0f } ?: return CurvePoint(0f, 0f)
    return delta * (1f / delta.distanceTo(CurvePoint(0f, 0f)))
  }
  val startDirection = direction(p1 - p0, p2 - p0, p3 - p0)
  val endDirection = direction(p3 - p2, p3 - p1, p3 - p0)

  fun coordinate(values: List<Float>, startTangent: Float, endTangent: Float): RemoteFloat {
    val table = RemoteFloatArray(values.map { it.rf })
    val onCurve = lerp(table[lower], table[upper], sampleIndex - lower)
    val before = values.first().rf + progress * (length * startTangent)
    val after = values.last().rf + (progress - 1f) * (length * endTangent)
    return selectIfLt(progress, 0f.rf, before, selectIfLt(1f.rf, progress, after, onCurve))
  }
  return Point(
    coordinate(points.map { it.x }, startDirection.x, endDirection.x),
    coordinate(points.map { it.y }, startDirection.y, endDirection.y),
  )
}

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

@file:Suppress("RestrictedApi")

package com.google.android.horologist.remotecompose.lottie.renderer.shapes

import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.abs
import androidx.compose.remote.creation.compose.state.atan2
import androidx.compose.remote.creation.compose.state.cos
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import androidx.compose.remote.creation.compose.state.selectIfLe
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.remote.creation.compose.state.sin
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStar
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStarType
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedScalarProperty
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierTopology
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import kotlin.math.PI
import kotlin.math.ceil

/** Conservative capacity from the keyframe/easing convex hull, not sampled animation frames. */
internal fun polystarCapacity(star: PolyStar): Int {
  val property =
    star.points as? AnimatedScalarProperty
      ?: error("Polystar point expressions require finite keyframe bounds")
  val values = property.keyframes.map { it.value.constantValue }
  val easing =
    property.keyframes
      .flatMap { key -> listOfNotNull(key.inTangent, key.outTangent).flatMap { it.yValues } }
      .map { it.constantValue }
  require(
    values.isNotEmpty() && values.all { it.isFinite() && it >= 0f } && easing.all { it.isFinite() }
  ) {
    "Polystar point count must be finite and nonnegative"
  }
  var maximum = values.max()
  for ((start, end) in property.keyframes.zipWithNext()) {
    if (start.hold.constantValue) continue
    val controls =
      listOf(0f, 1f) +
        listOfNotNull(start.inTangent, start.outTangent)
          .flatMap { it.yValues }
          .map { it.constantValue }
    val delta = end.value.constantValue - start.value.constantValue
    maximum =
      maxOf(
        maximum,
        start.value.constantValue + delta * controls.min(),
        start.value.constantValue + delta * controls.max(),
      )
  }
  require(maximum <= 1024f) { "Polystar requires more than 1024 recorded points" }
  return ceil(maximum.toDouble()).toInt().coerceAtLeast(1)
}

private data class StarPoint(val x: RemoteFloat, val y: RemoteFloat) {
  operator fun times(scale: RemoteFloat) = StarPoint(x * scale, y * scale)

  fun values() = listOf(x, y)
}

private fun chooseStar(flag: RemoteFloat, yes: StarPoint, no: StarPoint) =
  StarPoint(selectIfGe(flag, 1f.rf, yes.x, no.x), selectIfGe(flag, 1f.rf, yes.y, no.y))

/** Fixed-capacity live topology; only the first collapsed slot owns the closing control. */
internal fun createLivePolystar(
  star: PolyStar,
  settings: LottieSettings,
  points: RemoteFloat,
  positionX: RemoteFloat,
  positionY: RemoteFloat,
): RemoteBezierValue {
  val isStar = star.starType == PolyStarType.Star
  val capacity = polystarCapacity(star) * if (isStar) 2 else 1
  val p = max(0f.rf, points)
  val fraction = p - floor(p)
  val partial = selectIfLe(fraction, 0f.rf, 0f.rf, 1f.rf)
  val count = if (isStar) (floor(p) + partial) * 2f else floor(p)
  val enabled = selectIfGe(count, (if (isStar) 1f else 3f).rf, 1f.rf, 0f.rf)
  val outer = animateScalar(star.outerRadius, settings)
  val inner = star.innerRadius?.let { animateScalar(it, settings) } ?: 0f.rf
  val outerRound = animateScalar(star.outerRoundness, settings) / 100f
  val innerRound = star.innerRoundness?.let { animateScalar(it, settings) / 100f } ?: 0f.rf
  val rotation = (animateScalar(star.rotation, settings) - 90f) * (PI.toFloat() / 180f)
  // lottie-android reverses stars but ignores polygon direction; preserve that reference contract.
  val direction = if (isStar && star.direction == 3) -1f else 1f
  val angle = (2f * PI.toFloat() * direction).rf / max(if (isStar) p else count, 0.000001f.rf)
  val half = angle / 2f
  val startAngle =
    rotation + if (isStar) selectIfGe(partial, 1f.rf, half * (1f.rf - fraction), 0f.rf) else 0f.rf
  val startRadius =
    if (isStar) selectIfGe(partial, 1f.rf, inner + (outer - inner) * fraction, outer) else outer
  fun polar(radius: RemoteFloat, theta: RemoteFloat) =
    StarPoint(radius * cos(theta), radius * sin(theta))
  val zero = StarPoint(0f.rf, 0f.rf)
  val first = polar(startRadius, startAngle) * enabled
  fun control(point: StarPoint, radius: RemoteFloat, rounding: RemoteFloat): StarPoint {
    val theta = atan2(point.y, point.x) - PI.toFloat() / 2f
    return polar(radius * rounding * (if (isStar) 0.47829f else 0.25f), theta) * enabled
  }
  val partialScale = if (isStar) selectIfGe(partial, 1f.rf, fraction, 1f.rf) else 1f.rf
  val firstControl = control(first, outer, outerRound) * partialScale
  val vertices = ArrayList<List<RemoteFloat>>(capacity)
  val incoming = ArrayList<List<RemoteFloat>>(capacity)
  val outgoing = ArrayList<List<RemoteFloat>>(capacity)
  val unrounded =
    outerRound.constantValueOrNull == 0f && (!isStar || innerRound.constantValueOrNull == 0f)
  for (index in 0 until capacity) {
    val j = index.toFloat().rf
    val active = selectIfLt(j, count, 1f.rf, 0f.rf) * enabled
    val firstCollapsed = selectIfLe(abs(j - count), 0f.rf, 1f.rf, 0f.rf) * enabled
    val radius = if (isStar && index % 2 == 1) inner else outer
    val roundness = if (isStar && index % 2 == 1) innerRound else outerRound
    val theta =
      if (isStar) {
        startAngle +
          selectIfGe(partial, 1f.rf, half * fraction, half) +
          half * (index - 1).toFloat()
      } else startAngle + angle * index.toFloat()
    val current = if (index == 0) first else polar(radius, theta)
    val point = chooseStar(active, current, first)
    val handle = if (index == 0) firstControl else control(current, radius, roundness)
    val incomingHandle =
      if (index == 0) {
        chooseStar(selectIfGe(count, capacity.toFloat().rf, 1f.rf, 0f.rf), firstControl, zero)
      } else chooseStar(active, handle, chooseStar(firstCollapsed, firstControl, zero))
    vertices.add(listOf(point.x + positionX, point.y + positionY))
    // Keep analytically zero handles literal: otherwise 0 * live trigonometry sends straight
    // polygons through the much larger curved-offset solver and introduces degenerate joins.
    incoming.add(if (unrounded) zero.values() else incomingHandle.values())
    outgoing.add(
      if (unrounded) zero.values() else chooseStar(active, handle * (-1f).rf, zero).values()
    )
  }
  val active = List(capacity) { selectIfLt(it.toFloat().rf, count, 1f.rf, 0f.rf) * enabled }
  return RemoteBezierValue(true, incoming, outgoing, vertices, RemoteBezierTopology(active, active))
}

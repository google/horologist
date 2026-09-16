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
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import androidx.compose.remote.creation.compose.state.selectIfLe
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.remote.creation.compose.state.sqrt
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.ZigZag
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedScalarProperty
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.StyledShapes
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar

/** Live zigzag geometry, following lottie-web's corner/normal and cubic-parameter conventions. */
internal fun evaluateZigZag(
  shapes: List<RemoteShape>,
  zigZag: ZigZag,
  animationSettings: LottieSettings,
): List<RemoteShape> {
  if (zigZag.hidden?.constantValue == true || shapes.isEmpty()) return shapes
  val size = animateScalar(zigZag.size, animationSettings)
  require(size.constantValueOrNull?.isFinite() != false) { "Invalid ZigZag size" }
  if (size.constantValueOrNull == 0f) return shapes
  val ridges = animateScalar(zigZag.ridgesPerSegment, animationSettings)
  val capacity = zigZagCapacity(zigZag, ridges)
  val frequency = max(0f.rf, floor(ridges + 0.5f))
  val pointType = animateScalar(zigZag.pointType, animationSettings)
  require(pointType.constantValueOrNull?.isFinite() != false) { "Invalid ZigZag point type" }
  return shapes.map { shape ->
    when (shape) {
      is RemoteBooleanPath -> error("ZigZag after a live boolean merge is not yet supported")
      is RemoteLottiePath -> {
        val source = shape.materializeTrim()
        source.withPath(source.path.map { applyZigZag(it, size, frequency, capacity, pointType) })
      }
      is RemoteGroup ->
        RemoteGroup(
          shape.childShapes.map {
            StyledShapes(evaluateZigZag(it.shapes, zigZag, animationSettings), it.style)
          },
          shape.animationSettings,
          shape.transform,
          shape.opacityMultiplier,
        )
      else -> shape
    }
  }
}

/** A conservative keyframe/easing bound; values remain live and are never sampled into frames. */
private fun zigZagCapacity(zigZag: ZigZag, value: RemoteFloat): Int {
  val maximum =
    value.constantValueOrNull?.also { require(it.isFinite()) { "Invalid ZigZag ridges" } }
      ?: run {
        val property =
          zigZag.ridgesPerSegment as? AnimatedScalarProperty
            ?: error("ZigZag ridge expressions require finite keyframe bounds")
        val values = property.keyframes.map { it.value.constantValue }
        val easing =
          property.keyframes
            .flatMap {
              listOfNotNull(it.inTangent, it.outTangent).flatMap { tangent -> tangent.yValues }
            }
            .map { it.constantValue }
        require(values.isNotEmpty() && (values + easing).all { it.isFinite() }) {
          "Invalid ZigZag ridges"
        }
        val extent = easing.maxOfOrNull { kotlin.math.abs(it) }?.coerceAtLeast(1f) ?: 1f
        values.max() + (values.max() - values.min()) * extent
      }
  require(maximum <= 1024f) { "ZigZag requires more than 1024 recorded ridges per segment" }
  return kotlin.math.floor(maximum + 0.5f).toInt().coerceAtLeast(0)
}

private data class ZigPoint(val x: RemoteFloat, val y: RemoteFloat) {
  operator fun plus(other: ZigPoint) = ZigPoint(x + other.x, y + other.y)

  operator fun minus(other: ZigPoint) = ZigPoint(x - other.x, y - other.y)

  operator fun times(scale: RemoteFloat) = ZigPoint(x * scale, y * scale)

  fun length() = sqrt(x * x + y * y)

  fun values() = listOf(x, y)
}

private val zigZero
  get() = ZigPoint(0f.rf, 0f.rf)

private data class ZigVertex(val point: ZigPoint, val incoming: ZigPoint, val outgoing: ZigPoint)

private fun choose(condition: RemoteFloat, yes: ZigPoint, no: ZigPoint) =
  ZigPoint(selectIfGe(condition, 1f.rf, yes.x, no.x), selectIfGe(condition, 1f.rf, yes.y, no.y))

private fun normal(delta: ZigPoint): ZigPoint {
  val length = delta.length()
  val safe = max(length, 0.000001f.rf)
  // atan2(0, 0) in the reference gives angle=0, hence normal=(1, 0).
  val nonzero = selectIfGe(length, 0.000001f.rf, 1f.rf, 0f.rf)
  return choose(nonzero, ZigPoint(delta.y / safe, (0f.rf - delta.x) / safe), ZigPoint(1f.rf, 0f.rf))
}

private fun samePoint(control: ZigPoint, endpoint: ZigPoint): RemoteFloat {
  fun equal(a: RemoteFloat, b: RemoteFloat) =
    selectIfLe(abs(a - b) * 100000f, min(abs(a), abs(b)), 1f.rf, 0f.rf)
  return equal(control.x, endpoint.x) * equal(control.y, endpoint.y)
}

private fun applyZigZag(
  path: RemoteBezierValue,
  amplitude: RemoteFloat,
  frequency: RemoteFloat,
  capacity: Int,
  pointType: RemoteFloat,
): RemoteBezierValue {
  val count = path.vertices.size
  if (count == 0 || (!path.closed && count == 1)) {
    return RemoteBezierValue(
      path.closed,
      emptyList(),
      emptyList(),
      emptyList(),
      visibility = path.visibility,
    )
  }
  val segments = if (path.closed) count else count - 1
  require(segments.toLong() * (capacity + 1) + 1 <= 65536) {
    "ZigZag exceeds 65536 recorded vertices"
  }
  fun point(values: List<List<RemoteFloat>>, index: Int): ZigPoint =
    values.getOrNull(index)?.let { ZigPoint(it.getOrElse(0) { 0f.rf }, it.getOrElse(1) { 0f.rf }) }
      ?: zigZero
  fun vertex(index: Int) = point(path.vertices, (index + count) % count)
  val zeroAmplitude = selectIfLe(abs(amplitude), 0f.rf, 1f.rf, 0f.rf)
  val effectiveFrequency = selectIfGe(zeroAmplitude, 1f.rf, 0f.rf, frequency)
  val divisor = effectiveFrequency + 1f
  // The reference uses exact equality, not rounding: intermediate values are corner points.
  val smooth = selectIfLe(abs(pointType - 2f), 0f.rf, 1f.rf, 0f.rf)
  fun corner(index: Int, direction: RemoteFloat): ZigVertex {
    val current = vertex(index)
    val previous = vertex(index - 1)
    val next = vertex(index + 1)
    val n = normal(next - previous)
    val tangent = ZigPoint(0f.rf - n.y, n.x)
    return ZigVertex(
      choose(zeroAmplitude, current, current + n * (direction * amplitude)),
      choose(
        zeroAmplitude,
        point(path.inTangents, index % count),
        choose(
          smooth,
          tangent * ((0f.rf - (current - previous).length()) / (divisor * 2f)),
          zigZero,
        ),
      ),
      choose(
        zeroAmplitude,
        point(path.outTangents, index % count),
        choose(smooth, tangent * ((next - current).length() / (divisor * 2f)), zigZero),
      ),
    )
  }
  val output = mutableListOf<ZigVertex>()
  var direction = (-1f).rf
  val first = corner(0, direction)
  output.add(
    if (path.closed) first.copy(incoming = choose(zeroAmplitude, zigZero, first.incoming))
    else first
  )
  val parity = 1f.rf - (effectiveFrequency - floor(effectiveFrequency / 2f) * 2f) * 2f
  for (segment in 0 until segments) {
    val next = (segment + 1) % count
    val p0 = vertex(segment)
    val p3 = vertex(next)
    val delta = p3 - p0
    val control1 = p0 + point(path.outTangents, segment)
    val control2 = p3 + point(path.inTangents, next)
    val linear1 = samePoint(control1, p0)
    val linear2 = samePoint(control2, p3)
    val straight = linear1 * linear2
    val p1 = choose(linear1, p0 + delta * (1f / 3f).rf, control1)
    val p2 = choose(linear2, p0 + delta * (2f / 3f).rf, control2)
    val endDirection = (0f.rf - direction) * parity
    val end = corner(next, endDirection)
    for (j in 1..capacity) {
      val t = min(j.toFloat().rf, divisor) / divisor
      val u = 1f.rf - t
      val center =
        choose(
          straight,
          p0 + delta * t,
          p0 * (u * u * u) + p1 * (3f.rf * u * u * t) + p2 * (3f.rf * u * t * t) + p3 * (t * t * t),
        )
      val derivative =
        choose(
          straight,
          delta,
          (p1 - p0) * (3f.rf * u * u) + (p2 - p1) * (6f.rf * u * t) + (p3 - p2) * (3f.rf * t * t),
        )
      val n = normal(derivative)
      val handle =
        choose(smooth, ZigPoint(0f.rf - n.y, n.x) * (delta.length() / (divisor * 2f)), zigZero)
      val ridgeDirection = direction * (if (j % 2 == 1) -1f else 1f).rf
      val active = selectIfLe(j.toFloat().rf, effectiveFrequency, 1f.rf, 0f.rf)
      val firstCollapsed = selectIfLt(j.toFloat().rf, effectiveFrequency + 2f, 1f.rf, 0f.rf)
      output.add(
        ZigVertex(
          choose(active, center + n * (ridgeDirection * amplitude), end.point),
          choose(active, handle * (-1f).rf, choose(firstCollapsed, end.incoming, zigZero)),
          choose(active, handle, zigZero),
        )
      )
    }
    // Inactive topology slots collapse at the endpoint. Only the first owns the incoming
    // handle and only the final one owns the outgoing handle, avoiding hidden cubic loops.
    output.add(
      end.copy(
        incoming =
          choose(
            selectIfGe(effectiveFrequency, capacity.toFloat().rf, 1f.rf, 0f.rf),
            end.incoming,
            zigZero,
          ),
        outgoing =
          if (path.closed && segment == segments - 1) choose(zeroAmplitude, zigZero, end.outgoing)
          else end.outgoing,
      )
    )
    direction = endDirection
  }
  return RemoteBezierValue(
    path.closed,
    output.map { it.incoming.values() },
    output.map { it.outgoing.values() },
    output.map { it.point.values() },
    visibility = path.visibility,
  )
}

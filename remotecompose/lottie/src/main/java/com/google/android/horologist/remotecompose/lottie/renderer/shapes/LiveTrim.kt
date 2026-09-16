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
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import androidx.compose.remote.creation.compose.state.selectIfLe
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.remote.creation.compose.state.sqrt
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemotePathTrim
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierTopology
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.rawReference

// Spatial approximation only: coordinates are reevaluated at playback time, never frame-baked.
private const val TRIM_LENGTH_STEPS = 256

private fun RemoteFloat.shared(): RemoteFloat = rawReference()

/** Balanced sums keep lazy state registration logarithmic in the number of samples. */
private fun balancedSum(
  values: List<RemoteFloat>,
  from: Int = 0,
  to: Int = values.size,
): RemoteFloat =
  when (to - from) {
    0 -> 0f.rf
    1 -> values[from]
    else -> {
      val middle = (from + to) / 2
      (balancedSum(values, from, middle) + balancedSum(values, middle, to)).shared()
    }
  }

private fun lengthPrefixes(lengths: List<RemoteFloat>): List<RemoteFloat> =
  lengths.runningFold(0f.rf) { prefix, length -> (prefix + length).shared() }

private data class TrimPoint(val x: RemoteFloat, val y: RemoteFloat) {
  operator fun plus(other: TrimPoint) = TrimPoint(x + other.x, y + other.y)

  operator fun minus(other: TrimPoint) = TrimPoint(x - other.x, y - other.y)

  operator fun times(f: RemoteFloat) = TrimPoint(x * f, y * f)

  fun shared() = TrimPoint(x.shared(), y.shared())

  fun length() = sqrt(x * x + y * y).shared()

  fun values() = listOf(x, y)

  fun logical() = LogicalPoint(x, y)
}

private fun mix(a: TrimPoint, b: TrimPoint, t: RemoteFloat) = (a + (b - a) * t).shared()

private data class TrimCubic(
  val a: TrimPoint,
  val b: TrimPoint,
  val c: TrimPoint,
  val d: TrimPoint,
) {
  fun point(t: RemoteFloat): TrimPoint {
    if (t.constantValueOrNull == 0f) return a
    if (t.constantValueOrNull == 1f) return d
    t.constantValueOrNull?.let { value ->
      val u = 1f - value
      return (a * (u * u * u).rf +
          b * (3f * u * u * value).rf +
          c * (3f * u * value * value).rf +
          d * (value * value * value).rf)
        .shared()
    }
    val ab = mix(a, b, t)
    val bc = mix(b, c, t)
    val cd = mix(c, d, t)
    return mix(mix(ab, bc, t), mix(bc, cd, t), t)
  }

  fun derivative(t: RemoteFloat): TrimPoint {
    val u = (1f.rf - t).shared()
    return ((b - a) * (3f.rf * u * u) + (c - b) * (6f.rf * u * t) + (d - c) * (3f.rf * t * t))
      .shared()
  }

  fun cut(from: RemoteFloat, to: RemoteFloat): TrimCubic {
    val start = point(from)
    val end = point(to)
    val third = ((to - from) / 3f).shared()
    return TrimCubic(
      start,
      (start + derivative(from) * third).shared(),
      (end - derivative(to) * third).shared(),
      end,
    )
  }
}

/** Arc fraction inversion of the authored zero-handle straight cubic's analytic polynomial. */
private fun straightParameter(fraction: RemoteFloat): RemoteFloat {
  var low = 0f.rf
  var high = 1f.rf
  repeat(22) {
    val t = ((low + high) / 2f).shared()
    val value = (t * t * (3f.rf - 2f.rf * t)).shared()
    low = selectIfLt(value, fraction, t, low).shared()
    high = selectIfLt(value, fraction, high, t).shared()
  }
  return selectIfLe(fraction, 0f.rf, 0f.rf, selectIfGe(fraction, 1f.rf, 1f.rf, (low + high) / 2f))
    .shared()
}

private class MeasuredCubic(val curve: TrimCubic, val straight: Boolean, present: RemoteFloat) {
  // Spatial samples approximate arc length, not time: every coordinate remains a live expression.
  private val table =
    if (straight) emptyList()
    else
      lengthPrefixes(
        buildList {
          var previous = curve.a
          for (j in 1..TRIM_LENGTH_STEPS) {
            val current = curve.point((j.toFloat() / TRIM_LENGTH_STEPS).rf)
            add((current - previous).length())
            previous = current
          }
        }
      )
  private val geometricLength = if (straight) (curve.d - curve.a).length() else table.last()
  val length = (geometricLength * present).shared()

  fun parameter(distance: RemoteFloat): RemoteFloat {
    if (straight)
      return straightParameter(
        clamp(distance / max(geometricLength, 1e-9f.rf), 0f.rf, 1f.rf).shared()
      )
    return balancedSum(
      (0 until table.lastIndex).map { j ->
        val part =
          clamp((distance - table[j]) / max(table[j + 1] - table[j], 1e-9f.rf), 0f.rf, 1f.rf)
        (part / TRIM_LENGTH_STEPS.toFloat()).shared()
      }
    )
  }
}

/**
 * Expose live cut vertices without baking animation frames or counting collapsed capacity slots.
 */
internal fun RemoteLottiePath.materializeTrim(): RemoteLottiePath {
  val cut = trim ?: return this
  val constant = materializeConstantTrim()
  if (constant !== this) return constant
  return materializeSharedTrim(listOf(this), cut).single()
}

/** Cut one shared arc-length domain, then return each operand to its original paint/transform. */
internal fun materializeSharedTrim(
  paths: List<RemoteLottiePath>,
  cut: RemotePathTrim,
): List<RemoteLottiePath> {
  val sources = paths.flatMap { it.path }.map { LogicalBezier(it).canonical() }
  val measured = sources.map(::measureContour)
  val lengths = measured.mapIndexed { index, curves ->
    (curves.fold(0f.rf) { sum, curve -> (sum + curve.length).shared() } * sources[index].visibility)
      .shared()
  }
  val prefixes = lengthPrefixes(lengths)
  val total = prefixes.last()
  val results = sources.mapIndexed { index, source ->
    val curves = measured[index]
    if (curves.isEmpty()) return@mapIndexed emptyList()
    val length = lengths[index]
    val ranges =
      if (sources.size == 1) cut.ranges
      else
        cut.ranges.map { (from, to) ->
          clamp((from * total - prefixes[index]) / max(length, 1e-9f.rf), 0f.rf, 1f.rf).shared() to
            clamp((to * total - prefixes[index]) / max(length, 1e-9f.rf), 0f.rf, 1f.rf).shared()
        }
    val intervals = ranges.map { (from, to) ->
      materializeInterval(source, curves, length, from, to)
    }
    // A partial compound cut can still cover a whole closed contour. Preserve that
    // contour's seam join and centroid instead of appending a duplicate endpoint.
    val full =
      if (source.closed) {
        val extent = ranges.fold(0f.rf) { sum, (from, to) -> sum + to - from }
        selectIfGe(extent, 1f.rf, 1f.rf, 0f.rf).shared()
      } else 0f.rf
    if (full.constantValueOrNull == 0f) intervals
    else {
      intervals.map { it.copy(visibility = (it.visibility * (1f.rf - full)).shared()) } +
        source.copy(visibility = (source.visibility * full).shared())
    }
  }
  var index = 0
  return paths.map { source ->
    val contours = results.subList(index, index + source.path.size).flatten()
    index += source.path.size
    RemoteLottiePath(
      contours,
      source.fillRule,
      geometryTransforms = source.geometryTransforms,
      geometryVisibility =
        source.geometryVisibility * contours.fold(0f.rf) { a, b -> max(a, b.visibility).shared() },
      identity = source.identity,
    )
  }
}

private fun measureContour(source: RemoteBezierValue): List<MeasuredCubic> {
  val count = source.vertices.size
  if (count == 0 || (count == 1 && !source.closed)) return emptyList()
  fun point(values: List<List<RemoteFloat>>, j: Int) =
    TrimPoint(values[j].getOrElse(0) { 0f.rf }, values[j].getOrElse(1) { 0f.rf }).shared()
  return (0 until if (source.closed) count else count - 1).map { j ->
    val next = (j + 1) % count
    val a = point(source.vertices, j)
    val d = point(source.vertices, next)
    val outgoing = point(source.outTangents, j)
    val incoming = point(source.inTangents, next)
    MeasuredCubic(
      TrimCubic(a, (a + outgoing).shared(), (d + incoming).shared(), d),
      (outgoing.values() + incoming.values()).all { it.constantValueOrNull == 0f },
      source.topology?.segments?.get(j) ?: 1f.rf,
    )
  }
}

private fun materializeInterval(
  source: RemoteBezierValue,
  curves: List<MeasuredCubic>,
  total: RemoteFloat,
  from: RemoteFloat,
  to: RemoteFloat,
): RemoteBezierValue {
  val count = curves.size + 1
  val start = (from * total).shared()
  val end = (to * total).shared()
  var prefix = 0f.rf
  val active = mutableListOf<RemoteFloat>()
  val pieces = curves.mapIndexed { j, curve ->
    val a = clamp(start - prefix, 0f.rf, curve.length).shared()
    val b = clamp(end - prefix, 0f.rf, curve.length).shared()
    active.add(
      (selectIfLe(b - a, 0f.rf, 0f.rf, 1f.rf) * (source.topology?.segments?.get(j) ?: 1f.rf))
        .shared()
    )
    prefix = (prefix + curve.length).shared()
    curve.curve.cut(curve.parameter(a), curve.parameter(b))
  }
  var tail = LogicalPoint(0f.rf, 0f.rf)
  var visible = 0f.rf
  for (j in pieces.indices) {
    tail = logicalChoose(active[j], pieces[j].d.logical(), tail)
    visible = max(visible, active[j]).shared()
  }
  val vertices = MutableList(count) { tail }
  var next = tail
  for (j in pieces.indices.reversed()) {
    next = logicalChoose(active[j], pieces[j].a.logical(), next)
    vertices[j] = next
  }
  val incoming =
    listOf(listOf(0f.rf, 0f.rf)) +
      pieces.mapIndexed { j, piece -> ((piece.c - piece.d) * active[j]).shared().values() }
  val outgoing =
    pieces.mapIndexed { j, piece -> ((piece.b - piece.a) * active[j]).shared().values() } +
      listOf(listOf(0f.rf, 0f.rf))
  return RemoteBezierValue(
    false,
    incoming,
    outgoing,
    vertices.map { it.values() },
    RemoteBezierTopology(active + visible, active + 0f.rf),
    visibility = source.visibility * visible,
  )
}

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
private const val TRIM_SIMPSON_PANELS = 32
private const val TRIM_LENGTH_STEPS = TRIM_SIMPSON_PANELS * 2

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
      (balancedSum(values, from, middle) + balancedSum(values, middle, to)).rawReference()
    }
  }

private fun lengthPrefixes(lengths: List<RemoteFloat>): List<RemoteFloat> =
  lengths.runningFold(0f.rf) { prefix, length -> (prefix + length).rawReference() }

private data class TrimPoint(val x: RemoteFloat, val y: RemoteFloat) {
  operator fun plus(other: TrimPoint) = TrimPoint(x + other.x, y + other.y)

  operator fun minus(other: TrimPoint) = TrimPoint(x - other.x, y - other.y)

  operator fun times(f: RemoteFloat) = TrimPoint(x * f, y * f)

  fun rawReference() = TrimPoint(x.rawReference(), y.rawReference())

  fun length() = sqrt(x * x + y * y).rawReference()

  fun values() = listOf(x, y)

  fun logical() = LogicalPoint(x, y)
}

private data class TrimCubic(
  val a: TrimPoint,
  val b: TrimPoint,
  val c: TrimPoint,
  val d: TrimPoint,
  val ab: TrimPoint? = null,
  val cd: TrimPoint? = null,
) {
  private val abDiff by lazy(LazyThreadSafetyMode.NONE) { ab ?: (b - a).rawReference() }
  private val cdDiff by lazy(LazyThreadSafetyMode.NONE) { cd ?: (d - c).rawReference() }
  private val bcDiff by
    lazy(LazyThreadSafetyMode.NONE) { ((d - a) - abDiff - cdDiff).rawReference() }

  fun point(t: RemoteFloat): TrimPoint {
    val u = (1f.rf - t).rawReference()
    val uu = (u * u).rawReference()
    val tt = (t * t).rawReference()
    val w0 = (uu * u).rawReference()
    val w1 = (3f.rf * uu * t).rawReference()
    val w2 = (3f.rf * u * tt).rawReference()
    val w3 = (tt * t).rawReference()
    return (a * w0 + b * w1 + c * w2 + d * w3).rawReference()
  }

  fun derivative(t: RemoteFloat): TrimPoint {
    val u = (1f.rf - t).rawReference()
    return (abDiff * (3f.rf * u * u) + bcDiff * (6f.rf * u * t) + cdDiff * (3f.rf * t * t))
      .rawReference()
  }

  fun cut(from: RemoteFloat, to: RemoteFloat): TrimCubic {
    val start = point(from)
    val end = point(to)
    val third = ((to - from) / 3f.rf).rawReference()
    return TrimCubic(
      start,
      (start + derivative(from) * third).rawReference(),
      (end - derivative(to) * third).rawReference(),
      end,
    )
  }
}

/** Arc fraction inversion of the authored zero-handle straight cubic's analytic polynomial. */
internal fun straightParameter(fraction: RemoteFloat): RemoteFloat {
  var low = 0f.rf
  var high = 1f.rf
  repeat(22) {
    val t = ((low + high) / 2f).rawReference()
    val value = (t * t * (3f.rf - 2f.rf * t)).rawReference()
    low = selectIfLt(value, fraction, t, low).rawReference()
    high = selectIfLt(value, fraction, high, t).rawReference()
  }
  return selectIfLe(fraction, 0f.rf, 0f.rf, selectIfGe(fraction, 1f.rf, 1f.rf, (low + high) / 2f))
    .rawReference()
}

private val straightSubdivisionT = floatArrayOf(0f, 0.125f, 0.25f, 0.5f, 0.75f, 0.875f, 1f)
private val straightSubdivisionS =
  FloatArray(straightSubdivisionT.size) {
    val t = straightSubdivisionT[it]
    t * t * (3f - 2f * t)
  }

/** Matches native PathMeasure subdivision of zero-handle straight cubics on rounded contours. */
internal fun roundedStraightParameter(fraction: RemoteFloat): RemoteFloat {
  var result = 0f.rf
  for (i in 0 until straightSubdivisionT.lastIndex) {
    val s0 = straightSubdivisionS[i]
    val s1 = straightSubdivisionS[i + 1]
    val dt = straightSubdivisionT[i + 1] - straightSubdivisionT[i]
    val u = clamp((fraction - s0.rf) / (s1 - s0).rf, 0f.rf, 1f.rf)
    result = (result + u * dt.rf).rawReference()
  }
  return result
}

private class MeasuredCubic(
  val curve: TrimCubic,
  val straight: Boolean,
  present: RemoteFloat,
  val roundedContour: Boolean = false,
) {
  private val absent = present.constantValueOrNull == 0f
  private val roundedCorner = roundedContour && !straight
  private val mid = if (roundedCorner && !absent) curve.point(0.5f.rf) else curve.a
  private val firstChord = if (roundedCorner && !absent) (mid - curve.a).length() else 0f.rf
  private val secondChord = if (roundedCorner && !absent) (curve.d - mid).length() else 0f.rf
  // Spatial samples approximate arc length, not time: every coordinate remains a live expression.
  private val speeds =
    if (straight || absent || roundedCorner) emptyList()
    else
      (0..TRIM_LENGTH_STEPS).map { k ->
        curve.derivative((k.toFloat() / TRIM_LENGTH_STEPS).rf).length()
      }
  private val table =
    if (straight || absent || roundedCorner) emptyList()
    else
      lengthPrefixes(
        buildList {
          val scale = (1f / (12f * TRIM_LENGTH_STEPS)).rf
          for (j in 0 until TRIM_SIMPSON_PANELS) {
            val s0 = speeds[2 * j]
            val s1 = speeds[2 * j + 1]
            val s2 = speeds[2 * j + 2]
            val dl0 = max((s0 * 5f.rf + s1 * 8f.rf - s2) * scale, 0f.rf)
            val dl1 = max((s1 * 8f.rf + s2 * 5f.rf - s0) * scale, 0f.rf)
            add(dl0.rawReference())
            add(dl1.rawReference())
          }
        }
      )
  private val binRatios =
    if (straight || absent || roundedCorner) emptyList()
    else
      (0 until TRIM_LENGTH_STEPS).map { k ->
        val v0 = speeds[k]
        val v1 = speeds[k + 1]
        ((v0 - v1) / max(v0 + v1, 1e-9f.rf)).rawReference()
      }
  private val geometricLength =
    when {
      absent -> 0f.rf
      straight -> (curve.d - curve.a).length()
      roundedCorner -> (firstChord + secondChord).rawReference()
      else -> table.last()
    }
  val length = (geometricLength * present).rawReference()

  fun parameter(distance: RemoteFloat): RemoteFloat {
    if (absent || distance.constantValueOrNull == 0f) return 0f.rf
    if (straight) {
      val fraction = clamp(distance / max(geometricLength, 1e-9f.rf), 0f.rf, 1f.rf).rawReference()
      return if (roundedContour) roundedStraightParameter(fraction) else straightParameter(fraction)
    }
    if (roundedCorner) {
      val u0 = clamp(distance / max(firstChord, 1e-9f.rf), 0f.rf, 1f.rf)
      val u1 = clamp((distance - firstChord) / max(secondChord, 1e-9f.rf), 0f.rf, 1f.rf)
      return ((u0 + u1) * 0.5f.rf).rawReference()
    }
    val invSteps = (1f / TRIM_LENGTH_STEPS).rf
    return balancedSum(
      (0 until table.lastIndex).map { k ->
        val denom = max(table[k + 1] - table[k], 1e-9f.rf)
        val u = clamp((distance - table[k]) / denom, 0f.rf, 1f.rf).rawReference()
        val r = binRatios[k]
        val uCorr = u - r * u * (1f.rf - u) * (1f.rf + r * (1f.rf - 2f.rf * u))
        (uCorr * invSteps).rawReference()
      }
    )
  }

  fun cut(fromDistance: RemoteFloat, toDistance: RemoteFloat): TrimCubic =
    curve.cut(parameter(fromDistance), parameter(toDistance))
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
    (curves.fold(0f.rf) { sum, curve -> (sum + curve.length).rawReference() } *
        sources[index].visibility)
      .rawReference()
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
          val denom = max(length, 1e-9f.rf)
          val localFrom =
            if (from.constantValueOrNull == 0f) 0f.rf
            else clamp((from * total - prefixes[index]) / denom, 0f.rf, 1f.rf).rawReference()
          val localTo =
            if (to.constantValueOrNull == 0f) 0f.rf
            else clamp((to * total - prefixes[index]) / denom, 0f.rf, 1f.rf).rawReference()
          localFrom to localTo
        }
    val intervals = ranges.map { (from, to) ->
      materializeInterval(source, curves, length, from, to)
    }
    // A partial compound cut can still cover a whole closed contour. Preserve that
    // contour's seam join and centroid instead of appending a duplicate endpoint.
    val full =
      if (source.closed) {
        val extent = ranges.fold(0f.rf) { sum, (from, to) -> sum + (to - from) }
        selectIfGe(extent, 1f.rf, 1f.rf, 0f.rf).rawReference()
      } else 0f.rf
    if (full.constantValueOrNull == 0f) intervals
    else {
      intervals.map { it.copy(visibility = (it.visibility * (1f.rf - full)).rawReference()) } +
        source.copy(visibility = (source.visibility * full).rawReference())
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
        source.geometryVisibility *
          contours.fold(0f.rf) { a, b -> max(a, b.visibility).rawReference() },
      identity = source.identity,
    )
  }
}

private fun measureContour(source: RemoteBezierValue): List<MeasuredCubic> {
  val count = source.vertices.size
  if (count == 0 || (count == 1 && !source.closed)) return emptyList()
  fun point(values: List<List<RemoteFloat>>, j: Int) =
    TrimPoint(values[j].getOrElse(0) { 0f.rf }, values[j].getOrElse(1) { 0f.rf }).rawReference()
  return (0 until if (source.closed) count else count - 1).map { j ->
    val next = (j + 1) % count
    val a = point(source.vertices, j)
    val d = point(source.vertices, next)
    val outgoing = point(source.outTangents, j)
    val incoming = point(source.inTangents, next)
    MeasuredCubic(
      TrimCubic(
        a,
        (a + outgoing).rawReference(),
        (d + incoming).rawReference(),
        d,
        ab = outgoing,
        cd = (TrimPoint(0f.rf, 0f.rf) - incoming).rawReference(),
      ),
      (outgoing.values() + incoming.values()).all { it.constantValueOrNull == 0f },
      source.topology?.segments?.get(j) ?: 1f.rf,
      roundedContour = source.topology?.roundedCorners == true,
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
  val start = (from * total).rawReference()
  val end = (to * total).rawReference()
  var prefix = 0f.rf
  val active = mutableListOf<RemoteFloat>()
  val pieces = curves.mapIndexed { j, curve ->
    val a =
      if (start.constantValueOrNull == 0f || curve.length.constantValueOrNull == 0f) 0f.rf
      else clamp(start - prefix, 0f.rf, curve.length).rawReference()
    val b =
      if (end.constantValueOrNull == 0f || curve.length.constantValueOrNull == 0f) 0f.rf
      else clamp(end - prefix, 0f.rf, curve.length).rawReference()
    val diff = b - a
    val segPresent = source.topology?.segments?.get(j) ?: 1f.rf
    val segActive = (selectIfLe(diff, 1e-5f.rf, 0f.rf, 1f.rf) * segPresent).rawReference()
    active.add(segActive)
    prefix = (prefix + curve.length).rawReference()
    curve.cut(a, b)
  }
  var tail = LogicalPoint(0f.rf, 0f.rf)
  var visible = 0f.rf
  for (j in pieces.indices) {
    tail = logicalChoose(active[j], pieces[j].d.logical(), tail)
    visible = max(visible, active[j]).rawReference()
  }
  val vertices = MutableList(count) { tail }
  var next = tail
  for (j in pieces.indices.reversed()) {
    next = logicalChoose(active[j], pieces[j].a.logical(), next)
    vertices[j] = next
  }
  val incoming =
    listOf(listOf(0f.rf, 0f.rf)) +
      pieces.mapIndexed { j, piece -> ((piece.c - piece.d) * active[j]).rawReference().values() }
  val outgoing =
    pieces.mapIndexed { j, piece -> ((piece.b - piece.a) * active[j]).rawReference().values() } +
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

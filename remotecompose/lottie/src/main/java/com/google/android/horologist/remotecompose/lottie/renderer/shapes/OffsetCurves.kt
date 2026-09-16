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
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.cos
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import androidx.compose.remote.creation.compose.state.selectIfLe
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.remote.creation.compose.state.sin
import androidx.compose.remote.creation.compose.state.sqrt
import androidx.compose.remote.creation.compose.state.tan
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.LineJoin
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierTopology
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.rawReference

private data class CurvePoint(val x: RemoteFloat, val y: RemoteFloat) {
  operator fun plus(p: CurvePoint) = CurvePoint(x + p.x, y + p.y)

  operator fun minus(p: CurvePoint) = CurvePoint(x - p.x, y - p.y)

  operator fun times(v: RemoteFloat) = CurvePoint(x * v, y * v)

  fun cross(p: CurvePoint) = x * p.y - y * p.x

  fun dot(p: CurvePoint) = x * p.x + y * p.y

  fun length() = sqrt(dot(this))

  fun unit(fallback: CurvePoint): CurvePoint {
    val length = length().rawReference()
    return choose(length, this * (1f.rf / max(length, 0.000001f.rf)), fallback)
  }

  fun right() = CurvePoint(y, 0f.rf - x)

  fun values() = listOf(x, y)

  fun shared() = CurvePoint(x.rawReference(), y.rawReference())
}

private fun choose(flag: RemoteFloat, yes: CurvePoint, no: CurvePoint): CurvePoint {
  flag.constantValueOrNull?.let {
    return if (it > 0.000001f) yes else no
  }
  return CurvePoint(
    selectIfLt(0.000001f.rf, flag, yes.x, no.x),
    selectIfLt(0.000001f.rf, flag, yes.y, no.y),
  )
}

private fun mix(a: CurvePoint, b: CurvePoint, t: RemoteFloat) = a + (b - a) * t

private data class OffsetCubic(
  val p0: CurvePoint,
  val p1: CurvePoint,
  val p2: CurvePoint,
  val p3: CurvePoint,
) {
  fun shared() = OffsetCubic(p0.shared(), p1.shared(), p2.shared(), p3.shared())

  fun reverse() = OffsetCubic(p3, p2, p1, p0)

  fun point(t: RemoteFloat): CurvePoint {
    val a = mix(p0, p1, t)
    val b = mix(p1, p2, t)
    val c = mix(p2, p3, t)
    return mix(mix(a, b, t), mix(b, c, t), t)
  }

  fun derivative(t: RemoteFloat) =
    ((p1 - p0) * ((1f.rf - t) * (1f.rf - t)) +
      (p2 - p1) * (2f.rf * t * (1f.rf - t)) +
      (p3 - p2) * (t * t)) * 3f.rf

  fun slice(a: RemoteFloat, b: RemoteFloat): OffsetCubic {
    if (a.constantValueOrNull == 0f && b.constantValueOrNull == 1f) return this
    if (a.constantValueOrNull != null && a.constantValueOrNull == b.constantValueOrNull) {
      val p =
        when (a.constantValueOrNull) {
          0f -> p0
          1f -> p3
          else -> point(a)
        }
      return OffsetCubic(p, p, p, p)
    }
    val inputs = listOf(p0, p1, p2, p3).flatMap { it.values() } + listOf(a, b)
    if (inputs.any { it.constantValueOrNull == null }) {
      val output = cubicSliceFunction(inputs)
      fun point(index: Int) = CurvePoint(output[index * 2], output[index * 2 + 1])
      return OffsetCubic(point(0), point(1), point(2), point(3))
    }
    val start = point(a)
    val end = point(b)
    return OffsetCubic(
      start,
      start + derivative(a) * ((b - a) / 3f),
      end - derivative(b) * ((b - a) / 3f),
      end,
    )
  }

  fun linearized() =
    OffsetCubic(
      p0,
      choose((p1 - p0).length(), p1, mix(p0, p3, (1f / 3f).rf)),
      choose((p2 - p3).length(), p2, mix(p0, p3, (2f / 3f).rf)),
      p3,
    )
}

private fun intersection(
  a: CurvePoint,
  da: CurvePoint,
  b: CurvePoint,
  db: CurvePoint,
  fallback: CurvePoint,
): CurvePoint {
  val det = da.cross(db).rawReference()
  val safe = selectIfLt(0.000001f.rf, abs(det), det, 1f.rf).rawReference()
  return choose(abs(det), a + da * ((b - a).cross(db) / safe), fallback)
}

private fun OffsetCubic.offset(
  amount: RemoteFloat,
  fallback: CurvePoint,
  flatStart: RemoteFloat,
  flatEnd: RemoteFloat,
): OffsetCubic {
  val a = (p1 - p0).unit(fallback).shared()
  // A coincident pair of interior controls has no supporting line. It must not borrow
  // the first edge's direction: that invents an intersection and changes the second handle.
  val b = (p2 - p1).unit(CurvePoint(0f.rf, 0f.rf)).shared()
  val c = (p3 - p2).unit(a).shared()
  val start = p0 + a.right() * amount
  val end = p3 + c.right() * amount
  val firstOffset = p1 + a.right() * amount
  val lastOffset = p2 + c.right() * amount
  // At a split inflection the supporting edges are parallel by construction. Do not
  // intersect a tiny angle introduced only by float32 de Casteljau roundoff.
  val control1 =
    choose(flatStart, firstOffset, intersection(start, a, p1 + b.right() * amount, b, firstOffset))
  val control2 =
    choose(flatEnd, lastOffset, intersection(end, c, p2 + b.right() * amount, b, lastOffset))
  return OffsetCubic(start, control1, control2, end)
}

/** Three fixed slots keep inflection splits live; absent roots collapse at t=1. */
private fun OffsetCubic.breakpoints(): List<RemoteFloat> {
  val a = (p1 - p2) * 3f.rf + p3 - p0
  val b = (p0 - p1 * 2f.rf + p2) * 3f.rf
  val c = (p1 - p0) * 3f.rf
  val det = b.cross(a)
  val valid = selectIfLt(0.00001f.rf, abs(det), 1f.rf, 0f.rf)
  val safe = selectIfGe(valid, 1f.rf, det, 1f.rf)
  val center = c.cross(a) * (-0.5f).rf / safe
  val square = center * center - c.cross(b) / (safe * 3f)
  val root = sqrt(max(square, 0f.rf))
  fun bounded(value: RemoteFloat) =
    selectIfGe(
      valid,
      1f.rf,
      selectIfLt(square, 0f.rf, 1f.rf, selectIfLt(0f.rf, value, clamp(value, 0f.rf, 1f.rf), 1f.rf)),
      1f.rf,
    )
  val low = bounded(center - root)
  val high = bounded(center + root)
  // An out-of-range lower root becomes 1 while the upper root may still be inside.
  return listOf(
    0f.rf,
    androidx.compose.remote.creation.compose.state.min(low, high),
    max(low, high),
    1f.rf,
  )
}

private fun chooseCubic(flag: RemoteFloat, yes: OffsetCubic, no: OffsetCubic) =
  OffsetCubic(
    choose(flag, yes.p0, no.p0),
    choose(flag, yes.p1, no.p1),
    choose(flag, yes.p2, no.p2),
    choose(flag, yes.p3, no.p3),
  )

private fun collapsed(point: CurvePoint) = OffsetCubic(point, point, point, point)

private class OffsetPieces(
  val original: List<OffsetCubic>,
  var active: List<RemoteFloat>,
  val pieces: MutableList<OffsetCubic>,
) {
  fun first(): OffsetCubic {
    var result = pieces.first()
    for (i in pieces.indices.reversed()) result = chooseCubic(active[i], pieces[i], result)
    return result
  }

  fun last(): OffsetCubic {
    var result = pieces.last()
    for (i in pieces.indices) result = chooseCubic(active[i], pieces[i], result)
    return result
  }

  fun shared() =
    OffsetPieces(
      original,
      active.map { it.rawReference() },
      pieces.map { it.shared() }.toMutableList(),
    )

  fun multiple() = selectIfLt(1.5f.rf, active.fold(0f.rf) { sum, flag -> sum + flag }, 1f.rf, 0f.rf)

  fun keep(curve: OffsetCubic, first: Boolean, flag: RemoteFloat) {
    val retained = MutableList(active.size) { 0f.rf }
    var unseen = 1f.rf
    for (i in if (first) active.indices else active.indices.reversed()) {
      retained[i] = (unseen * active[i]).rawReference()
      unseen = (unseen * (1f.rf - active[i])).rawReference()
    }
    var passed = 0f.rf
    for (i in pieces.indices) {
      val empty = collapsed(choose(passed, curve.p3, curve.p0))
      pieces[i] = chooseCubic(flag, chooseCubic(retained[i], curve, empty), pieces[i]).shared()
      passed = max(passed, retained[i]).rawReference()
    }
    active = active.indices.map { selectIfGe(flag, 1f.rf, retained[it], active[it]).rawReference() }
  }
}

/** Solve a local adjacent-curve crossing without sampling animation time. */
private fun pruneAdjacent(
  left: OffsetPieces,
  right: OffsetPieces,
  enabled: RemoteFloat = 1f.rf,
): RemoteFloat {
  if (enabled.constantValueOrNull == 0f) return 0f.rf
  val a = left.last().shared()
  val b = right.first().shared()
  fun coordinates(curve: OffsetCubic) =
    listOf(curve.p0, curve.p1, curve.p2, curve.p3).flatMap { it.values() }
  val (t, u) = cubicIntersection(coordinates(a), coordinates(b), enabled)
  val hit =
    enabled *
      selectIfLt(
        (a.point(t) - b.point(u)).length(),
        0.0005f.rf,
        selectIfLt(t, 0.99999f.rf, selectIfLt(0.00001f.rf, u, 1f.rf, 0f.rf), 0f.rf),
        0f.rf,
      )
  val clippedA = a.slice(0f.rf, t)
  val clippedB = b.slice(u, 1f.rf)
  var last = hit
  for (i in left.pieces.indices.reversed()) {
    val selected = last * left.active[i]
    left.pieces[i] =
      chooseCubic(
        selected,
        clippedA,
        chooseCubic(last * (1f.rf - left.active[i]), collapsed(clippedA.p3), left.pieces[i]),
      )
    last *= 1f.rf - left.active[i]
  }
  var first = hit
  for (i in right.pieces.indices) {
    val selected = first * right.active[i]
    right.pieces[i] =
      chooseCubic(
        selected,
        clippedB,
        chooseCubic(first * (1f.rf - right.active[i]), collapsed(clippedB.p0), right.pieces[i]),
      )
    first *= 1f.rf - right.active[i]
  }
  // When both source segments split, an outer crossing can swallow every intervening piece.
  // The first-left and last-right pieces are unaffected by the boundary clipping above.
  val outerEnabled = (enabled * left.multiple() * right.multiple()).rawReference()
  if (outerEnabled.constantValueOrNull == 0f) return hit
  val outerA = left.first().shared()
  val outerB = right.last().shared()
  val (outerT, outerU) = cubicIntersection(coordinates(outerA), coordinates(outerB), outerEnabled)
  val outerHit =
    (outerEnabled *
        selectIfLt(outerT, .99999f.rf, selectIfLt(.00001f.rf, outerU, 1f.rf, 0f.rf), 0f.rf))
      .rawReference()
  left.keep(outerA.slice(0f.rf, outerT).shared(), first = true, flag = outerHit)
  right.keep(outerB.slice(outerU, 1f.rf).shared(), first = false, flag = outerHit)
  return max(hit, outerHit).rawReference()
}

private fun choosePieces(flag: RemoteFloat, yes: OffsetPieces, no: OffsetPieces) =
  OffsetPieces(
      emptyList(),
      yes.active.indices.map { selectIfGe(flag, 1f.rf, yes.active[it], no.active[it]) },
      yes.pieces.indices.map { chooseCubic(flag, yes.pieces[it], no.pieces[it]) }.toMutableList(),
    )
    .shared()

/** Prune in logical contour order; fixed-capacity empty slots must never become neighbours. */
private fun pruneLogicalGroups(
  groups: List<OffsetPieces>,
  present: List<RemoteFloat>,
): Pair<List<OffsetPieces>, List<RemoteFloat>> {
  // Carry the complete preceding active group. An outer crossing can remove all but its
  // first piece, so remembering only a head/tail pair loses both geometry and live activity.
  var previous = groups.first().shared()
  var seen = 0f.rf
  val firstFlags = mutableListOf<RemoteFloat>()
  val leftUpdates = mutableListOf<OffsetPieces>()
  val hits = mutableListOf<RemoteFloat>()
  for (i in groups.indices) {
    firstFlags += (present[i] * (1f.rf - seen)).rawReference()
    val current = groups[i].shared()
    val hit =
      pruneAdjacent(previous, current, if (i == 0) 0f.rf else present[i] * seen).rawReference()
    hits += hit
    leftUpdates += previous.shared()
    previous = choosePieces(present[i], current, previous)
    seen = max(seen, present[i]).rawReference()
  }
  // Each next active group owns the final update to its predecessor, including collapsed pieces.
  var next = previous
  var nextHit = 0f.rf
  var hasNext = 0f.rf
  val lastFlags = MutableList(groups.size) { 0f.rf }
  val updated = MutableList(groups.size) { previous }
  val afterHits = MutableList(groups.size) { 0f.rf }
  for (i in groups.indices.reversed()) {
    updated[i] = next
    afterHits[i] = nextHit
    lastFlags[i] = (present[i] * (1f.rf - hasNext)).rawReference()
    next = choosePieces(present[i], leftUpdates[i], next)
    nextHit = selectIfGe(present[i], 1f.rf, hits[i], nextHit).rawReference()
    hasNext = max(hasNext, present[i]).rawReference()
  }
  var first = updated.first().shared()
  for (i in groups.indices.reversed()) {
    first = choosePieces(firstFlags[i], updated[i], first)
  }
  val count = present.fold(0f.rf) { sum, value -> sum + value }
  val last = previous.shared()
  val closingHit = pruneAdjacent(last, first, selectIfLt(1f.rf, count, 1f.rf, 0f.rf)).rawReference()
  for (i in groups.indices) {
    updated[i] = choosePieces(lastFlags[i], last, choosePieces(firstFlags[i], first, updated[i]))
    afterHits[i] = selectIfGe(lastFlags[i], 1f.rf, closingHit, afterHits[i]).rawReference()
  }
  return updated to afterHits
}

/** Live control-polygon offset, with fixed inflection topology and explicit open-path end caps. */
internal fun offsetCurves(
  input: RemoteBezierValue,
  amount: RemoteFloat,
  join: LineJoin,
  miterLimit: RemoteFloat,
): RemoteBezierValue {
  val path = LogicalBezier(input).canonical()
  if (path.vertices.isEmpty() || (path.vertices.size == 1 && !path.closed)) return path
  require(path.vertices.size <= 1024) { "OffsetPath exceeds 1024 curved source vertices" }
  fun point(values: List<RemoteFloat>) =
    CurvePoint(
      values.getOrElse(0) { 0f.rf }.rawReference(),
      values.getOrElse(1) { 0f.rf }.rawReference(),
    )
  val zero = CurvePoint(0f.rf, 0f.rf)
  val source =
    (0 until if (path.closed) path.vertices.size else path.vertices.size - 1)
      .map { i ->
        val next = (i + 1) % path.vertices.size
        val a = point(path.vertices[i])
        val b = point(path.vertices[next])
        // Share absolute handles before linearization builds larger expressions. Otherwise RC
        // auto-materializes these sums through its NaN-insensitive expression cache (R45).
        OffsetCubic(
            a,
            a + point(path.outTangents.getOrNull(i) ?: emptyList()),
            b + point(path.inTangents.getOrNull(next) ?: emptyList()),
            b,
          )
          .shared()
      }
      .filterNot { curve ->
        // Exporters can repeat a vertex at a contour seam. A zero-length control polygon has
        // no normal and must not introduce a synthetic offset segment pointing along an axis.
        val x = curve.p0.x.constantValueOrNull
        val y = curve.p0.y.constantValueOrNull
        x != null &&
          y != null &&
          listOf(curve.p1, curve.p2, curve.p3).all {
            it.x.constantValueOrNull == x && it.y.constantValueOrNull == y
          }
      }
  if (source.isEmpty()) return path
  val curves = if (path.closed) source else source + source.reversed().map { it.reverse() }
  val present = curves.map { curve ->
    val length =
      (curve.p1 - curve.p0).length() +
        (curve.p2 - curve.p1).length() +
        (curve.p3 - curve.p2).length()
    selectIfLt(0.000001f.rf, length, 1f.rf, 0f.rf).rawReference()
  }
  var groups = curves.mapIndexed { index, raw ->
    val curve = raw.linearized().shared()
    val breaks = curve.breakpoints().map { it.rawReference() }
    val fallback = (curve.p3 - curve.p0).unit(CurvePoint(1f.rf, 0f.rf))
    OffsetPieces(
      (0..2).map {
        if (index < source.size) {
          if (it == 0) raw else collapsed(raw.p3)
        } else collapsed(source.last().p3)
      },
      (0..2).map { selectIfLt(breaks[it], breaks[it + 1], 1f.rf, 0f.rf) },
      (0..2)
        .map { piece ->
          var part = curve.slice(breaks[piece], breaks[piece + 1]).shared()
          if (piece == 1) {
            // Between two distinct inflections the inner controls coincide analytically.
            // Enforce that identity before normalizing: float32 subtraction can otherwise turn
            // roundoff into an arbitrary unit-length edge and a large spurious intersection.
            val betweenInflections =
              selectIfLt(0f.rf, breaks[1], selectIfLt(breaks[2], 1f.rf, 1f.rf, 0f.rf), 0f.rf)
            part = part.copy(p2 = choose(betweenInflections, part.p1, part.p2))
          }
          fun interior(t: RemoteFloat) =
            selectIfLt(0f.rf, t, selectIfLt(t, 1f.rf, 1f.rf, 0f.rf), 0f.rf)
          part
            .linearized()
            .offset(
              amount,
              curve.derivative(breaks[piece]).unit(fallback),
              interior(breaks[piece]),
              interior(breaks[piece + 1]),
            )
            .shared()
        }
        .toMutableList(),
    )
  }
  // A closed one-segment loop joins its own endpoints, but is not an adjacent pair.
  // Comparing a curve with itself finds coincident interior points and destroys the loop.
  val pruned =
    if (path.topology != null) {
      val (updated, hits) = pruneLogicalGroups(groups, present)
      groups = updated.mapIndexed { i, group ->
        OffsetPieces(groups[i].original, group.active, group.pieces)
      }
      hits
    } else
      groups.indices.map {
        if (groups.size > 1) pruneAdjacent(groups[it], groups[(it + 1) % groups.size]) else 0f.rf
      }
  val vertices = mutableListOf<List<RemoteFloat>>()
  val incoming = mutableListOf<List<RemoteFloat>>()
  val outgoing = mutableListOf<List<RemoteFloat>>()
  val owners = mutableListOf<RemoteFloat>()
  var owner = 1f.rf
  val nonzero = selectIfLt(0f.rf, abs(amount), 1f.rf, 0f.rf)
  fun add(p: CurvePoint, inside: CurvePoint = zero, outside: CurvePoint = zero) {
    vertices.add(p.values())
    incoming.add(inside.values())
    outgoing.add(outside.values())
    owners.add(owner)
  }
  fun emit(cubic: OffsetCubic, original: OffsetCubic) {
    val a = choose(nonzero, cubic.p0, original.p0)
    val b = choose(nonzero, cubic.p3, original.p3)
    if (vertices.isEmpty()) add(a)
    owners[owners.lastIndex] = owner
    // Each boundary is one vertex shared by its incoming and outgoing cubics. Recording
    // duplicate near-coincident points here can create tiny spurs at pruned crossings.
    outgoing[outgoing.lastIndex] =
      (choose(nonzero, cubic.p1, original.p1) - point(vertices.last())).values()
    add(b, inside = choose(nonzero, cubic.p2, original.p2) - b)
  }
  val nextHeads = groups.map { it.first().shared() }.toMutableList()
  if (path.topology != null) {
    var nextHead = nextHeads.first()
    for (i in groups.indices.reversed()) nextHead =
      chooseCubic(present[i], nextHeads[i], nextHead).shared()
    for (i in groups.indices.reversed()) {
      val ownHead = nextHeads[i]
      nextHeads[i] = nextHead
      nextHead = chooseCubic(present[i], ownHead, nextHead).shared()
    }
  }
  for ((index, raw) in curves.withIndex()) {
    owner = present[index]
    val curve = raw.linearized()
    val fallback = (curve.p3 - curve.p0).unit(CurvePoint(1f.rf, 0f.rf))
    for (piece in 0..2) {
      if (groups[index].active[piece].constantValueOrNull == 0f) continue
      emit(groups[index].pieces[piece], groups[index].original[piece])
    }
    val tail = groups[index].last()
    val next = if (path.topology != null) nextHeads[index] else nextHeads[(index + 1) % groups.size]
    val center = curve.p3
    val before = (tail.p3 - tail.p2).unit(fallback)
    val after = (next.p1 - next.p0).unit(fallback)
    val a = tail.p3
    val b = next.p0
    // Match the reference's connected-endpoint check, including its relative tolerance.
    // Shared smooth endpoints need no join even when no crossing was pruned.
    fun equalCoordinate(x: RemoteFloat, y: RemoteFloat) =
      selectIfLe(abs(x - y) * 100000f, min(abs(x), abs(y)), 1f.rf, 0f.rf)
    val connected =
      max(pruned[index], equalCoordinate(a.x, b.x) * equalCoordinate(a.y, b.y)).rawReference()
    val inactive = if (index < source.size) raw.p3 else source.last().p3
    if (join == LineJoin.Round) {
      val cross = before.cross(after)
      val dot = before.dot(after)
      val angle =
        selectIfLt(
          abs(cross),
          0.000001f.rf,
          selectIfLt(
            dot,
            0f.rf,
            selectIfLt(amount, 0f.rf, (-Math.PI).toFloat().rf, Math.PI.toFloat().rf),
            0f.rf,
          ),
          atan2(cross, dot),
        )
      val r = a - center
      val middle =
        CurvePoint(
          r.x * cos(angle / 2f) - r.y * sin(angle / 2f),
          r.x * sin(angle / 2f) + r.y * cos(angle / 2f),
        )
      val k = tan(angle / 8f) * (4f / 3f) * (1f.rf - connected)
      val ta = r.right() * (0f.rf - k)
      val tm = middle.right() * (0f.rf - k)
      val tb = (b - center).right() * (0f.rf - k)
      outgoing[outgoing.lastIndex] = ta.values()
      add(choose(nonzero, choose(connected, a, center + middle), inactive), tm * (-1f).rf, tm)
      add(choose(nonzero, b, inactive), tb * (-1f).rf)
    } else {
      val miter = intersection(a, before, b, after, a)
      val middle =
        if (join == LineJoin.Miter)
          // Stabilize an exact limit boundary against float32 intersection roundoff.
          choose(
            selectIfLt(
              (miter - a).length(),
              miterLimit + max(abs(miterLimit), 1f.rf) * 0.000001f,
              1f.rf,
              0f.rf,
            ),
            miter,
            a,
          )
        else a
      val isCap = !path.closed && (index == source.lastIndex || index == curves.lastIndex)
      // A pruned crossing already is the join. Re-intersecting its nearly coincident endpoints
      // can magnify float32 error into a long miter when the clipped tangents are near parallel.
      if (join == LineJoin.Miter && !isCap)
        add(choose(nonzero, choose(connected, a, middle), inactive))
      add(choose(nonzero, b, inactive))
    }
  }
  val output =
    RemoteBezierValue(path.closed, incoming, outgoing, vertices, visibility = path.visibility)
  if (path.topology == null) return output
  val edges =
    vertices.indices.map { i ->
      val j = (i + 1) % vertices.size
      val length =
        point(outgoing[i]).length() +
          point(incoming[j]).length() +
          (point(vertices[j]) - point(vertices[i])).length()
      owners[i] * selectIfLt(0.000001f.rf, length, 1f.rf, 0f.rf)
    }
  val active = edges.toMutableList()
  if (!path.closed) active[active.lastIndex] = present.fold(0f.rf) { sum, flag -> max(sum, flag) }
  return LogicalBezier(output.copy(topology = RemoteBezierTopology(active, edges))).canonical()
}

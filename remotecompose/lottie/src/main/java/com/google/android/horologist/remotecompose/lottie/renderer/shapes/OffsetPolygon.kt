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
import androidx.compose.remote.creation.compose.state.max
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

private data class OffsetPoint(val x: RemoteFloat, val y: RemoteFloat) {
  operator fun plus(p: OffsetPoint) = OffsetPoint(x + p.x, y + p.y)

  operator fun minus(p: OffsetPoint) = OffsetPoint(x - p.x, y - p.y)

  operator fun times(v: RemoteFloat) = OffsetPoint(x * v, y * v)

  fun length() = sqrt(x * x + y * y)

  fun values() = listOf(x, y)

  fun perpendicular() = OffsetPoint(0f.rf - y, x)
}

private fun chooseOffset(flag: RemoteFloat, yes: OffsetPoint, no: OffsetPoint) =
  OffsetPoint(selectIfGe(flag, 1f.rf, yes.x, no.x), selectIfGe(flag, 1f.rf, yes.y, no.y))

/** Offset straight-edge contours with live vertices, amount, and absolute miter limit. */
internal fun offsetPolygon(
  path: RemoteBezierValue,
  amount: RemoteFloat,
  join: LineJoin,
  miterLimit: RemoteFloat,
): RemoteBezierValue {
  if (path.vertices.size < 2) return path
  // Open paths have two sides. Traversing the reverse side also supplies end-cap joins.
  val indices =
    if (path.closed) path.vertices.indices.toList()
    else path.vertices.indices.toList() + (path.vertices.size - 2 downTo 1)
  val points = indices.map { i ->
    OffsetPoint(path.vertices[i].getOrElse(0) { 0f.rf }, path.vertices[i].getOrElse(1) { 0f.rf })
  }
  require(points.size <= 16384) { "OffsetPath exceeds 16384 source polygon vertices" }
  val zero = OffsetPoint(0f.rf, 0f.rf)
  val directions =
    points.indices.map { i ->
      val delta = points[(i + 1) % points.size] - points[i]
      delta * (1f.rf / max(delta.length(), 0.000001f.rf))
    }
  val active = directions.map { selectIfGe(it.length(), 0.5f.rf, 1f.rf, 0f.rf) }
  var previousDirection = zero
  for (i in points.indices) previousDirection =
    chooseOffset(active[i], directions[i], previousDirection)
  val before =
    points.indices.map { i ->
      val result = previousDirection
      previousDirection = chooseOffset(active[i], directions[i], previousDirection)
      result
    }
  var nextDirection = zero
  for (i in points.indices.reversed()) nextDirection =
    chooseOffset(active[i], directions[i], nextDirection)
  val after = MutableList(points.size) { zero }
  for (i in points.indices.reversed()) {
    nextDirection = chooseOffset(active[i], directions[i], nextDirection)
    after[i] = nextDirection
  }
  val vertices = mutableListOf<List<RemoteFloat>>()
  val incoming = mutableListOf<List<RemoteFloat>>()
  val outgoing = mutableListOf<List<RemoteFloat>>()
  val logicalVertices = mutableListOf<RemoteFloat>()
  val logicalSegments = mutableListOf<RemoteFloat>()
  fun add(point: OffsetPoint, inControl: OffsetPoint = zero, outControl: OffsetPoint = zero) {
    vertices.add(point.values())
    incoming.add(inControl.values())
    outgoing.add(outControl.values())
  }
  for (i in points.indices) {
    val center = points[i]
    val previous = before[i]
    val current = after[i]
    val n0 = OffsetPoint(previous.y, 0f.rf - previous.x)
    val n1 = OffsetPoint(current.y, 0f.rf - current.x)
    val a = center + n0 * amount
    val b = center + n1 * amount
    val determinant = previous.x * current.y - previous.y * current.x
    val intersects = selectIfGe(abs(determinant), 0.000001f.rf, 1f.rf, 0f.rf)
    val safeDet = selectIfGe(intersects, 1f.rf, determinant, 1f.rf)
    val delta = b - a
    val intersection = a + previous * ((delta.x * current.y - delta.y * current.x) / safeDet)
    val inner = selectIfLt(determinant * amount, 0f.rf, intersects, 0f.rf)
    val shortMiter = selectIfLt((intersection - a).length(), miterLimit, intersects, 0f.rf)
    val collapse = if (join == LineJoin.Miter) max(inner, shortMiter) else inner
    val first = chooseOffset(collapse, intersection, a)
    val last = chooseOffset(active[i], chooseOffset(collapse, intersection, b), first)
    val extra = active[i] * (1f.rf - collapse) * selectIfLe(abs(amount), 0f.rf, 0f.rf, 1f.rf)
    logicalVertices.addAll(listOf(active[i], if (join == LineJoin.Round) extra else 0f.rf, extra))
    logicalSegments.addAll(listOf(if (join == LineJoin.Round) extra else 0f.rf, extra, active[i]))
    if (join != LineJoin.Round) {
      // A fixed third vertex collapses when a live miter limit changes to/from bevel.
      add(first)
      add(chooseOffset(collapse, intersection, a))
      add(last)
      continue
    }
    val dot = n0.x * n1.x + n0.y * n1.y
    val angle =
      selectIfLt(
        abs(determinant),
        0.000001f.rf,
        selectIfLt(
          dot,
          0f.rf,
          selectIfLt(amount, 0f.rf, (-Math.PI).toFloat().rf, Math.PI.toFloat().rf),
          0f.rf,
        ),
        atan2(determinant, dot),
      )
    val radius = a - center
    val half = angle / 2f
    val middleRadius =
      OffsetPoint(
        radius.x * cos(half) - radius.y * sin(half),
        radius.x * sin(half) + radius.y * cos(half),
      )
    val middle =
      chooseOffset(active[i], chooseOffset(collapse, intersection, center + middleRadius), first)
    // Two <=90-degree cubics also represent round caps on a 180-degree turn.
    val k = tan(angle / 8f) * (4f / 3f) * active[i]
    val ta = chooseOffset(collapse, zero, radius.perpendicular() * k)
    val tm = chooseOffset(collapse, zero, middleRadius.perpendicular() * k)
    val tb = chooseOffset(collapse, zero, (b - center).perpendicular() * k)
    add(first, zero, ta)
    add(middle, tm * (-1f).rf, tm)
    add(last, tb * (-1f).rf, zero)
  }
  return RemoteBezierValue(
    true,
    incoming,
    outgoing,
    vertices,
    RemoteBezierTopology(logicalVertices, logicalSegments),
    visibility = path.visibility,
  )
}

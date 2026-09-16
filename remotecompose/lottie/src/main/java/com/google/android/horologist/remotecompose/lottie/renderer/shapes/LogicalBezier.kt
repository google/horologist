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
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierTopology
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.rawReference

internal data class LogicalPoint(val x: RemoteFloat, val y: RemoteFloat) {
  fun values() = listOf(x, y)

  fun reference() = LogicalPoint(x.rawReference(), y.rawReference())
}

private val logicalZero
  get() = LogicalPoint(0f.rf, 0f.rf)

internal fun logicalChoose(flag: RemoteFloat, yes: LogicalPoint, no: LogicalPoint) =
  when (flag.constantValueOrNull) {
    0f -> no
    1f -> yes
    else ->
      LogicalPoint(selectIfGe(flag, 1f.rf, yes.x, no.x), selectIfGe(flag, 1f.rf, yes.y, no.y))
        .reference()
  }

/** Nearest active item in path order, in O(capacity) expressions rather than pairwise lookups. */
internal fun logicalScan(
  values: List<LogicalPoint>,
  active: List<RemoteFloat>,
  closed: Boolean,
  forward: Boolean,
  inclusive: Boolean,
): List<LogicalPoint> {
  val indices = if (forward) values.indices.reversed() else values.indices
  var current = logicalZero
  if (closed) for (j in indices) current = logicalChoose(active[j], values[j], current)
  val result = MutableList(values.size) { logicalZero }
  for (j in indices) {
    if (!inclusive) result[j] = current
    current = logicalChoose(active[j], values[j], current)
    if (inclusive) result[j] = current
  }
  return result
}

/** Logical handles can live on an adjacent collapsed recording slot rather than its vertex. */
internal class LogicalBezier(val path: RemoteBezierValue) {
  private val count = path.vertices.size

  private fun read(values: List<List<RemoteFloat>>) =
    List(count) { j ->
      LogicalPoint(
        values.getOrNull(j)?.getOrNull(0) ?: 0f.rf,
        values.getOrNull(j)?.getOrNull(1) ?: 0f.rf,
      )
    }

  val vertices = read(path.vertices)
  val active = path.topology?.vertices ?: List(count) { 1f.rf }
  val segments =
    path.topology?.segments ?: List(count) { if (!path.closed && it == count - 1) 0f.rf else 1f.rf }
  private val rawIncoming = read(path.inTangents)
  private val rawOutgoing = read(path.outTangents)
  val incoming =
    logicalScan(List(count) { rawIncoming[(it + 1) % count] }, segments, path.closed, false, false)
  val outgoing = logicalScan(rawOutgoing, segments, path.closed, true, true)
  val previous = logicalScan(vertices, active, path.closed, false, false)
  val next = logicalScan(vertices, active, path.closed, true, false)

  /** Empty slots close at the next logical vertex; only real vertices own outgoing edges. */
  fun canonical(): RemoteBezierValue {
    if (count == 0 || path.topology == null) return path
    val nextVertex = logicalScan(vertices, active, path.closed, true, true)
    // If the active vertices are exactly the edge starts, the retained outgoing control is
    // already on that slot, and its incoming control is already on the following physical
    // slot. Searching to the next logical vertex and back to this edge is an identity.
    val ownsEdges = path.closed && active == segments
    val nextIncoming =
      if (ownsEdges) rawIncoming else logicalScan(incoming, active, path.closed, true, true)
    // In a closed contour every active vertex has a successor (possibly itself). A circular
    // per-slot search for that same Boolean duplicates hundreds of states on padded contours.
    val hasAny =
      if (path.closed) active.fold(0f.rf) { result, flag -> max(result, flag) }.rawReference()
      else null
    val hasNext =
      if (path.closed) emptyList()
      else logicalScan(active.map { LogicalPoint(it, it) }, active, false, true, false)
    val edges = if (path.closed) active else List(count) { active[it] * hasNext[it].x }
    val points =
      List(count) { j ->
        // Open trailing padding remains at the final point, not at the scan's zero sentinel.
        if (hasAny != null) logicalChoose(hasAny, nextVertex[j], vertices[j])
        else
          logicalChoose(
            active[j],
            vertices[j],
            logicalChoose(hasNext[j].x, nextVertex[j], vertices[j]),
          )
      }
    return path.copy(
      vertices = points.map { it.values() },
      inTangents =
        List(count) { j ->
          val previousEdge = if (j == 0 && !path.closed) 0f.rf else edges[(j + count - 1) % count]
          logicalChoose(previousEdge, nextIncoming[j], logicalZero).values()
        },
      outTangents =
        List(count) {
          logicalChoose(edges[it], if (ownsEdges) rawOutgoing[it] else outgoing[it], logicalZero)
            .values()
        },
      topology = RemoteBezierTopology(active, edges),
    )
  }
}

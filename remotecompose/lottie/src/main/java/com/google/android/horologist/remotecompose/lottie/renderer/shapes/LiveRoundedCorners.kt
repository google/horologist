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
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import androidx.compose.remote.creation.compose.state.selectIfLe
import androidx.compose.remote.creation.compose.state.sqrt
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierTopology
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue

private data class RoundPoint(val x: RemoteFloat, val y: RemoteFloat) {
  operator fun plus(other: RoundPoint) = RoundPoint(x + other.x, y + other.y)

  operator fun minus(other: RoundPoint) = RoundPoint(x - other.x, y - other.y)

  operator fun times(scale: RemoteFloat) = RoundPoint(x * scale, y * scale)

  fun length() = sqrt(x * x + y * y)

  fun values() = listOf(x, y)
}

private data class RoundVertex(
  val point: RoundPoint,
  val incoming: RoundPoint,
  val outgoing: RoundPoint,
)

private fun roundChoose(flag: RemoteFloat, yes: RoundPoint, no: RoundPoint) =
  RoundPoint(selectIfGe(flag, 1f.rf, yes.x, no.x), selectIfGe(flag, 1f.rf, yes.y, no.y))

/**
 * Live sharp-corner splitting with independently clamped adjacent edges. Logical adjacency is
 * independent of recording padding, including changing polystars and earlier modifier output.
 */
internal fun roundRemoteBezier(path: RemoteBezierValue, radius: RemoteFloat): RemoteBezierValue {
  val capacity = path.vertices.size
  if (capacity < 2 || radius.constantValueOrNull == 0f) return path
  require(capacity <= 32768) { "Rounded corners exceed 65536 recorded vertices" }
  require(radius.constantValueOrNull?.isFinite() != false) { "Invalid rounded-corner radius" }
  // Share the evaluated radius across topology tests and coordinates. Inlining a keyframed
  // radius into successive rounding stages produces a playback-only cusp on alpha19.
  val r =
    max(radius, 0f.rf).let { if (it.constantValueOrNull == null) it.createReference() else it }
  val zero = RoundPoint(0f.rf, 0f.rf)
  val logical = LogicalBezier(path)
  fun LogicalPoint.roundPoint() = RoundPoint(x, y)
  val hasPrevious =
    logicalScan(
      logical.active.map { LogicalPoint(it, it) },
      logical.active,
      path.closed,
      false,
      false,
    )
  val hasNext =
    logicalScan(
      logical.active.map { LogicalPoint(it, it) },
      logical.active,
      path.closed,
      true,
      false,
    )
  val output = ArrayList<RoundVertex>(capacity * 2)
  val activeVertices = ArrayList<RemoteFloat>(capacity * 2)
  val activeSegments = ArrayList<RemoteFloat>(capacity * 2)
  for (j in 0 until capacity) {
    val current = logical.vertices[j].roundPoint()
    val previous = logical.previous[j].roundPoint()
    val next = logical.next[j].roundPoint()
    val incoming = logical.incoming[j].roundPoint()
    val outgoing = logical.outgoing[j].roundPoint()
    val sharp =
      selectIfLe(
        abs(incoming.x) + abs(incoming.y) + abs(outgoing.x) + abs(outgoing.y),
        0f.rf,
        1f.rf,
        0f.rf,
      )
    val round = sharp * hasPrevious[j].x * hasNext[j].x
    val sourceActive = logical.active[j]
    val split = sourceActive * round * selectIfLe(r, 0f.rf, 0f.rf, 1f.rf)
    activeVertices.add(sourceActive)
    activeVertices.add(split)
    activeSegments.add(split)
    activeSegments.add(sourceActive * hasNext[j].x)
    fun toward(neighbor: RoundPoint): RoundPoint {
      val delta = neighbor - current
      val length = delta.length()
      val amount = selectIfLe(length, 0f.rf, 0f.rf, min(0.5f.rf, r / max(length, 0.000001f.rf)))
      return current + delta * amount
    }
    val a = roundChoose(round, toward(previous), current)
    val b = roundChoose(round, toward(next), current)
    output.add(
      RoundVertex(
        a,
        roundChoose(round, zero, incoming),
        roundChoose(round, (current - a) * 0.5519f.rf, zero),
      )
    )
    output.add(
      RoundVertex(
        b,
        roundChoose(round, (current - b) * 0.5519f.rf, zero),
        roundChoose(round, zero, outgoing),
      )
    )
  }
  // The real incoming control belongs to A, the real outgoing control to B. Intermediate
  // padding must first be placed at the next A before canonical redistribution of those handles.
  val nextA =
    logicalScan(
      output
        .filterIndexed { index, _ -> index % 2 == 0 }
        .map { LogicalPoint(it.point.x, it.point.y) },
      logical.active,
      path.closed,
      true,
      true,
    )
  val incomingA =
    logicalScan(
      output
        .filterIndexed { index, _ -> index % 2 == 0 }
        .map { LogicalPoint(it.incoming.x, it.incoming.y) },
      logical.active,
      path.closed,
      true,
      true,
    )
  for (j in 0 until capacity) {
    if (logical.active[j].constantValueOrNull == 1f) continue
    for (k in 0..1) {
      val v = output[j * 2 + k]
      output[j * 2 + k] =
        RoundVertex(
          roundChoose(logical.active[j], v.point, nextA[j].roundPoint()),
          roundChoose(
            logical.active[j],
            v.incoming,
            if (k == 0) incomingA[j].roundPoint() else zero,
          ),
          roundChoose(logical.active[j], v.outgoing, zero),
        )
    }
  }
  return LogicalBezier(
      RemoteBezierValue(
        path.closed,
        output.map { it.incoming.values() },
        output.map { it.outgoing.values() },
        output.map { it.point.values() },
        RemoteBezierTopology(activeVertices, activeSegments),
        visibility = path.visibility,
      )
    )
    .canonical()
}

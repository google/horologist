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

package com.google.android.horologist.remotecompose.lottie.renderer.shapes

import com.google.android.horologist.remotecompose.lottie.format.values.BezierValue
import kotlin.math.absoluteValue
import kotlin.math.hypot

/** Represents a 2D point used for Bézier calculations. */
internal data class Point(val x: Float, val y: Float) {
  operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

  operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)

  operator fun times(scalar: Float): Point = Point(x * scalar, y * scalar)

  fun distanceTo(other: Point): Float = hypot(x - other.x, y - other.y)

  companion object {
    fun lerp(p0: Point, p1: Point, t: Float): Point =
      Point(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t)
  }
}

/**
 * A cubic Bézier curve segment defined by start point [p0], start control point [p1], end control
 * point [p2], and end point [p3].
 */
internal data class CubicSegment(val p0: Point, val p1: Point, val p2: Point, val p3: Point) {
  /** Evaluates the point on the cubic Bézier curve at parameter [t] in [0, 1]. */
  fun pointAt(t: Float): Point {
    val clampedT = t.coerceIn(0f, 1f)
    val u = 1f - clampedT
    val tt = clampedT * clampedT
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * clampedT

    val x = uuu * p0.x + 3f * uu * clampedT * p1.x + 3f * u * tt * p2.x + ttt * p3.x
    val y = uuu * p0.y + 3f * uu * clampedT * p1.y + 3f * u * tt * p2.y + ttt * p3.y
    return Point(x, y)
  }

  /** Approximates the arc length of the cubic segment by sampling [samples] sub-intervals. */
  fun approximateLength(samples: Int = 16): Float {
    var length = 0f
    var prev = p0
    for (i in 1..samples) {
      val t = i.toFloat() / samples
      val curr = pointAt(t)
      length += prev.distanceTo(curr)
      prev = curr
    }
    return length
  }

  /** Computes a cumulative length table of size [samples] + 1 for arc-length parameterization. */
  fun computeLengthTable(samples: Int = 16): FloatArray {
    val table = FloatArray(samples + 1)
    table[0] = 0f
    var prev = p0
    var accumulated = 0f
    for (i in 1..samples) {
      val t = i.toFloat() / samples
      val curr = pointAt(t)
      accumulated += prev.distanceTo(curr)
      table[i] = accumulated
      prev = curr
    }
    return table
  }

  /**
   * Finds the parametric value [t] corresponding to [targetDistance] using linear interpolation
   * between samples in [lengthTable].
   */
  fun tAtDistance(targetDistance: Float, lengthTable: FloatArray): Float {
    val totalLength = lengthTable.last()
    if (targetDistance <= 0f || totalLength <= 0f) return 0f
    if (targetDistance >= totalLength) return 1f

    val samples = lengthTable.size - 1
    for (i in 0 until samples) {
      val d0 = lengthTable[i]
      val d1 = lengthTable[i + 1]
      if (targetDistance in d0..d1) {
        val segmentDist = d1 - d0
        val alpha = if (segmentDist > 0.00001f) (targetDistance - d0) / segmentDist else 0f
        return (i + alpha) / samples
      }
    }
    return 1f
  }

  /**
   * Splits this cubic Bézier curve at parameter [t] into two curves using de Casteljau's algorithm.
   */
  fun split(t: Float): Pair<CubicSegment, CubicSegment> {
    val clampedT = t.coerceIn(0f, 1f)
    val p01 = Point.lerp(p0, p1, clampedT)
    val p12 = Point.lerp(p1, p2, clampedT)
    val p23 = Point.lerp(p2, p3, clampedT)

    val p012 = Point.lerp(p01, p12, clampedT)
    val p123 = Point.lerp(p12, p23, clampedT)

    val p0123 = Point.lerp(p012, p123, clampedT)

    val left = CubicSegment(p0, p01, p012, p0123)
    val right = CubicSegment(p0123, p123, p23, p3)
    return Pair(left, right)
  }

  /**
   * Trims this cubic Bézier segment between parameters [tStart] and [tEnd], where 0 <= tStart <=
   * tEnd <= 1.
   */
  fun subsegment(tStart: Float, tEnd: Float): CubicSegment {
    val s = tStart.coerceIn(0f, 1f)
    val e = tEnd.coerceIn(0f, 1f)
    if (s <= 0f && e >= 1f) return this
    if (s >= e) {
      val pt = pointAt(s)
      return CubicSegment(pt, pt, pt, pt)
    }

    val (_, rightOfStart) = split(s)
    val scaledEnd = ((e - s) / (1f - s)).coerceIn(0f, 1f)
    val (sub, _) = rightOfStart.split(scaledEnd)
    return sub
  }
}

/** Converts a [BezierValue] subpath into a list of [CubicSegment]s. */
internal fun BezierValue.toCubicSegments(): List<CubicSegment> {
  val count = vertices.size
  if (count == 0 || (count == 1 && !closed.constantValue)) return emptyList()

  val maxIndex = if (closed.constantValue) count else count - 1
  val segments = mutableListOf<CubicSegment>()

  for (i in 0 until maxIndex) {
    val nextIndex = (i + 1) % count
    val p0 = Point(vertices[i].getOrElse(0) { 0f }, vertices[i].getOrElse(1) { 0f })
    val outTangent = outTangents.getOrNull(i)
    val p1 =
      Point(
        p0.x + (outTangent?.getOrElse(0) { 0f } ?: 0f),
        p0.y + (outTangent?.getOrElse(1) { 0f } ?: 0f),
      )

    val p3 = Point(vertices[nextIndex].getOrElse(0) { 0f }, vertices[nextIndex].getOrElse(1) { 0f })
    val inTangent = inTangents.getOrNull(nextIndex)
    val p2 =
      Point(
        p3.x + (inTangent?.getOrElse(0) { 0f } ?: 0f),
        p3.y + (inTangent?.getOrElse(1) { 0f } ?: 0f),
      )

    segments.add(CubicSegment(p0, p1, p2, p3))
  }

  return segments
}

/** Converts a list of connected [CubicSegment]s into a [BezierValue] open subpath. */
internal fun List<CubicSegment>.toBezierValue(): BezierValue {
  if (isEmpty()) {
    return BezierValue(
      closed = false,
      inTangents = emptyList(),
      outTangents = emptyList(),
      vertices = emptyList(),
    )
  }

  val vertices = mutableListOf<List<Float>>()
  val inTangents = mutableListOf<List<Float>>()
  val outTangents = mutableListOf<List<Float>>()

  // First vertex
  vertices.add(listOf(this[0].p0.x, this[0].p0.y))
  inTangents.add(listOf(0f, 0f))
  outTangents.add(listOf(this[0].p1.x - this[0].p0.x, this[0].p1.y - this[0].p0.y))

  for (i in 0 until size - 1) {
    val curr = this[i]
    val next = this[i + 1]
    vertices.add(listOf(curr.p3.x, curr.p3.y))
    inTangents.add(listOf(curr.p2.x - curr.p3.x, curr.p2.y - curr.p3.y))
    outTangents.add(listOf(next.p1.x - next.p0.x, next.p1.y - next.p0.y))
  }

  // Last vertex
  val last = this.last()
  vertices.add(listOf(last.p3.x, last.p3.y))
  inTangents.add(listOf(last.p2.x - last.p3.x, last.p2.y - last.p3.y))
  outTangents.add(listOf(0f, 0f))

  return BezierValue(
    closed = false,
    inTangents = inTangents,
    outTangents = outTangents,
    vertices = vertices,
  )
}

/**
 * Dynamically trims a [BezierValue] subpath based on [startFraction], [endFraction], and
 * [offsetFraction].
 *
 * All fractions are normalized to [0, 1] interval (where 100% = 1.0, 360 deg = 1.0). Supports
 * wrapping intervals, closed/open subpaths, and degenerate structure preservation.
 */
internal fun trimBezierValue(
  subpath: BezierValue,
  startFraction: Float,
  endFraction: Float,
  offsetFraction: Float = 0f,
  keepStructureIfDegenerate: Boolean = true,
): List<BezierValue> {
  if (subpath.vertices.isEmpty()) return emptyList()
  val segments = subpath.toCubicSegments()
  if (segments.isEmpty()) return emptyList()

  val diff = (endFraction - startFraction).absoluteValue
  if (diff >= 1f && offsetFraction == 0f) {
    return if (keepStructureIfDegenerate) listOf(segments.toBezierValue()) else listOf(subpath)
  }

  val lengthTables = segments.map { it.computeLengthTable(256) }
  val segmentLengths = lengthTables.map { it.last() }
  val totalLength = segmentLengths.sum()
  if (totalLength <= 0.0001f) {
    return emptyList()
  }

  val s = startFraction + offsetFraction
  val e = endFraction + offsetFraction

  val minVal = minOf(s, e)
  val maxVal = maxOf(s, e)
  val span = maxVal - minVal

  if (span <= 0.00001f) {
    if (!keepStructureIfDegenerate) return emptyList()
    val startNorm = ((minVal % 1f) + 1f) % 1f
    val normDist = startNorm * totalLength

    var accumulated = 0f
    var degPoint = segments.first().p0
    for (idx in segments.indices) {
      val segLen = segmentLengths[idx]
      if (normDist <= accumulated + segLen || idx == segments.lastIndex) {
        val distInSeg = (normDist - accumulated).coerceIn(0f, segLen)
        val t = segments[idx].tAtDistance(distInSeg, lengthTables[idx])
        degPoint = segments[idx].pointAt(t)
        break
      }
      accumulated += segLen
    }

    val vertexCount = segments.size + 1
    val vertices = List(vertexCount) { listOf(degPoint.x, degPoint.y) }
    val inTangents = List(vertexCount) { listOf(0f, 0f) }
    val outTangents = List(vertexCount) { listOf(0f, 0f) }
    return listOf(
      BezierValue(
        closed = false,
        inTangents = inTangents,
        outTangents = outTangents,
        vertices = vertices,
      )
    )
  }

  if (span >= 1f) {
    return if (keepStructureIfDegenerate) listOf(segments.toBezierValue()) else listOf(subpath)
  }

  val startNorm = ((minVal % 1f) + 1f) % 1f
  val endNorm = startNorm + span

  val intervals = mutableListOf<Pair<Float, Float>>()
  if (endNorm <= 1f) {
    intervals.add(startNorm * totalLength to endNorm * totalLength)
  } else {
    intervals.add(startNorm * totalLength to totalLength)
    intervals.add(0f to (endNorm - 1f) * totalLength)
  }

  val result = mutableListOf<BezierValue>()
  for ((dStart, dEnd) in intervals) {
    if (dStart >= dEnd) continue
    val trimmedSegments = mutableListOf<CubicSegment>()

    var pTrimStart: Point? = null
    var pTrimEnd: Point? = null
    var acc = 0f
    for (idx in segments.indices) {
      val seg = segments[idx]
      val segLen = segmentLengths[idx]
      val segStart = acc
      val segEnd = acc + segLen
      acc = segEnd
      if (pTrimStart == null && dStart <= segEnd) {
        val distInSeg = (dStart - segStart).coerceIn(0f, segLen)
        val t = if (segLen > 0.0001f) seg.tAtDistance(distInSeg, lengthTables[idx]) else 0f
        pTrimStart = seg.pointAt(t)
      }
      if (pTrimEnd == null && dEnd <= segEnd) {
        val distInSeg = (dEnd - segStart).coerceIn(0f, segLen)
        val t = if (segLen > 0.0001f) seg.tAtDistance(distInSeg, lengthTables[idx]) else 1f
        pTrimEnd = seg.pointAt(t)
      }
    }
    val startPt = pTrimStart ?: segments.first().p0
    val endPt = pTrimEnd ?: segments.last().p3

    var accumulated = 0f
    for (idx in segments.indices) {
      val seg = segments[idx]
      val segLen = segmentLengths[idx]
      val segStart = accumulated
      val segEnd = accumulated + segLen
      accumulated = segEnd

      if (segEnd <= dStart) {
        if (keepStructureIfDegenerate) {
          trimmedSegments.add(CubicSegment(startPt, startPt, startPt, startPt))
        }
      } else if (segStart >= dEnd) {
        if (keepStructureIfDegenerate) {
          trimmedSegments.add(CubicSegment(endPt, endPt, endPt, endPt))
        }
      } else {
        val overlapStart = maxOf(dStart, segStart)
        val overlapEnd = minOf(dEnd, segEnd)
        if (overlapStart < overlapEnd && segLen > 0.0001f) {
          val distStartInSeg = (overlapStart - segStart).coerceIn(0f, segLen)
          val distEndInSeg = (overlapEnd - segStart).coerceIn(0f, segLen)

          val t0 = seg.tAtDistance(distStartInSeg, lengthTables[idx])
          val t1 = seg.tAtDistance(distEndInSeg, lengthTables[idx])

          trimmedSegments.add(seg.subsegment(t0, t1))
        } else if (keepStructureIfDegenerate) {
          trimmedSegments.add(CubicSegment(startPt, startPt, startPt, startPt))
        }
      }
    }

    if (trimmedSegments.isNotEmpty()) {
      result.add(trimmedSegments.toBezierValue())
    }
  }

  return result
}

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

import kotlin.math.abs
import kotlin.math.hypot

private data class HomogeneousPoint(val x: Double, val y: Double, val weight: Double) {
  fun midpoint(other: HomogeneousPoint) =
    HomogeneousPoint((x + other.x) / 2, (y + other.y) / 2, (weight + other.weight) / 2)
}

internal data class ConicCubic(val points: FloatArray, val from: Double, val to: Double)

/**
 * Convert a native rational quadratic to polynomial cubics without dropping its weight. The
 * degree-five numerator of (rational curve - cubic) bounds the error over the whole interval,
 * rather than checking a few sample points. Subdivision is spatial, never temporal.
 */
internal fun conicToCubics(points: FloatArray): List<ConicCubic> {
  require((0..6).all { points[it].isFinite() } && points[6] >= 0f) {
    "Cannot convert a non-finite or negative-weight conic"
  }
  val w = points[6].toDouble()
  val a = HomogeneousPoint(points[0].toDouble(), points[1].toDouble(), 1.0)
  val b = HomogeneousPoint(points[2] * w, points[3] * w, w)
  val c = HomogeneousPoint(points[4].toDouble(), points[5].toDouble(), 1.0)
  // At very large coordinates Float quantization is already larger than the geometric bound.
  val tolerance = maxOf(.0001, (0..5).maxOf { Math.ulp(abs(points[it])).toDouble() } * 4)
  val result = mutableListOf<ConicCubic>()
  val choose2 = doubleArrayOf(1.0, 2.0, 1.0)
  val choose3 = doubleArrayOf(1.0, 3.0, 3.0, 1.0)
  val choose5 = doubleArrayOf(1.0, 5.0, 10.0, 10.0, 5.0, 1.0)
  fun emit(
    a: HomogeneousPoint,
    b: HomogeneousPoint,
    c: HomogeneousPoint,
    from: Double,
    to: Double,
    depth: Int,
  ) {
    val h = listOf(a, b, c)
    val minimumWeight = h.minOf { it.weight }
    if (minimumWeight > 0.0) {
      val x0 = a.x / a.weight
      val y0 = a.y / a.weight
      val x2 = c.x / c.weight
      val y2 = c.y / c.weight
      val cubic =
        doubleArrayOf(
          x0,
          y0,
          x0 + 2 * (b.x - b.weight * x0) / (3 * a.weight),
          y0 + 2 * (b.y - b.weight * y0) / (3 * a.weight),
          x2 + 2 * (b.x - b.weight * x2) / (3 * c.weight),
          y2 + 2 * (b.y - b.weight * y2) / (3 * c.weight),
          x2,
          y2,
        )
      var error = 0.0
      for (k in 0..5) {
        var ex = 0.0
        var ey = 0.0
        for (j in 0..2) {
          val i = k - j
          if (i !in 0..3) continue
          val factor = choose2[j] * choose3[i] / choose5[k]
          ex += factor * (h[j].x - h[j].weight * cubic[2 * i])
          ey += factor * (h[j].y - h[j].weight * cubic[2 * i + 1])
        }
        error = maxOf(error, hypot(ex, ey) / minimumWeight)
      }
      if (error <= tolerance) {
        result.add(ConicCubic(FloatArray(8) { cubic[it].toFloat() }, from, to))
        return
      }
    }
    check(depth < 20) { "Conic conversion did not converge to the required spatial precision" }
    val ab = a.midpoint(b)
    val bc = b.midpoint(c)
    val middle = ab.midpoint(bc)
    val split = (from + to) / 2
    emit(a, ab, middle, from, split, depth + 1)
    emit(middle, bc, c, split, to, depth + 1)
  }
  emit(a, b, c, 0.0, 1.0, 0)
  return result
}

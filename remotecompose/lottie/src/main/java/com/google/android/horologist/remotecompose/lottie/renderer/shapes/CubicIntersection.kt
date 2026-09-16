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

import androidx.compose.remote.core.operations.FloatExpression
import androidx.compose.remote.core.operations.Utils
import androidx.compose.remote.core.operations.utilities.AnimatedFloatExpression as Op
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfGe
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteFloatFunction
import kotlin.math.abs

/**
 * Search multiple basins of adjacent cubic intersections, returning the lowest validated t. The 3x3
 * endpoint-inclusive seeds sample curve parameter space, never animation time. This bounded search
 * is not exhaustive root isolation; tangencies and multiple roots in one basin need further work.
 */
internal fun cubicIntersection(
  a: List<RemoteFloat>,
  b: List<RemoteFloat>,
  enabled: RemoteFloat = 1f.rf,
): Pair<RemoteFloat, RemoteFloat> {
  require(a.size == 8 && b.size == 8)
  if (enabled.constantValueOrNull == 0f) return 1f.rf to 0f.rf
  val inputs = a + b
  val result =
    if (inputs.all { it.constantValueOrNull != null }) {
      val roots = constantIntersection(inputs.map { it.constantValue }.toFloatArray())
      listOf(
        selectIfGe(enabled, 1f.rf, roots[0].rf, 1f.rf),
        selectIfGe(enabled, 1f.rf, roots[1].rf, 0f.rf),
      )
    } else intersectionFunction(inputs + enabled)
  return result[0] to result[1]
}

private fun coefficients(points: FloatArray, axis: Int): FloatArray =
  floatArrayOf(
    points[axis + 6] - points[axis + 4] * 3f + points[axis + 2] * 3f - points[axis],
    (points[axis + 4] - points[axis + 2] * 2f + points[axis]) * 3f,
    (points[axis + 2] - points[axis]) * 3f,
    points[axis],
  )

private fun constantIntersection(points: FloatArray): FloatArray {
  val ax = coefficients(points, 0)
  val ay = coefficients(points, 1)
  val bx = coefficients(points, 8)
  val by = coefficients(points, 9)
  fun point(c: FloatArray, t: Float) = ((c[0] * t + c[1]) * t + c[2]) * t + c[3]
  fun derivative(c: FloatArray, t: Float) = (c[0] * (3f * t) + c[1] * 2f) * t + c[2]
  var bestT = 1f
  var bestU = 0f
  repeat(9) { seed ->
    var t = (seed / 3) / 2f
    var u = (seed % 3) / 2f
    repeat(10) {
      val ex = point(ax, t) - point(bx, u)
      val ey = point(ay, t) - point(by, u)
      val dax = derivative(ax, t)
      val day = derivative(ay, t)
      val dbx = derivative(bx, u)
      val dby = derivative(by, u)
      val det = dax * dby - day * dbx
      val safe = if (abs(det) > 0.000001f) det else 1f
      t = (t - (ex * dby - ey * dbx) / safe).coerceIn(0f, 1f)
      u = (u - (ex * day - ey * dax) / safe).coerceIn(0f, 1f)
    }
    val ex = point(ax, t) - point(bx, u)
    val ey = point(ay, t) - point(by, u)
    if (ex * ex + ey * ey < 0.0005f * 0.0005f && t < 0.99999f && u > 0.00001f && t < bestT) {
      bestT = t
      bestU = u
    }
  }
  return floatArrayOf(bestT, bestU)
}

private val intersectionFunction =
  RemoteFloatFunction(17, 2) { writer, args ->
    with(writer) {
      fun expression(vararg values: Float) = floatExpression(*values)
      fun assign(id: Float, vararg values: Float) =
        FloatExpression.apply(buffer.buffer, Utils.idFromNan(id), values, null)
      fun bound(axis: Int, op: Float) =
        expression(args[axis], args[axis + 2], op, args[axis + 4], op, args[axis + 6], op)
      fun overlap(axis: Int) =
        expression(
          bound(axis, Op.MAX),
          bound(axis + 8, Op.MAX),
          Op.MIN,
          bound(axis, Op.MIN),
          bound(axis + 8, Op.MIN),
          Op.MAX,
          Op.SUB,
        )
      // Cubics stay inside their control-polygon bounds. Skip disjoint pairs and inactive
      // logical groups at playback, rather than searching for a result that will be ignored.
      val overlapX = overlap(0)
      val overlapY = overlap(1)
      val boundedIterations =
        expression(
          0f,
          args[16],
          9f,
          Op.MUL,
          overlapX,
          overlapY,
          Op.MIN,
          .000001f,
          Op.ADD,
          Op.IFELSE,
        )
      // If the control hulls lie strictly on opposite sides of a shared endpoint's tangent
      // plane, their interiors cannot intersect. Ignore only float-roundoff at that endpoint.
      val axisX = expression(args[6], args[4], Op.SUB)
      val axisY = expression(args[7], args[5], Op.SUB)
      fun projection(index: Int) =
        expression(
          args[index],
          args[6],
          Op.SUB,
          axisX,
          Op.MUL,
          args[index + 1],
          args[7],
          Op.SUB,
          axisY,
          Op.MUL,
          Op.ADD,
        )
      val maxA = expression(projection(0), projection(2), Op.MAX, projection(4), Op.MAX)
      val minB = expression(projection(10), projection(12), Op.MIN, projection(14), Op.MIN)
      val endX = expression(args[6], args[8], Op.SUB)
      val endY = expression(args[7], args[9], Op.SUB)
      val endDistance = expression(endX, endX, Op.MUL, endY, endY, Op.MUL, Op.ADD)
      val separated =
        expression(0f, maxA, Op.SUB, minB, Op.MIN, 1e-10f, endDistance, Op.SUB, Op.MIN)
      val iterations = expression(boundedIterations, 0f, separated, Op.IFELSE)
      fun coefficients(axis: Int) =
        floatArrayOf(
          expression(
            args[axis + 6],
            args[axis + 4],
            3f,
            Op.MUL,
            Op.SUB,
            args[axis + 2],
            3f,
            Op.MUL,
            Op.ADD,
            args[axis],
            Op.SUB,
          ),
          expression(
            args[axis + 4],
            args[axis + 2],
            2f,
            Op.MUL,
            Op.SUB,
            args[axis],
            Op.ADD,
            3f,
            Op.MUL,
          ),
          expression(args[axis + 2], args[axis], Op.SUB, 3f, Op.MUL),
          args[axis],
        )
      val ax = coefficients(0)
      val ay = coefficients(1)
      val bx = coefficients(8)
      val by = coefficients(9)
      fun point(c: FloatArray, t: Float) =
        floatArrayOf(
          c[0],
          t,
          Op.MUL,
          c[1],
          Op.ADD,
          t,
          Op.MUL,
          c[2],
          Op.ADD,
          t,
          Op.MUL,
          c[3],
          Op.ADD,
        )
      fun derivative(c: FloatArray, t: Float) =
        expression(c[0], 3f, t, Op.MUL, Op.MUL, c[1], 2f, Op.MUL, Op.ADD, t, Op.MUL, c[2], Op.ADD)
      val bestT = createFloatId()
      val bestU = createFloatId()
      val t = createFloatId()
      val u = createFloatId()
      assign(bestT, 1f)
      assign(bestU, 0f)
      val seed = startLoopVar(0f, 1f, iterations)
      assign(t, seed, 3f, Op.DIV, Op.FLOOR, 2f, Op.DIV)
      assign(u, seed, 3f, Op.MOD, 2f, Op.DIV)
      // An indexed loop refreshes dirty dependent expressions on every iteration.
      startLoopVar(0f, 1f, 10f)
      val ex = expression(*point(ax, t), *point(bx, u), Op.SUB)
      val ey = expression(*point(ay, t), *point(by, u), Op.SUB)
      val dax = derivative(ax, t)
      val day = derivative(ay, t)
      val dbx = derivative(bx, u)
      val dby = derivative(by, u)
      val det = expression(dax, dby, Op.MUL, day, dbx, Op.MUL, Op.SUB)
      // IFELSE is [falseValue, trueValue, condition]; only a positive condition selects true.
      val safe = expression(1f, det, det, Op.ABS, 0.000001f, Op.SUB, Op.IFELSE)
      assign(
        t,
        t,
        ex,
        dby,
        Op.MUL,
        ey,
        dbx,
        Op.MUL,
        Op.SUB,
        safe,
        Op.DIV,
        Op.SUB,
        0f,
        Op.MAX,
        1f,
        Op.MIN,
      )
      assign(
        u,
        u,
        ex,
        day,
        Op.MUL,
        ey,
        dax,
        Op.MUL,
        Op.SUB,
        safe,
        Op.DIV,
        Op.SUB,
        0f,
        Op.MAX,
        1f,
        Op.MIN,
      )
      endLoop()
      val finalX = expression(*point(ax, t), *point(bx, u), Op.SUB)
      val finalY = expression(*point(ay, t), *point(by, u), Op.SUB)
      val squaredError = expression(finalX, finalX, Op.MUL, finalY, finalY, Op.MUL, Op.ADD)
      val accepted =
        expression(
          0f,
          1f,
          0.0005f * 0.0005f,
          squaredError,
          Op.SUB,
          0.99999f,
          t,
          Op.SUB,
          Op.MIN,
          u,
          0.00001f,
          Op.SUB,
          Op.MIN,
          bestT,
          t,
          Op.SUB,
          Op.MIN,
          Op.IFELSE,
        )
      assign(bestT, bestT, t, accepted, Op.IFELSE)
      assign(bestU, bestU, u, accepted, Op.IFELSE)
      endLoop()
      floatArrayOf(bestT, bestU)
    }
  }

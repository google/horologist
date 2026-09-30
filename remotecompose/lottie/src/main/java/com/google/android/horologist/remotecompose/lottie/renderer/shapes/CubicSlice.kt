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

import androidx.compose.remote.core.operations.utilities.AnimatedFloatExpression as Op
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteFloatFunction

/** Shared de Casteljau evaluation; only the eight output coordinates consume state per call. */
internal val cubicSliceFunction =
  RemoteFloatFunction(10, 8) { writer, args ->
    with(writer) {
      fun expression(vararg values: Float) = floatExpression(*values)
      fun mix(a: Float, b: Float, t: Float) = expression(a, b, a, Op.SUB, t, Op.MUL, Op.ADD)
      val start = args[8]
      val end = args[9]
      val span = expression(end, start, Op.SUB, 3f, Op.DIV)
      val inverseStart = expression(1f, start, Op.SUB)
      val inverseEnd = expression(1f, end, Op.SUB)
      val result = FloatArray(8)
      for (axis in 0..1) {
        val p0 = args[axis]
        val p1 = args[axis + 2]
        val p2 = args[axis + 4]
        val p3 = args[axis + 6]
        fun point(t: Float): Float {
          val a = mix(p0, p1, t)
          val b = mix(p1, p2, t)
          val c = mix(p2, p3, t)
          return mix(mix(a, b, t), mix(b, c, t), t)
        }
        val d0 = expression(p1, p0, Op.SUB)
        val d1 = expression(p2, p1, Op.SUB)
        val d2 = expression(p3, p2, Op.SUB)
        fun derivative(t: Float, inverse: Float): Float {
          val a = expression(d0, inverse, inverse, Op.MUL, Op.MUL)
          val b = expression(d1, 2f, t, Op.MUL, inverse, Op.MUL, Op.MUL)
          val c = expression(d2, t, t, Op.MUL, Op.MUL)
          return expression(a, b, Op.ADD, c, Op.ADD, 3f, Op.MUL)
        }
        val a = point(start)
        val b = point(end)
        result[axis] = a
        result[axis + 2] = expression(a, derivative(start, inverseStart), span, Op.MUL, Op.ADD)
        result[axis + 4] = expression(b, derivative(end, inverseEnd), span, Op.MUL, Op.SUB)
        result[axis + 6] = b
      }
      result
    }
  }

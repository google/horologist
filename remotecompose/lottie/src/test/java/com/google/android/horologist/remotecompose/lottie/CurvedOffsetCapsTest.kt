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

package com.google.android.horologist.remotecompose.lottie

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import org.junit.Test

/** Analytic quarter-circle control polygons and circular caps; no production offset oracle. */
class CurvedOffsetCapsTest : MotionPixelHarness() {
  private data class P(val x: Double, val y: Double) {
    operator fun plus(p: P) = P(x + p.x, y + p.y)

    operator fun minus(p: P) = P(x - p.x, y - p.y)

    operator fun times(n: Double) = P(x * n, y * n)

    fun normal() = P(-y, x)

    fun rotate(theta: Double) = P(x * cos(theta) - y * sin(theta), x * sin(theta) + y * cos(theta))

    fun json() = "[$x,$y]"
  }

  private data class C(val a: P, val b: P, val c: P, val d: P) {
    fun reverse() = C(d, c, b, a)
  }

  private fun quarter(radius: Double, offset: Double = 0.0): C {
    val r = radius + offset
    val h = radius * (4.0 / 3.0) * tan(PI / 8) + offset * (sqrt(2.0) - 1)
    return C(P(40 - r, 40.0), P(40 - r, 40 - h), P(40 - h, 40 - r), P(40.0, 40 - r))
  }

  private fun cap(center: P, from: P, to: P, amount: Double, round: Boolean): List<C> {
    if (!round) return listOf(C(from, from, to, to))
    val radius = from - center
    val sweep = sign(amount) * PI / 2
    val k = (4.0 / 3.0) * tan(sweep / 4)
    return (0..1).map { i ->
      val a = radius.rotate(sweep * i)
      val b = radius.rotate(sweep * (i + 1))
      C(center + a, center + a + a.normal() * k, center + b - b.normal() * k, center + b)
    }
  }

  private fun geometry(curves: List<C>, closed: Boolean = false): String {
    val v = mutableListOf<P>()
    val inside = mutableListOf<P>()
    val outside = mutableListOf<P>()
    val zero = P(0.0, 0.0)
    for (curve in curves) {
      if (v.isEmpty() || v.last() != curve.a) {
        v.add(curve.a)
        inside.add(zero)
        outside.add(zero)
      }
      outside[outside.lastIndex] = curve.b - curve.a
      v.add(curve.d)
      inside.add(curve.c - curve.d)
      outside.add(zero)
    }
    if (closed && v.last() == v.first()) {
      inside[0] = inside.last()
      v.removeAt(v.lastIndex)
      inside.removeAt(inside.lastIndex)
      outside.removeAt(outside.lastIndex)
    }
    fun json(points: List<P>) = points.joinToString(",", "[", "]") { it.json() }
    return """{"c":$closed,"v":${json(v)},"i":${json(inside)},"o":${json(outside)}}"""
  }

  private fun expected(radius: Double, amount: Double, round: Boolean): String {
    if (amount == 0.0) return geometry(listOf(quarter(radius)))
    val forward = quarter(radius, amount)
    val backward = quarter(radius, -amount).reverse()
    return geometry(
      listOf(forward) +
        cap(P(40.0, 40 - radius), forward.d, backward.a, amount, round) +
        listOf(backward) +
        cap(P(40 - radius, 40.0), backward.d, forward.a, amount, round)
    )
  }

  @Test fun signedRoundCapsKeepTheirOutsideSweep() = verify(2, false, false)

  @Test fun signedRoundCapsAlsoMatchFilledOutline() = verify(2, false, true)

  @Test fun liveCurvedGeometryKeepsRoundCaps() = verify(2, true, false)

  @Test fun signedBevelCapsCloseTheOutline() = verify(3, false, false)

  @Test fun parallelMiterCapsFallBackToBevel() = verify(1, false, false)

  @Test fun singleVertexLoopKeepsLiveMiterOffset() = verifyLoop(1, false, false)

  @Test fun singleVertexLoopKeepsLiveBevelOffset() = verifyLoop(3, false, false)

  @Test fun singleVertexLoopMiterFillMatchesAnalyticControls() = verifyLoop(1, false, true)

  @Test fun singleVertexLoopBevelFillMatchesAnalyticControls() = verifyLoop(3, false, true)

  @Test fun singleVertexLoopKeepsMovingControls() = verifyLoop(1, true, false)

  @Test fun singleVertexLoopMovingFillMatchesAnalyticControls() = verifyLoop(3, true, true)

  @Test fun singleVertexLoopKeepsSignedRoundJoin() = verifyLoop(2, false, false)

  @Test fun singleVertexLoopRoundFillMatchesAnalyticArc() = verifyLoop(2, false, true)

  @Test fun singleVertexLoopMovingRoundJoinMatchesAnalyticArc() = verifyLoop(2, true, false)

  private fun verifyLoop(join: Int, moving: Boolean, fill: Boolean) {
    fun source(width: Double) =
      """{"c":true,"v":[[32,48]],"i":[[${-width},-32]],"o":[[$width,-32]]}"""
    fun expected(width: Double, amount: Double): String {
      if (amount == 0.0) return source(width)
      // Intersect the three parallel-offset edges of the isosceles control triangle.
      // These closed-form coordinates are independent of the renderer's normal/intersection code.
      val length = sqrt(width * width + 32.0 * 32.0)
      val a = P(32 - 32 * amount / length, 48 - width * amount / length)
      val b = P(32 + width - amount * (length + width) / 32, 16 + amount)
      val c = P(32 - width + amount * (length + width) / 32, 16 + amount)
      val d = P(32 + 32 * amount / length, 48 - width * amount / length)
      val joinPoint = P(32.0, 48 - amount * length / width)
      val curves = mutableListOf(C(a, b, c, d))
      if (join == 1) {
        curves.add(C(d, d, joinPoint, joinPoint))
        curves.add(C(joinPoint, joinPoint, a, a))
      } else if (join == 2) {
        val center = P(32.0, 48.0)
        val radius = d - center
        val half = -atan2(32.0, width)
        val k = 4.0 / 3.0 * tan(half / 4)
        for (index in 0..1) {
          val from = radius.rotate(half * index)
          val to = radius.rotate(half * (index + 1))
          curves.add(
            C(
              center + from,
              center + from + from.normal() * k,
              center + to - to.normal() * k,
              if (index == 1) a else center + to,
            )
          )
        }
      } else curves.add(C(d, d, a, a))
      return geometry(curves, closed = true)
    }
    val path =
      if (moving) animated("[${source(16.0)}]", "[${source(20.0)}]") else fixed(source(16.0))
    val amount = if (moving) fixed("4") else animated("[-4]", "[6]")
    val paint =
      if (fill) redFill
      else
        """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":100}"""
    assertMatchesStaticFrames(
      animation(
        """{"ty":"sh","ks":$path},{"ty":"op","a":$amount,"lj":$join,"ml":${fixed("100")}},$paint"""
      ),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "curveoffset-loop-$join-$moving-$fill",
      frames = (0..10).toList() + listOf(5, 0, 0),
      maxMeanRedError = 0.0001f,
      // The inward miter's final reference has only seven visible pixels at the 64px probe
      // grid. It is legitimate tiny artwork; keep the strict full-resolution pixel gate.
      minVisiblePixels = if (fill) 0 else 20,
      // Player control floats also pass the independent analytic coordinate test. Tiny float
      // differences can change native fill AA; require zero changed pixels away from the edge.
      maxMeanEdgeRedError = if (fill) .02f else null,
      staticAt = { frame ->
        val width = if (moving) 16 + frame * .4 else 16.0
        val a = if (moving) 4.0 else frame - 4.0
        animation("""{"ty":"sh","ks":${fixed(expected(width, a))}},$paint""")
      },
    )
  }

  @Test
  fun closedCurvedContourKeepsLiveSignedOffset() {
    fun circle(amount: Double): String {
      val q = quarter(16.0, amount)
      val curves =
        (0..3).map { index ->
          fun rotate(p: P) = (p - P(40.0, 40.0)).rotate(index * PI / 2) + P(32.0, 32.0)
          C(rotate(q.a), rotate(q.b), rotate(q.c), rotate(q.d))
        }
      // Trigonometric quarter turns may leave a tiny endpoint residue; close the final arc.
      return geometry(curves.dropLast(1) + curves.last().copy(d = curves.first().a), closed = true)
    }
    assertMatchesStaticFrames(
      animation(
        """{"ty":"sh","ks":${fixed(circle(0.0))}},{"ty":"op","a":${animated("[-4]", "[6]")},"lj":2,"ml":${fixed("8")}},$redFill"""
      ),
      referenceProbeAt = { Probe(32, 32, 1f) },
      artifactName = "curveoffset-circle",
      frames = (0..10).toList(),
      maxMeanRedError = 0.0001f,
      staticAt = { frame ->
        animation("""{"ty":"sh","ks":${fixed(circle(frame - 4.0))}},$redFill""")
      },
    )
  }

  private fun verify(join: Int, moving: Boolean, fill: Boolean) {
    val path =
      if (moving)
        animated("[${geometry(listOf(quarter(20.0)))}]", "[${geometry(listOf(quarter(28.0)))}]")
      else fixed(geometry(listOf(quarter(24.0))))
    val amount = if (moving) fixed("4") else animated("[-4]", "[6]")
    val paint =
      if (fill) redFill
      else
        """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""
    assertMatchesStaticFrames(
      animation(
        """{"ty":"sh","ks":$path},{"ty":"op","a":$amount,"lj":$join,"ml":${fixed("8")}},$paint"""
      ),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "curveoffset-quarter-$join-$moving-$fill",
      frames = (0..10).toList(),
      maxMeanRedError = 0.0001f,
      staticAt = { frame ->
        val radius = if (moving) 20 + frame * 0.8 else 24.0
        val a = if (moving) 4.0 else frame - 4.0
        animation("""{"ty":"sh","ks":${fixed(expected(radius, a, join == 2))}},$paint""")
      },
    )
  }
}

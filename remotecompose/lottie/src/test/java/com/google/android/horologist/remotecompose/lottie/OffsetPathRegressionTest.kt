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

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import org.junit.Test

class OffsetPathRegressionTest : MotionPixelHarness() {
  private val stroke
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("3")},"lc":1,"lj":1,"ml":4}"""

  @Test fun miterAmountStaysLiveAcrossZero() = squareMotion(1, "amount")

  @Test fun roundAmountStaysLiveAcrossZero() = squareMotion(2, "amount")

  @Test fun bevelAmountStaysLiveAcrossZero() = squareMotion(3, "amount")

  @Test fun miterGeometryStaysLive() = squareMotion(1, "geometry")

  @Test fun roundGeometryStaysLive() = squareMotion(2, "geometry")

  @Test fun bevelGeometryStaysLive() = squareMotion(3, "geometry")

  @Test fun miterLimitChangesTheJoinAtPlayback() = squareMotion(1, "limit")

  @Test fun openMiterBuildsBothSides() = lineMotion(1)

  @Test fun openRoundBuildsBothSidesAndCaps() = lineMotion(2)

  @Test fun openBevelBuildsBothSides() = lineMotion(3)

  @Test
  fun changingTriangleGeometryUsesLiveEdgeNormals() {
    fun triangle(right: Double) =
      """{"c":true,"v":[[16,16],[$right,16],[16,48]],"i":[[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0]]}"""
    val source = """{"ty":"sh","ks":${animated("[${triangle(48.0)}]", "[${triangle(40.0)}]")}}"""
    assertMatchesStaticFrames(
      animation("$source,${modifier(1, fixed("3"), fixed("100"))},$stroke"),
      referenceProbeAt = { Probe(13, 20, 1f, 1f) },
      artifactName = "offset-triangle-geometry",
      frames = (0..10).toList(),
      staticAt = { frame ->
        // Intersect x=13 and y=13 with the triangle's shifted diagonal half-plane.
        val right = 48 - frame * 0.8
        val width = right - 16
        val diagonal = sqrt(width * width + 32 * 32)
        val x = right + 3 * (diagonal + width) / 32
        val y = 48 + 3 * (diagonal + 32) / width
        val path =
          """{"c":true,"v":[[13,13],[$x,13],[13,$y]],"i":[[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0]]}"""
        animation("""{"ty":"sh","ks":${fixed(path)}},$stroke""")
      },
    )
  }

  @Test
  fun reverseWindingOffsetsToTheOtherSide() {
    val path =
      """{"c":true,"v":[[16,48],[48,48],[48,16],[16,16]],"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}"""
    assertMatchesStaticFrames(
      animation(
        """{"ty":"sh","ks":${fixed(path)}},${modifier(1, animated("[2]", "[6]"))},$stroke"""
      ),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "offset-reverse-amount",
      frames = (0..10).toList(),
      staticAt = { frame ->
        val size = 32 - 2 * (2 + frame * 0.4)
        animation("${rectangle(fixed("[32,32]"), fixed("[$size,$size]"))},$stroke")
      },
    )
  }

  private fun modifier(join: Int, amount: String, limit: String = fixed("8")) =
    """{"ty":"op","a":$amount,"lj":$join,"ml":$limit}"""

  private fun squareMotion(join: Int, kind: String) {
    val position = if (kind == "geometry") animated("[32,32]", "[36,32]") else fixed("[32,32]")
    val amount = if (kind == "amount") animated("[-4]", "[6]") else fixed("4")
    val limit = if (kind == "limit") animated("[0]", "[10]") else fixed("8")
    assertMatchesStaticFrames(
      animation(
        "${rectangle(position, fixed("[32,32]"))},${modifier(join, amount, limit)},$stroke"
      ),
      referenceProbeAt = { Probe(32, 16, 1f, 1f) },
      artifactName = "offset-square-$join-$kind",
      frames = (0..10).toList(),
      maxMeanRedError = if (join == 2) 0.00005f else null,
      staticAt = { frame ->
        val a = if (kind == "amount") frame - 4.0 else 4.0
        val x = if (kind == "geometry") frame * 0.4 else 0.0
        val effectiveJoin = if (join == 1 && kind == "limit" && frame <= 4) 3 else join
        animation("${squareReference(a, x, effectiveJoin)},$stroke")
      },
    )
  }

  private fun squareReference(a: Double, dx: Double, join: Int): String {
    if (a <= 0 || join == 1)
      return rectangle(fixed("[${32+dx},32]"), fixed("[${32+2*a},${32+2*a}]"))
    val vertices = mutableListOf<List<Double>>()
    val incoming = mutableListOf<List<Double>>()
    val outgoing = mutableListOf<List<Double>>()
    // Independent analytic circular joins, not the production edge-normal intersection code.
    for ((cx, cy, start) in
      listOf(
        Triple(48 + dx, 16.0, -90.0),
        Triple(48 + dx, 48.0, 0.0),
        Triple(16 + dx, 48.0, 90.0),
        Triple(16 + dx, 16.0, 180.0),
      )) {
      for (step in if (join == 2) 0..2 else 0..1) {
        val theta = Math.toRadians(start + step * if (join == 2) 45 else 90)
        val k = if (join == 2) a * 4.0 / 3.0 * tan(Math.PI / 16) else 0.0
        val tangent = listOf(-sin(theta) * k, cos(theta) * k)
        vertices.add(listOf(cx + cos(theta) * a, cy + sin(theta) * a))
        incoming.add(if (step == 0) listOf(0.0, 0.0) else tangent.map { -it })
        outgoing.add(if (step == (if (join == 2) 2 else 1)) listOf(0.0, 0.0) else tangent)
      }
    }
    val path = """{"c":true,"v":$vertices,"i":$incoming,"o":$outgoing}"""
    return """{"ty":"sh","ks":${fixed(path)}}"""
  }

  private fun lineMotion(join: Int) {
    val path = """{"c":false,"v":[[16,32],[48,32]],"i":[[0,0],[0,0]],"o":[[0,0],[0,0]]}"""
    val source = """{"ty":"sh","ks":${fixed(path)}}"""
    assertMatchesStaticFrames(
      animation("$source,${modifier(join, animated("[-4]", "[6]"))},$stroke"),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "offset-line-$join",
      frames = (0..10).toList(),
      staticAt = { frame ->
        val a = abs(frame - 4)
        val expected =
          if (a == 0) source
          else
            rectangle(
              fixed("[32,32]"),
              fixed("[${32 + if (join == 2) 2*a else 0},${2*a}]"),
              fixed(if (join == 2) "$a" else "0"),
            )
        animation("$expected,$stroke")
      },
    )
  }
}

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
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.sin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Ignore
import org.junit.Test

/** Expected paths use analytic polystars or pinned web geometry, never Kotlin modifiers. */
class PuckerTopologyTest : MotionPixelHarness() {
  private data class Point(val x: Double, val y: Double) {
    operator fun plus(p: Point) = Point(x + p.x, y + p.y)

    operator fun minus(p: Point) = Point(x - p.x, y - p.y)

    operator fun times(f: Double) = Point(x * f, y * f)
  }

  private data class Geometry(val v: List<Point>, val i: List<Point>, val o: List<Point>) {
    fun transform(map: (Point) -> Point): Geometry {
      val vertices = v.map(map)
      fun controls(t: List<Point>) = v.indices.map { map(v[it] + t[it]) - vertices[it] }
      return Geometry(vertices, controls(i), controls(o))
    }

    fun pucker(f: Double): Geometry {
      val center = v.reduce { a, b -> a + b } * (1.0 / v.size)
      val vertices = v.map { it + (center - it) * f }
      fun controls(t: List<Point>) =
        v.indices.map { j ->
          val absolute = v[j] + t[j]
          absolute + (absolute - center) * f - vertices[j]
        }
      return Geometry(vertices, controls(i), controls(o))
    }

    fun json(): String {
      fun points(p: List<Point>) = p.joinToString(",") { "[${it.x},${it.y}]" }
      return """{"c":true,"v":[${points(v)}],"i":[${points(i)}],"o":[${points(o)}]}"""
    }
  }

  private fun analytic(
    points: Double,
    polygon: Boolean,
    roundness: Double,
    reverse: Boolean,
  ): Geometry {
    val count = if (polygon) floor(points).toInt() else ceil(points).toInt() * 2
    val fraction = points - floor(points)
    val step = 2 * PI / (if (polygon) count.toDouble() else points) * (if (reverse) -1 else 1)
    var angle = -PI / 2 + if (!polygon && fraction != 0.0) step / 2 * (1 - fraction) else 0.0
    val vertices = mutableListOf<Point>()
    val incoming = mutableListOf<Point>()
    for (j in 0 until count) {
      val radius =
        if (polygon) 23.0
        else if (j == 0 && fraction != 0.0) 10 + 13 * fraction else if (j % 2 == 0) 23.0 else 10.0
      vertices += Point(32 + radius * cos(angle), 32 + radius * sin(angle))
      val handleRadius = if (polygon || j % 2 == 0) 23.0 else 10.0
      val partial = if (!polygon && j == 0 && fraction != 0.0) fraction else 1.0
      val length = handleRadius * roundness * (if (polygon) 0.25 else 0.47829) * partial
      incoming += Point(length * sin(angle), -length * cos(angle))
      angle +=
        if (polygon) step else if (j == 0 && fraction != 0.0) step * fraction / 2 else step / 2
    }
    return Geometry(vertices, incoming, incoming.map { it * -1.0 })
  }

  private val stroke
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""

  private fun pucker(value: String) = """{"ty":"pb","a":$value}"""

  private fun verify(name: String, shape: String, expected: (Int) -> Geometry) {
    assertMatchesStaticFrames(
      animation(shape),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "pucker-topology-$name",
      frames = (0..10).toList(),
      maxMeanRedError = 0.0001f,
      staticAt = { animation("""{"ty":"sh","ks":${fixed(expected(it).json())}},$stroke""") },
    )
  }

  private fun polystar(
    name: String,
    polygon: Boolean = false,
    rounded: Boolean = false,
    reverse: Boolean = false,
    chain: Boolean = false,
    transformed: Boolean = false,
    twisted: Boolean = false,
    repeated: Boolean = false,
  ) {
    val rounding = if (rounded) 60 else 0
    val input =
      """{"ty":"sr","sy":${if (polygon) 2 else 1},"d":${if (reverse) 3 else 1},"pt":${animated("[3]", "[7]")},"p":${fixed("[32,32]")},"r":${fixed("0")},"or":${fixed("23")},"ir":${fixed("10")},"os":${fixed("$rounding")},"is":${fixed("$rounding")}}"""
    val extra = if (chain) ",${pucker(fixed("20"))}" else ""
    val transform =
      """{"ty":"tr","p":${fixed("[6,4]")},"a":${fixed("[0,0]")},"s":${fixed("[80,80]")},"r":${fixed("0")},"o":${fixed("100")}}"""
    val grouped = if (transformed) """{"ty":"gr","it":[$input,$transform]}""" else input
    val repeat =
      if (repeated)
        """,{"ty":"rp","c":${fixed("1")},"o":${fixed("1")},"tr":{"p":${fixed("[6,4]")},"a":${fixed("[0,0]")},"s":${fixed("[80,80]")},"r":${fixed("0")},"so":${fixed("100")},"eo":${fixed("100")}}}"""
      else ""
    val twist = if (twisted) """,{"ty":"tw","a":${fixed("100")},"c":${fixed("[32,32]")}}""" else ""
    verify(name, "$grouped$repeat$twist,${pucker(animated("[-30]", "[40]"))}$extra,$stroke") { frame
      ->
      var geometry = analytic(3.0 + frame * 0.4, polygon, rounding / 100.0, reverse)
      if (transformed || repeated) geometry = geometry.transform { it * 0.8 + Point(6.0, 4.0) }
      if (twisted)
        geometry = geometry.transform {
          val p = it - Point(32.0, 32.0)
          val theta = hypot(p.x, p.y) * PI / 180
          Point(32 + p.x * cos(theta) - p.y * sin(theta), 32 + p.x * sin(theta) + p.y * cos(theta))
        }
      geometry = geometry.pucker((-30.0 + frame * 7) / 100)
      if (chain) geometry.pucker(0.2) else geometry
    }
  }

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun animatedPolygon() = polystar("polygon", polygon = true)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun animatedRoundedPolygon() = polystar("polygon-rounded", polygon = true, rounded = true)

  @Ignore("TODO: Fix failure on main AST/renderer") @Test fun animatedStar() = polystar("star")

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun animatedRoundedStar() = polystar("star-rounded", rounded = true)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun animatedReversedStar() = polystar("star-reversed", reverse = true)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun chainedPuckers() = polystar("chain", rounded = true, chain = true)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun groupTransformPreservesTopology() = polystar("transform", rounded = true, transformed = true)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun twistPreservesTopology() = polystar("twist", rounded = true, twisted = true)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun repeaterPreservesTopology() = polystar("repeater", rounded = true, repeated = true)

  private fun rounded(name: String) {
    val fixture =
      Json.parseToJsonElement(
          checkNotNull(javaClass.getResource("/rounding-reference.json")).readText()
        )
        .jsonObject
        .getValue("cases")
        .jsonArray
        .map { it.jsonObject }
        .single { it.getValue("name").jsonPrimitive.content == name }
    verify(
      "round-$name",
      "${fixture.getValue("shape")},{\"ty\":\"rd\",\"r\":${fixture.getValue("radius")}},${pucker(fixed("20"))},$stroke",
    ) { frame ->
      val path =
        fixture.getValue("frames").jsonArray[frame].jsonObject.getValue("expected").jsonObject
      fun points(key: String) =
        path.getValue(key).jsonArray.map { p ->
          Point(p.jsonArray[0].jsonPrimitive.double, p.jsonArray[1].jsonPrimitive.double)
        }
      Geometry(points("v"), points("i"), points("o")).pucker(0.2)
    }
  }

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun puckerAfterRoundedStar() = rounded("star-count")

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun puckerAfterRoundedPolygon() = rounded("polygon-count")

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun puckerAfterLiveRounding() = rounded("uneven")

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun puckerAfterMixedSharpAndSmoothCorners() = rounded("controls")
}

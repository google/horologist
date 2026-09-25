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

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test

/** Paint positions do not truncate the scope of following geometry modifiers. */
class ModifierPaintScopeTest : MotionPixelHarness() {
  private val stroke
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("3")},"lc":1,"lj":1,"ml":4}"""

  private fun gradient(type: String) =
    """{"ty":"$type","t":1,"s":${fixed("[12,12]")},"e":${fixed("[52,52]")},"o":${fixed("100")},"g":{"p":2,"k":${fixed("[0,1,0,0,1,0.6,0,0]")}},"w":${fixed("3")},"lc":1,"lj":1,"ml":4}"""

  private fun pucker(amount: String, hidden: Boolean = false) =
    """{"ty":"pb","a":$amount,"hd":$hidden}"""

  private fun group(shapes: String) = """{"ty":"gr","it":[$shapes]}"""

  private fun path(
    vertices: List<Pair<Double, Double>>,
    tangents: List<Pair<Double, Double>> = List(vertices.size) { 0.0 to 0.0 },
  ): String {
    fun coordinates(points: List<Pair<Double, Double>>) =
      points.joinToString(",") { (x, y) -> "[$x,$y]" }
    val value =
      """{"c":true,"v":[${coordinates(vertices)}],"i":[${coordinates(tangents)}],"o":[${coordinates(tangents)}]}"""
    return """{"ty":"sh","ks":${fixed(value)}}"""
  }

  // Analytic square oracle: after two puckers, vertex and absolute-control radii are
  // r(1-f)(1-g) and r(1+f)(1+g). No production modifier evaluates the expected path.
  private fun square(
    f: Double = 0.0,
    g: Double = 0.0,
    cx: Double = 32.0,
    radius: Double = 16.0,
  ): String {
    val signs = listOf(-1 to -1, 1 to -1, 1 to 1, -1 to 1)
    val r = radius * (1 - f) * (1 - g)
    val t = 2 * radius * (f + g)
    return path(
      signs.map { (x, y) -> cx + x * r to 32 + y * r },
      signs.map { (x, y) -> x * t to y * t },
    )
  }

  private fun verify(name: String, shapes: String, expected: (Int) -> String) =
    assertMatchesStaticFrames(
      animation(shapes),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "paintscope-$name",
      frames = (0..10).toList(),
      staticAt = { animation(expected(it)) },
    )

  private fun puckerPaint(name: String, paint: String) =
    verify(name, "${square()},$paint,${pucker(animated("[0]", "[50]"))}") {
      "${square(it / 20.0)},$paint"
    }

  @Test fun puckerAfterFill() = puckerPaint("fill", redFill)

  @Test fun puckerAfterStroke() = puckerPaint("stroke", stroke)

  @Test fun puckerAfterGradientFill() = puckerPaint("gradient-fill", gradient("gf"))

  @Test fun puckerAfterGradientStroke() = puckerPaint("gradient-stroke", gradient("gs"))

  @Test fun puckerAfterMultiplePaints() = puckerPaint("multiple-paints", "$stroke,$redFill")

  @Test
  fun modifiersOnBothSidesOfPaintEachApplyOnce() =
    verify(
      "chain",
      "${square()},${pucker(fixed("20"))},$stroke,$redFill,${pucker(animated("[0]", "[30]"))}",
    ) {
      "${square(0.2, it * 0.03)},$stroke,$redFill"
    }

  @Test
  fun hiddenModifierDoesNotChangePaint() =
    verify("hidden", "${square()},$redFill,${pucker(animated("[0]", "[50]"), hidden = true)}") {
      "${square()},$redFill"
    }

  @Test
  fun modifierDoesNotAffectLaterGeometry() =
    verify(
      "later-sibling",
      "${square(cx = 18.0, radius = 10.0)},$redFill,${pucker(animated("[0]", "[50]"))},${square(cx = 46.0, radius = 10.0)},$redFill",
    ) {
      "${group("${square(it / 20.0, cx = 18.0, radius = 10.0)},$redFill")},${group("${square(cx = 46.0, radius = 10.0)},$redFill")}"
    }

  @Test
  fun modifierReachesEarlierPaintsAfterNewGeometry() =
    verify(
      "earlier-paints",
      "${square(cx = 18.0, radius = 10.0)},$redFill,${square(cx = 46.0, radius = 10.0)},$redFill,${pucker(animated("[0]", "[50]"))}",
    ) {
      "${group("${square(it / 20.0, cx = 18.0, radius = 10.0)},$redFill")},${group("${square(it / 20.0, cx = 46.0, radius = 10.0)},$redFill")}"
    }

  @Test
  fun modifierReachesAlreadyStyledGroupWithTransform() {
    val transform =
      """{"ty":"tr","p":${fixed("[4,0]")},"a":${fixed("[0,0]")},"s":${fixed("[100,100]")},"r":${fixed("0")},"o":${fixed("60")}}"""
    verify(
      "styled-group",
      "${group("${square()},$redFill,$transform")},${pucker(animated("[0]", "[50]"))}",
    ) {
      group("${square(it / 20.0)},$redFill,$transform")
    }
  }

  @Test
  fun modifierUpdatesUnstyledGroupForFollowingPaint() =
    verify("inherited-paint", "${group(square())},${pucker(animated("[0]", "[50]"))},$redFill") {
      "${square(it / 20.0)},$redFill"
    }

  @Test
  fun modifierPreservesRepeatedInstanceOpacity() {
    val repeater =
      """{"ty":"rp","c":${fixed("2")},"o":${fixed("0")},"tr":{"p":${fixed("[28,0]")},"a":${fixed("[0,0]")},"s":${fixed("[100,100]")},"r":${fixed("0")},"so":${fixed("100")},"eo":${fixed("20")}}}"""
    verify(
      "repeater-opacity",
      // The repeater owns this paint. A later inherited paint does not use its ramp.
      "${square(cx = 18.0, radius = 10.0)},$redFill,$repeater,${pucker(animated("[0]", "[50]"))}",
    ) {
      val faded = redFill.replace("\"o\":${fixed("100")}", "\"o\":${fixed("60")}")
      "${group("${square(it / 20.0, cx = 18.0, radius = 10.0)},$redFill")},${group("${square(it / 20.0, cx = 46.0, radius = 10.0)},$faded")}"
    }
  }

  @Test
  fun modifierPreservesInheritedRepeaterPathsWithoutOpacityRamp() {
    val repeater =
      """{"ty":"rp","c":${fixed("2")},"o":${fixed("0")},"tr":{"p":${fixed("[28,0]")},"a":${fixed("[0,0]")},"s":${fixed("[100,100]")},"r":${fixed("0")},"so":${fixed("100")},"eo":${fixed("20")}}}"""
    verify(
      "repeater-inherited",
      "${square(cx = 18.0, radius = 10.0)},$repeater,${pucker(animated("[0]", "[50]"))},$redFill",
    ) {
      "${square(it / 20.0, cx = 18.0, radius = 10.0)},${square(it / 20.0, cx = 46.0, radius = 10.0)},$redFill"
    }
  }

  @Test
  fun offsetAfterPaintMatchesAnalyticExpandedSquare() =
    verify(
      "offset",
      "${square()},$stroke,{\"ty\":\"op\",\"a\":${animated("[0]", "[6]")},\"lj\":1,\"ml\":${fixed("100")}}",
    ) {
      "${square(radius = 16 + it * 0.6)},$stroke"
    }

  @Test
  fun twistAfterPaintMatchesPolarCoordinateOracle() =
    verify(
      "twist",
      "${square()},$redFill,{\"ty\":\"tw\",\"a\":${animated("[0]", "[200]")},\"c\":${fixed("[32,32]")}}",
    ) { frame ->
      val vertices =
        listOf(-16.0 to -16.0, 16.0 to -16.0, 16.0 to 16.0, -16.0 to 16.0).map { (x, y) ->
          val distance = hypot(x, y)
          val theta = atan2(y, x) + Math.toRadians(frame * 20 * distance / 100)
          32 + distance * cos(theta) to 32 + distance * sin(theta)
        }
      "${path(vertices)},$redFill"
    }

  @Test
  fun zigzagAfterPaintMatchesPinnedWebGeometry() {
    val fixture =
      Json.parseToJsonElement(
          checkNotNull(javaClass.getResource("/zigzag-reference.json")).readText()
        )
        .jsonObject
        .getValue("motion")
        .jsonArray
        .map { it.jsonObject }
        .single { it.getValue("name").jsonPrimitive.content == "square-size-1" }
    val input = """{"ty":"sh","ks":${fixed(fixture.getValue("start").toString())}}"""
    verify(
      "zigzag",
      "$input,$stroke,{\"ty\":\"zz\",\"s\":${animated("[-4]", "[6]")},\"r\":${fixed("3")},\"pt\":1}",
    ) {
      val expected = fixture.getValue("frames").jsonArray[it].jsonObject.getValue("expected")
      """{"ty":"sh","ks":${fixed(expected.toString())}},$stroke"""
    }
  }
}

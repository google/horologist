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

import org.junit.Test

class PolystarTopologyRegressionTest : FixtureParityHarness() {
  @Test fun growingStar() = verify("star-grow", 1, animated("[3]", "[7]"))

  @Test fun shrinkingStar() = verify("star-shrink", 1, animated("[7]", "[3]"))

  @Test
  fun roundedFractionalStar() =
    verify("star-rounded", 1, animated("[4.25]", "[5.75]"), roundness = "55")

  @Test
  fun reversedFractionalStar() =
    verify("star-reverse", 1, animated("[4.25]", "[5.75]"), direction = 3, roundness = "55")

  @Test fun heldStarCounts() = verify("star-hold", 1, held)

  @Test fun trimmedChangingStar() = verify("star-trim", 1, animated("[3]", "[7]"), trim = true)

  @Test fun growingPolygon() = verify("polygon-grow", 2, animated("[3]", "[7]"))

  @Test
  fun shrinkingRoundedPolygon() =
    verify("polygon-shrink", 2, animated("[7]", "[3]"), roundness = "55")

  @Test fun heldPolygonCounts() = verify("polygon-hold", 2, held)

  @Test
  fun trimmedChangingPolygon() =
    verify("polygon-trim", 2, animated("[3]", "[7]"), roundness = "55", trim = true)

  @Test
  fun staticFractionalStar() =
    verify("star-static-fraction", 1, fixed("4.5"), moving = false, roundness = "55")

  @Test
  fun staticFractionalPolygon() =
    verify("polygon-static-fraction", 2, fixed("4.5"), moving = false, roundness = "55")

  @Test
  fun changingStarGeometryAndTopology() =
    verify("star-geometry", 1, animated("[3]", "[7]"), liveGeometry = true)

  @Test
  fun changingPolygonGeometryAndTopology() =
    verify("polygon-geometry", 2, animated("[3]", "[7]"), liveGeometry = true)

  @Test
  fun changingStarFill() =
    verify("star-fill", 1, animated("[3]", "[7]"), fill = true, direction = 3)

  @Test fun changingPolygonFill() = verify("polygon-fill", 2, animated("[3]", "[7]"), fill = true)

  @Test
  fun oppositeWindingRetainsHole() =
    verify("star-hole", 1, animated("[3]", "[7]"), fill = true, hole = true)

  private val held
    get() =
      """{"a":1,"k":[{"t":0,"s":[3],"h":1},{"t":4,"s":[8],"h":1},{"t":7,"s":[4],"h":1},{"t":10,"s":[4]}]}"""

  private fun verify(
    name: String,
    type: Int,
    points: String,
    direction: Int = 1,
    roundness: String = "0",
    moving: Boolean = true,
    trim: Boolean = false,
    liveGeometry: Boolean = false,
    fill: Boolean = false,
    hole: Boolean = false,
  ) {
    val position = if (liveGeometry) animated("[32,32]", "[28,34]") else fixed("[32,32]")
    val rotation = if (liveGeometry) animated("[17]", "[80]") else fixed("17")
    val outer = if (liveGeometry) animated("[23]", "[18]") else fixed("23")
    val inner = if (liveGeometry) animated("[10]", "[14]") else fixed("10")
    val rounding = if (liveGeometry) animated("[0]", "[70]") else fixed(roundness)
    val modifier =
      if (trim) """,{"ty":"tm","s":${fixed("15")},"e":${fixed("65")},"o":${fixed("0")},"m":1}"""
      else ""
    val innerContour =
      if (hole)
        """,{"ty":"sr","sy":1,"d":3,"pt":$points,"p":$position,
      "r":$rotation,"or":${fixed("8")},"ir":${fixed("3")},"os":${fixed("0")},"is":${fixed("0")}}"""
      else ""
    val json =
      animation(
          """{"ty":"sr","sy":$type,"d":$direction,"pt":$points,"p":$position,
        "r":$rotation,"or":$outer,"ir":$inner,"os":$rounding,"is":$rounding}$modifier$innerContour,
        {"ty":"${if (fill) "fl" else "st"}","r":1,"c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""
        )
        .replace("\"op\":40", "\"op\":10.01")
    compare(json, "topology_$name", animated = moving, maxForegroundError = 0.03)
  }
}

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

class RoundedPathParityTest : FixtureParityHarness() {
  @Test fun liveRadiusMatchesAndroid() = verify("radius")

  @Test fun movingRoundedPathMatchesAndroid() = verify("geometry")

  @Test fun roundedPathWithLiveTrimMatchesAndroid() = verify("trim")

  @Test fun intrinsicStarRoundnessRetainsControls() = intrinsic(1)

  @Test fun intrinsicPolygonRoundnessRetainsControls() = intrinsic(2)

  private fun intrinsic(type: Int) {
    compare(
      animation(
          """{"ty":"sr","sy":$type,"pt":${animated("[3]","[7]")},
      "p":${fixed("[32,32]")},"r":${fixed("0")},"or":${fixed("23")},"ir":${fixed("10")},
      "os":${animated("[10]","[70]")},"is":${animated("[10]","[70]")}},
      {"ty":"rd","r":${fixed("8")}},
      {"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""
        )
        .replace("\"op\":40", "\"op\":10.01"),
      "roundpath_intrinsic$type",
      maxForegroundError = 0.03,
    )
  }

  private fun verify(name: String) {
    fun path(y: Int) =
      """{"c":true,"v":[[12,12],[52,12],[52,$y],[12,52]],
      "i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}"""
    val shape =
      if (name == "geometry") animated("[${path(20)}]", "[${path(44)}]") else fixed(path(20))
    val radius = if (name == "radius") animated("[0]", "[24]") else fixed("8")
    val trim =
      if (name == "trim")
        """,{"ty":"tm","s":${animated("[0]","[30]")},"e":${animated("[60]","[100]")},"o":${fixed("0")},"m":1}"""
      else ""
    compare(
      animation(
          """{"ty":"sh","ks":$shape},{"ty":"rd","r":$radius}$trim,
      {"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""
        )
        .replace("\"op\":40", "\"op\":10.01"),
      "roundpath_$name",
      maxForegroundError = 0.03,
    )
  }
}

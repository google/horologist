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

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test

class RoundedCornersRegressionTest : MotionPixelHarness() {
  @Test fun unequalEdgesClampIndependently() = verify("uneven")

  @Test fun changingGeometryAndRadiusStayLive() = verify("moving")

  @Test fun openEndpointsStayUnrounded() = verify("open")

  @Test fun changingSharpnessKeepsOriginalControls() = verify("controls")

  @Test fun duplicateVerticesStayFinite() = verify("duplicate")

  @Test fun starRadiusStaysLive() = verify("star-radius")

  @Test fun changingStarCountRoundsActiveVertices() = verify("star-count")

  @Test fun reversedStarRoundsCorrectly() = verify("star-reverse")

  @Test fun changingPolygonCountRoundsActiveVertices() = verify("polygon-count")

  @Test fun roundedTopologyCanBeTrimmed() = verify("star-trim")

  private fun verify(name: String) {
    val data =
      Json.parseToJsonElement(
          checkNotNull(javaClass.getResource("/rounding-reference.json")).readText()
        )
        .jsonObject
    val item =
      data
        .getValue("cases")
        .jsonArray
        .map { it.jsonObject }
        .single { it.getValue("name").jsonPrimitive.content == name }
    val trim =
      if (item["trim"]?.jsonPrimitive?.booleanOrNull == true)
        """,{"ty":"tm","s":${fixed("15")},"e":${fixed("65")},"o":${fixed("0")},"m":1}"""
      else ""
    val stroke =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""
    assertMatchesStaticFrames(
      animation(
        """${item.getValue("shape")},{"ty":"rd","r":${item.getValue("radius")}}$trim,$stroke"""
      ),
      referenceProbeAt = { Probe(32, 20, 1f, tolerance = 1f) },
      artifactName = "rounding-$name",
      frames = (0..10).toList(),
      staticAt = { frame ->
        val expected = item.getValue("frames").jsonArray[frame].jsonObject.getValue("expected")
        // Radius zero bypasses rounding, but selects the native trim path for the explicit
        // reference geometry too. RoundedPathParityTest separately checks trim against Android.
        val nativeTrim = if (trim.isNotEmpty()) """,{"ty":"rd","r":${fixed("0")}}""" else ""
        animation("""{"ty":"sh","ks":${fixed(expected.toString())}}$nativeTrim$trim,$stroke""")
      },
    )
  }
}

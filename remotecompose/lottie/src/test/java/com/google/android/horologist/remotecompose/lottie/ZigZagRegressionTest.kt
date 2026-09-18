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

import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.ZigZag
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateZigZag
import com.google.common.truth.Truth.assertWithMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test

class ZigZagRegressionTest : MotionPixelHarness() {
  private val fixtures =
    Json.parseToJsonElement(
        checkNotNull(javaClass.getResource("/zigzag-reference.json")).readText()
      )
      .jsonObject

  @Test fun squareCornersMatchWebGeometry() = verifyGeometry("square", 1)

  @Test fun squareSmoothPointsMatchWebGeometry() = verifyGeometry("square", 2)

  @Test fun curvedCornersMatchWebGeometry() = verifyGeometry("curve", 1)

  @Test fun curvedSmoothPointsMatchWebGeometry() = verifyGeometry("curve", 2)

  @Test fun cornerSizeStaysLiveIncludingZero() = verifyMotion("size-1")

  @Test fun smoothSizeStaysLiveIncludingZero() = verifyMotion("size-2")

  @Test fun cornerRidgeCountStaysLive() = verifyMotion("ridges-1")

  @Test fun smoothRidgeCountStaysLive() = verifyMotion("ridges-2")

  @Test fun curvedCornerGeometryStaysLive() = verifyMotion("geometry-1")

  @Test fun curvedSmoothGeometryStaysLive() = verifyMotion("geometry-2")

  @Test fun closedCornerSizeStaysLiveIncludingZero() = verifyMotion("square-size-1")

  @Test fun closedSmoothSizeStaysLiveIncludingZero() = verifyMotion("square-size-2")

  @Test fun closedCornerRidgeCountStaysLive() = verifyMotion("square-ridges-1")

  @Test fun closedSmoothRidgeCountStaysLive() = verifyMotion("square-ridges-2")

  @Test fun closedCornerGeometryStaysLive() = verifyMotion("square-geometry-1")

  @Test fun closedSmoothGeometryStaysLive() = verifyMotion("square-geometry-2")

  @Test fun degenerateCornersStayFinite() = verifyGeometry("degenerate", 1)

  @Test fun degenerateSmoothPointsStayFinite() = verifyGeometry("degenerate", 2)

  @Test fun curvedStaticSmoothPropertyIsDecoded() = verifyMotion("curve-type-static")

  @Test fun closedStaticSmoothPropertyIsDecoded() = verifyMotion("square-type-static")

  @Test fun curvedPointTypeHoldsStayLive() = verifyMotion("curve-type-hold")

  @Test fun closedPointTypeHoldsStayLive() = verifyMotion("square-type-hold")

  @Test fun curvedInterpolatedPointTypeStaysLive() = verifyMotion("curve-type-linear")

  @Test fun closedInterpolatedPointTypeStaysLive() = verifyMotion("square-type-linear")

  @Test fun curvedReversePointTypeStaysLive() = verifyMotion("curve-type-reverse")

  @Test fun closedReversePointTypeStaysLive() = verifyMotion("square-type-reverse")

  @Test
  fun linearTimingPreservesAnExactZeroCrossing() {
    for ((out, inside) in listOf(0f to 1f, 0.25f to 0.75f, 0.42f to 0.58f)) {
      val fraction = lookupValueInBezier(out, out, inside, inside, 10f, 4f.rf)
      assertWithMessage("diagonal timing must be exactly linear")
        .that(lerp((-4f).rf, 6f.rf, fraction).constantValue)
        .isEqualTo(0f)
    }
  }

  private fun verifyGeometry(prefix: String, pt: Int) {
    val cases =
      fixtures
        .getValue("cases")
        .jsonArray
        .map { it.jsonObject }
        .filter { it.getValue("name").jsonPrimitive.content.startsWith("$prefix-$pt-") }
    assertWithMessage("reference case coverage")
      .that(cases)
      .hasSize(if (prefix == "degenerate") 4 else 6)
    for (item in cases) {
      val input = bezier(item.getValue("input"))
      val output =
        (evaluateZigZag(
              listOf(RemoteLottiePath(listOf(input))),
              ZigZag(
                size = StaticScalarProperty(value = item.getValue("size").jsonPrimitive.float.rf),
                ridgesPerSegment =
                  StaticScalarProperty(value = item.getValue("ridges").jsonPrimitive.float.rf),
                pointType = StaticScalarProperty(value = pt.toFloat().rf),
              ),
              LottieSettings(0f.rf),
            )
            .single() as RemoteLottiePath)
          .path
          .single()
      val expected = bezier(item.getValue("expected"))
      val label = item.getValue("name").jsonPrimitive.content
      assertWithMessage(label).that(output.closed).isEqualTo(expected.closed)
      for ((actual, reference) in
        listOf(
          output.vertices to expected.vertices,
          output.inTangents to expected.inTangents,
          output.outTangents to expected.outTangents,
        )) {
        assertWithMessage(label).that(actual.size).isEqualTo(reference.size)
        actual.flatten().zip(reference.flatten()).forEach { (a, e) ->
          assertWithMessage(label).that(a.constantValue).isWithin(0.001f).of(e.constantValue)
        }
      }
    }
  }

  private fun verifyMotion(name: String) {
    val item =
      fixtures
        .getValue("motion")
        .jsonArray
        .map { it.jsonObject }
        .single { it.getValue("name").jsonPrimitive.content == name }
    val kind = item.getValue("kind").jsonPrimitive.content
    val path =
      if (kind == "geometry") animated("[${item.getValue("start")}]", "[${item.getValue("end")}]")
      else fixed(item.getValue("start").toString())
    val size = if (kind == "size") animated("[-4]", "[6]") else fixed("4")
    val ridges = if (kind == "ridges") animated("[0]", "[4]") else fixed("3")
    val pt =
      when (kind) {
        "type-static" -> fixed("2")
        "type-hold" ->
          """{"a":1,"k":[{"t":0,"s":[1],"h":1},{"t":5,"s":[2],"h":1},{"t":8,"s":[1],"h":1},{"t":10,"s":[1]}]}"""
        "type-linear" -> animated("[1]", "[3]")
        "type-reverse" -> animated("[3]", "[1]")
        else -> item.getValue("pt").jsonPrimitive.int.toString()
      }
    val stroke =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("3")},"lc":1,"lj":1,"ml":4}"""
    assertMatchesStaticFrames(
      animation("""{"ty":"sh","ks":$path},{"ty":"zz","s":$size,"r":$ridges,"pt":$pt},$stroke"""),
      referenceProbeAt = { Probe(32, 20, 1f, tolerance = 1f) },
      artifactName = "zigzag-$name",
      frames = (0..10).toList(),
      // R27: float32-expression vs generated-reference join coverage; all other cases
      // retain the harness's strict sampled-pixel comparison. No geometry bound is relaxed.
      maxMeanRedError = if (name in setOf("square-size-2", "square-geometry-2")) 0.00005f else null,
      staticAt = { frame ->
        val expected = item.getValue("frames").jsonArray[frame].jsonObject.getValue("expected")
        animation("""{"ty":"sh","ks":${fixed(expected.toString())}},$stroke""")
      },
    )
  }

  private fun bezier(value: JsonElement): RemoteBezierValue {
    val obj = value.jsonObject
    fun points(key: String) =
      obj.getValue(key).jsonArray.map { point -> point.jsonArray.map { it.jsonPrimitive.float.rf } }
    return RemoteBezierValue(
      obj.getValue("c").jsonPrimitive.boolean,
      points("i"),
      points("o"),
      points("v"),
    )
  }
}

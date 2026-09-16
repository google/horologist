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

import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.gatherShapesForTest
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.LogicalBezier
import com.google.common.truth.Truth.assertWithMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test

class ModifierOrderTest : MotionPixelHarness() {
  @Test
  fun twoRoundsPreserveStaticLogicalControls() {
    val fixture =
      Json.parseToJsonElement(
          checkNotNull(javaClass.getResource("/modifier-order-reference.json")).readText()
        )
        .jsonObject
        .getValue("cases")
        .jsonArray
        .map { it.jsonObject }
        .single { it.getValue("name").jsonPrimitive.content == "double-round" }
    val shapes =
      (Animation.decodeFromString(animation(fixture.getValue("input").jsonArray.joinToString(",")))
          .layers
          .single() as ShapeLayer)
        .shapes
    for (frame in 0..10) {
      val actual =
        (gatherShapesForTest(shapes, LottieSettings(frame.toFloat().rf)).single().shapes.single()
            as RemoteLottiePath)
          .path
          .single()
      val logical = LogicalBezier(actual)
      val indices = logical.active.indices.filter { logical.active[it].constantValue == 1f }
      val expected =
        fixture.getValue("frames").jsonArray[frame].jsonObject.getValue("expected").jsonObject
      for ((key, points) in
        listOf("v" to logical.vertices, "i" to logical.incoming, "o" to logical.outgoing)) {
        val reference = expected.getValue(key).jsonArray
        assertWithMessage("$frame/$key count").that(indices.size).isEqualTo(reference.size)
        for ((n, j) in indices.withIndex()) for (axis in 0..1) {
          assertWithMessage("$frame/$key/$n/$axis")
            .that(points[j].values()[axis].constantValue)
            .isWithin(0.0002f)
            .of(reference[n].jsonArray[axis].jsonPrimitive.float)
        }
      }
    }
  }

  private fun verify(name: String) {
    val fixture =
      Json.parseToJsonElement(
          checkNotNull(javaClass.getResource("/modifier-order-reference.json")).readText()
        )
        .jsonObject
        .getValue("cases")
        .jsonArray
        .map { it.jsonObject }
        .single { it.getValue("name").jsonPrimitive.content == name }
    val input = fixture.getValue("input").jsonArray.joinToString(",")
    val stroke =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""
    assertMatchesStaticFrames(
      animation(input),
      referenceProbeAt = { Probe(32, 32, 1f, 1f) },
      artifactName = "modifier-order-$name",
      frames = (0..10).toList(),
      maxMeanRedError = 0.0001f,
      staticAt = { frame ->
        val expected = fixture.getValue("frames").jsonArray[frame].jsonObject.getValue("expected")
        animation("""{"ty":"sh","ks":${fixed(expected.toString())}},$stroke""")
      },
    )
  }

  @Test fun puckerThenRound() = verify("pucker-round")

  @Test fun roundThenPucker() = verify("round-pucker")

  @Test fun twistThenRound() = verify("twist-round")

  @Test fun roundThenTwist() = verify("round-twist")

  @Test fun modifiersAfterPaintKeepTheirOrder() = verify("paint-pucker-round")

  @Test fun paintBetweenModifiersKeepsTheirOrder() = verify("pucker-paint-round")

  @Test fun childModifierBeforeParentRounding() = verify("nested-pucker-round")

  @Test fun inheritedPaintRetainsOrder() = verify("inherited-pucker-round")

  @Test fun twoRoundedCornerModifiers() = verify("double-round")

  @Test fun roundingOnEitherSideOfPucker() = verify("round-pucker-round")

  @Test fun rectangleExternalRounding() = verify("rectangle-round")

  @Test fun rectanglePuckerThenRound() = verify("rectangle-pucker-round")

  @Test fun polygonOffsetThenRound() = verify("offset-round")

  @Test fun liveStarPuckerThenRound() = verify("star-pucker-round")
}

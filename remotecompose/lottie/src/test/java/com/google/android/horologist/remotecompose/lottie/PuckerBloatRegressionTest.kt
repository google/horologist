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
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.PuckerBloat
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluatePuckerBloat
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PuckerBloatRegressionTest : MotionPixelHarness() {
  @Test
  fun positiveAmountMovesVerticesInwardAndAbsoluteControlsOutward() = checkGeometry(50f, 24f, -16f)

  @Test
  fun negativeAmountMovesVerticesOutwardAndAbsoluteControlsInward() = checkGeometry(-50f, 8f, 16f)

  private fun checkGeometry(amount: Float, vertex: Float, tangent: Float) {
    val points = listOf(listOf(16f, 16f), listOf(48f, 16f), listOf(48f, 48f), listOf(16f, 48f))
    val input =
      RemoteBezierValue(
        true,
        List(4) { listOf(0f.rf, 0f.rf) },
        List(4) { listOf(0f.rf, 0f.rf) },
        points.map { p -> p.map { it.rf } },
      )
    val result =
      (evaluatePuckerBloat(
            listOf(RemoteLottiePath(listOf(input))),
            PuckerBloat(amount = StaticScalarProperty(value = amount.rf)),
            LottieSettings(0f.rf),
          )
          .single() as RemoteLottiePath)
        .path
        .single()
    assertThat(result.vertices.first().map { it.constantValue }).containsExactly(vertex, vertex)
    assertThat(result.inTangents.first().map { it.constantValue }).containsExactly(tangent, tangent)
    assertThat(result.outTangents.first().map { it.constantValue })
      .containsExactly(tangent, tangent)
  }

  @Test
  fun animatedAmountMatchesExplicitControlPointsWithoutRemounting() {
    val input = """{"ty":"sh","ks":${fixed(square(0f, 0f))}}"""
    assertMatchesStaticFrames(
      animation("$input,{\"ty\":\"pb\",\"a\":${animated("[0]", "[50]")}},$redFill"),
      referenceProbeAt = { Probe(32, 32, 1f) },
      staticAt = { frame -> explicitShape(0f, frame / 20f) },
    )
  }

  @Test
  fun animatedGeometryKeepsItsLiveCentroid() {
    val path = animated("[${square(0f, 0f)}]", "[${square(8f, 0f)}]")
    assertMatchesStaticFrames(
      animation("""{"ty":"sh","ks":$path},{"ty":"pb","a":${fixed("25")}},$redFill"""),
      referenceProbeAt = { frame -> Probe(32 + frame * 8 / 10, 32, 1f) },
      staticAt = { frame -> explicitShape(frame * 0.8f, 0.25f) },
    )
  }

  private fun explicitShape(dx: Float, fraction: Float): String =
    animation("""{"ty":"sh","ks":${fixed(square(dx, fraction))}},$redFill""")

  // Independent explicit square fixture: vertices move toward (32,32), absolute controls away.
  private fun square(dx: Float, fraction: Float): String {
    val low = 16f + fraction * 16f
    val high = 48f - fraction * 16f
    val t = fraction * 32f
    val tangents = "[[-$t,-$t],[$t,-$t],[$t,$t],[-$t,$t]]"
    return """{"c":true,"v":[[${low+dx},$low],[${high+dx},$low],[${high+dx},$high],[${low+dx},$high]],"i":$tangents,"o":$tangents}"""
  }
}

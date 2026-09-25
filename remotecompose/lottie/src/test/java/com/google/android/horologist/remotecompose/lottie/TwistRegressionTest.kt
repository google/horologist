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
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.Twist
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateTwist
import com.google.common.truth.Truth.assertThat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import org.junit.Test

/** Tests the distance-weighted twist contract, not After Effects reference-renderer parity. */
class TwistRegressionTest : MotionPixelHarness() {
  @Test
  fun controlsRotateAtTheirOwnDistanceAndRemainRelativeToMovedVertices() {
    val path =
      RemoteBezierValue(
        false,
        listOf(listOf(0f.rf, 0f.rf), listOf(0f.rf, 0f.rf)),
        listOf(listOf(10f.rf, 0f.rf), listOf(10f.rf, 0f.rf)),
        listOf(listOf(42f.rf, 32f.rf), listOf(32f.rf, 32f.rf)),
      )
    val result =
      (evaluateTwist(
            listOf(RemoteLottiePath(listOf(path))),
            Twist(
              angle = StaticScalarProperty(value = 900f.rf),
              center = StaticPositionProperty(value = Point(32f.rf, 32f.rf)),
            ),
            LottieSettings(0f.rf),
          )
          .single() as RemoteLottiePath)
        .path
        .single()
    // At distance 10, angle=900 means 90 degrees; at distance 20 it means 180.
    assertThat(result.vertices[0][0].constantValue).isWithin(0.0001f).of(32f)
    assertThat(result.vertices[0][1].constantValue).isWithin(0.0001f).of(42f)
    assertThat(result.outTangents[0][0].constantValue).isWithin(0.0001f).of(-20f)
    assertThat(result.outTangents[0][1].constantValue).isWithin(0.0001f).of(-10f)
    // A vertex at the center must stay finite and stationary, even with a moving control.
    assertThat(result.vertices[1].map { it.constantValue }).containsExactly(32f, 32f)
    assertThat(result.outTangents[1][0].constantValue).isWithin(0.0001f).of(0f)
    assertThat(result.outTangents[1][1].constantValue).isWithin(0.0001f).of(10f)
  }

  @Test
  fun animatedAngleUpdatesTheExistingDocument() =
    assertMatchesStaticFrames(
      twisted(fixed(square(12.0)), animated("[0]", "[300]"), fixed("[32,32]")),
      referenceProbeAt = { Probe(32, 32, 1f) },
      staticAt = { frame -> reference(12.0, frame * 30.0, 32.0, 32.0) },
      artifactName = "twist-angle",
    )

  @Test
  fun animatedCenterUpdatesTheExistingDocument() =
    assertMatchesStaticFrames(
      twisted(fixed(square(12.0)), fixed("-240"), animated("[28,30]", "[36,34]")),
      referenceProbeAt = { Probe(32, 32, 1f) },
      staticAt = { frame -> reference(12.0, -240.0, 28 + frame * 0.8, 30 + frame * 0.4) },
      artifactName = "twist-center",
    )

  @Test
  fun animatedGeometryUpdatesDistancesAtPlayback() =
    assertMatchesStaticFrames(
      twisted(animated("[${square(8.0)}]", "[${square(16.0)}]"), fixed("250"), fixed("[32,32]")),
      referenceProbeAt = { Probe(32, 32, 1f) },
      staticAt = { frame -> reference(8 + frame * 0.8, 250.0, 32.0, 32.0) },
      artifactName = "twist-geometry",
    )

  private fun twisted(path: String, angle: String, center: String): String =
    animation("""{"ty":"sh","ks":$path},{"ty":"tw","a":$angle,"c":$center},$redFill""")

  private fun square(radius: Double): String = path(squarePoints(radius))

  private fun squarePoints(radius: Double): List<Pair<Double, Double>> =
    listOf(
      32 - radius to 32 - radius,
      32 + radius to 32 - radius,
      32 + radius to 32 + radius,
      32 - radius to 32 + radius,
    )

  private fun reference(radius: Double, angle: Double, cx: Double, cy: Double): String {
    // Independent polar-coordinate fixture, with no production modifier or RemoteFloat evaluation.
    val points =
      squarePoints(radius).map { (x, y) ->
        val distance = hypot(x - cx, y - cy)
        val theta = atan2(y - cy, x - cx) + Math.toRadians(angle * distance / 100)
        cx + distance * cos(theta) to cy + distance * sin(theta)
      }
    return animation("""{"ty":"sh","ks":${fixed(path(points))}},$redFill""")
  }

  private fun path(points: List<Pair<Double, Double>>): String {
    val vertices = points.joinToString(",") { (x, y) -> "[$x,$y]" }
    return """{"c":true,"v":[$vertices],"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}"""
  }
}

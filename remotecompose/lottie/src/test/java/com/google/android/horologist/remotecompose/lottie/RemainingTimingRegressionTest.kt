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
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorPropertySerializer
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Assert.assertThrows
import org.junit.Test

class RemainingTimingRegressionTest : MotionPixelHarness() {
  // [SP_REMAINING_TIMING_07] Both zero and negative dimensions must fail before canvas fitting.
  @Test
  fun rejectsNonpositiveRootDimensionsWithArgumentErrors() {
    val valid = Animation.decodeFromString(root(shapeLayer()))
    assertRecordingRejects(
      listOf(
        "zero width" to valid.copy(width = 0),
        "negative width" to valid.copy(width = -1),
        "zero height" to valid.copy(height = 0),
        "negative height" to valid.copy(height = -1),
      )
    )
  }

  // [SP_REMAINING_TIMING_08] Recording requires a positive finite frame rate.
  @Test
  fun rejectsNonpositiveAndNonfiniteFrameRatesWithArgumentErrors() {
    val valid = Animation.decodeFromString(root(shapeLayer()))
    assertRecordingRejects(
      listOf(0f, -1f, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY).map {
        "frame rate $it" to valid.copy(frameRate = it)
      }
    )
  }

  // [SP_REMAINING_TIMING_09] Empty, reversed, and nonfinite root frame intervals are invalid.
  @Test
  fun rejectsDegenerateRootIntervalsWithArgumentErrors() {
    val valid = Animation.decodeFromString(root(shapeLayer()))
    assertRecordingRejects(
      listOf(
        "empty interval" to valid.copy(startFrame = 10f, endFrame = 10f),
        "reversed interval" to valid.copy(startFrame = 20f, endFrame = 10f),
        "NaN start" to valid.copy(startFrame = Float.NaN),
        "infinite start" to valid.copy(startFrame = Float.NEGATIVE_INFINITY),
        "NaN end" to valid.copy(endFrame = Float.NaN),
        "infinite end" to valid.copy(endFrame = Float.POSITIVE_INFINITY),
      )
    )
  }

  // [SP_REMAINING_TIMING_10] The permissive decoder's empty root still fails at recording.
  @Test
  fun rejectsDecodedEmptyRootWithAnArgumentError() {
    assertRecordingRejects(listOf("empty root" to Animation.decodeFromString("{}")))
  }

  // [SP_REMAINING_TIMING_11] Transform scale requires both x and y components.
  @Test
  fun rejectsEmptyAndSingleComponentTransformScalesWithArgumentErrors() {
    assertRecordingRejects(
      listOf("[]", "[100]").map { scale ->
        "scale $scale" to Animation.decodeFromString(root(shapeLayer(scale = fixed(scale))))
      }
    )
  }

  // [SP_REMAINING_TIMING_01] Decoding is permissive, but evaluating an empty animation is invalid.
  @Test
  fun rejectsEmptyAnimatedVectorWithAnArgumentError() {
    val vector =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, """{"a":1,"k":[]}""")
    val failure =
      assertThrows(IllegalArgumentException::class.java) {
        animateVector(vector, LottieSettings(5f.rf, SlotMap.Empty))
      }
    assertThat(failure.message).isNotEmpty()
  }

  // [SP_REMAINING_TIMING_02] A valid static vector supplies a positive evaluation control.
  @Test
  fun retainsBothComponentsOfAValidStaticVector() {
    val vector =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, fixed("[100,75]"))
    val result = animateVector(vector, LottieSettings(5f.rf, SlotMap.Empty))
    assertThat(result.map { it.constantValue }).containsExactly(100f, 75f).inOrder()
  }

  // [SP_REMAINING_TIMING_03] A layer includes its in-point and excludes its out-point.
  @Test
  fun hidesLayerAtItsOutPointWithinALongerRootTimeline() {
    val progress = show(root(shapeLayer(start = 5, end = 20)))
    assertPixels(Probe(32, 32, 0f), Probe(4, 4, 0f))
    advance(progress, 5f)
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
    advance(progress, 19.998f)
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
    advance(progress, 20f)
    assertPixels(Probe(32, 32, 0f), Probe(4, 4, 0f))
  }

  // [SP_REMAINING_TIMING_04] Public progress=1 selects the final frame preceding root op.
  @Test
  fun retainsFinalArtworkAtProgressOne() {
    val progress = show(root(shapeLayer()))
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
    advance(progress, 40f)
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
  }

  // [SP_REMAINING_TIMING_13] The keyframe exactly at op is outside the public progress range.
  @Test
  fun keepsTheHeldValueBeforeRootOutPointAtProgressOne() {
    val position = """{"a":1,"k":[{"t":0,"s":[16,32],"h":1},{"t":40,"s":[48,32]}]}"""
    val progress = show(animation("${rectangle(position)},$redFill"))
    assertPixels(Probe(16, 32, 1f), Probe(48, 32, 0f), Probe(4, 4, 0f))
    advance(progress, 40f)
    assertPixels(Probe(16, 32, 1f), Probe(48, 32, 0f), Probe(4, 4, 0f))
  }

  // [SP_REMAINING_TIMING_05] Endpoint policy also applies where Float ULP exceeds 0.01 frames.
  @Test
  fun retainsFinalArtworkAtProgressOneOnALargeShiftedTimeline() {
    val progress =
      show(root(shapeLayer(start = 1000000, end = 1000040), start = 1000000, end = 1000040))
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
    // The harness divides by 40; this deliberately sets normalized progress=1, not frame=40.
    advance(progress, 40f)
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
  }

  // [SP_REMAINING_TIMING_06] Child visibility uses its local half-open interval after st/sr.
  @Test
  fun hidesChildAtItsOutPointInsideAShiftedStretchedPrecomp() {
    val child = """{"id":"child","layers":[${shapeLayer()}]}"""
    val precomp =
      """{"ty":0,"ind":1,"refId":"child","w":64,"h":64,"ip":0,"op":40,
      "st":-60,"sr":2,"ks":${transform()}}"""
    val progress = show(root(precomp, assets = child))
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
    advance(progress, 19.998f)
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
    advance(progress, 20f)
    assertPixels(Probe(32, 32, 0f), Probe(4, 4, 0f))
    advance(progress, 30f)
    assertPixels(Probe(32, 32, 0f), Probe(4, 4, 0f))
  }

  // [SP_REMAINING_TIMING_12] A negative but finite in-point is a valid timeline origin.
  @Test
  fun rendersValidNegativeOriginAtBothProgressEndpoints() {
    val progress = show(root(shapeLayer(start = -10, end = 30), start = -10, end = 30))
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
    advance(progress, 40f)
    assertPixels(Probe(32, 32, 1f), Probe(4, 4, 0f))
  }

  private fun assertRecordingRejects(cases: List<Pair<String, Animation>>) {
    for ((description, decoded) in cases) {
      val failure =
        assertThrows(description, IllegalArgumentException::class.java) {
          decoded.validateForRecording()
        }
      assertWithMessage("$description must explain the invalid input")
        .that(failure.message?.trim())
        .isNotEmpty()
    }
  }

  private fun root(layers: String, start: Int = 0, end: Int = 40, assets: String = ""): String =
    """{"v":"5.7.4","fr":20,"ip":$start,"op":$end,"w":64,"h":64,"ddd":0,
      "assets":[$assets],"layers":[$layers]}"""

  private fun shapeLayer(
    start: Int = 0,
    end: Int = 40,
    scale: String = fixed("[100,100]"),
  ): String =
    """{"ty":4,"ind":1,"ip":$start,"op":$end,"st":0,"sr":1,"ks":${transform(scale)},
      "shapes":[${rectangle(fixed("[32,32]"))},$redFill]}"""

  private fun transform(scale: String = fixed("[100,100]")): String =
    """{"a":${fixed("[0,0]")},"p":${fixed("[0,0]")},"r":${fixed("0")},
      "s":$scale,"o":${fixed("100")}}"""
}

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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorPropertySerializer
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewRegressionTest {
  // [SP_REVIEW_EASING_01] A positive subframe duration retains its midpoint and endpoints.
  @Test
  fun returnsHalfProgressAtMidpointOfHalfFrameDuration() {
    assertLinearProgress(duration = 0.5f, frame = 0.25f, expected = 0.5f)
  }

  // [SP_REVIEW_EASING_02] Fractional durations end at their actual frame, without rounding.
  @Test
  fun returnsOneAtEndOfHalfFrameDuration() {
    assertLinearProgress(duration = 0.5f, frame = 0.5f, expected = 1f)
  }

  // [SP_REVIEW_EASING_02] A duration above one frame also preserves its fractional endpoint.
  @Test
  fun returnsOneAtEndOfTwoAndHalfFrameDuration() {
    assertLinearProgress(duration = 2.5f, frame = 2.5f, expected = 1f)
  }

  // [SP_REVIEW_EASING_03] Sampling after a positive fractional duration holds its endpoint.
  @Test
  fun returnsOneAfterEndOfHalfFrameDuration() {
    assertLinearProgress(duration = 0.5f, frame = 0.75f, expected = 1f)
  }

  // [SP_REVIEW_EASING_03] Holding after the end also applies to durations above one frame.
  @Test
  fun returnsOneAfterEndOfTwoAndHalfFrameDuration() {
    assertLinearProgress(duration = 2.5f, frame = 2.75f, expected = 1f)
  }

  // [SP_REVIEW_EASING_04] Sampling at or before the start returns zero progress.
  @Test
  fun returnsZeroAtAndBeforeStartOfFractionalDurations() {
    for (duration in listOf(0.5f, 2.5f)) {
      for (frame in listOf(-0.25f, 0f)) {
        assertLinearProgress(duration = duration, frame = frame, expected = 0f)
      }
    }
  }

  // [SP_REVIEW_EASING_05] Zero duration is an immediate finite endpoint.
  @Test
  fun returnsFiniteEndpointForZeroDuration() {
    for (frame in listOf(0f, 0.25f)) {
      assertLinearProgress(duration = 0f, frame = frame, expected = 1f)
    }
  }

  // [SP_REVIEW_BEZIER_01] The first keyframe value is held before a negative start time.
  @Test
  fun holdsFirstBezierVertexBeforeNegativeFirstKeyframe() {
    for (hold in listOf(0, 1)) {
      assertFirstBezierVertexBeforeNegativeFirstKeyframe(hold)
    }
  }

  // [SP_REVIEW_HOLD_01] Scalar hold includes its start and ends at the next keyframe.
  @Test
  fun holdsScalarUntilNextKeyframeAndThenReturnsNextValue() {
    val property =
      LottieDecoder.json.decodeFromString(
        BaseScalarPropertySerializer,
        """{"a":1,"k":[{"t":0,"s":[0],"h":1},{"t":10,"s":[100]}]}""",
      )
    for ((frame, expected) in holdFrames) {
      val result = animateScalar(property, LottieSettings(frame.rf, SlotMap.Empty))
      assertThat(result.constantValue).isEqualTo(expected)
    }
  }

  // [SP_REVIEW_HOLD_02] Both vector components remain constant during a hold interval.
  @Test
  fun holdsVectorUntilNextKeyframeAndThenReturnsNextValue() {
    val property =
      LottieDecoder.json.decodeFromString(
        BaseVectorPropertySerializer,
        """{"a":1,"k":[{"t":0,"s":[0,0],"h":1},{"t":10,"s":[100,100]}]}""",
      )
    for ((frame, expected) in holdFrames) {
      val result = animateVector(property, LottieSettings(frame.rf, SlotMap.Empty))
      assertThat(result.map { it.constantValue }).containsExactly(expected, expected).inOrder()
    }
  }

  // [SP_REVIEW_HOLD_03] Position hold prevents motion until the next keyframe.
  @Test
  fun holdsPositionUntilNextKeyframeAndThenReturnsNextValue() {
    val property =
      LottieDecoder.json.decodeFromString(
        BasePositionPropertySerializer,
        """{"a":1,"k":[{"t":0,"s":[0,0],"h":1},{"t":10,"s":[100,100]}]}""",
      )
    for ((frame, expected) in holdFrames) {
      val result = animatePosition(property, LottieSettings(frame.rf, SlotMap.Empty))
      assertThat(result.x.constantValue).isEqualTo(expected)
      assertThat(result.y.constantValue).isEqualTo(expected)
    }
  }

  private val holdFrames = listOf(0f to 0f, 5f to 0f, 9.99f to 0f, 10f to 100f)

  private fun assertFirstBezierVertexBeforeNegativeFirstKeyframe(hold: Int) {
    val property =
      LottieDecoder.json.decodeFromString(
        BaseBezierPropertySerializer,
        """
        {
          "a": 1,
          "k": [
            {
              "t": -10,
              "h": $hold,
              "s": [{"c": false, "v": [[10, 20]], "i": [[0, 0]], "o": [[0, 0]]}],
              "o": {"x": 0, "y": 0},
              "i": {"x": 1, "y": 1}
            },
            {
              "t": 10,
              "s": [{"c": false, "v": [[100, 200]], "i": [[0, 0]], "o": [[0, 0]]}]
            }
          ]
        }
        """
          .trimIndent(),
      )

    for (frame in listOf(-20f, -10f)) {
      val result = animateBezier(property, LottieSettings(frame.rf, SlotMap.Empty))
      assertThat(result).hasSize(1)
      assertThat(result.single().vertices.single().map { it.constantValue })
        .containsExactly(10f, 20f)
        .inOrder()
    }
  }

  private fun assertLinearProgress(duration: Float, frame: Float, expected: Float) {
    val actual = lookupValueInBezier(0f, 0f, 1f, 1f, duration, frame.rf).constantValue
    assertThat(actual.isFinite()).isTrue()
    assertThat(actual).isWithin(0.001f).of(expected)
  }
}

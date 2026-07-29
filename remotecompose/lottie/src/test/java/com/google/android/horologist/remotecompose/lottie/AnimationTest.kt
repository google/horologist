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
import com.google.android.horologist.remotecompose.lottie.format.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.VectorPropertyKeyframe
import com.google.android.horologist.remotecompose.lottie.renderer.SlotMap
import com.google.android.horologist.remotecompose.lottie.renderer.animateVector
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimationTest {
  private val emptySlotMap = SlotMap(emptyMap())

  @Test
  fun animateVectorWithStaticInput_returnsInput() {
    val staticVector = StaticVectorProperty(value = floatArrayOf(1f, 2f, 3f))

    val result1 = animateVector(staticVector, LottieSettings(0.rf, emptySlotMap))
    assertThat(result1.map { it.constantValue }.toFloatArray())
      .isEqualTo(staticVector.value)

    val result2 = animateVector(staticVector, LottieSettings(5.rf, emptySlotMap))
    assertThat(result2.map { it.constantValue }.toFloatArray())
      .isEqualTo(staticVector.value)
  }

  @Test
  fun animateVectorWithSingleKeyframe_returnsInput() {
    val animatedVector = AnimatedVectorProperty(
      keyframes = listOf(VectorPropertyKeyframe(frame = 0f, value = floatArrayOf(1f, 2f, 3f)))
    )

    val result1 = animateVector(animatedVector, LottieSettings(0.rf, emptySlotMap))
    assertThat(result1.map { it.constantValue }.toFloatArray())
      .isEqualTo(animatedVector.keyframes[0].value)

    val result2 = animateVector(animatedVector, LottieSettings(5.rf, emptySlotMap))
    assertThat(result2.map { it.constantValue }.toFloatArray())
      .isEqualTo(animatedVector.keyframes[0].value)
  }

  @Test
  fun animateVectorWithTwoKeyframes_returnsAnimatedValues() {
    val animatedVector = AnimatedVectorProperty(
      keyframes = listOf(
        VectorPropertyKeyframe(frame = 0f, value = floatArrayOf(1f, 2f, 3f)),
        VectorPropertyKeyframe(frame = 10f, value = floatArrayOf(4f, 5f, 6f)),
      )
    )

    val firstFrameResult = animateVector(animatedVector, LottieSettings(0.rf, emptySlotMap))
    val middleFrameResult = animateVector(animatedVector, LottieSettings(5.rf, emptySlotMap))
    val lastFrameResult = animateVector(animatedVector, LottieSettings(10.rf, emptySlotMap))
    val afterAnimationResult = animateVector(animatedVector, LottieSettings(15.rf, emptySlotMap))

    assertThat(firstFrameResult.map { it.constantValue }.toFloatArray())
      .isEqualTo(animatedVector.keyframes[0].value)
    assertThat(middleFrameResult.map { it.constantValue }.toFloatArray())
      .isEqualTo(floatArrayOf(2.5f, 3.5f, 4.5f))
    assertThat(lastFrameResult.map { it.constantValue }.toFloatArray())
      .isEqualTo(animatedVector.keyframes[1].value)
    assertThat(afterAnimationResult.map { it.constantValue }.toFloatArray())
      .isEqualTo(animatedVector.keyframes[1].value)
  }
}

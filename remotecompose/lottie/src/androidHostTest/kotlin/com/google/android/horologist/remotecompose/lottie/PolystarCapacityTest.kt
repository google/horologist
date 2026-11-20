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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStar
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.polystarCapacity
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PolystarCapacityTest {
  @Test
  fun linearMotionCanReachTheCapacityLimit() {
    assertThat(polystarCapacity(star(990, 1024))).isEqualTo(1024)
  }

  @Test
  fun heldMotionDoesNotUseInactiveEasingBounds() {
    assertThat(polystarCapacity(star(3, 5, controls = 100, hold = 1))).isEqualTo(5)
  }

  @Test
  fun increasingOvershootIsIncluded() {
    assertThat(polystarCapacity(star(3, 5, controls = 2))).isEqualTo(7)
  }

  @Test
  fun decreasingOvershootIsIncluded() {
    assertThat(polystarCapacity(star(5, 3, controls = -1))).isEqualTo(7)
  }

  @Test
  fun excessiveCapacityIsRejected() {
    assertThrows(IllegalArgumentException::class.java) {
      polystarCapacity(star(1023, 1024, controls = 3))
    }
  }

  @Test
  fun negativeAuthoredCountsAreRejected() {
    assertThrows(IllegalArgumentException::class.java) { polystarCapacity(star(-1, 3)) }
  }

  private fun star(start: Int, end: Int, controls: Int = 1, hold: Int = 0): PolyStar =
    Json.decodeFromString(
      """{"pt":{"a":1,"k":[{"t":0,"s":[$start],"h":$hold,
        "o":{"x":0.3,"y":$controls},"i":{"x":0.7,"y":$controls}},{"t":10,"s":[$end]}]},
        "p":{"a":0,"k":[32,32]},"r":{"a":0,"k":0},"or":{"a":0,"k":20},"os":{"a":0,"k":0}}"""
    )
}

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
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.ZigZag
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZigZagPointTypeTest {
  @Test
  fun legacyNumbersKeepTheirExactValue() {
    for (value in listOf("1", "2", "2.0", "2.5", "3")) {
      val modifier = Json.decodeFromString<ZigZag>("""{"pt":$value}""")
      assertThat((modifier.pointType as StaticScalarProperty).value.constantValue)
        .isEqualTo(value.toFloat())
    }
  }

  @Test
  fun missingPointTypeDefaultsToCorner() {
    val modifier = Json.decodeFromString<ZigZag>("{}")
    assertThat((modifier.pointType as StaticScalarProperty).value.constantValue).isEqualTo(1f)
  }

  @Test
  fun staticPropertyRoundTrips() {
    val modifier = Json.decodeFromString<ZigZag>("""{"pt":{"a":0,"k":2}}""")
    val restored = Json.decodeFromString<ZigZag>(Json.encodeToString(ZigZag.serializer(), modifier))
    assertThat((restored.pointType as StaticScalarProperty).value.constantValue).isEqualTo(2f)
  }

  @Test
  fun animatedPropertyRoundTripsWithoutLosingHolds() {
    val modifier =
      Json.decodeFromString<ZigZag>(
        """{"pt":{"a":1,"k":[{"t":0,"s":[1],"h":1},{"t":5,"s":[2],"h":1},{"t":10,"s":[1]}]}}"""
      )
    val restored = Json.decodeFromString<ZigZag>(Json.encodeToString(ZigZag.serializer(), modifier))
    for ((frame, expected) in listOf(0f to 1f, 4f to 1f, 5f to 2f, 9f to 2f, 10f to 1f)) {
      assertThat(animateScalar(restored.pointType, LottieSettings(frame.rf)).constantValue)
        .isEqualTo(expected)
    }
  }

  @Test
  fun malformedPointTypeIsNotSilentlyReplaced() {
    for (value in
      listOf("null", "true", "\"smooth\"", "[]", "{}", """{"a":0}""", """{"a":2,"k":2}""")) {
      assertThrows(SerializationException::class.java) {
        Json.decodeFromString<ZigZag>("""{"pt":$value}""")
      }
    }
  }
}

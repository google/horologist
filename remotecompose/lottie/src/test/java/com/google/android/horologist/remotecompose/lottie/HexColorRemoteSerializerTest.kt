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

import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.layer.Layer
import com.google.android.horologist.remotecompose.lottie.format.layer.LayerType
import com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer
import com.google.android.horologist.remotecompose.lottie.format.values.HexColorRemoteSerializer
import com.google.android.horologist.remotecompose.lottie.format.values.RemoteColorSerializer
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableHexColor
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for [HexColorRemoteSerializer] conforming to Lottie's
 * [Hex Color](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#hex-color).
 */
@RunWith(AndroidJUnit4::class)
class HexColorRemoteSerializerTest {

  @Serializable private data class ColorHolder(val color: SerializableHexColor)

  @Test
  fun deserializesStandard6DigitHexColor() {
    val json = """{"color": "#ff8000"}"""
    val holder = LottieDecoder.json.decodeFromString<ColorHolder>(json)
    val color = holder.color.constantValue

    assertThat(color.red).isWithin(0.01f).of(1.0f)
    assertThat(color.green).isWithin(0.01f).of(0.502f)
    assertThat(color.blue).isWithin(0.01f).of(0.0f)
    assertThat(color.alpha).isEqualTo(1.0f)
  }

  @Test
  fun deserializesCaseInsensitiveHexColor() {
    val json = """{"color": "#00FF00"}"""
    val holder = LottieDecoder.json.decodeFromString<ColorHolder>(json)
    val color = holder.color.constantValue

    assertThat(color.red).isEqualTo(0.0f)
    assertThat(color.green).isEqualTo(1.0f)
    assertThat(color.blue).isEqualTo(0.0f)
    assertThat(color.alpha).isEqualTo(1.0f)
  }

  @Test
  fun deserializesHexColorWithoutLeadingHash() {
    val json = """{"color": "0000ff"}"""
    val holder = LottieDecoder.json.decodeFromString<ColorHolder>(json)
    val color = holder.color.constantValue

    assertThat(color.red).isEqualTo(0.0f)
    assertThat(color.green).isEqualTo(0.0f)
    assertThat(color.blue).isEqualTo(1.0f)
    assertThat(color.alpha).isEqualTo(1.0f)
  }

  @Test
  fun deserializes8DigitArgbHexColor() {
    val json = """{"color": "#80ff0000"}"""
    val holder = LottieDecoder.json.decodeFromString<ColorHolder>(json)
    val color = holder.color.constantValue

    assertThat(color.alpha).isWithin(0.01f).of(0.502f)
    assertThat(color.red).isEqualTo(1.0f)
    assertThat(color.green).isEqualTo(0.0f)
    assertThat(color.blue).isEqualTo(0.0f)
  }

  @Test
  fun deserializes3DigitShorthandHexColor() {
    val json = """{"color": "#f00"}"""
    val holder = LottieDecoder.json.decodeFromString<ColorHolder>(json)
    val color = holder.color.constantValue

    assertThat(color.red).isEqualTo(1.0f)
    assertThat(color.green).isEqualTo(0.0f)
    assertThat(color.blue).isEqualTo(0.0f)
    assertThat(color.alpha).isEqualTo(1.0f)
  }

  @Test
  fun throwsSerializationExceptionWhenHexColorIsInvalidLength() {
    val invalidLengths = listOf("#1", "#12", "#1234", "#12345", "#1234567", "#123456789")
    for (invalid in invalidLengths) {
      val json = """{"color": "$invalid"}"""
      assertThrows(SerializationException::class.java) {
        LottieDecoder.json.decodeFromString<ColorHolder>(json)
      }
    }
  }

  @Test
  fun throwsSerializationExceptionWhenHexColorContainsInvalidHexCharacters() {
    val json = """{"color": "#zz0000"}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString<ColorHolder>(json)
    }
  }

  @Test
  fun throwsSerializationExceptionWhenHexColorIsNumericArray() {
    val json = """{"color": [1.0, 0.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString<ColorHolder>(json)
    }
  }

  @Test
  fun serializesOpaqueColorTo6DigitHex() {
    val holder = ColorHolder(color = Color(1f, 0f, 0f, 1f).rc)
    val encoded = LottieDecoder.json.encodeToString(ColorHolder.serializer(), holder)

    assertThat(encoded).isEqualTo("""{"color":"#ff0000"}""")
  }

  @Test
  fun serializesSemiTransparentColorTo8DigitHex() {
    val holder = ColorHolder(color = Color(0f, 1f, 0f, 0.5f).rc)
    val encoded = LottieDecoder.json.encodeToString(ColorHolder.serializer(), holder)

    assertThat(encoded).isEqualTo("""{"color":"#8000ff00"}""")
  }

  @Test
  fun roundTripSerializationFidelity() {
    val original = ColorHolder(color = Color(0.2f, 0.4f, 0.6f, 1.0f).rc)
    val encoded = LottieDecoder.json.encodeToString(ColorHolder.serializer(), original)
    val decoded = LottieDecoder.json.decodeFromString<ColorHolder>(encoded)

    assertThat(decoded.color.constantValue.red).isWithin(0.01f).of(0.2f)
    assertThat(decoded.color.constantValue.green).isWithin(0.01f).of(0.4f)
    assertThat(decoded.color.constantValue.blue).isWithin(0.01f).of(0.6f)
    assertThat(decoded.color.constantValue.alpha).isEqualTo(1.0f)
  }

  @Test
  fun deserializesSolidColorLayerWithHexColor() {
    val json =
      """
      {
        "ty": 1,
        "nm": "Background Solid",
        "ip": 0,
        "op": 60,
        "sw": 1920,
        "sh": 1080,
        "sc": "#3b5998"
      }
      """
        .trimIndent()

    val layer = LottieDecoder.json.decodeFromString(Layer.serializer(), json)
    assertThat(layer).isInstanceOf(SolidColorLayer::class.java)
    val solid = layer as SolidColorLayer
    assertThat(solid.type).isEqualTo(LayerType.Solid)
    assertThat(solid.name).isEqualTo("Background Solid")
    assertThat(solid.solidWidth.constantValue).isEqualTo(1920)
    assertThat(solid.solidHeight.constantValue).isEqualTo(1080)
    val color = solid.solidColor.constantValue
    assertThat(color.red).isWithin(0.01f).of(0x3b / 255f)
    assertThat(color.green).isWithin(0.01f).of(0x59 / 255f)
    assertThat(color.blue).isWithin(0.01f).of(0x98 / 255f)
    assertThat(color.alpha).isEqualTo(1.0f)
  }

  @Test
  fun remoteColorSerializerRejectsBareString() {
    val json = """"#ffffff""""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(RemoteColorSerializer, json)
    }
  }
}

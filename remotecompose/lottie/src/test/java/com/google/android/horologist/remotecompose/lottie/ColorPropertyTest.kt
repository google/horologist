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

import androidx.compose.remote.creation.compose.state.RemoteBoolean
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.properties.ScalarKeyframeEasing
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Functional black-box tests for Lottie Color Property specification (SP_LOT_CLR).
 *
 * Contracts under test:
 * - BaseColorPropertySerializer: Polymorphic schema validation and deserialization
 *   [SP_LOT_CLR_02_01]
 * - StaticColorProperty: Constant RGB/RGBA color representation [SP_LOT_CLR_01_01]
 * - AnimatedColorProperty: Keyframed color animation sequence [SP_LOT_CLR_01_02]
 * - ColorPropertyKeyframe: Temporal color keyframe with easing/hold [SP_LOT_CLR_01_03]
 * - animateColor: Timeline evaluation with clamping, interpolation, and slot overrides
 *   [SP_LOT_CLR_02_02]
 */
@RunWith(AndroidJUnit4::class)
class ColorPropertyTest {
  private val emptySlotMap = SlotMap.Empty

  private fun extractFloat(value: Any): Float =
    when (value) {
      is RemoteFloat -> value.constantValue
      is Number -> value.toFloat()
      else -> error("Unexpected float value type: ${value::class}")
    }

  private fun extractBoolean(value: Any?): Boolean =
    when (value) {
      null -> false
      is RemoteBoolean -> value.constantValue
      is Boolean -> value
      else -> error("Unexpected boolean value type: ${value::class}")
    }

  private fun extractFloatList(list: List<Any>): List<Float> = list.map { extractFloat(it) }

  private fun extractColor(value: Any): Color =
    when (value) {
      is RemoteColor -> value.constantValue
      is Color -> value
      else -> error("Unexpected color value type: ${value::class}")
    }

  // =========================================================================================
  // Suite A: BaseColorPropertySerializer Deserialization & Strict Schema Validation
  // =========================================================================================

  /**
   * [SP_LOT_CLR_02_01] Rejects bare primitive number without enclosing JSON object.
   *
   * Verifies that when a bare numeric literal is provided instead of a JSON object,
   * BaseColorPropertySerializer throws [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveNumber() {
    val json = "0.5"
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects bare primitive string without enclosing JSON object.
   *
   * Verifies that when a bare string literal is provided instead of a JSON object,
   * BaseColorPropertySerializer throws [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveString() {
    val json = "\"red\""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects bare primitive boolean literal without enclosing JSON object.
   *
   * Verifies that when a bare boolean literal is provided, BaseColorPropertySerializer throws
   * [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveBoolean() {
    val json = "true"
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects bare array without enclosing JSON object.
   *
   * Verifies that bare component arrays lacking an enclosing object with discriminator 'a' throw
   * [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBareArray() {
    val json = "[1.0, 0.0, 0.0]"
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color property missing mandatory discriminator 'a'.
   *
   * Verifies that an object omitting the integer-boolean discriminator 'a' throws
   * [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsMissing() {
    val json = """{"k": [1.0, 0.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color property with invalid discriminator integer value.
   *
   * Verifies that an integer discriminator other than 0 or 1 throws [SerializationException].
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsInvalidInteger() {
    val json = """{"a": 2, "k": [1.0, 0.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color property with negative discriminator integer value.
   *
   * Verifies that negative discriminator values throw [SerializationException].
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsNegativeInteger() {
    val json = """{"a": -1, "k": [1.0, 0.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color property when discriminator 'a' is a JSON boolean literal.
   *
   * Verifies that boolean literals (true/false) are rejected because the schema requires integer 0
   * or 1.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteral() {
    val json = """{"a": true, "k": [1.0, 0.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color property when discriminator 'a' is a string literal.
   *
   * Verifies that string representations of numbers like "0" are rejected per schema strictness.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsString() {
    val json = """{"a": "0", "k": [1.0, 0.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite B: Static Color Property Deserialization, Normalization & Slot ID
  // =========================================================================================

  /**
   * [SP_LOT_CLR_01_01] Deserializes static RGB color property when discriminator 'a' is integer 0.
   *
   * Verifies that an RGB array [r, g, b] deserializes into a [StaticColorProperty] with implicit
   * alpha = 1.0.
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun deserializesStaticRgbColorPropertyWhenDiscriminatorIsZero() {
    val json = """{"a": 0, "k": [1.0, 0.0, 0.5]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json) as StaticColorProperty
    assertThat(extractBoolean(prop.animated)).isFalse()
    assertThat(prop.slotId).isNull()
    assertThat(extractFloatList(prop.value)).containsExactly(1.0f, 0.0f, 0.5f).inOrder()
  }

  /**
   * [SP_LOT_CLR_01_01] Deserializes static RGBA color property with explicit alpha channel.
   *
   * Verifies that a 4-component array [r, g, b, a] deserializes preserving the alpha component.
   *
   * Specification:
   * [Lottie Color Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color)
   */
  @Test
  fun deserializesStaticRgbaColorPropertyWithAlphaChannel() {
    val json = """{"a": 0, "k": [0.0, 1.0, 0.0, 0.8]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json) as StaticColorProperty
    assertThat(extractBoolean(prop.animated)).isFalse()
    assertThat(extractFloatList(prop.value)).containsExactly(0.0f, 1.0f, 0.0f, 0.8f).inOrder()
  }

  /**
   * [SP_LOT_CLR_01_01] Deserializes static color property with slot identifier when 'sid' is
   * present.
   *
   * Verifies that the optional slot identifier 'sid' is preserved on [StaticColorProperty].
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesStaticColorPropertyWithSlotIdWhenSidPresent() {
    val json = """{"a": 0, "k": [1.0, 1.0, 1.0], "sid": "theme_color"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json) as StaticColorProperty
    assertThat(prop.slotId).isEqualTo("theme_color")
    assertThat(extractFloatList(prop.value)).containsExactly(1.0f, 1.0f, 1.0f).inOrder()
  }

  /**
   * [SP_LOT_CLR_03_02] Normalizes legacy color component values exceeding 1.0 by dividing by 255.
   *
   * Verifies backward compatibility for legacy animations with [0..255] color values, normalizing
   * them to [0.0..1.0].
   *
   * Specification:
   * [Lottie Color Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color)
   */
  @Test
  fun normalizesLegacyColorValuesExceedingOneByScaling255() {
    val json = """{"a": 0, "k": [255.0, 127.5, 0.0, 255.0]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json) as StaticColorProperty
    val values = extractFloatList(prop.value)
    assertThat(values[0]).isWithin(1e-4f).of(1.0f)
    assertThat(values[1]).isWithin(1e-4f).of(0.5f)
    assertThat(values[2]).isWithin(1e-4f).of(0.0f)
    assertThat(values[3]).isWithin(1e-4f).of(1.0f)
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects static color property missing value field 'k'.
   *
   * Verifies that a static color property object omitting 'k' throws [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyMissingK() {
    val json = """{"a": 0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects static color property when 'k' is not a JSON array.
   *
   * Verifies that primitive values for 'k' throw [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsNotArray() {
    val json = """{"a": 0, "k": 1.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects static color property when 'k' has fewer than three components.
   *
   * Verifies that arrays with length < 3 (minItems: 3) throw [SerializationException].
   *
   * Specification:
   * [Lottie Color Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKHasFewerThanThreeComponents() {
    val json = """{"a": 0, "k": [1.0, 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects static color property when 'k' has more than four components.
   *
   * Verifies that arrays with length > 4 (maxItems: 4) throw [SerializationException].
   *
   * Specification:
   * [Lottie Color Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKHasMoreThanFourComponents() {
    val json = """{"a": 0, "k": [1.0, 0.0, 0.0, 1.0, 0.5]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects static color property when 'k' contains non-numeric elements.
   *
   * Verifies that arrays containing non-numeric values throw [SerializationException].
   *
   * Specification:
   * [Lottie Color Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKContainsNonNumericElements() {
    val json = """{"a": 0, "k": [1.0, "red", 0.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite C: Animated Color Property & Keyframe Schema Validation
  // =========================================================================================

  /**
   * [SP_LOT_CLR_01_02] Deserializes animated color property when discriminator 'a' is integer 1.
   *
   * Verifies that an animated color property deserializes into [AnimatedColorProperty] containing
   * chronological keyframes.
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun deserializesAnimatedColorPropertyWhenDiscriminatorIsOne() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0]}, {"t": 10, "s": [0.0, 0.0, 1.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
        as AnimatedColorProperty
    assertThat(extractBoolean(prop.animated)).isTrue()
    assertThat(prop.keyframes).hasSize(2)
    assertThat(extractFloat(prop.keyframes[0].frame)).isEqualTo(0.0f)
    assertThat(extractFloatList(prop.keyframes[0].value))
      .containsExactly(1.0f, 0.0f, 0.0f)
      .inOrder()
    assertThat(extractFloat(prop.keyframes[1].frame)).isEqualTo(10.0f)
    assertThat(extractFloatList(prop.keyframes[1].value))
      .containsExactly(0.0f, 0.0f, 1.0f)
      .inOrder()
  }

  /**
   * [SP_LOT_CLR_01_02] Deserializes animated color property with slot identifier when 'sid' is
   * present.
   *
   * Verifies that 'sid' is captured on [AnimatedColorProperty].
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesAnimatedColorPropertyWithSlotId() {
    val json = """{"a": 1, "sid": "anim_slot", "k": [{"t": 0, "s": [1.0, 0.0, 0.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
        as AnimatedColorProperty
    assertThat(prop.slotId).isEqualTo("anim_slot")
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects animated color property missing keyframes array 'k'.
   *
   * Verifies that an animated property omitting 'k' throws [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyMissingK() {
    val json = """{"a": 1}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects animated color property when 'k' is a primitive value.
   *
   * Verifies that a primitive number or string for 'k' on an animated property throws
   * [SerializationException].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyKIsPrimitive() {
    val json = """{"a": 1, "k": 10}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color keyframe missing mandatory start frame 't'.
   *
   * Verifies that keyframe objects lacking 't' throw [SerializationException].
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingT() {
    val json = """{"a": 1, "k": [{"s": [1.0, 0.0, 0.0]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color keyframe missing mandatory start value 's'.
   *
   * Verifies that keyframe objects lacking 's' throw [SerializationException].
   *
   * Specification:
   * [Lottie Color Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingS() {
    val json = """{"a": 1, "k": [{"t": 0}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color keyframe when 's' has fewer than three components.
   *
   * Verifies that keyframe value arrays with fewer than 3 elements throw [SerializationException].
   *
   * Specification:
   * [Lottie Color Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeSHasInvalidComponentCount() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color keyframe when hold flag 'h' is a boolean literal.
   *
   * Verifies that boolean literals for 'h' are rejected per the integer-boolean schema requirement.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsBooleanLiteral() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0], "h": true}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_02_01] Rejects color keyframe when hold flag 'h' is an integer other than 0 or 1.
   *
   * Verifies that invalid hold values like 2 throw [SerializationException].
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsInvalidInteger() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0], "h": 2}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_CLR_01_03] Deserializes color keyframe with hold flag set to integer 1.
   *
   * Verifies that 'h = 1' deserializes to hold = true.rb on [ColorPropertyKeyframe].
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun deserializesKeyframeWithHoldFlagOne() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0], "h": 1}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
        as AnimatedColorProperty
    assertThat(extractBoolean(prop.keyframes[0].hold)).isTrue()
  }

  /**
   * [SP_LOT_CLR_01_03] Deserializes color keyframe with cubic Bézier easing tangents.
   *
   * Verifies that 'i' and 'o' easing handles are deserialized into [ScalarKeyframeEasing].
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun deserializesKeyframeWithBezierTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0], "i": {"x": [0.4], "y": [1.0]}, "o": {"x": [0.2], "y": [0.0]}}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
        as AnimatedColorProperty
    val kf = prop.keyframes[0]
    assertThat(kf.inTangent).isNotNull()
    assertThat(kf.outTangent).isNotNull()
  }

  // =========================================================================================
  // Suite D: Serialization & Round-Trip Fidelity
  // =========================================================================================

  /**
   * [SP_LOT_CLR_01_01] Serializes and deserializes static color property preserving exact component
   * fidelity.
   *
   * Verifies round-trip encoding and decoding for [StaticColorProperty].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun serializesAndDeserializesStaticColorPropertyPreservingComponents() {
    val original =
      StaticColorProperty(value = listOf(1.0f.rf, 0.5f.rf, 0.25f.rf, 1.0f.rf), slotId = "accent")
    val encoded = LottieDecoder.json.encodeToString(BaseColorPropertySerializer, original)
    val decoded =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, encoded)
        as StaticColorProperty
    assertThat(extractBoolean(decoded.animated)).isFalse()
    assertThat(decoded.slotId).isEqualTo("accent")
    assertThat(extractFloatList(decoded.value)).containsExactly(1.0f, 0.5f, 0.25f, 1.0f).inOrder()
  }

  /**
   * [SP_LOT_CLR_01_02] Serializes and deserializes animated color property preserving keyframe
   * sequences.
   *
   * Verifies round-trip encoding and decoding for [AnimatedColorProperty].
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun serializesAndDeserializesAnimatedColorPropertyPreservingKeyframes() {
    val original =
      AnimatedColorProperty(
        keyframes =
          listOf(
            ColorPropertyKeyframe(
              frame = 0f.rf,
              value = listOf(1f.rf, 0f.rf, 0f.rf, 1f.rf),
              hold = false.rb,
            ),
            ColorPropertyKeyframe(
              frame = 10f.rf,
              value = listOf(0f.rf, 1f.rf, 0f.rf, 1f.rf),
              hold = true.rb,
            ),
          ),
        slotId = "anim_theme",
      )
    val encoded = LottieDecoder.json.encodeToString(BaseColorPropertySerializer, original)
    val decoded =
      LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, encoded)
        as AnimatedColorProperty
    assertThat(extractBoolean(decoded.animated)).isTrue()
    assertThat(decoded.slotId).isEqualTo("anim_theme")
    assertThat(decoded.keyframes).hasSize(2)
    assertThat(extractFloat(decoded.keyframes[0].frame)).isEqualTo(0f)
    assertThat(extractFloatList(decoded.keyframes[0].value))
      .containsExactly(1f, 0f, 0f, 1f)
      .inOrder()
    assertThat(extractBoolean(decoded.keyframes[0].hold)).isFalse()
    assertThat(extractFloat(decoded.keyframes[1].frame)).isEqualTo(10f)
    assertThat(extractFloatList(decoded.keyframes[1].value))
      .containsExactly(0f, 1f, 0f, 1f)
      .inOrder()
    assertThat(extractBoolean(decoded.keyframes[1].hold)).isTrue()
  }

  // =========================================================================================
  // Suite E: Timeline Evaluation (animateColor) - Static & Slot Overrides
  // =========================================================================================

  /**
   * [SP_LOT_CLR_02_02] Evaluates static color property as a constant RemoteColor across timeline
   * frames.
   *
   * Verifies that [animateColor] returns an identical color at any timeline frame for static
   * properties.
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun returnsConstantColorForStaticColorPropertyAcrossAllFrames() {
    val json = """{"a": 0, "k": [1.0, 0.0, 0.0]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val frames = listOf(-10f, 0f, 5f, 20f, 100f)
    for (frame in frames) {
      val evaluated = animateColor(colorProp, LottieSettings(frame.rf, emptySlotMap))
      val color = extractColor(evaluated)
      assertThat(color.red).isEqualTo(1.0f)
      assertThat(color.green).isEqualTo(0.0f)
      assertThat(color.blue).isEqualTo(0.0f)
      assertThat(color.alpha).isEqualTo(1.0f)
    }
  }

  /**
   * [SP_LOT_CLR_02_02] Evaluates slot override color when 'sid' matches an entry in SlotMap.
   *
   * Verifies that dynamic slot overrides take precedence over the static property value.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun returnsSlotOverrideColorWhenSlotIdMatchesInSlotMap() {
    val json = """{"a": 0, "k": [0.0, 0.0, 1.0], "sid": "theme_color"}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    val slotMap = SlotMap(colors = mapOf("theme_color" to 0xFF00FF00.toInt())) // Green override

    val evaluated = animateColor(colorProp, LottieSettings(0f.rf, slotMap))
    val color = extractColor(evaluated)
    assertThat(color.red).isEqualTo(0.0f)
    assertThat(color.green).isEqualTo(1.0f)
    assertThat(color.blue).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_CLR_02_02] Falls back to default color value when slotId is not present in SlotMap.
   *
   * Verifies fallback behavior when the slotId has no corresponding mapping.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun fallsBackToPropertyValueWhenSlotIdNotInSlotMap() {
    val json = """{"a": 0, "k": [0.0, 0.0, 1.0], "sid": "unmatched_slot"}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    val slotMap = SlotMap(colors = mapOf("other_slot" to 0xFFFF0000.toInt()))

    val evaluated = animateColor(colorProp, LottieSettings(0f.rf, slotMap))
    val color = extractColor(evaluated)
    assertThat(color.red).isEqualTo(0.0f)
    assertThat(color.green).isEqualTo(0.0f)
    assertThat(color.blue).isEqualTo(1.0f)
  }

  /**
   * [SP_LOT_CLR_02_02] Evaluates slot override color for animated property when slot matches.
   *
   * Verifies that slot overrides supersede animated keyframe evaluation across all frames.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun returnsSlotOverrideColorForAnimatedColorPropertyWhenSlotMatches() {
    val json =
      """{"a": 1, "sid": "anim_slot", "k": [{"t": 0, "s": [1.0, 0.0, 0.0]}, {"t": 10, "s": [0.0, 0.0, 1.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)
    val slotMap = SlotMap(colors = mapOf("anim_slot" to 0xFFFFFF00.toInt())) // Yellow override

    val frames = listOf(0f, 5f, 10f, 20f)
    for (frame in frames) {
      val evaluated = animateColor(colorProp, LottieSettings(frame.rf, slotMap))
      val color = extractColor(evaluated)
      assertThat(color.red).isEqualTo(1.0f)
      assertThat(color.green).isEqualTo(1.0f)
      assertThat(color.blue).isEqualTo(0.0f)
    }
  }

  /**
   * [SP_LOT_CLR_02_02] Returns transparent color when animated keyframes list is empty.
   *
   * Verifies boundary fallback to Color.Transparent when an animated property has no keyframes.
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun returnsTransparentColorWhenAnimatedKeyframesListIsEmpty() {
    val json = """{"a": 1, "k": []}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val evaluated = animateColor(colorProp, LottieSettings(5f.rf, emptySlotMap))
    val color = extractColor(evaluated)
    assertThat(color.alpha).isEqualTo(0.0f)
  }

  // =========================================================================================
  // Suite F: Timeline Evaluation (animateColor) - Clamping, Lerp, Bézier & Hold
  // =========================================================================================

  /**
   * [SP_LOT_CLR_02_02] Evaluates single keyframe as a constant color across the entire timeline.
   *
   * Verifies that when exactly one keyframe exists, its color value is held unconditionally.
   *
   * Specification:
   * [Lottie Color Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-keyframe)
   */
  @Test
  fun returnsSingleKeyframeColorConstantAcrossTimeline() {
    val json = """{"a": 1, "k": [{"t": 5, "s": [0.0, 1.0, 0.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val frames = listOf(0f, 5f, 10f, 50f)
    for (frame in frames) {
      val evaluated = animateColor(colorProp, LottieSettings(frame.rf, emptySlotMap))
      val color = extractColor(evaluated)
      assertThat(color.red).isEqualTo(0.0f)
      assertThat(color.green).isEqualTo(1.0f)
      assertThat(color.blue).isEqualTo(0.0f)
    }
  }

  /**
   * [SP_LOT_CLR_02_02] Clamps to first keyframe color when timeline frame precedes first keyframe.
   *
   * Verifies pre-animation clamping behavior before the first keyframe timestamp.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun clampsToFirstKeyframeColorWhenCurrentFramePrecedesFirstKeyframe() {
    val json =
      """{"a": 1, "k": [{"t": 10, "s": [0.0, 0.0, 1.0]}, {"t": 20, "s": [1.0, 0.0, 0.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val frames = listOf(-5f, 0f, 5f, 9.9f)
    for (frame in frames) {
      val evaluated = animateColor(colorProp, LottieSettings(frame.rf, emptySlotMap))
      val color = extractColor(evaluated)
      assertThat(color.red).isEqualTo(0.0f)
      assertThat(color.green).isEqualTo(0.0f)
      assertThat(color.blue).isEqualTo(1.0f)
    }
  }

  /**
   * [SP_LOT_CLR_02_02] Holds last keyframe color when timeline frame exceeds last keyframe
   * timestamp.
   *
   * Verifies post-animation holding behavior after the animation sequence ends.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun holdsAtLastKeyframeColorWhenCurrentFrameExceedsLastKeyframe() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0, 1.0]}, {"t": 10, "s": [1.0, 1.0, 0.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val frames = listOf(10f, 15f, 100f)
    for (frame in frames) {
      val evaluated = animateColor(colorProp, LottieSettings(frame.rf, emptySlotMap))
      val color = extractColor(evaluated)
      assertThat(color.red).isEqualTo(1.0f)
      assertThat(color.green).isEqualTo(1.0f)
      assertThat(color.blue).isEqualTo(0.0f)
    }
  }

  /**
   * [SP_LOT_CLR_02_02] Linearly interpolates RGBA color channels between consecutive keyframes.
   *
   * Verifies component-wise lerp interpolation across red, green, blue, and alpha channels.
   *
   * Specification:
   * [Lottie Color Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-keyframe)
   */
  @Test
  fun linearlyInterpolatesColorChannelsBetweenConsecutiveKeyframes() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0, 1.0]}, {"t": 10, "s": [0.0, 0.0, 1.0, 1.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val eval0 = animateColor(colorProp, LottieSettings(0f.rf, emptySlotMap))
    val c0 = extractColor(eval0)
    assertThat(c0.red).isWithin(1e-4f).of(1.0f)
    assertThat(c0.blue).isWithin(1e-4f).of(0.0f)

    val eval5 = animateColor(colorProp, LottieSettings(5f.rf, emptySlotMap))
    val c5 = extractColor(eval5)
    assertThat(c5.red).isWithin(1e-4f).of(0.5f)
    assertThat(c5.green).isWithin(1e-4f).of(0.0f)
    assertThat(c5.blue).isWithin(1e-4f).of(0.5f)
    assertThat(c5.alpha).isWithin(1e-4f).of(1.0f)

    val eval10 = animateColor(colorProp, LottieSettings(10f.rf, emptySlotMap))
    val c10 = extractColor(eval10)
    assertThat(c10.red).isWithin(1e-4f).of(0.0f)
    assertThat(c10.blue).isWithin(1e-4f).of(1.0f)
  }

  /**
   * [SP_LOT_CLR_02_02] Interpolates color across multiple consecutive keyframe segments.
   *
   * Verifies that color transitions smoothly through chained keyframe intervals.
   *
   * Specification:
   * [Lottie Color Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#color-property)
   */
  @Test
  fun interpolatesMultiSegmentKeyframesAcrossConsecutiveIntervals() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0]}, {"t": 10, "s": [0.0, 0.0, 1.0]}, {"t": 20, "s": [0.0, 1.0, 0.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val eval5 = animateColor(colorProp, LottieSettings(5f.rf, emptySlotMap))
    val c5 = extractColor(eval5)
    assertThat(c5.red).isWithin(1e-4f).of(0.5f)
    assertThat(c5.blue).isWithin(1e-4f).of(0.5f)

    val eval15 = animateColor(colorProp, LottieSettings(15f.rf, emptySlotMap))
    val c15 = extractColor(eval15)
    assertThat(c15.blue).isWithin(1e-4f).of(0.5f)
    assertThat(c15.green).isWithin(1e-4f).of(0.5f)
  }

  /**
   * [SP_LOT_CLR_02_02] Holds color constant until next keyframe when hold flag 'h' is integer 1.
   *
   * Verifies step interpolation: start color is held until the exact moment of the next keyframe.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun holdsColorConstantUntilNextKeyframeWhenHoldFlagIsOne() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [1.0, 0.0, 0.0], "h": 1}, {"t": 10, "s": [0.0, 0.0, 1.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val framesHeld = listOf(0f, 5f, 9.99f)
    for (frame in framesHeld) {
      val evaluated = animateColor(colorProp, LottieSettings(frame.rf, emptySlotMap))
      val color = extractColor(evaluated)
      assertThat(color.red).isEqualTo(1.0f)
      assertThat(color.blue).isEqualTo(0.0f)
    }

    val eval10 = animateColor(colorProp, LottieSettings(10.0f.rf, emptySlotMap))
    val c10 = extractColor(eval10)
    assertThat(c10.red).isEqualTo(0.0f)
    assertThat(c10.blue).isEqualTo(1.0f)
  }

  /**
   * [SP_LOT_CLR_02_02] Interpolates color channels using cubic Bézier easing tangents.
   *
   * Verifies that easing curves alter intermediate channel progress relative to linear progression.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Test
  fun interpolatesColorWithCubicBezierTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0, 0.0], "i": {"x": [0.0], "y": [1.0]}, "o": {"x": [0.0], "y": [0.0]}}, {"t": 10, "s": [1.0, 1.0, 1.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val eval0 = animateColor(colorProp, LottieSettings(0f.rf, emptySlotMap))
    val c0 = extractColor(eval0)
    assertThat(c0.red).isEqualTo(0.0f)

    val eval10 = animateColor(colorProp, LottieSettings(10f.rf, emptySlotMap))
    val c10 = extractColor(eval10)
    assertThat(c10.red).isEqualTo(1.0f)

    // Eased midpoint departs from linear 0.5
    val eval5 = animateColor(colorProp, LottieSettings(5f.rf, emptySlotMap))
    val c5 = extractColor(eval5)
    assertThat(c5.red).isNotEqualTo(0.5f)
  }

  /**
   * [SP_LOT_CLR_02_02] Evaluates keyframes with negative frame timestamps.
   *
   * Verifies timeline evaluation across negative time ranges.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun evaluatesKeyframesWithNegativeFramesAndValues() {
    val json =
      """{"a": 1, "k": [{"t": -10, "s": [0.0, 0.0, 0.0]}, {"t": 10, "s": [1.0, 1.0, 1.0]}]}"""
    val colorProp = LottieDecoder.json.decodeFromString(BaseColorPropertySerializer, json)

    val evalNeg10 = animateColor(colorProp, LottieSettings(-10f.rf, emptySlotMap))
    val cNeg10 = extractColor(evalNeg10)
    assertThat(cNeg10.red).isEqualTo(0.0f)

    val eval0 = animateColor(colorProp, LottieSettings(0f.rf, emptySlotMap))
    val c0 = extractColor(eval0)
    assertThat(c0.red).isWithin(1e-4f).of(0.5f)

    val eval10 = animateColor(colorProp, LottieSettings(10f.rf, emptySlotMap))
    val c10 = extractColor(eval10)
    assertThat(c10.red).isEqualTo(1.0f)
  }
}

// =========================================================================================
// Test Fixtures & Contract Wrappers for SP_LOT_CLR Specification (TDD Red Phase)
// =========================================================================================

/** Abstract sealed base class for all animatable color properties [SP_LOT_CLR_01_00]. */
internal sealed class BaseColorProperty {
  abstract val animated: RemoteBoolean
  abstract val slotId: String?
}

/** Static constant color property holding RGB/RGBA component array [SP_LOT_CLR_01_01]. */
internal data class StaticColorProperty(
  val value: List<RemoteFloat>,
  override val slotId: String? = null,
  override val animated: RemoteBoolean = false.rb,
) : BaseColorProperty() {
  fun asRemoteColor(): RemoteColor {
    val r = value.getOrNull(0)?.constantValue ?: 0f
    val g = value.getOrNull(1)?.constantValue ?: 0f
    val b = value.getOrNull(2)?.constantValue ?: 0f
    val a = value.getOrNull(3)?.constantValue ?: 1f
    return Color(r, g, b, a).rc
  }
}

/** Animated color property holding temporal keyframes sequence [SP_LOT_CLR_01_02]. */
internal data class AnimatedColorProperty(
  val keyframes: List<ColorPropertyKeyframe>,
  override val slotId: String? = null,
  override val animated: RemoteBoolean = true.rb,
) : BaseColorProperty()

/** Color keyframe representation [SP_LOT_CLR_01_03]. */
internal data class ColorPropertyKeyframe(
  val frame: RemoteFloat,
  val value: List<RemoteFloat>,
  val hold: RemoteBoolean = false.rb,
  val inTangent: ScalarKeyframeEasing? = null,
  val outTangent: ScalarKeyframeEasing? = null,
) {
  fun asRemoteColor(): RemoteColor {
    val r = value.getOrNull(0)?.constantValue ?: 0f
    val g = value.getOrNull(1)?.constantValue ?: 0f
    val b = value.getOrNull(2)?.constantValue ?: 0f
    val a = value.getOrNull(3)?.constantValue ?: 1f
    return Color(r, g, b, a).rc
  }
}

/**
 * Polymorphic serializer for BaseColorProperty [SP_LOT_CLR_02_01]. Throws [NotImplementedError] in
 * TDD Red phase until production implementation is provided.
 */
internal object BaseColorPropertySerializer : KSerializer<BaseColorProperty> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BaseColorProperty")

  override fun deserialize(decoder: Decoder): BaseColorProperty {
    throw NotImplementedError(
      "Pending SP_LOT_CLR production implementation: BaseColorPropertySerializer.deserialize"
    )
  }

  override fun serialize(encoder: Encoder, value: BaseColorProperty) {
    throw NotImplementedError(
      "Pending SP_LOT_CLR production implementation: BaseColorPropertySerializer.serialize"
    )
  }
}

/**
 * Timeline color evaluation contract [SP_LOT_CLR_02_02]. Throws [NotImplementedError] in TDD Red
 * phase until production implementation is provided.
 */
internal fun animateColor(
  property: BaseColorProperty,
  animationSettings: LottieSettings,
): RemoteColor {
  throw NotImplementedError("Pending SP_LOT_CLR production implementation: animateColor")
}

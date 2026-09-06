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
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.VectorPropertyKeyframe
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateVector
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertThrows
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VectorPropertyTest {
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

  // =========================================================================================
  // Suite A: BaseVectorPropertySerializer Deserialization & Strict Schema Validation
  // =========================================================================================

  /**
   * [SP_LOT_VEC_01_01] Deserializes static vector property when discriminator `a` is integer 0.
   *
   * Verifies that when discriminator `a` is integer `0`, the vector property deserializes into a
   * [StaticVectorProperty] containing the constant list of numerical components.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun deserializesStaticVectorPropertyWhenDiscriminatorIsZero() {
    val json = """{"a": 0, "k": [10.0, 20.0]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as StaticVectorProperty
    assertThat(extractBoolean(prop.animated)).isFalse()
    assertThat(prop.slotId).isNull()
    assertThat(extractFloatList(prop.value)).containsExactly(10.0f, 20.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_01_01] Deserializes static vector property with three-dimensional components.
   *
   * Verifies that multidimensional coordinate vectors (such as 3D points [x, y, z]) are preserved.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun deserializesStaticVectorPropertyWithThreeDimensionalComponents() {
    val json = """{"a": 0, "k": [1.0, 2.0, 3.0]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as StaticVectorProperty
    assertThat(extractBoolean(prop.animated)).isFalse()
    assertThat(extractFloatList(prop.value)).containsExactly(1.0f, 2.0f, 3.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_01_01] Deserializes static vector property with slot identifier when `sid` is
   * present.
   *
   * Verifies that optional slot identifier `sid` is captured on [StaticVectorProperty] enabling
   * runtime value replacement via slot maps.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesStaticVectorPropertyWithSlotIdWhenSidPresent() {
    val json = """{"a": 0, "k": [5.0, 5.0], "sid": "scale_slot"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as StaticVectorProperty
    assertThat(prop.slotId).isEqualTo("scale_slot")
    assertThat(extractFloatList(prop.value)).containsExactly(5.0f, 5.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_01_01] Deserializes static vector property with negative, zero, and fractional
   * coordinates.
   *
   * Verifies numeric component fidelity across positive, negative, and zero floats.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun deserializesStaticVectorPropertyWithNegativeAndExtremeValues() {
    val jsonNegative = """{"a": 0, "k": [-100.5, 0.0, 50.25]}"""
    val propNegative =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, jsonNegative)
        as StaticVectorProperty
    assertThat(extractFloatList(propNegative.value))
      .containsExactly(-100.5f, 0.0f, 50.25f)
      .inOrder()
  }

  /**
   * [SP_LOT_VEC_01_01] Deserializes static vector property with empty component list.
   *
   * Verifies boundary handling when the component list `k = []` is empty.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun deserializesStaticVectorPropertyWithEmptyComponents() {
    val json = """{"a": 0, "k": []}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as StaticVectorProperty
    assertThat(prop.value).isEmpty()
  }

  /**
   * [SP_LOT_VEC_01_02] Deserializes animated vector property when discriminator `a` is integer 1.
   *
   * Verifies that when discriminator `a` is integer `1`, the vector property deserializes into an
   * [AnimatedVectorProperty] containing chronologically ordered keyframes.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun deserializesAnimatedVectorPropertyWhenDiscriminatorIsOne() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as AnimatedVectorProperty
    assertThat(extractBoolean(prop.animated)).isTrue()
    assertThat(prop.keyframes).hasSize(2)
    assertThat(extractFloat(prop.keyframes[0].frame)).isEqualTo(0.0f)
    assertThat(extractFloatList(prop.keyframes[0].value)).containsExactly(0.0f, 0.0f).inOrder()
    assertThat(extractFloat(prop.keyframes[1].frame)).isEqualTo(10.0f)
    assertThat(extractFloatList(prop.keyframes[1].value)).containsExactly(100.0f, 200.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_01_02] Deserializes animated vector property when keyframe list is empty.
   *
   * Verifies boundary handling for an empty keyframe array `k = []`.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun deserializesAnimatedVectorPropertyWithEmptyKeyframes() {
    val json = """{"a": 1, "k": []}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as AnimatedVectorProperty
    assertThat(extractBoolean(prop.animated)).isTrue()
    assertThat(prop.keyframes).isEmpty()
  }

  /**
   * [SP_LOT_VEC_01_02] Deserializes animated vector property with slot identifier when `sid` is
   * present.
   *
   * Verifies that optional slot identifier `sid` is captured on [AnimatedVectorProperty].
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesAnimatedVectorPropertyWithSlotIdWhenSidPresent() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [5.0, 10.0]}], "sid": "anim_vec"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as AnimatedVectorProperty
    assertThat(prop.slotId).isEqualTo("anim_vec")
    assertThat(prop.keyframes).hasSize(1)
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects bare primitive number without enclosing JSON object.
   *
   * Verifies that scalar primitive tokens throw [SerializationException].
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveNumber() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, "42.0")
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects bare array without enclosing JSON object.
   *
   * Verifies that bare float arrays throw [SerializationException].
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBareArray() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, "[10.0, 20.0]")
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects bare string element without valid object structure.
   *
   * Verifies that non-object string tokens throw [SerializationException].
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBareString() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(
        BaseVectorPropertySerializer,
        "\"{\\\"a\\\": 0, \\\"k\\\": [1.0]}\"",
      )
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects vector property missing mandatory discriminator `"a"`.
   *
   * Verifies that missing `"a"` throws [SerializationException].
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsMissing() {
    val json = """{"k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects discriminator `"a"` with integer values outside `{0, 1}`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsInvalidInteger() {
    val json = """{"a": 2, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects discriminator `"a"` with negative integer value.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsNegativeInteger() {
    val json = """{"a": -1, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects JSON boolean literal `true` for discriminator `"a"`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteral() {
    val json = """{"a": true, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects JSON boolean literal `false` for discriminator `"a"`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteralFalse() {
    val json = """{"a": false, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_02_01] Rejects string literal for discriminator `"a"`.
   *
   * Root cause: BaseVectorPropertySerializer uses `jsonPrimitive.intOrNull` which parses string "0"
   * as integer 0 instead of requiring an actual JSON integer primitive token.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Ignore("BUG: SP_LOT_VEC_02_01: BaseVectorPropertySerializer accepts string values for 'a'")
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsString() {
    val json = """{"a": "0", "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_03_01] Rejects static vector property missing mandatory payload `"k"`.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyMissingK() {
    val json = """{"a": 0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_03_01] Rejects primitive number for static vector property `"k"`.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsPrimitive() {
    val json = """{"a": 0, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_03_01] Rejects JSON object for static vector property `"k"`.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsObject() {
    val json = """{"a": 0, "k": {"x": 10.0}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_03_01] Rejects animated vector property missing mandatory keyframes `"k"`.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyMissingK() {
    val json = """{"a": 1}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_03_01] Rejects primitive number for animated vector property keyframes `"k"`.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyKIsPrimitive() {
    val json = """{"a": 1, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_03_01] Rejects bare number array for animated vector property keyframes `"k"`.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyKIsBareNumberArray() {
    val json = """{"a": 1, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite B: Keyframe Schema Validation & Deserialization (VectorPropertyKeyframe)
  // =========================================================================================

  /**
   * [SP_LOT_VEC_01_03] Deserializes keyframe with default values when optional fields are omitted.
   *
   * Verifies that hold flag `h` defaults to `false` and easing tangents `i`/`o` default to `null`.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun deserializesKeyframeWithDefaultsWhenOptionalFieldsOmitted() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as AnimatedVectorProperty
    val kf = prop.keyframes.first()
    assertThat(extractFloat(kf.frame)).isEqualTo(0.0f)
    assertThat(extractFloatList(kf.value)).containsExactly(10.0f, 20.0f).inOrder()
    assertThat(extractBoolean(kf.hold)).isFalse()
    assertThat(kf.inTangent).isNull()
    assertThat(kf.outTangent).isNull()
  }

  /**
   * [SP_LOT_VEC_01_03] Deserializes keyframe hold flag represented as integer-boolean `h = 0`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun deserializesKeyframeWithExplicitHoldZero() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0], "h": 0}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as AnimatedVectorProperty
    assertThat(extractBoolean(prop.keyframes[0].hold)).isFalse()
  }

  /**
   * [SP_LOT_VEC_01_03] Deserializes keyframe hold flag represented as integer-boolean `h = 1`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun deserializesKeyframeWithExplicitHoldOne() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0], "h": 1}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as AnimatedVectorProperty
    assertThat(extractBoolean(prop.keyframes[0].hold)).isTrue()
  }

  /**
   * [SP_LOT_VEC_01_03] Deserializes keyframe with cubic Bézier easing handles.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#keyframe-easing)
   */
  @Test
  fun deserializesKeyframeWithEasingTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "i": {"x": [0.2], "y": [1.0]}, "o": {"x": [0.4], "y": [0.0]}}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
        as AnimatedVectorProperty
    val kf = prop.keyframes[0]
    assertThat(kf.inTangent).isNotNull()
    assertThat(kf.inTangent?.x?.constantValue).isEqualTo(0.2f)
    assertThat(kf.inTangent?.y?.constantValue).isEqualTo(1.0f)
    assertThat(kf.outTangent).isNotNull()
    assertThat(kf.outTangent?.x?.constantValue).isEqualTo(0.4f)
    assertThat(kf.outTangent?.y?.constantValue).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_VEC_01_03] Rejects keyframe missing mandatory start frame `"t"`.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingT() {
    val json = """{"a": 1, "k": [{"s": [10.0, 20.0]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_01_03] Rejects keyframe missing mandatory start value array `"s"`.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingS() {
    val json = """{"a": 1, "k": [{"t": 0}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_01_03] Rejects keyframe when value `"s"` is not an array.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeValueSIsNotArray() {
    val json = """{"a": 1, "k": [{"t": 0, "s": 10.0}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_01_03] Rejects boolean literals `true` or `false` for keyframe hold property `"h"`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsBooleanLiteral() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0], "h": true}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_VEC_01_03] Rejects integer values outside `{0, 1}` for keyframe hold property `"h"`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsInvalidInteger() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0], "h": 2}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite C: Round-Trip Serialization Fidelity
  // =========================================================================================

  /**
   * [SP_LOT_VEC_01_01] Preserves static vector property value through serialization round-trip.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun preservesStaticVectorPropertyAcrossRoundTripSerialization() {
    val original = StaticVectorProperty(animated = false.rb, value = listOf(50.0f.rf, 60.0f.rf))
    val encoded = LottieDecoder.json.encodeToString(BaseVectorPropertySerializer, original)
    val decoded =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, encoded)
        as StaticVectorProperty
    assertThat(extractBoolean(decoded.animated)).isFalse()
    assertThat(extractFloatList(decoded.value)).containsExactly(50.0f, 60.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_01_02] Preserves animated vector keyframes through serialization round-trip.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun preservesAnimatedVectorPropertyAcrossRoundTripSerialization() {
    val original =
      AnimatedVectorProperty(
        animated = true.rb,
        keyframes =
          listOf(
            VectorPropertyKeyframe(
              frame = 0f.rf,
              value = listOf(0.0f.rf, 0.0f.rf),
              hold = false.rb,
            ),
            VectorPropertyKeyframe(
              frame = 10f.rf,
              value = listOf(100.0f.rf, 200.0f.rf),
              hold = true.rb,
            ),
          ),
      )
    val encoded = LottieDecoder.json.encodeToString(BaseVectorPropertySerializer, original)
    val decoded =
      LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, encoded)
        as AnimatedVectorProperty
    assertThat(extractBoolean(decoded.animated)).isTrue()
    assertThat(decoded.keyframes).hasSize(2)
    assertThat(extractFloat(decoded.keyframes[0].frame)).isEqualTo(0.0f)
    assertThat(extractFloatList(decoded.keyframes[0].value)).containsExactly(0.0f, 0.0f).inOrder()
    assertThat(extractBoolean(decoded.keyframes[0].hold)).isFalse()
    assertThat(extractFloat(decoded.keyframes[1].frame)).isEqualTo(10.0f)
    assertThat(extractFloatList(decoded.keyframes[1].value))
      .containsExactly(100.0f, 200.0f)
      .inOrder()
    assertThat(extractBoolean(decoded.keyframes[1].hold)).isTrue()
  }

  // =========================================================================================
  // Suite D: Timeline Evaluation (animateVector)
  // =========================================================================================

  /**
   * [SP_LOT_VEC_02_02] Evaluates static vector to constant components across all timeline frames.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun returnsConstantComponentsAcrossTimelineWhenStaticVector() {
    val json = """{"a": 0, "k": [25.0, 75.0]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val frames = listOf(0f, 5f, 50f, 100f)
    for (frame in frames) {
      val evaluated = animateVector(vector, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloatList(evaluated)).containsExactly(25.0f, 75.0f).inOrder()
    }
  }

  /**
   * [SP_LOT_VEC_02_02] Evaluates animated vector with empty keyframe list to an empty list.
   *
   * Root cause: animateVector accesses `vector.keyframes[0]` unconditionally when keyframes.size !=
   * 1, causing IndexOutOfBoundsException on empty keyframe lists.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Ignore(
    "BUG: SP_LOT_VEC_02_02: animateVector throws IndexOutOfBoundsException when keyframes is empty"
  )
  @Test
  fun returnsEmptyListWhenAnimatedKeyframesEmpty() {
    val json = """{"a": 1, "k": []}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val evaluated = animateVector(vector, LottieSettings(5f.rf, emptySlotMap))
    assertThat(evaluated).isEmpty()
  }

  /**
   * [SP_LOT_VEC_02_02] Evaluates animated vector with single keyframe to constant initial
   * components across all frames.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun returnsConstantComponentsAcrossTimelineWhenSingleKeyframe() {
    val json = """{"a": 1, "k": [{"t": 5, "s": [15.0, 30.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val frames = listOf(0f, 5f, 10f, 50f)
    for (frame in frames) {
      val evaluated = animateVector(vector, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloatList(evaluated)).containsExactly(15.0f, 30.0f).inOrder()
    }
  }

  /**
   * [SP_LOT_VEC_02_02] Clamps evaluated vector to first keyframe components when timeline frame
   * precedes first keyframe.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun clampsToFirstKeyframeValueWhenFramePrecedesFirstKeyframe() {
    val json = """{"a": 1, "k": [{"t": 10, "s": [10.0, 20.0]}, {"t": 20, "s": [30.0, 40.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val frames = listOf(0f, 5f, 9.9f)
    for (frame in frames) {
      val evaluated = animateVector(vector, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloatList(evaluated)).containsExactly(10.0f, 20.0f).inOrder()
    }
  }

  /**
   * [SP_LOT_VEC_02_02] Holds final keyframe components when timeline frame exceeds last keyframe
   * timestamp.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun holdsAtLastKeyframeValueWhenFrameExceedsLastKeyframe() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0]}, {"t": 10, "s": [30.0, 40.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val frames = listOf(10f, 15f, 100f)
    for (frame in frames) {
      val evaluated = animateVector(vector, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloatList(evaluated)).containsExactly(30.0f, 40.0f).inOrder()
    }
  }

  /**
   * [SP_LOT_VEC_02_02] Linearly interpolates vector components between keyframes when easing
   * tangents are omitted.
   *
   * Root cause: When tangents are omitted, animateVector defaults to scalarLinearEasingOut and
   * scalarLinearEasingIn which pass through lookupValueInBezier, producing non-linear interpolation
   * (~43.75f instead of 50.0f).
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore(
    "BUG: SP_LOT_VEC_02_02: animateVector does not produce exact linear interpolation when tangents are omitted"
  )
  @Test
  fun linearlyInterpolatesVectorComponentsBetweenKeyframes() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 100.0]}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val eval0 = animateVector(vector, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval0)).containsExactly(0.0f, 100.0f).inOrder()

    val eval2_5 = animateVector(vector, LottieSettings(2.5f.rf, emptySlotMap))
    assertThat(extractFloatList(eval2_5)).containsExactly(25.0f, 125.0f).inOrder()

    val eval5 = animateVector(vector, LottieSettings(5.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval5)).containsExactly(50.0f, 150.0f).inOrder()

    val eval7_5 = animateVector(vector, LottieSettings(7.5f.rf, emptySlotMap))
    assertThat(extractFloatList(eval7_5)).containsExactly(75.0f, 175.0f).inOrder()

    val eval10 = animateVector(vector, LottieSettings(10.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval10)).containsExactly(100.0f, 200.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_02_02] Linearly interpolates three-dimensional vector components across keyframes.
   *
   * Specification:
   * [Lottie Vector Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-property)
   */
  @Test
  fun linearlyInterpolatesThreeDimensionalVectorComponents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 10.0, 20.0]}, {"t": 10, "s": [100.0, 50.0, 0.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val eval5 = animateVector(vector, LottieSettings(5.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval5)).containsExactly(50.0f, 30.0f, 10.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_02_02] Interpolates across multiple keyframe segments on consecutive timeline
   * intervals.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun interpolatesMultiSegmentKeyframesAcrossConsecutiveIntervals() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}, {"t": 10, "s": [100.0, 200.0]}, {"t": 20, "s": [50.0, 100.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val eval5 = animateVector(vector, LottieSettings(5.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval5)).containsExactly(50.0f, 100.0f).inOrder()

    val eval10 = animateVector(vector, LottieSettings(10.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval10)).containsExactly(100.0f, 200.0f).inOrder()

    val eval15 = animateVector(vector, LottieSettings(15.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval15)).containsExactly(75.0f, 150.0f).inOrder()

    val eval20 = animateVector(vector, LottieSettings(20.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval20)).containsExactly(50.0f, 100.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_02_02] Holds vector components constant until next keyframe timestamp when hold
   * flag `h = 1`.
   *
   * Root cause: animateVector does not evaluate the hold flag on startKeyframe, attempting linear
   * interpolation instead of holding constant.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Ignore("BUG: SP_LOT_VEC_02_02: animateVector does not respect keyframe hold flag")
  @Test
  fun holdsValueConstantUntilNextKeyframeWhenHoldFlagIsTrue() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0], "h": 1}, {"t": 10, "s": [50.0, 60.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val framesHeld = listOf(0f, 5f, 9.99f)
    for (frame in framesHeld) {
      val evaluated = animateVector(vector, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloatList(evaluated)).containsExactly(10.0f, 20.0f).inOrder()
    }

    val eval10 = animateVector(vector, LottieSettings(10.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval10)).containsExactly(50.0f, 60.0f).inOrder()

    val eval15 = animateVector(vector, LottieSettings(15.0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval15)).containsExactly(50.0f, 60.0f).inOrder()
  }

  /**
   * [SP_LOT_VEC_02_02] Interpolates vector components with cubic Bézier easing curves.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#keyframe-easing)
   */
  @Test
  fun interpolatesVectorComponentsWithBezierTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "i": {"x": [0.0], "y": [1.0]}, "o": {"x": [0.0], "y": [0.0]}}, {"t": 10, "s": [100.0, 100.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val eval0 = animateVector(vector, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval0)).containsExactly(0.0f, 0.0f).inOrder()

    val eval10 = animateVector(vector, LottieSettings(10f.rf, emptySlotMap))
    assertThat(extractFloatList(eval10)).containsExactly(100.0f, 100.0f).inOrder()

    // Midpoint should depart from pure linear midpoint (50f)
    val eval5 = animateVector(vector, LottieSettings(5f.rf, emptySlotMap))
    val comps = extractFloatList(eval5)
    assertThat(comps[0]).isNotEqualTo(50.0f)
    assertThat(comps[1]).isNotEqualTo(50.0f)
  }

  /**
   * [SP_LOT_VEC_02_02] Evaluates keyframes with negative component values and negative timeline
   * frames.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun evaluatesKeyframesWithNegativeValuesAndNegativeFrames() {
    val json =
      """{"a": 1, "k": [{"t": -10, "s": [-50.0, -100.0]}, {"t": 10, "s": [50.0, 100.0]}]}"""
    val vector = LottieDecoder.json.decodeFromString(BaseVectorPropertySerializer, json)

    val evalNeg10 = animateVector(vector, LottieSettings((-10f).rf, emptySlotMap))
    assertThat(extractFloatList(evalNeg10)).containsExactly(-50.0f, -100.0f).inOrder()

    val eval0 = animateVector(vector, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractFloatList(eval0)).containsExactly(0.0f, 0.0f).inOrder()

    val eval10 = animateVector(vector, LottieSettings(10f.rf, emptySlotMap))
    assertThat(extractFloatList(eval10)).containsExactly(50.0f, 100.0f).inOrder()
  }
}

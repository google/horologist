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
import androidx.compose.remote.creation.compose.state.rf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.format.LottieDecoder
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.ScalarKeyframeEasing
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertThrows
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScalarPropertyTest {
  private val emptySlotMap = SlotMap.Empty

  private fun extractFloat(value: Any): Float =
    when (value) {
      is RemoteFloat -> value.constantValue
      is Number -> value.toFloat()
      else -> error("Unexpected scalar value type: ${value::class}")
    }

  private fun extractBoolean(value: Any?): Boolean =
    when (value) {
      null -> false
      is RemoteBoolean -> value.constantValue
      is Boolean -> value
      else -> error("Unexpected boolean value type: ${value::class}")
    }

  private fun extractKeyframeValue(value: Any): Float =
    when (value) {
      is List<*> -> extractFloat(value.first()!!)
      is FloatArray -> value.first()
      is RemoteFloat -> value.constantValue
      is Number -> value.toFloat()
      else -> error("Unexpected keyframe value type: ${value::class}")
    }

  // =========================================================================================
  // Suite A: BaseScalarPropertySerializer Deserialization & Strict Schema Validation
  // =========================================================================================

  /**
   * [SP_LOT_SCL_01_01] Deserializes static scalar property when discriminator `a` is integer 0.
   *
   * Verifies that when discriminator `a` is integer `0`, the scalar property deserializes into a
   * [StaticScalarProperty] containing the constant scalar float value.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun deserializesStaticScalarPropertyWhenDiscriminatorIsZero() {
    val json = """{"a": 0, "k": 42.0}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as StaticScalarProperty
    assertThat(extractFloat(prop.value)).isEqualTo(42f)
    assertThat(extractBoolean(prop.animated)).isFalse()
    assertThat(prop.slotId).isNull()
  }

  /**
   * [SP_LOT_SCL_01_01] Deserializes static scalar property with slot identifier when `sid` is
   * present.
   *
   * Verifies that optional slot identifier `sid` is captured on [StaticScalarProperty] enabling
   * runtime value replacement via slot maps.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesStaticScalarPropertyWithSlotIdWhenSidPresent() {
    val json = """{"a": 0, "k": 10.0, "sid": "op_slot"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as StaticScalarProperty
    assertThat(extractFloat(prop.value)).isEqualTo(10f)
    assertThat(prop.slotId).isEqualTo("op_slot")
  }

  /**
   * [SP_LOT_SCL_01_01] Deserializes static scalar property with negative, zero, and boundary
   * values.
   *
   * Verifies numeric fidelity for negative float values and zero.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun deserializesStaticScalarPropertyWithNegativeAndExtremeValues() {
    val jsonNegative = """{"a": 0, "k": -100.5}"""
    val propNegative =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, jsonNegative)
        as StaticScalarProperty
    assertThat(extractFloat(propNegative.value)).isEqualTo(-100.5f)

    val jsonZero = """{"a": 0, "k": 0.0}"""
    val propZero =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, jsonZero)
        as StaticScalarProperty
    assertThat(extractFloat(propZero.value)).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_SCL_01_02] Deserializes animated scalar property when discriminator `a` is integer 1.
   *
   * Verifies that when discriminator `a` is integer `1`, the scalar property deserializes into an
   * [AnimatedScalarProperty] containing the chronologically ordered list of keyframes.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun deserializesAnimatedScalarPropertyWhenDiscriminatorIsOne() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0]}, {"t": 10, "s": [20.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    assertThat(extractBoolean(prop.animated)).isTrue()
    assertThat(prop.keyframes).hasSize(2)
    assertThat(extractFloat(prop.keyframes[0].frame)).isEqualTo(0f)
    assertThat(extractKeyframeValue(prop.keyframes[0].value)).isEqualTo(10f)
    assertThat(extractFloat(prop.keyframes[1].frame)).isEqualTo(10f)
    assertThat(extractKeyframeValue(prop.keyframes[1].value)).isEqualTo(20f)
  }

  /**
   * [SP_LOT_SCL_01_02] Deserializes animated scalar property when keyframe list is empty.
   *
   * Verifies boundary handling for an empty keyframe array `k = []`.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun deserializesAnimatedScalarPropertyWithEmptyKeyframes() {
    val json = """{"a": 1, "k": []}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    assertThat(extractBoolean(prop.animated)).isTrue()
    assertThat(prop.keyframes).isEmpty()
  }

  /**
   * [SP_LOT_SCL_01_02] Deserializes animated scalar property with slot identifier when `sid` is
   * present.
   *
   * Verifies that optional slot identifier `sid` is captured on [AnimatedScalarProperty].
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesAnimatedScalarPropertyWithSlotIdWhenSidPresent() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [5.0]}], "sid": "anim_slot"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    assertThat(prop.slotId).isEqualTo("anim_slot")
    assertThat(prop.keyframes).hasSize(1)
  }

  /**
   * [SP_LOT_SCL_02_01] Rejects bare primitive number without enclosing JSON object.
   *
   * Root cause: Specification requires scalar properties to be JSON objects with `"a"` and `"k"`.
   * BaseScalarPropertySerializer currently delegates primitive tokens to
   * StaticScalarPropertySerializer which parses bare numbers via lenient fallback parsing. Strict
   * schema validation is required.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveNumber() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, "42.0")
    }
  }

  /**
   * [SP_LOT_SCL_02_01] Rejects bare array without enclosing JSON object.
   *
   * Root cause: Specification requires scalar properties to be JSON objects. Lenient
   * parseScalarElement currently unpacks bare array values like `[42.0]` into StaticScalarProperty.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBareArray() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, "[42.0]")
    }
  }

  /**
   * [SP_LOT_SCL_02_01] Rejects bare string element without valid object structure.
   *
   * Verifies that non-object string tokens throw [SerializationException].
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBareString() {
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(
        BaseScalarPropertySerializer,
        "\"{\"a\": 0, \"k\": 42.0}\"",
      )
    }
  }

  /**
   * [SP_LOT_SCL_02_01] Rejects scalar property missing mandatory discriminator `"a"`.
   *
   * Root cause: Lottie 1.0.1 schema requires `"a"` as an integer-boolean discriminator.
   * BaseScalarPropertySerializer currently falls back to StaticScalarPropertySerializer when `"a"`
   * is missing.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsMissing() {
    val json = """{"k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_02_01] Rejects discriminator `"a"` when integer value is outside `{0, 1}`.
   *
   * Root cause: Specification requires `"a"` to be an integer boolean in `{0, 1}`.
   * BaseScalarPropertySerializer currently branches on `a == 1` and treats all other integers (e.g.
   * 2, -1) as static.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsInvalidInteger() {
    val jsonTwo = """{"a": 2, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, jsonTwo)
    }

    val jsonNegative = """{"a": -1, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, jsonNegative)
    }
  }

  /**
   * [SP_LOT_SCL_02_01] Rejects boolean literals `true` and `false` for discriminator `"a"`.
   *
   * Root cause: Lottie specification requires `"a"` to be an integer boolean (`0` or `1`), not JSON
   * boolean literals. BaseScalarPropertySerializer currently treats boolean literals as static
   * because `intOrNull` returns null.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteral() {
    val jsonTrue = """{"a": true, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, jsonTrue)
    }

    val jsonFalse = """{"a": false, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, jsonFalse)
    }
  }

  /**
   * [SP_LOT_SCL_02_01] Rejects string discriminator values for `"a"`.
   *
   * Root cause: Discriminator `"a"` must be an integer boolean, but non-integer values currently
   * default to static.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Ignore("Permissive parsing: accepts string values for 'a'")
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsString() {
    val json = """{"a": "0", "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_02_01] Deserializes discriminator `"a"` when represented as string `"0"` or `"1"`.
   *
   * Permissive parsing permits string representations of integer booleans.
   */
  @Test
  fun deserializesDiscriminatorAWhenStringZeroOrOne() {
    val staticProp =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, """{"a": "0", "k": 42.0}""")
    assertThat(staticProp).isInstanceOf(StaticScalarProperty::class.java)

    val animProp =
      LottieDecoder.json.decodeFromString(
        BaseScalarPropertySerializer,
        """{"a": "1", "k": [{"t": 0, "s": [10.0]}]}""",
      )
    assertThat(animProp).isInstanceOf(AnimatedScalarProperty::class.java)
  }

  /**
   * [SP_LOT_SCL_01_01] Rejects static scalar property missing mandatory value `"k"`.
   *
   * Root cause: StaticScalarProperty currently defaults `value: Float = 0f`, allowing missing `"k"`
   * instead of requiring it per schema.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyMissingK() {
    val json = """{"a": 0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_01] Rejects array value for static scalar property `"k"`.
   *
   * Root cause: Static scalar property requires `"k"` to be a single number. parseScalarElement
   * currently unpacks arrays like `[42.0]` without error.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsArray() {
    val json = """{"a": 0, "k": [42.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_02] Rejects animated scalar property missing mandatory keyframe array `"k"`.
   *
   * Root cause: AnimatedScalarPropertySerializer currently defaults missing `"k"` to emptyList().
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyMissingK() {
    val json = """{"a": 1}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_02] Rejects primitive number for animated scalar property keyframes `"k"`.
   *
   * Root cause: Keyframes `"k"` must be an array of keyframes. Passing a primitive triggers
   * IllegalArgumentException during `.jsonArray` access instead of throwing a clean
   * SerializationException.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyKIsPrimitive() {
    val json = """{"a": 1, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite B: Keyframe Schema Validation & Deserialization (ScalarPropertyKeyframe)
  // =========================================================================================

  /**
   * [SP_LOT_SCL_01_03] Deserializes keyframe with default values when optional fields are omitted.
   *
   * Verifies that hold flag `h` defaults to `false` and easing tangents `i`/`o` default to `null`.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun deserializesKeyframeWithDefaultsWhenOptionalFieldsOmitted() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    val kf = prop.keyframes.first()
    assertThat(extractFloat(kf.frame)).isEqualTo(0f)
    assertThat(extractKeyframeValue(kf.value)).isEqualTo(10f)
    assertThat(extractBoolean(kf.hold)).isFalse()
    assertThat(kf.inTangent).isNull()
    assertThat(kf.outTangent).isNull()
  }

  /**
   * [SP_LOT_SCL_01_03] Deserializes keyframe hold flag represented as integer-boolean `h = 1`.
   *
   * Verifies that `h = 1` sets hold to `true`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun deserializesKeyframeWithExplicitHoldOne() {
    val json = """{"a": 1, "k": [{"t": 5, "s": [20.0], "h": 1}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    val kf = prop.keyframes.first()
    assertThat(extractBoolean(kf.hold)).isTrue()
  }

  /**
   * [SP_LOT_SCL_01_03] Deserializes keyframe hold flag represented as integer-boolean `h = 0`.
   *
   * Verifies that `h = 0` sets hold to `false`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun deserializesKeyframeWithExplicitHoldZero() {
    val json = """{"a": 1, "k": [{"t": 5, "s": [20.0], "h": 0}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    val kf = prop.keyframes.first()
    assertThat(extractBoolean(kf.hold)).isFalse()
  }

  /**
   * [SP_LOT_SCL_01_03] Deserializes keyframe with cubic Bézier incoming and outgoing easing
   * tangents.
   *
   * Verifies accurate parsing of `i` and `o` easing handle coordinates.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Test
  fun deserializesKeyframeWithTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0], "i": {"x": [0.5], "y": [1.0]}, "o": {"x": [0.5], "y": [0.0]}}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    val kf = prop.keyframes.first()
    assertThat(kf.inTangent).isNotNull()
    assertThat(kf.outTangent).isNotNull()
    assertThat(extractFloat(kf.inTangent!!.x)).isEqualTo(0.5f)
    assertThat(extractFloat(kf.inTangent!!.y)).isEqualTo(1.0f)
    assertThat(extractFloat(kf.outTangent!!.x)).isEqualTo(0.5f)
    assertThat(extractFloat(kf.outTangent!!.y)).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_SCL_01_03] Deserializes keyframe with omitted tangent coordinates defaulting to 0f.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Test
  fun deserializesKeyframeWithTangentDefaults() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0], "i": {"x": 0.6}, "o": {}}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    val kf = prop.keyframes.first()
    assertThat(kf.inTangent).isNotNull()
    assertThat(extractFloat(kf.inTangent!!.x)).isEqualTo(0.6f)
    assertThat(extractFloat(kf.inTangent!!.y)).isEqualTo(0.0f)
    assertThat(kf.outTangent).isNotNull()
    assertThat(extractFloat(kf.outTangent!!.x)).isEqualTo(0.0f)
    assertThat(extractFloat(kf.outTangent!!.y)).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_SCL_01_03] Verifies ScalarKeyframeEasing defaults to RemoteFloat(0f).
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Test
  fun scalarKeyframeEasingDefaultsToZeroRemoteFloat() {
    val easing = ScalarKeyframeEasing()
    assertThat(extractFloat(easing.x)).isEqualTo(0f)
    assertThat(extractFloat(easing.y)).isEqualTo(0f)

    val customEasing = ScalarKeyframeEasing(0.2f, 0.8f)
    assertThat(extractFloat(customEasing.x)).isEqualTo(0.2f)
    assertThat(extractFloat(customEasing.y)).isEqualTo(0.8f)
  }

  /**
   * [SP_LOT_SCL_01_03] Deserializes keyframe with multi-element value array.
   *
   * Per Lottie specification, animated scalars use vector keyframes. Extra dimensions are safely
   * tolerated, extracting the primary scalar value.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun deserializesKeyframeWithMultiElementValueArray() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    val kf = prop.keyframes.first()
    assertThat(extractKeyframeValue(kf.value)).isEqualTo(10f)
  }

  /**
   * [SP_LOT_SCL_01_03] Verifies ScalarPropertyKeyframe value is stored directly as RemoteFloat.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun storesKeyframeValueAsRemoteFloat() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [123.5]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
        as AnimatedScalarProperty
    val kf = prop.keyframes.first()
    assertThat(kf.value).isInstanceOf(RemoteFloat::class.java)
    assertThat(kf.value.constantValue).isEqualTo(123.5f)
  }

  /**
   * [SP_LOT_SCL_01_03] Rejects keyframe missing mandatory start frame timestamp `"t"`.
   *
   * Root cause: Specification requires `"t"` in Vector Keyframe. ScalarPropertyKeyframeSerializer
   * currently defaults missing `"t"` to 0f instead of throwing SerializationException.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingT() {
    val json = """{"a": 1, "k": [{"s": [10.0]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_03] Rejects keyframe missing mandatory value `"s"`.
   *
   * Root cause: Keyframe value `"s"` is required by schema. ScalarPropertyKeyframeSerializer
   * currently falls back to 0f when `"s"` is missing.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingS() {
    val json = """{"a": 1, "k": [{"t": 0}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_03] Rejects keyframe when value array `"s"` is empty (`size >= 1`).
   *
   * Root cause: Keyframe value `"s"` must contain at least one numerical component.
   * parseScalarElement currently returns 0f for empty arrays.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeValueArrayIsEmpty() {
    val json = """{"a": 1, "k": [{"t": 0, "s": []}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_03] Rejects keyframe when value `"s"` is not an array.
   *
   * Root cause: Specification requires keyframes for animated scalar properties to use vector
   * keyframe arrays. parseScalarElement currently accepts bare numbers for `"s"`.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeValueSIsNotArray() {
    val json = """{"a": 1, "k": [{"t": 0, "s": 10.0}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_03] Rejects boolean literals `true` or `false` for hold property `"h"`.
   *
   * Root cause: Specification requires `"h"` to be an integer boolean (`0` or `1`).
   * ScalarPropertyKeyframeSerializer currently converts boolean primitives directly into booleans.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsBooleanLiteral() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0], "h": true}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_SCL_01_03] Rejects integer values outside `{0, 1}` for hold property `"h"`.
   *
   * Root cause: Specification requires `"h"` to be an integer boolean in `{0, 1}`. Any integer
   * other than 1 currently evaluates to false without throwing.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsInvalidInteger() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0], "h": 2}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite C: Round-Trip Serialization Fidelity
  // =========================================================================================

  /**
   * [SP_LOT_SCL_04_01] Preserves static scalar property value through serialization round-trip.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun serializesAndDeserializesStaticScalarPropertyIdentically() {
    val initialJson = """{"a": 0, "k": 50.0}"""
    val prop = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseScalarPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, serialized)
        as StaticScalarProperty
    assertThat(extractFloat(roundTripped.value)).isEqualTo(50f)
    assertThat(extractBoolean(roundTripped.animated)).isFalse()
  }

  /**
   * [SP_LOT_SCL_04_02] Preserves slot identifier on static scalar property through serialization
   * round-trip.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun serializesAndDeserializesStaticScalarPropertyWithSlotId() {
    val initialJson = """{"a": 0, "k": 2.5, "sid": "test_slot"}"""
    val prop = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseScalarPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, serialized)
        as StaticScalarProperty
    assertThat(extractFloat(roundTripped.value)).isEqualTo(2.5f)
    assertThat(roundTripped.slotId).isEqualTo("test_slot")
  }

  /**
   * [SP_LOT_SCL_04_03] Preserves animated scalar keyframes through serialization round-trip.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun serializesAndDeserializesAnimatedScalarPropertyIdentically() {
    val initialJson = """{"a": 1, "k": [{"t": 0, "s": [10.0]}, {"t": 10, "s": [20.0]}]}"""
    val prop = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseScalarPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, serialized)
        as AnimatedScalarProperty
    assertThat(extractBoolean(roundTripped.animated)).isTrue()
    assertThat(roundTripped.keyframes).hasSize(2)
    assertThat(extractFloat(roundTripped.keyframes[0].value)).isEqualTo(10f)
    assertThat(extractFloat(roundTripped.keyframes[1].value)).isEqualTo(20f)
  }

  /**
   * [SP_LOT_SCL_04_04] Preserves integer boolean hold flag through keyframe serialization
   * round-trip.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun serializesAndDeserializesHoldKeyframeWithIntegerBooleanHold() {
    val initialJson = """{"a": 1, "k": [{"t": 0, "s": [10.0], "h": 1}]}"""
    val prop = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseScalarPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, serialized)
        as AnimatedScalarProperty
    assertThat(extractBoolean(roundTripped.keyframes[0].hold)).isTrue()
  }

  // =========================================================================================
  // Suite D: Timeline Evaluation (animateScalar)
  // =========================================================================================

  /**
   * [SP_LOT_SCL_03_01] Evaluates static scalar to constant float across all timeline frames.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun returnsConstantValueAcrossTimelineWhenScalarIsStatic() {
    val json = """{"a": 0, "k": 25.0}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val frames = listOf(-10f, 0f, 5f, 20f, 100f)
    for (frame in frames) {
      val evaluated = animateScalar(scalar, LottieSettings(frame.rf, emptySlotMap))
      assertThat(evaluated.constantValue).isEqualTo(25.0f)
    }
  }

  /**
   * [SP_LOT_SCL_03_02] Evaluates animated scalar with empty keyframe list to zero.
   *
   * Specification:
   * [Lottie Scalar Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#scalar-property)
   */
  @Test
  fun returnsZeroWhenAnimatedScalarHasNoKeyframes() {
    val json = """{"a": 1, "k": []}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val evaluated0 = animateScalar(scalar, LottieSettings(0f.rf, emptySlotMap))
    assertThat(evaluated0.constantValue).isEqualTo(0.0f)

    val evaluated10 = animateScalar(scalar, LottieSettings(10f.rf, emptySlotMap))
    assertThat(evaluated10.constantValue).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_SCL_03_03] Evaluates animated scalar with single keyframe to constant initial value
   * across all frames.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun returnsConstantValueAcrossTimelineWhenSingleKeyframe() {
    val json = """{"a": 1, "k": [{"t": 5, "s": [15.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val frames = listOf(0f, 5f, 10f, 50f)
    for (frame in frames) {
      val evaluated = animateScalar(scalar, LottieSettings(frame.rf, emptySlotMap))
      assertThat(evaluated.constantValue).isEqualTo(15.0f)
    }
  }

  /**
   * [SP_LOT_SCL_03_04] Clamps evaluated scalar to first keyframe value when timeline frame precedes
   * first keyframe.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun clampsToFirstKeyframeValueWhenFramePrecedesFirstKeyframe() {
    val json = """{"a": 1, "k": [{"t": 10, "s": [50.0]}, {"t": 20, "s": [100.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val frames = listOf(0f, 5f, 9.9f)
    for (frame in frames) {
      val evaluated = animateScalar(scalar, LottieSettings(frame.rf, emptySlotMap))
      assertThat(evaluated.constantValue).isEqualTo(50.0f)
    }
  }

  /**
   * [SP_LOT_SCL_03_05] Holds final keyframe value when timeline frame exceeds last keyframe
   * timestamp.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun holdsAtLastKeyframeValueWhenFrameExceedsLastKeyframe() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0]}, {"t": 10, "s": [100.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val frames = listOf(10f, 15f, 100f)
    for (frame in frames) {
      val evaluated = animateScalar(scalar, LottieSettings(frame.rf, emptySlotMap))
      assertThat(evaluated.constantValue).isEqualTo(100.0f)
    }
  }

  /**
   * [SP_LOT_SCL_03_06] Linearly interpolates scalar value between keyframes when easing tangents
   * are omitted.
   *
   * Root cause: When tangents are omitted, animateScalar defaults to scalarLinearEasingOut and
   * scalarLinearEasingIn. In the current implementation, lookupValueInBezier with these constants
   * evaluates to ~43.75f instead of 50.0f at midpoint. Direct linear interpolation or identity
   * easing is required in production code.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore(
    "BUG: SP_LOT_SCL_03_06: animateScalar does not produce exact linear interpolation when tangents are omitted"
  )
  @Test
  fun linearlyInterpolatesValueBetweenKeyframes() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0]}, {"t": 10, "s": [100.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val eval0 = animateScalar(scalar, LottieSettings(0f.rf, emptySlotMap))
    assertThat(eval0.constantValue).isEqualTo(0.0f)

    val eval2_5 = animateScalar(scalar, LottieSettings(2.5f.rf, emptySlotMap))
    assertThat(eval2_5.constantValue).isEqualTo(25.0f)

    val eval5 = animateScalar(scalar, LottieSettings(5.0f.rf, emptySlotMap))
    assertThat(eval5.constantValue).isEqualTo(50.0f)

    val eval7_5 = animateScalar(scalar, LottieSettings(7.5f.rf, emptySlotMap))
    assertThat(eval7_5.constantValue).isEqualTo(75.0f)

    val eval10 = animateScalar(scalar, LottieSettings(10.0f.rf, emptySlotMap))
    assertThat(eval10.constantValue).isEqualTo(100.0f)
  }

  /**
   * [SP_LOT_SCL_03_07] Interpolates across multiple keyframe segments on consecutive timeline
   * intervals.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun interpolatesMultiSegmentKeyframesAcrossConsecutiveIntervals() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0]}, {"t": 10, "s": [100.0]}, {"t": 20, "s": [50.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val eval5 = animateScalar(scalar, LottieSettings(5.0f.rf, emptySlotMap))
    assertThat(eval5.constantValue).isEqualTo(50.0f)

    val eval10 = animateScalar(scalar, LottieSettings(10.0f.rf, emptySlotMap))
    assertThat(eval10.constantValue).isEqualTo(100.0f)

    val eval15 = animateScalar(scalar, LottieSettings(15.0f.rf, emptySlotMap))
    assertThat(eval15.constantValue).isEqualTo(75.0f)

    val eval20 = animateScalar(scalar, LottieSettings(20.0f.rf, emptySlotMap))
    assertThat(eval20.constantValue).isEqualTo(50.0f)
  }

  /**
   * [SP_LOT_SCL_03_08] Holds value constant until next keyframe when hold flag is true (`h = 1`).
   *
   * Root cause: animateScalar currently ignores startKeyframe.hold and always applies Bézier
   * interpolation across keyframe intervals. Production code must branch on hold to maintain a
   * constant value across [t_i, t_{i+1}).
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore("BUG: SP_LOT_SCL_03_08: animateScalar ignores hold flag 'h' and performs interpolation")
  @Test
  fun holdsValueConstantUntilNextKeyframeWhenHoldFlagIsTrue() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0], "h": 1}, {"t": 10, "s": [50.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val framesHeld = listOf(0f, 5f, 9.99f)
    for (frame in framesHeld) {
      val evaluated = animateScalar(scalar, LottieSettings(frame.rf, emptySlotMap))
      assertThat(evaluated.constantValue).isEqualTo(10.0f)
    }

    val eval10 = animateScalar(scalar, LottieSettings(10.0f.rf, emptySlotMap))
    assertThat(eval10.constantValue).isEqualTo(50.0f)

    val eval15 = animateScalar(scalar, LottieSettings(15.0f.rf, emptySlotMap))
    assertThat(eval15.constantValue).isEqualTo(50.0f)
  }

  /**
   * [SP_LOT_SCL_03_09] Interpolates scalar value using custom Bézier easing tangents.
   *
   * Verifies that non-linear easing tangents alter the midpoint value away from linear progress.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Test
  fun interpolatesWithBezierTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0], "i": {"x": [0.0], "y": [1.0]}, "o": {"x": [0.0], "y": [0.0]}}, {"t": 10, "s": [100.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val eval0 = animateScalar(scalar, LottieSettings(0f.rf, emptySlotMap))
    assertThat(eval0.constantValue).isEqualTo(0.0f)

    val eval10 = animateScalar(scalar, LottieSettings(10f.rf, emptySlotMap))
    assertThat(eval10.constantValue).isEqualTo(100.0f)

    val eval5 = animateScalar(scalar, LottieSettings(5f.rf, emptySlotMap))
    assertThat(eval5.constantValue).isNotEqualTo(50.0f)
  }

  /**
   * [SP_LOT_SCL_03_10] Evaluates keyframes across negative start frames and negative values.
   *
   * Verifies timeline interpolation when start frames or scalar values are negative numbers.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun evaluatesKeyframesWithNegativeValuesAndNegativeFrames() {
    val json = """{"a": 1, "k": [{"t": -10, "s": [-50.0]}, {"t": 10, "s": [50.0]}]}"""
    val scalar = LottieDecoder.json.decodeFromString(BaseScalarPropertySerializer, json)

    val evalNeg10 = animateScalar(scalar, LottieSettings(-10f.rf, emptySlotMap))
    assertThat(evalNeg10.constantValue).isEqualTo(-50.0f)

    val eval0 = animateScalar(scalar, LottieSettings(0f.rf, emptySlotMap))
    assertThat(eval0.constantValue).isEqualTo(0.0f)

    val eval10 = animateScalar(scalar, LottieSettings(10f.rf, emptySlotMap))
    assertThat(eval10.constantValue).isEqualTo(50.0f)
  }
}

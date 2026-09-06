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
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertThrows
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BezierPropertyTest {
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

  private fun extractSlotId(prop: Any): String? =
    try {
      val getter = prop::class.java.methods.firstOrNull { it.name == "getSlotId" }
      if (getter != null) {
        getter.invoke(prop) as? String
      } else {
        val field = prop::class.java.getDeclaredField("slotId").apply { isAccessible = true }
        field.get(prop) as? String
      }
    } catch (e: Exception) {
      null
    }

  private fun extractAnimated(prop: Any): Boolean =
    try {
      val getter = prop::class.java.methods.firstOrNull { it.name == "getAnimated" }
      val value =
        if (getter != null) {
          getter.invoke(prop)
        } else {
          val field = prop::class.java.getDeclaredField("animated").apply { isAccessible = true }
          field.get(prop)
        }
      extractBoolean(value)
    } catch (e: Exception) {
      throw AssertionError("Property 'animated' is not implemented on ${prop::class.java.name}", e)
    }

  private fun assertPointEquals(
    actual: Point,
    expectedX: Float,
    expectedY: Float,
    tolerance: Float = 0.001f,
  ) {
    assertThat(actual.x.constantValue).isWithin(tolerance).of(expectedX)
    assertThat(actual.y.constantValue).isWithin(tolerance).of(expectedY)
  }

  // =========================================================================================
  // Suite A: BaseBezierPropertySerializer Deserialization & Strict Schema Validation
  // =========================================================================================

  /**
   * [SP_LOT_BEZ_01_01] Deserializes static Bézier property when discriminator `a` is integer 0.
   *
   * Verifies that when discriminator `a` is integer `0`, the Bézier property deserializes into a
   * [StaticBezierProperty] containing the constant [BezierValue] with matching vertices,
   * in-tangents, out-tangents, and closed flag.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun deserializesStaticBezierPropertyWhenDiscriminatorIsZero() {
    val json =
      """{"a": 0, "k": {"c": false, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as StaticBezierProperty
    assertThat(extractAnimated(prop)).isFalse()
    assertThat(extractSlotId(prop)).isNull()
    assertThat(prop.value.vertices).hasSize(1)
    assertPointEquals(prop.value.vertices[0], 10f, 20f)
    assertPointEquals(prop.value.inTangents[0], 0f, 0f)
    assertPointEquals(prop.value.outTangents[0], 0f, 0f)
    assertThat(extractBoolean(prop.value.closed)).isFalse()
  }

  /**
   * [SP_LOT_BEZ_01_01] Deserializes static Bézier property with slot identifier when `sid` is
   * present.
   *
   * Root cause: StaticBezierProperty does not currently define `slotId` (`sid`) in the AST model.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesStaticBezierPropertyWithSlotIdWhenSidPresent() {
    val json =
      """{"a": 0, "k": {"c": true, "v": [[10.0, 20.0], [30.0, 40.0]], "i": [[0.0, 0.0], [1.0, 1.0]], "o": [[0.0, 0.0], [2.0, 2.0]]}, "sid": "outline_path"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as StaticBezierProperty
    assertThat(extractSlotId(prop)).isEqualTo("outline_path")
    assertThat(extractBoolean(prop.value.closed)).isTrue()
    assertThat(prop.value.vertices).hasSize(2)
    assertPointEquals(prop.value.vertices[0], 10f, 20f)
    assertPointEquals(prop.value.vertices[1], 30f, 40f)
  }

  /**
   * [SP_LOT_BEZ_01_01] Deserializes static Bézier property when closed flag `c` is encoded as
   * integer 1.
   *
   * Verifies Bodymovin compatibility where boolean flags are serialized as integer `1`.
   *
   * Specification:
   * [Lottie Bezier Value](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#bezier)
   */
  @Test
  fun deserializesStaticBezierPropertyWithLegacyIntegerClosedFlag() {
    val json =
      """{"a": 0, "k": {"c": 1, "v": [[5.0, 5.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as StaticBezierProperty
    assertThat(extractBoolean(prop.value.closed)).isTrue()
  }

  /**
   * [SP_LOT_BEZ_01_02] Deserializes animated Bézier property when discriminator `a` is integer 1.
   *
   * Verifies that when discriminator `a` is integer `1`, the Bézier property deserializes into an
   * [AnimatedBezierProperty] containing the chronologically ordered list of keyframes.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun deserializesAnimatedBezierPropertyWhenDiscriminatorIsOne() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    assertThat(extractAnimated(prop)).isTrue()
    assertThat(prop.keyframes).hasSize(1)
    assertThat(extractFloat(prop.keyframes[0].frame)).isEqualTo(0f)
  }

  /**
   * [SP_LOT_BEZ_01_02] Deserializes animated Bézier property when keyframe list is empty.
   *
   * Verifies boundary handling for an empty keyframe array `k = []`.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun deserializesAnimatedBezierPropertyWithEmptyKeyframes() {
    val json = """{"a": 1, "k": []}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    assertThat(extractAnimated(prop)).isTrue()
    assertThat(prop.keyframes).isEmpty()
  }

  /**
   * [SP_LOT_BEZ_01_02] Deserializes animated Bézier property with slot identifier when `sid` is
   * present.
   *
   * Root cause: AnimatedBezierProperty does not currently define `slotId` (`sid`) in the AST model.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun deserializesAnimatedBezierPropertyWithSlotIdWhenSidPresent() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}], "sid": "anim_slot"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    assertThat(extractSlotId(prop)).isEqualTo("anim_slot")
    assertThat(prop.keyframes).hasSize(1)
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects bare primitive number without enclosing JSON object.
   *
   * Root cause: Specification requires Bézier properties to be JSON objects containing `"a"` and
   * `"k"`. BaseBezierPropertySerializer currently attempts to cast element to JsonObject without
   * checking, throwing IllegalArgumentException or ClassCastException instead of a clean
   * SerializationException.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveNumber() {
    val json = "42.0"
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects bare string primitive without enclosing JSON object.
   *
   * Root cause: Specification requires Bézier properties to be JSON objects. Passing a string
   * literal triggers IllegalArgumentException during jsonObject access instead of throwing a clean
   * SerializationException.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveString() {
    val json = "\"bezier_curve\""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects bare boolean primitive without enclosing JSON object.
   *
   * Root cause: Specification requires Bézier properties to be JSON objects. Passing a boolean
   * literal triggers IllegalArgumentException during jsonObject access instead of throwing a clean
   * SerializationException.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBarePrimitiveBoolean() {
    val json = "true"
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects bare array without enclosing JSON object.
   *
   * Root cause: Specification requires Bézier properties to be JSON objects. Passing a bare array
   * triggers IllegalArgumentException during jsonObject access instead of throwing a clean
   * SerializationException.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenElementIsBareArray() {
    val json = """[{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects Bézier property missing mandatory discriminator `"a"`.
   *
   * Root cause: Lottie 1.0.1 schema requires `"a"` as an integer-boolean discriminator.
   * BaseBezierPropertySerializer currently falls back to StaticBezierPropertySerializer when `"a"`
   * is missing instead of throwing SerializationException.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsMissing() {
    val json = """{"k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects discriminator `"a"` when integer value is outside `{0, 1}`.
   *
   * Root cause: Specification requires `"a"` to be an integer boolean in `{0, 1}`.
   * BaseBezierPropertySerializer currently branches on `a == 1` and treats all other integers (e.g.
   * 2, -1) as static without throwing SerializationException.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsInvalidInteger() {
    val jsonTwo =
      """{"a": 2, "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonTwo)
    }

    val jsonNegative =
      """{"a": -1, "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonNegative)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects extreme integer values for discriminator `"a"`.
   *
   * Root cause: Specification requires `"a"` to be an integer boolean in `{0, 1}`. Boundary
   * integers like Int.MAX_VALUE or Int.MIN_VALUE must throw SerializationException.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsExtremeInteger() {
    val jsonMax =
      """{"a": 2147483647, "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonMax)
    }

    val jsonMin =
      """{"a": -2147483648, "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonMin)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects boolean literal `true` for discriminator `"a"`.
   *
   * Root cause: Lottie specification requires `"a"` to be an integer boolean (`0` or `1`), not JSON
   * boolean literals. BaseBezierPropertySerializer currently treats boolean literals as static
   * because `intOrNull` returns null.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteralTrue() {
    val json =
      """{"a": true, "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects boolean literal `false` for discriminator `"a"`.
   *
   * Root cause: Lottie specification requires `"a"` to be an integer boolean (`0` or `1`), not JSON
   * boolean literals. BaseBezierPropertySerializer currently treats boolean literals as static
   * because `intOrNull` returns null.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteralFalse() {
    val json =
      """{"a": false, "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects string discriminator values for `"a"`.
   *
   * Root cause: Discriminator `"a"` must be an integer boolean, but non-integer string values
   * currently default to static.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  /**
   * [SP_LOT_BEZ_02_01] Deserializes discriminator `"a"` when represented as string `"0"` or `"1"`.
   *
   * Permissive parsing permits string representations of integer booleans.
   */
  @Test
  fun deserializesStaticBezierPropertyWhenDiscriminatorIsStringZero() {
    val json =
      """{"a": "0", "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    val prop = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    assertThat(prop).isInstanceOf(StaticBezierProperty::class.java)
  }

  @Test
  fun deserializesAnimatedBezierPropertyWhenDiscriminatorIsStringOne() {
    val json =
      """{"a": "1", "k": [{"t": 0.0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val prop = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    assertThat(prop).isInstanceOf(AnimatedBezierProperty::class.java)
  }

  @Ignore("Permissive parsing: accepts string values for 'a'")
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsString() {
    val jsonZeroStr =
      """{"a": "0", "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonZeroStr)
    }
  }

  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsNonNumericString() {
    val jsonStaticStr =
      """{"a": "static", "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonStaticStr)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects static Bézier property missing mandatory value field `"k"`.
   *
   * Verifies that when `"k"` is omitted on static property, [SerializationException] is thrown.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyMissingK() {
    val json = """{"a": 0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects array value for static Bézier property `"k"`.
   *
   * Root cause: Static Bézier property requires `"k"` to be a JSON object (`BezierValue`). Passing
   * an array triggers IllegalArgumentException during jsonObject access instead of a clean
   * SerializationException.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsArray() {
    val json =
      """{"a": 0, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects primitive value for static Bézier property `"k"`.
   *
   * Root cause: Static Bézier property requires `"k"` to be a JSON object (`BezierValue`). Passing
   * a primitive number triggers IllegalArgumentException during jsonObject access instead of a
   * clean SerializationException.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsPrimitive() {
    val json = """{"a": 0, "k": 42.0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects animated Bézier property missing mandatory keyframe array `"k"`.
   *
   * Verifies that when `"k"` is omitted on animated property, [SerializationException] is thrown.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyMissingK() {
    val json = """{"a": 1}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects object value for animated Bézier property `"k"`.
   *
   * Verifies that passing a JSON object instead of an array of keyframes throws
   * [SerializationException].
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyKIsObject() {
    val json =
      """{"a": 1, "k": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_02_01] Rejects primitive value for animated Bézier property `"k"`.
   *
   * Verifies that passing a primitive string instead of an array of keyframes throws
   * [SerializationException].
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyKIsPrimitive() {
    val json = """{"a": 1, "k": "invalid_array"}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite B: Keyframe Schema Validation & Deserialization (BezierKeyframe)
  // =========================================================================================

  /**
   * [SP_LOT_BEZ_01_03] Deserializes keyframe with default values when optional fields are omitted.
   *
   * Verifies that hold flag `h` defaults to `false` and easing tangents `i`/`o` default to `null`.
   *
   * Specification:
   * [Lottie Bezier Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-keyframe)
   */
  @Test
  fun deserializesKeyframeWithDefaultsWhenOptionalFieldsOmitted() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    val kf = prop.keyframes.first()
    assertThat(extractFloat(kf.frame)).isEqualTo(0f)
    assertThat(extractBoolean(kf.hold)).isFalse()
    assertThat(kf.inTangent).isNull()
    assertThat(kf.outTangent).isNull()
  }

  /**
   * [SP_LOT_BEZ_01_03] Deserializes keyframe hold flag represented as integer-boolean `h = 0` and
   * `h = 1`.
   *
   * Root cause: BezierKeyframe currently defines `hold: Boolean = false`, which causes
   * kotlinx.serialization to fail when parsing integer values `0` and `1`. Schema requires Integer
   * Boolean.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun deserializesKeyframeWithHoldFlagZeroAndOne() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 0}, {"t": 10, "s": [{"c": false, "v": [[10.0, 10.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 1}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    assertThat(extractBoolean(prop.keyframes[0].hold)).isFalse()
    assertThat(extractBoolean(prop.keyframes[1].hold)).isTrue()
  }

  /**
   * [SP_LOT_BEZ_01_03] Deserializes keyframe with cubic Bézier incoming and outgoing easing
   * tangents.
   *
   * Verifies parsing of `i` and `o` easing handle coordinates on [BezierKeyframe].
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Test
  fun deserializesKeyframeWithEasingTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "i": {"x": [0.33], "y": [0.33]}, "o": {"x": [0.67], "y": [0.67]}}, {"t": 10, "s": [{"c": false, "v": [[10.0, 10.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    assertThat(prop.keyframes[0].inTangent).isNotNull()
    assertThat(prop.keyframes[0].outTangent).isNotNull()
  }

  /**
   * [SP_LOT_BEZ_01_03] Deserializes multi-element keyframe list preserving chronological order.
   *
   * Verifies that keyframes are preserved in sequence with timestamps and Bézier values intact.
   *
   * Specification:
   * [Lottie Bezier Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-keyframe)
   */
  @Test
  fun deserializesAnimatedBezierPropertyWithMultipleKeyframes() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}, {"t": 10, "s": [{"c": false, "v": [[100.0, 200.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    assertThat(extractAnimated(prop)).isTrue()
    assertThat(prop.keyframes).hasSize(2)

    val kf0 = prop.keyframes[0]
    assertThat(extractFloat(kf0.frame)).isEqualTo(0f)
    assertThat(kf0.value).hasSize(1)
    assertPointEquals(kf0.value[0].vertices[0], 0f, 0f)

    val kf1 = prop.keyframes[1]
    assertThat(extractFloat(kf1.frame)).isEqualTo(10f)
    assertThat(kf1.value).hasSize(1)
    assertPointEquals(kf1.value[0].vertices[0], 100f, 200f)
  }

  /**
   * [SP_LOT_BEZ_01_03] Rejects keyframe missing mandatory start frame timestamp `"t"`.
   *
   * Root cause: BezierKeyframe currently defaults `frame: Float = 0f`, allowing missing `"t"`
   * instead of requiring it per Lottie 1.0.1 schema.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingT() {
    val json =
      """{"a": 1, "k": [{"s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_01_03] Rejects keyframe missing mandatory value array `"s"`.
   *
   * Verifies that omitting `"s"` from [BezierKeyframe] throws [SerializationException].
   *
   * Specification:
   * [Lottie Bezier Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingS() {
    val json = """{"a": 1, "k": [{"t": 0}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_01_03] Rejects keyframe when value `"s"` is not an array.
   *
   * Verifies that passing a bare object for `"s"` throws [SerializationException].
   *
   * Specification:
   * [Lottie Bezier Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeSIsNotArray() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": {"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_01_03] Rejects keyframe when value array `"s"` is empty (`minItems: 1`).
   *
   * Root cause: Lottie 1.0.1 schema specifies `minItems: 1` for `"s"` in Bezier Keyframe.
   * Deserializer currently accepts `s = []` without throwing SerializationException.
   *
   * Specification:
   * [Lottie Bezier Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeValueArrayIsEmpty() {
    val json = """{"a": 1, "k": [{"t": 0, "s": []}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_BEZ_01_03] Rejects boolean literals `true` or `false` for hold property `"h"`.
   *
   * Root cause: Lottie 1.0.1 schema requires `"h"` to be an Integer Boolean (`0` or `1`).
   * BezierKeyframe currently accepts boolean literals because `hold: Boolean = false`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsBooleanLiteral() {
    val jsonTrue =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": true}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonTrue)
    }

    val jsonFalse =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": false}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonFalse)
    }
  }

  /**
   * [SP_LOT_BEZ_01_03] Rejects invalid integer values outside `{0, 1}` for hold property `"h"`.
   *
   * Verifies that out-of-range integer values for `"h"` (e.g. 2, -1) throw
   * [SerializationException].
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsInvalidInteger() {
    val jsonTwo =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 2}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonTwo)
    }

    val jsonNegative =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": -1}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, jsonNegative)
    }
  }

  // =========================================================================================
  // Suite C: Round-Trip Serialization Fidelity
  // =========================================================================================

  /**
   * [SP_LOT_BEZ_04_01] Preserves static Bézier property value through serialization round-trip.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun serializesAndDeserializesStaticBezierPropertyIdentically() {
    val initialJson =
      """{"a": 0, "k": {"c": true, "v": [[10.0, 20.0], [30.0, 40.0]], "i": [[0.0, 0.0], [1.0, 1.0]], "o": [[0.0, 0.0], [2.0, 2.0]]}}"""
    val prop = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseBezierPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, serialized)
        as StaticBezierProperty

    assertThat(extractSlotId(roundTripped)).isNull()
    assertThat(extractAnimated(roundTripped)).isFalse()
    assertThat(extractBoolean(roundTripped.value.closed)).isTrue()
    assertThat(roundTripped.value.vertices).hasSize(2)
    assertPointEquals(roundTripped.value.vertices[0], 10f, 20f)
    assertPointEquals(roundTripped.value.vertices[1], 30f, 40f)
    assertPointEquals(roundTripped.value.inTangents[1], 1f, 1f)
    assertPointEquals(roundTripped.value.outTangents[1], 2f, 2f)
  }

  /**
   * [SP_LOT_BEZ_04_02] Preserves slot identifier on static Bézier property through serialization
   * round-trip.
   *
   * Root cause: StaticBezierProperty does not define slotId ('sid') in AST model.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun serializesAndDeserializesStaticBezierPropertyWithSlotId() {
    val initialJson =
      """{"a": 0, "k": {"c": true, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}, "sid": "path_slot"}"""
    val prop = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseBezierPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, serialized)
        as StaticBezierProperty

    assertThat(extractSlotId(roundTripped)).isEqualTo("path_slot")
  }

  /**
   * [SP_LOT_BEZ_04_03] Preserves animated Bézier keyframes through serialization round-trip.
   *
   * Root cause: BezierKeyframe currently fails on integer-boolean `h = 0` / `h = 1`.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun serializesAndDeserializesAnimatedBezierPropertyIdentically() {
    val initialJson =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 0}, {"t": 10, "s": [{"c": false, "v": [[100.0, 100.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 1}]}"""
    val prop = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseBezierPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, serialized)
        as AnimatedBezierProperty

    assertThat(extractAnimated(roundTripped)).isTrue()
    assertThat(roundTripped.keyframes).hasSize(2)
    assertThat(extractFloat(roundTripped.keyframes[0].frame)).isEqualTo(0f)
    assertThat(extractBoolean(roundTripped.keyframes[0].hold)).isFalse()
    assertThat(extractFloat(roundTripped.keyframes[1].frame)).isEqualTo(10f)
    assertThat(extractBoolean(roundTripped.keyframes[1].hold)).isTrue()
  }

  /**
   * [SP_LOT_BEZ_04_04] Preserves integer boolean hold flag through keyframe serialization
   * round-trip.
   *
   * Root cause: BezierKeyframe currently fails on integer-boolean `h = 1`.
   *
   * Specification:
   * [Lottie Bezier Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-keyframe)
   */
  @Test
  fun serializesAndDeserializesHoldKeyframeWithIntegerBooleanHold() {
    val initialJson =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 1}]}"""
    val prop = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BaseBezierPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, serialized)
        as AnimatedBezierProperty

    assertThat(extractBoolean(roundTripped.keyframes[0].hold)).isTrue()
  }

  // =========================================================================================
  // Suite D: Timeline Evaluation (animateBezier)
  // =========================================================================================

  /**
   * [SP_LOT_BEZ_03_01] Evaluates static Bézier property to constant value across all timeline
   * frames.
   *
   * Verifies that `animateBezier` returns the identical [BezierValue] regardless of current frame.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun returnsConstantValueAcrossTimelineWhenBezierIsStatic() {
    val json =
      """{"a": 0, "k": {"c": true, "v": [[15.0, 25.0]], "i": [[-2.0, -2.0]], "o": [[2.0, 2.0]]}}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val framesToTest = listOf(-10f, 0f, 5f, 50f, 1000f)
    for (frame in framesToTest) {
      val evaluated = animateBezier(bezier, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractBoolean(evaluated.closed)).isTrue()
      assertThat(evaluated.vertices).hasSize(1)
      assertPointEquals(evaluated.vertices[0], 15f, 25f)
      assertPointEquals(evaluated.inTangents[0], -2f, -2f)
      assertPointEquals(evaluated.outTangents[0], 2f, 2f)
    }
  }

  /**
   * [SP_LOT_BEZ_03_02] Evaluates animated Bézier property with empty keyframe list to empty Bézier
   * value.
   *
   * Root cause: `animateBezier` currently accesses `keyframes[0]` unconditionally when keyframes is
   * empty, throwing IndexOutOfBoundsException instead of returning an empty BezierValue.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Test
  fun returnsEmptyBezierValueWhenAnimatedBezierHasNoKeyframes() {
    val json = """{"a": 1, "k": []}"""
    val animated =
      LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)
        as AnimatedBezierProperty
    val evaluated = animateBezier(animated, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractBoolean(evaluated.closed)).isFalse()
    assertThat(evaluated.vertices).isEmpty()
    assertThat(evaluated.inTangents).isEmpty()
    assertThat(evaluated.outTangents).isEmpty()
  }

  /**
   * [SP_LOT_BEZ_03_03] Evaluates single-keyframe animated Bézier property to constant value across
   * timeline.
   *
   * Verifies that when only 1 keyframe is present, `animateBezier` returns `keyframes[0].value[0]`
   * at all frames.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun returnsConstantValueAcrossTimelineWhenSingleKeyframe() {
    val json =
      """{"a": 1, "k": [{"t": 10, "s": [{"c": false, "v": [[7.0, 14.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val framesToTest = listOf(-5f, 0f, 10f, 15f, 100f)
    for (frame in framesToTest) {
      val evaluated = animateBezier(bezier, LottieSettings(frame.rf, emptySlotMap))
      assertThat(evaluated.vertices).hasSize(1)
      assertPointEquals(evaluated.vertices[0], 7f, 14f)
    }
  }

  /**
   * [SP_LOT_BEZ_03_04] Clamps evaluated Bézier to first keyframe value when timeline frame precedes
   * first keyframe.
   *
   * Verifies that when `currentFrame <= keyframes.first().frame`, `animateBezier` returns the
   * initial keyframe value.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun clampsToFirstKeyframeValueWhenFramePrecedesFirstKeyframe() {
    val json =
      """{"a": 1, "k": [{"t": 10, "s": [{"c": false, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}, {"t": 20, "s": [{"c": false, "v": [[100.0, 200.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val evalNeg5 = animateBezier(bezier, LottieSettings(-5f.rf, emptySlotMap))
    assertPointEquals(evalNeg5.vertices[0], 10f, 20f)

    val eval0 = animateBezier(bezier, LottieSettings(0f.rf, emptySlotMap))
    assertPointEquals(eval0.vertices[0], 10f, 20f)

    val eval10 = animateBezier(bezier, LottieSettings(10f.rf, emptySlotMap))
    assertPointEquals(eval10.vertices[0], 10f, 20f)
  }

  /**
   * [SP_LOT_BEZ_03_05] Holds final keyframe value when timeline frame exceeds last keyframe
   * timestamp.
   *
   * Root cause: `animateBezier` currently contains a placeholder stub that returns un-interpolated
   * points from the start keyframe rather than clamping/holding at the end keyframe.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore(
    "b/442404202: Path morphing deferred pending RemoteCompose client-side path expression support"
  )
  @Test
  fun holdsAtLastKeyframeValueWhenFrameExceedsLastKeyframe() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}, {"t": 10, "s": [{"c": false, "v": [[100.0, 200.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val eval10 = animateBezier(bezier, LottieSettings(10f.rf, emptySlotMap))
    assertPointEquals(eval10.vertices[0], 100f, 200f)

    val eval15 = animateBezier(bezier, LottieSettings(15f.rf, emptySlotMap))
    assertPointEquals(eval15.vertices[0], 100f, 200f)

    val eval50 = animateBezier(bezier, LottieSettings(50f.rf, emptySlotMap))
    assertPointEquals(eval50.vertices[0], 100f, 200f)
  }

  /**
   * [SP_LOT_BEZ_03_06] Evaluates exact keyframe frames without interpolation artifacts.
   *
   * Root cause: `animateBezier` currently does not evaluate endKeyframe values when `currentFrame
   * == endKeyframe.frame`.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore(
    "b/442404202: Path morphing deferred pending RemoteCompose client-side path expression support"
  )
  @Test
  fun evaluatesExactKeyframeFramesWithoutInterpolationArtifacts() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": true, "v": [[0.0, 0.0]], "i": [[-1.0, -1.0]], "o": [[1.0, 1.0]]}]}, {"t": 10, "s": [{"c": true, "v": [[50.0, 50.0]], "i": [[-5.0, -5.0]], "o": [[5.0, 5.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val eval0 = animateBezier(bezier, LottieSettings(0f.rf, emptySlotMap))
    assertPointEquals(eval0.vertices[0], 0f, 0f)
    assertPointEquals(eval0.inTangents[0], -1f, -1f)
    assertPointEquals(eval0.outTangents[0], 1f, 1f)

    val eval10 = animateBezier(bezier, LottieSettings(10f.rf, emptySlotMap))
    assertPointEquals(eval10.vertices[0], 50f, 50f)
    assertPointEquals(eval10.inTangents[0], -5f, -5f)
    assertPointEquals(eval10.outTangents[0], 5f, 5f)
  }

  /**
   * [SP_LOT_BEZ_03_07] Linearly interpolates vertices, tangents, and closed flag between keyframes.
   *
   * Root cause: `animatePoints` in `renderer/properties/Bezier.kt` is a TODO stub that returns
   * un-interpolated points: `from.mapIndexed { _, point -> point }`. Actual interpolation of
   * coordinates is required.
   *
   * Specification:
   * [Lottie Bezier Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#bezier-property)
   */
  @Ignore(
    "b/442404202: Path morphing deferred pending RemoteCompose client-side path expression support"
  )
  @Test
  fun linearlyInterpolatesVerticesTangentsAndClosedFlagBetweenKeyframes() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": true, "v": [[0.0, 0.0], [10.0, 20.0]], "i": [[-1.0, -1.0], [-2.0, -2.0]], "o": [[1.0, 1.0], [2.0, 2.0]]}]}, {"t": 10, "s": [{"c": true, "v": [[100.0, 200.0], [30.0, 60.0]], "i": [[9.0, 9.0], [8.0, 8.0]], "o": [[11.0, 11.0], [12.0, 12.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val eval5 = animateBezier(bezier, LottieSettings(5f.rf, emptySlotMap))

    assertThat(extractBoolean(eval5.closed)).isTrue()
    assertThat(eval5.vertices).hasSize(2)

    assertPointEquals(eval5.vertices[0], 50f, 100f)
    assertPointEquals(eval5.vertices[1], 20f, 40f)

    assertPointEquals(eval5.inTangents[0], 4f, 4f)
    assertPointEquals(eval5.inTangents[1], 3f, 3f)

    assertPointEquals(eval5.outTangents[0], 6f, 6f)
    assertPointEquals(eval5.outTangents[1], 7f, 7f)

    val eval2_5 = animateBezier(bezier, LottieSettings(2.5f.rf, emptySlotMap))
    assertPointEquals(eval2_5.vertices[0], 25f, 50f)
    assertPointEquals(eval2_5.vertices[1], 15f, 30f)
  }

  /**
   * [SP_LOT_BEZ_03_08] Holds value constant until next keyframe when hold flag is true (`h = 1`).
   *
   * Root cause: BezierKeyframe fails deserialization for `h = 1`, and `animateBezier` does not
   * branch on hold flag.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore(
    "b/442404202: Path morphing deferred pending RemoteCompose client-side path expression support"
  )
  @Test
  fun holdsValueConstantUntilNextKeyframeWhenHoldFlagIsTrue() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[10.0, 20.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 1}, {"t": 10, "s": [{"c": false, "v": [[100.0, 200.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val framesHeld = listOf(0f, 0.1f, 5f, 9.99f)
    for (frame in framesHeld) {
      val evaluated = animateBezier(bezier, LottieSettings(frame.rf, emptySlotMap))
      assertPointEquals(evaluated.vertices[0], 10f, 20f)
    }

    val eval10 = animateBezier(bezier, LottieSettings(10f.rf, emptySlotMap))
    assertPointEquals(eval10.vertices[0], 100f, 200f)
  }

  /**
   * [SP_LOT_BEZ_03_09] Interpolates Bézier geometry using custom cubic easing tangents.
   *
   * Root cause: `animatePoints` in `renderer/properties/Bezier.kt` is a TODO stub that does not
   * apply the computed `currentBezierValue`.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Ignore(
    "b/442404202: Path morphing deferred pending RemoteCompose client-side path expression support"
  )
  @Test
  fun interpolatesWithCubicBezierEasingDepartingFromLinearMidpoint() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "i": {"x": [1.0], "y": [0.0]}, "o": {"x": [0.0], "y": [0.0]}}, {"t": 10, "s": [{"c": false, "v": [[100.0, 100.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val eval0 = animateBezier(bezier, LottieSettings(0f.rf, emptySlotMap))
    assertPointEquals(eval0.vertices[0], 0f, 0f)

    val eval10 = animateBezier(bezier, LottieSettings(10f.rf, emptySlotMap))
    assertPointEquals(eval10.vertices[0], 100f, 100f)

    val eval5 = animateBezier(bezier, LottieSettings(5f.rf, emptySlotMap))
    assertThat(eval5.vertices[0].x.constantValue).isNotEqualTo(50.0f)
    assertThat(eval5.vertices[0].y.constantValue).isNotEqualTo(50.0f)
  }

  /**
   * [SP_LOT_BEZ_03_10] Evaluates multi-segment keyframes sequentially across consecutive timeline
   * intervals.
   *
   * Root cause: `animateBezier` currently only evaluates the first interval `[keyframes[0],
   * keyframes[1]]` and does not support chained animations across three or more keyframes.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore(
    "b/442404202: Path morphing deferred pending RemoteCompose client-side path expression support"
  )
  @Test
  fun evaluatesMultiSegmentKeyframesSequentially() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [{"c": false, "v": [[0.0, 0.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 1}, {"t": 10, "s": [{"c": false, "v": [[100.0, 100.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}], "h": 0}, {"t": 20, "s": [{"c": false, "v": [[200.0, 300.0]], "i": [[0.0, 0.0]], "o": [[0.0, 0.0]]}]}]}"""
    val bezier = LottieDecoder.json.decodeFromString(BaseBezierPropertySerializer, json)

    val eval5 = animateBezier(bezier, LottieSettings(5f.rf, emptySlotMap))
    assertPointEquals(eval5.vertices[0], 0f, 0f)

    val eval10 = animateBezier(bezier, LottieSettings(10f.rf, emptySlotMap))
    assertPointEquals(eval10.vertices[0], 100f, 100f)

    val eval15 = animateBezier(bezier, LottieSettings(15f.rf, emptySlotMap))
    assertPointEquals(eval15.vertices[0], 150f, 200f)

    val eval20 = animateBezier(bezier, LottieSettings(20f.rf, emptySlotMap))
    assertPointEquals(eval20.vertices[0], 200f, 300f)

    val eval25 = animateBezier(bezier, LottieSettings(25f.rf, emptySlotMap))
    assertPointEquals(eval25.vertices[0], 200f, 300f)
  }
}

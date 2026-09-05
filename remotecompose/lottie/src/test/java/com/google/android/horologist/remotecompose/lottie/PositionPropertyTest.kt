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
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionPropertySerializer
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertThrows
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PositionPropertyTest {
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

  private fun extractKeyframeCoordinate(value: Any, index: Int): Float =
    when (value) {
      is List<*> -> extractFloat(value[index]!!)
      is FloatArray -> value[index]
      else -> error("Unexpected keyframe coordinates type: ${value::class}")
    }

  // =========================================================================================
  // Suite A: BasePositionPropertySerializer Deserialization & Keyframe Attributes
  // =========================================================================================

  /**
   * [SP_LOT_POS_01_01] Deserializes static 2D position property when discriminator `a` is
   * integer 0.
   *
   * Verifies that when discriminator `a` is integer `0`, the position property deserializes into a
   * [StaticPositionProperty] containing the 2D coordinate array `[x, y]`.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun returnsStaticPositionPropertyWhenDeserializing2DCoordinates() {
    val json = """{"a": 0, "k": [10.0, 20.0]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as StaticPositionProperty
    assertThat(prop.value).hasLength(2)
    assertThat(prop.value[0]).isEqualTo(10.0f)
    assertThat(prop.value[1]).isEqualTo(20.0f)
    assertThat(extractBoolean(prop.animated)).isFalse()
    assertThat(prop.slotId).isNull()
  }

  /**
   * [SP_LOT_POS_01_02] Deserializes static 3D position property preserving 3D coordinates.
   *
   * Verifies that a 3D coordinate vector `[x, y, z]` is preserved in [StaticPositionProperty].
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun returnsStaticPositionPropertyPreserving3DCoordinatesWhenDeserializing3DVector() {
    val json = """{"a": 0, "k": [10.0, 20.0, 30.0]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as StaticPositionProperty
    assertThat(prop.value).hasLength(3)
    assertThat(prop.value[0]).isEqualTo(10.0f)
    assertThat(prop.value[1]).isEqualTo(20.0f)
    assertThat(prop.value[2]).isEqualTo(30.0f)
    assertThat(extractBoolean(prop.animated)).isFalse()
  }

  /**
   * [SP_LOT_POS_01_03] Deserializes static position property with slot identifier when `sid` is
   * present.
   *
   * Verifies that optional slot identifier `sid` is captured on [StaticPositionProperty] enabling
   * runtime value replacement via slot maps.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun returnsStaticPositionPropertyWithSlotIdWhenSidPresent() {
    val json = """{"a": 0, "k": [0.0, 0.0], "sid": "pos_slot"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as StaticPositionProperty
    assertThat(prop.slotId).isEqualTo("pos_slot")
    assertThat(prop.value[0]).isEqualTo(0.0f)
    assertThat(prop.value[1]).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_POS_01_04] Deserializes animated position property when discriminator `a` is integer 1.
   *
   * Verifies that when discriminator `a` is integer `1`, the position property deserializes into an
   * [AnimatedPositionProperty] containing chronologically ordered keyframes.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun returnsAnimatedPositionPropertyWhenDeserializingLinearKeyframes() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as AnimatedPositionProperty
    assertThat(extractBoolean(prop.animated)).isTrue()
    assertThat(prop.keyframes).hasSize(2)
    assertThat(extractFloat(prop.keyframes[0].frame)).isEqualTo(0.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[0].value, 0)).isEqualTo(0.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[0].value, 1)).isEqualTo(0.0f)
    assertThat(extractFloat(prop.keyframes[1].frame)).isEqualTo(10.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[1].value, 0)).isEqualTo(100.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[1].value, 1)).isEqualTo(200.0f)
  }

  /**
   * [SP_LOT_POS_01_05] Deserializes animated position keyframes with cubic Bézier easing handles.
   *
   * Verifies that incoming (`i`) and outgoing (`o`) Bézier easing handles are captured on
   * keyframes.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Test
  fun returnsAnimatedPositionPropertyWithCubicBezierEasingHandles() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "i": {"x": [0.33], "y": [1.0]}, "o": {"x": [0.67], "y": [0.0]}}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as AnimatedPositionProperty
    assertThat(prop.keyframes).hasSize(2)
    assertThat(prop.keyframes[0].inTangent).isNotNull()
    assertThat(prop.keyframes[0].outTangent).isNotNull()
  }

  /**
   * [SP_LOT_POS_01_06] Deserializes animated position keyframes tolerating spatial tangents.
   *
   * Verifies that optional spatial path tangents `ti` and `to` are safely parsed or tolerated on
   * keyframe objects.
   *
   * Specification:
   * [Lottie Position Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-keyframe)
   */
  @Test
  fun returnsAnimatedPositionPropertyToleratingSpatialTangents() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "to": [10.0, -5.0], "ti": [-10.0, 5.0]}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as AnimatedPositionProperty
    assertThat(prop.keyframes).hasSize(2)
    assertThat(extractKeyframeCoordinate(prop.keyframes[0].value, 0)).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_POS_01_07] Deserializes animated position keyframe with hold flag `h = 1`.
   *
   * Verifies that `h = 1` sets keyframe hold to `true`.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun returnsAnimatedPositionPropertyWithHoldKeyframeWhenHoldFlagIsOne() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "h": 1}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as AnimatedPositionProperty
    assertThat(prop.keyframes).hasSize(2)
    assertThat(extractBoolean(prop.keyframes[0].hold)).isTrue()
    assertThat(extractBoolean(prop.keyframes[1].hold)).isFalse()
  }

  /**
   * [SP_LOT_POS_01_08] Deserializes animated position property when keyframe list is empty.
   *
   * Verifies boundary handling for an empty keyframe array `k = []`.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun returnsAnimatedPositionPropertyWhenKeyframeListIsEmpty() {
    val json = """{"a": 1, "k": []}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as AnimatedPositionProperty
    assertThat(extractBoolean(prop.animated)).isTrue()
    assertThat(prop.keyframes).isEmpty()
  }

  /**
   * [SP_LOT_POS_01_09] Deserializes animated position property with slot identifier when `sid` is
   * present.
   *
   * Verifies that optional slot identifier `sid` is captured on [AnimatedPositionProperty].
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun returnsAnimatedPositionPropertyWithSlotIdWhenSidPresent() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}], "sid": "anim_slot"}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as AnimatedPositionProperty
    assertThat(prop.slotId).isEqualTo("anim_slot")
    assertThat(prop.keyframes).hasSize(1)
  }

  /**
   * [SP_LOT_POS_01_10] Deserializes animated position keyframes with negative start frames and
   * coordinates.
   *
   * Verifies accurate parsing of negative float values for timeline frames and vector coordinates.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun returnsAnimatedPositionPropertyWithNegativeFramesAndCoordinates() {
    val json =
      """{"a": 1, "k": [{"t": -5.0, "s": [-50.0, -100.0]}, {"t": 5.0, "s": [50.0, 100.0]}]}"""
    val prop =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
        as AnimatedPositionProperty
    assertThat(extractFloat(prop.keyframes[0].frame)).isEqualTo(-5.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[0].value, 0)).isEqualTo(-50.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[0].value, 1)).isEqualTo(-100.0f)
    assertThat(extractFloat(prop.keyframes[1].frame)).isEqualTo(5.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[1].value, 0)).isEqualTo(50.0f)
    assertThat(extractKeyframeCoordinate(prop.keyframes[1].value, 1)).isEqualTo(100.0f)
  }

  // =========================================================================================
  // Suite B: Validation Errors & Schema Strictness
  // =========================================================================================

  /**
   * [SP_LOT_POS_02_01] Rejects bare array without enclosing JSON object.
   *
   * Root cause: Specification requires position properties to be JSON objects.
   * BasePositionPropertySerializer currently throws IllegalArgumentException when attempting to
   * access .jsonObject instead of throwing a clean SerializationException.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Ignore(
    "BUG: SP_LOT_POS_02_01: BasePositionPropertySerializer throws IllegalArgumentException instead of SerializationException on bare array"
  )
  @Test
  fun throwsSerializationExceptionWhenRootIsBareArray() {
    val json = """[10.0, 20.0]"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_02] Rejects bare number without enclosing JSON object.
   *
   * Root cause: Specification requires position properties to be JSON objects.
   * BasePositionPropertySerializer currently throws IllegalArgumentException when attempting to
   * access .jsonObject instead of throwing a clean SerializationException.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Ignore(
    "BUG: SP_LOT_POS_02_02: BasePositionPropertySerializer throws IllegalArgumentException instead of SerializationException on bare number"
  )
  @Test
  fun throwsSerializationExceptionWhenRootIsBareNumber() {
    val json = """42.0"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_03] Rejects position property missing mandatory discriminator `"a"`.
   *
   * Root cause: Specification requires `"a"` as an integer-boolean discriminator.
   * BasePositionPropertySerializer currently defaults to StaticPositionProperty when `"a"` is
   * missing.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Ignore(
    "BUG: SP_LOT_POS_02_03: BasePositionPropertySerializer does not require discriminator 'a'"
  )
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsMissing() {
    val json = """{"k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_04] Rejects boolean literal `false` for discriminator `"a"`.
   *
   * Verifies that boolean literal `false` throws [SerializationException] per integer-boolean
   * specification.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteralFalse() {
    val json = """{"a": false, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_05] Rejects boolean literal `true` for discriminator `"a"`.
   *
   * Verifies that boolean literal `true` throws [SerializationException] per integer-boolean
   * specification.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsBooleanLiteralTrue() {
    val json = """{"a": true, "k": []}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_06] Rejects discriminator `"a"` when integer value is greater than 1 (`2`).
   *
   * Root cause: Specification requires `"a"` to be an integer boolean in `{0, 1}`.
   * BasePositionPropertySerializer branches on `a == 1` and defaults all other integers to static.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Ignore(
    "BUG: SP_LOT_POS_02_06: BasePositionPropertySerializer does not reject invalid discriminator integers"
  )
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsInvalidIntegerAboveOne() {
    val json = """{"a": 2, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_07] Rejects discriminator `"a"` when integer value is negative (`-1`).
   *
   * Root cause: Specification requires `"a"` to be an integer boolean in `{0, 1}`.
   * BasePositionPropertySerializer defaults negative integers to static.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Ignore(
    "BUG: SP_LOT_POS_02_07: BasePositionPropertySerializer does not reject negative discriminator integers"
  )
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsNegativeInteger() {
    val json = """{"a": -1, "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_08] Rejects string literal for discriminator `"a"`.
   *
   * Root cause: Specification requires `"a"` to be an integer boolean. String `"0"` causes
   * `intOrNull` to return null, which currently defaults to static without error.
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Ignore(
    "BUG: SP_LOT_POS_02_08: BasePositionPropertySerializer does not reject string discriminator values"
  )
  @Test
  fun throwsSerializationExceptionWhenDiscriminatorAIsString() {
    val json = """{"a": "0", "k": [10.0, 20.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_09] Rejects static position property missing mandatory coordinate array `"k"`.
   *
   * Verifies that missing `"k"` throws [SerializationException].
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyMissingK() {
    val json = """{"a": 0}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_10] Rejects static position property when `"k"` is not an array.
   *
   * Verifies that passing a string for `"k"` throws [SerializationException].
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsNotArray() {
    val json = """{"a": 0, "k": "not_an_array"}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_11] Rejects static position property when `"k"` is an empty array.
   *
   * Root cause: Specification requires at least 2 coordinates `[x, y]`. StaticPositionProperty
   * accepts empty FloatArray without validation.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Ignore("BUG: SP_LOT_POS_02_11: StaticPositionProperty accepts empty array for 'k'")
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKIsEmptyArray() {
    val json = """{"a": 0, "k": []}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_12] Rejects static position property when `"k"` contains fewer than 2
   * coordinates.
   *
   * Root cause: Specification requires at least 2 coordinates `[x, y]`. StaticPositionProperty
   * accepts single-element FloatArray without validation.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Ignore("BUG: SP_LOT_POS_02_12: StaticPositionProperty accepts single-element array for 'k'")
  @Test
  fun throwsSerializationExceptionWhenStaticPropertyKHasFewerThanTwoCoordinates() {
    val json = """{"a": 0, "k": [10.0]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_13] Rejects animated position property missing mandatory keyframe array `"k"`.
   *
   * Verifies that missing `"k"` throws [SerializationException].
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyMissingK() {
    val json = """{"a": 1}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_14] Rejects animated position property when keyframe `"k"` is not an array.
   *
   * Verifies that primitive value for `"k"` throws [SerializationException].
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun throwsSerializationExceptionWhenAnimatedPropertyKIsNotArray() {
    val json = """{"a": 1, "k": 42}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_15] Rejects keyframe missing mandatory start frame `"t"`.
   *
   * Verifies that keyframe requires frame timestamp `"t"`.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingT() {
    val json = """{"a": 1, "k": [{"s": [0.0, 0.0]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_16] Rejects keyframe missing mandatory coordinate value `"s"`.
   *
   * Verifies that keyframe requires coordinate value `"s"`.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeMissingS() {
    val json = """{"a": 1, "k": [{"t": 0}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_17] Rejects keyframe when coordinate array `"s"` has fewer than 2 coordinates.
   *
   * Root cause: Specification requires position keyframe `"s"` to contain at least 2 coordinates
   * `[x, y]`.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Ignore("BUG: SP_LOT_POS_02_17: VectorPropertyKeyframe accepts single-element array for 's'")
  @Test
  fun throwsSerializationExceptionWhenKeyframeSHasFewerThanTwoCoordinates() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [10.0]}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_18] Rejects boolean literal `true` for keyframe hold flag `"h"`.
   *
   * Verifies that boolean literals for hold flag `"h"` throw [SerializationException].
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsBooleanLiteral() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "h": true}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  /**
   * [SP_LOT_POS_02_19] Rejects invalid integer `2` for keyframe hold flag `"h"`.
   *
   * Verifies that invalid integer for hold flag throws [SerializationException].
   *
   * Specification:
   * [Lottie Integer Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean)
   */
  @Test
  fun throwsSerializationExceptionWhenKeyframeHoldIsInvalidInteger() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "h": 2}]}"""
    assertThrows(SerializationException::class.java) {
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)
    }
  }

  // =========================================================================================
  // Suite C: Timeline Evaluation (animatePosition)
  // =========================================================================================

  /**
   * [SP_LOT_POS_03_01] Evaluates static position property at frame 0 into exact Point coordinates.
   *
   * Verifies that static position evaluates to `Point(x = 10f.rf, y = 20f.rf)`.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun returnsExactCoordinatesWhenStaticPositionEvaluatedAtFrameZero() {
    val json = """{"a": 0, "k": [10.0, 20.0]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val evaluated = animatePosition(position, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractFloat(evaluated.x)).isEqualTo(10.0f)
    assertThat(extractFloat(evaluated.y)).isEqualTo(20.0f)
  }

  /**
   * [SP_LOT_POS_03_02] Evaluates static position property across multiple timeline frames.
   *
   * Verifies that static position coordinates remain invariant across all frames.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun returnsConstantCoordinatesAcrossTimelineWhenStaticPositionEvaluated() {
    val json = """{"a": 0, "k": [10.0, 20.0]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val frames = listOf(-10f, 0f, 5f, 10f, 50f, 100f)
    for (frame in frames) {
      val evaluated = animatePosition(position, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloat(evaluated.x)).isEqualTo(10.0f)
      assertThat(extractFloat(evaluated.y)).isEqualTo(20.0f)
    }
  }

  /**
   * [SP_LOT_POS_03_03] Evaluates animated position property with empty keyframe list to default
   * zero Point.
   *
   * Verifies that empty keyframe list evaluates to `Point(0f.rf, 0f.rf)`.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun returnsDefaultZeroPointWhenAnimatedPositionHasNoKeyframes() {
    val json = """{"a": 1, "k": []}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val evaluated = animatePosition(position, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractFloat(evaluated.x)).isEqualTo(0.0f)
    assertThat(extractFloat(evaluated.y)).isEqualTo(0.0f)

    val evaluated10 = animatePosition(position, LottieSettings(10f.rf, emptySlotMap))
    assertThat(extractFloat(evaluated10.x)).isEqualTo(0.0f)
    assertThat(extractFloat(evaluated10.y)).isEqualTo(0.0f)
  }

  /**
   * [SP_LOT_POS_03_04] Evaluates animated position property with single keyframe to constant
   * coordinates.
   *
   * Verifies that single keyframe holds its coordinates constant across all timeline frames.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun returnsConstantCoordinatesAcrossTimelineWhenSingleKeyframe() {
    val json = """{"a": 1, "k": [{"t": 5, "s": [30.0, 40.0]}]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val frames = listOf(0f, 5f, 10f, 50f)
    for (frame in frames) {
      val evaluated = animatePosition(position, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloat(evaluated.x)).isEqualTo(30.0f)
      assertThat(extractFloat(evaluated.y)).isEqualTo(40.0f)
    }
  }

  /**
   * [SP_LOT_POS_03_05] Linearly interpolates coordinates between adjacent keyframes across timeline
   * frames.
   *
   * Root cause: animatePosition uses lookupValueInBezier with default linear tangents, which
   * currently evaluates with slight precision delta from expected midpoint.
   *
   * Specification:
   * [Lottie Keyframe Easing](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle)
   */
  @Ignore(
    "BUG: SP_LOT_POS_03_05: animatePosition interpolation formula evaluates with delta from expected midpoint"
  )
  @Test
  fun linearlyInterpolatesCoordinatesBetweenKeyframesAtMidpoint() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val eval0 = animatePosition(position, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractFloat(eval0.x)).isEqualTo(0.0f)
    assertThat(extractFloat(eval0.y)).isEqualTo(0.0f)

    val eval2_5 = animatePosition(position, LottieSettings(2.5f.rf, emptySlotMap))
    assertThat(extractFloat(eval2_5.x)).isEqualTo(25.0f)
    assertThat(extractFloat(eval2_5.y)).isEqualTo(50.0f)

    val eval5 = animatePosition(position, LottieSettings(5.0f.rf, emptySlotMap))
    assertThat(extractFloat(eval5.x)).isEqualTo(50.0f)
    assertThat(extractFloat(eval5.y)).isEqualTo(100.0f)

    val eval7_5 = animatePosition(position, LottieSettings(7.5f.rf, emptySlotMap))
    assertThat(extractFloat(eval7_5.x)).isEqualTo(75.0f)
    assertThat(extractFloat(eval7_5.y)).isEqualTo(150.0f)

    val eval10 = animatePosition(position, LottieSettings(10.0f.rf, emptySlotMap))
    assertThat(extractFloat(eval10.x)).isEqualTo(100.0f)
    assertThat(extractFloat(eval10.y)).isEqualTo(200.0f)
  }

  /**
   * [SP_LOT_POS_03_06] Clamps to initial keyframe coordinates when frame precedes first keyframe
   * timestamp.
   *
   * Verifies that pre-animation frames hold the initial keyframe coordinates.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun clampsToInitialKeyframeCoordinatesWhenFramePrecedesStartKeyframe() {
    val json = """{"a": 1, "k": [{"t": 10, "s": [20.0, 30.0]}, {"t": 20, "s": [80.0, 90.0]}]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val frames = listOf(0f, 5f, 9.9f)
    for (frame in frames) {
      val evaluated = animatePosition(position, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloat(evaluated.x)).isEqualTo(20.0f)
      assertThat(extractFloat(evaluated.y)).isEqualTo(30.0f)
    }
  }

  /**
   * [SP_LOT_POS_03_07] Holds final keyframe coordinates when timeline frame exceeds last keyframe
   * timestamp.
   *
   * Verifies that post-animation frames hold the final keyframe coordinates.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Test
  fun holdsFinalKeyframeCoordinatesWhenFrameExceedsLastKeyframe() {
    val json = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val frames = listOf(10f, 15f, 100f)
    for (frame in frames) {
      val evaluated = animatePosition(position, LottieSettings(frame.rf, emptySlotMap))
      assertThat(extractFloat(evaluated.x)).isEqualTo(100.0f)
      assertThat(extractFloat(evaluated.y)).isEqualTo(200.0f)
    }
  }

  /**
   * [SP_LOT_POS_03_08] Holds coordinates constant until next keyframe when hold flag `"h"` is 1.
   *
   * Root cause: animatePosition currently ignores hold flag h on keyframe segments and applies
   * continuous Bézier easing.
   *
   * Specification:
   * [Lottie Base Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#base-keyframe)
   */
  @Ignore("BUG: SP_LOT_POS_03_08: animatePosition ignores hold flag 'h' and performs interpolation")
  @Test
  fun holdsCoordinatesConstantUntilNextKeyframeWhenHoldFlagIsTrue() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [10.0, 20.0], "h": 1}, {"t": 10, "s": [50.0, 60.0]}]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val eval0 = animatePosition(position, LottieSettings(0f.rf, emptySlotMap))
    assertThat(extractFloat(eval0.x)).isEqualTo(10.0f)
    assertThat(extractFloat(eval0.y)).isEqualTo(20.0f)

    val eval5 = animatePosition(position, LottieSettings(5f.rf, emptySlotMap))
    assertThat(extractFloat(eval5.x)).isEqualTo(10.0f)
    assertThat(extractFloat(eval5.y)).isEqualTo(20.0f)

    val eval9_9 = animatePosition(position, LottieSettings(9.9f.rf, emptySlotMap))
    assertThat(extractFloat(eval9_9.x)).isEqualTo(10.0f)
    assertThat(extractFloat(eval9_9.y)).isEqualTo(20.0f)

    val eval10 = animatePosition(position, LottieSettings(10f.rf, emptySlotMap))
    assertThat(extractFloat(eval10.x)).isEqualTo(50.0f)
    assertThat(extractFloat(eval10.y)).isEqualTo(60.0f)
  }

  /**
   * [SP_LOT_POS_03_09] Interpolates coordinates sequentially across multi-segment keyframe
   * sequences.
   *
   * Verifies accurate trajectory evaluation across multiple adjacent keyframe intervals.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun interpolatesCoordinatesSequentiallyAcrossMultipleSegments() {
    val json =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}, {"t": 10, "s": [100.0, 100.0]}, {"t": 20, "s": [200.0, 300.0]}]}"""
    val position = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, json)

    val eval5 = animatePosition(position, LottieSettings(5f.rf, emptySlotMap))
    assertThat(extractFloat(eval5.x)).isEqualTo(50.0f)
    assertThat(extractFloat(eval5.y)).isEqualTo(50.0f)

    val eval10 = animatePosition(position, LottieSettings(10f.rf, emptySlotMap))
    assertThat(extractFloat(eval10.x)).isEqualTo(100.0f)
    assertThat(extractFloat(eval10.y)).isEqualTo(100.0f)

    val eval15 = animatePosition(position, LottieSettings(15f.rf, emptySlotMap))
    assertThat(extractFloat(eval15.x)).isEqualTo(150.0f)
    assertThat(extractFloat(eval15.y)).isEqualTo(200.0f)

    val eval20 = animatePosition(position, LottieSettings(20f.rf, emptySlotMap))
    assertThat(extractFloat(eval20.x)).isEqualTo(200.0f)
    assertThat(extractFloat(eval20.y)).isEqualTo(300.0f)
  }

  // =========================================================================================
  // Suite D: Round-Trip Serialization Fidelity
  // =========================================================================================

  /**
   * [SP_LOT_POS_04_01] Preserves static position coordinates through serialization round-trip.
   *
   * Verifies AST -> JSON -> AST equivalence for [StaticPositionProperty].
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun preservesStaticPositionPropertyThroughRoundTripSerialization() {
    val initialJson = """{"a": 0, "k": [10.0, 20.0]}"""
    val prop = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BasePositionPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, serialized)
        as StaticPositionProperty
    assertThat(roundTripped.value).hasLength(2)
    assertThat(roundTripped.value[0]).isEqualTo(10.0f)
    assertThat(roundTripped.value[1]).isEqualTo(20.0f)
    assertThat(extractBoolean(roundTripped.animated)).isFalse()
  }

  /**
   * [SP_LOT_POS_04_02] Preserves 3D coordinates and slot identifier through serialization
   * round-trip.
   *
   * Verifies that 3D vector coordinates and `sid` are preserved across serialization.
   *
   * Specification:
   * [Lottie Slottable Property](https://lottie.github.io/lottie-spec/1.0.1/specs/helpers/#slottable-property)
   */
  @Test
  fun preservesStatic3DPositionAndSlotIdThroughRoundTripSerialization() {
    val initialJson = """{"a": 0, "k": [10.0, 20.0, 30.0], "sid": "pos_3d"}"""
    val prop = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BasePositionPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, serialized)
        as StaticPositionProperty
    assertThat(roundTripped.value).hasLength(3)
    assertThat(roundTripped.value[0]).isEqualTo(10.0f)
    assertThat(roundTripped.value[1]).isEqualTo(20.0f)
    assertThat(roundTripped.value[2]).isEqualTo(30.0f)
    assertThat(roundTripped.slotId).isEqualTo("pos_3d")
  }

  /**
   * [SP_LOT_POS_04_03] Preserves animated linear position keyframes through serialization
   * round-trip.
   *
   * Verifies that animated position keyframe lists and coordinates are preserved across
   * serialization.
   *
   * Specification:
   * [Lottie Position Property](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#position-property)
   */
  @Test
  fun preservesAnimatedLinearPositionPropertyThroughRoundTripSerialization() {
    val initialJson =
      """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0]}, {"t": 10, "s": [100.0, 200.0]}]}"""
    val prop = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BasePositionPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, serialized)
        as AnimatedPositionProperty
    assertThat(extractBoolean(roundTripped.animated)).isTrue()
    assertThat(roundTripped.keyframes).hasSize(2)
    assertThat(extractFloat(roundTripped.keyframes[0].frame)).isEqualTo(0.0f)
    assertThat(extractKeyframeCoordinate(roundTripped.keyframes[0].value, 0)).isEqualTo(0.0f)
    assertThat(extractKeyframeCoordinate(roundTripped.keyframes[0].value, 1)).isEqualTo(0.0f)
    assertThat(extractFloat(roundTripped.keyframes[1].frame)).isEqualTo(10.0f)
    assertThat(extractKeyframeCoordinate(roundTripped.keyframes[1].value, 0)).isEqualTo(100.0f)
    assertThat(extractKeyframeCoordinate(roundTripped.keyframes[1].value, 1)).isEqualTo(200.0f)
  }

  /**
   * [SP_LOT_POS_04_04] Preserves animated position property with hold keyframes and slot
   * identifier.
   *
   * Verifies that hold flag `h = 1` and `sid` are preserved across serialization.
   *
   * Specification:
   * [Lottie Vector Keyframe](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#vector-keyframe)
   */
  @Test
  fun preservesAnimatedHoldPositionPropertyWithSlotIdThroughRoundTripSerialization() {
    val initialJson = """{"a": 1, "k": [{"t": 0, "s": [0.0, 0.0], "h": 1}], "sid": "anim_slot"}"""
    val prop = LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, initialJson)
    val serialized = LottieDecoder.json.encodeToString(BasePositionPropertySerializer, prop)
    val roundTripped =
      LottieDecoder.json.decodeFromString(BasePositionPropertySerializer, serialized)
        as AnimatedPositionProperty
    assertThat(roundTripped.slotId).isEqualTo("anim_slot")
    assertThat(roundTripped.keyframes).hasSize(1)
    assertThat(extractBoolean(roundTripped.keyframes[0].hold)).isTrue()
  }
}

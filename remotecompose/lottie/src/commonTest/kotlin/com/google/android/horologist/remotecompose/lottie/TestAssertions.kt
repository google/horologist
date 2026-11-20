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

package com.google.android.horologist.remotecompose.lottie.testcompat

import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.rc
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class Color(val argb: Int) {
  constructor(argb: Long) : this(argb.toInt())

  constructor(
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float = 1f,
  ) : this(
    (((alpha * 255f).roundToInt().coerceIn(0, 255)) shl 24) or
      (((red * 255f).roundToInt().coerceIn(0, 255)) shl 16) or
      (((green * 255f).roundToInt().coerceIn(0, 255)) shl 8) or
      ((blue * 255f).roundToInt().coerceIn(0, 255))
  )

  val alpha: Float
    get() = ((argb ushr 24) and 0xFF) / 255f

  val red: Float
    get() = ((argb ushr 16) and 0xFF) / 255f

  val green: Float
    get() = ((argb ushr 8) and 0xFF) / 255f

  val blue: Float
    get() = (argb and 0xFF) / 255f

  companion object {
    val Red = Color(0xFFFF0000.toInt())
    val Blue = Color(0xFF0000FF.toInt())
    val Transparent = Color(0x00000000)
  }
}

internal val Color.rc: RemoteColor
  get() = argb.rc

internal fun <T : Throwable> assertThrows(expected: KClass<T>, block: () -> Unit): T =
  assertFailsWith(expected) { block() }

internal class Ordered(private val inOrderCheck: () -> Unit) {
  fun inOrder() {
    inOrderCheck()
  }
}

internal class FloatToleranceCheck(private val actual: Float?, private val tolerance: Float) {
  fun of(expected: Float) {
    assertNotNull(actual, "Expected non-null float within $tolerance of $expected")
    val diff = abs(actual - expected)
    assertTrue(
      diff <= tolerance,
      "Expected <$actual> to be within <$tolerance> of <$expected> (diff=$diff)",
    )
  }

  fun of(expected: Double) = of(expected.toFloat())
}

internal class DoubleToleranceCheck(private val actual: Double?, private val tolerance: Double) {
  fun of(expected: Double) {
    assertNotNull(actual, "Expected non-null double within $tolerance of $expected")
    val diff = abs(actual - expected)
    assertTrue(
      diff <= tolerance,
      "Expected <$actual> to be within <$tolerance> of <$expected> (diff=$diff)",
    )
  }
}

internal open class Subject(private val actual: Any?) {
  open fun isEqualTo(expected: Any?) {
    if (actual is FloatArray && expected is FloatArray) {
      assertTrue(
        actual.contentEquals(expected),
        "Expected <${actual.contentToString()}> to equal <${expected.contentToString()}>",
      )
    } else {
      assertEquals(expected, actual)
    }
  }

  fun isNotEqualTo(unexpected: Any?) {
    assertNotEquals(unexpected, actual)
  }

  fun isNull() {
    assertNull(actual)
  }

  fun isNotNull() {
    assertNotNull(actual)
  }

  fun isInstanceOf(expected: KClass<*>) {
    assertTrue(
      expected.isInstance(actual),
      "Expected instance of ${expected.simpleName} but was ${actual?.let { it::class.simpleName }}",
    )
  }
}

internal class BooleanSubject(private val actual: Boolean?) : Subject(actual) {
  fun isTrue() {
    assertEquals(true, actual)
  }

  fun isFalse() {
    assertEquals(false, actual)
  }
}

internal class FloatSubject(private val actual: Float?) : Subject(actual) {
  fun isWithin(tolerance: Float): FloatToleranceCheck = FloatToleranceCheck(actual, tolerance)

  fun isWithin(tolerance: Double): FloatToleranceCheck =
    FloatToleranceCheck(actual, tolerance.toFloat())

  fun isGreaterThan(expected: Float) {
    val value = assertNotNull(actual)
    assertTrue(value > expected, "Expected <$value> to be greater than <$expected>")
  }
}

internal class DoubleSubject(private val actual: Double?) : Subject(actual) {
  fun isWithin(tolerance: Double): DoubleToleranceCheck = DoubleToleranceCheck(actual, tolerance)

  fun isGreaterThan(expected: Double) {
    val value = assertNotNull(actual)
    assertTrue(value > expected, "Expected <$value> to be greater than <$expected>")
  }
}

internal class IntSubject(private val actual: Int?) : Subject(actual) {
  fun isGreaterThan(expected: Int) {
    val value = assertNotNull(actual)
    assertTrue(value > expected, "Expected <$value> to be greater than <$expected>")
  }
}

internal class IterableSubject(private val actual: Iterable<*>?) : Subject(actual) {
  fun hasSize(expectedSize: Int) {
    val list = assertNotNull(actual).toList()
    assertEquals(expectedSize, list.size)
  }

  fun isEmpty() {
    val list = assertNotNull(actual).toList()
    assertTrue(list.isEmpty(), "Expected empty iterable but was $list")
  }

  fun isNotEmpty() {
    val list = assertNotNull(actual).toList()
    assertTrue(list.isNotEmpty(), "Expected non-empty iterable")
  }

  fun containsExactly(vararg expected: Any?): Ordered {
    return containsExactlyElementsIn(expected.toList())
  }

  fun containsExactlyElementsIn(expected: Iterable<*>): Ordered {
    val actualList = assertNotNull(actual).toList()
    val expectedList = expected.toList()
    assertEquals(expectedList.size, actualList.size, "Size mismatch: $actualList vs $expectedList")
    assertEquals(
      expectedList.groupingBy { it }.eachCount(),
      actualList.groupingBy { it }.eachCount(),
      "Elements mismatch: $actualList vs $expectedList",
    )
    return Ordered { assertEquals(expectedList, actualList) }
  }
}

internal class MapSubject(private val actual: Map<*, *>?) : Subject(actual) {
  fun isEmpty() {
    val map = assertNotNull(actual)
    assertTrue(map.isEmpty(), "Expected empty map but was $map")
  }

  fun containsKey(key: Any?) {
    val map = assertNotNull(actual)
    assertTrue(key in map, "Expected map $map to contain key <$key>")
  }

  fun containsExactly(vararg keyValues: Any?): Ordered {
    require(keyValues.size % 2 == 0) { "Expected even number of key/value args" }
    val actualMap = assertNotNull(actual)
    val expectedPairs = (0 until keyValues.size step 2).map { keyValues[it] to keyValues[it + 1] }
    assertEquals(expectedPairs.toMap(), actualMap)
    return Ordered { assertEquals(expectedPairs, actualMap.entries.map { it.key to it.value }) }
  }
}

internal class AssertionMessageBuilder(@Suppress("UNUSED_PARAMETER") private val message: String) {
  fun that(actual: Boolean?): BooleanSubject = assertThat(actual)

  fun that(actual: Float?): FloatSubject = assertThat(actual)

  fun that(actual: Double?): DoubleSubject = assertThat(actual)

  fun that(actual: Int?): IntSubject = assertThat(actual)

  fun that(actual: Iterable<*>?): IterableSubject = assertThat(actual)

  fun that(actual: Map<*, *>?): MapSubject = assertThat(actual)

  fun that(actual: Any?): Subject = assertThat(actual)
}

internal fun assertWithMessage(message: String): AssertionMessageBuilder =
  AssertionMessageBuilder(message)

internal fun assertThat(actual: Boolean?): BooleanSubject = BooleanSubject(actual)

internal fun assertThat(actual: Float?): FloatSubject = FloatSubject(actual)

internal fun assertThat(actual: Double?): DoubleSubject = DoubleSubject(actual)

internal fun assertThat(actual: Int?): IntSubject = IntSubject(actual)

internal fun assertThat(actual: Iterable<*>?): IterableSubject = IterableSubject(actual)

internal fun assertThat(actual: Map<*, *>?): MapSubject = MapSubject(actual)

internal fun assertThat(actual: Any?): Subject = Subject(actual)

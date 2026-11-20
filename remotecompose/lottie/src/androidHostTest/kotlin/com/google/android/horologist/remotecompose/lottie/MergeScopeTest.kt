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

import android.graphics.Bitmap
import android.graphics.Canvas
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test

/** Greedy merge operands retain authored order and each group's compound path boundary. */
class MergeScopeTest : FixtureParityHarness() {
  private val a
    get() = rectangle(animated("[28,28]", "[36,28]"), fixed("[32,32]"))

  private val b
    get() = rectangle(fixed("[22,28]"), fixed("[12,44]"))

  private val c
    get() = rectangle(fixed("[40,28]"), fixed("[12,44]"))

  private val fill
    get() = redFill.replace("\"r\":1", "\"r\":2")

  private fun group(content: String) =
    """{"ty":"gr","it":[${if(content.isEmpty()) "" else "$content,"}{"ty":"tr","a":${fixed("[0,0]")},"p":${fixed("[2,2]")},"s":${fixed("[90,90]")},"r":${fixed("0")},"o":${fixed("100")}}]}"""

  private fun merge(mode: Int) = """{"ty":"mm","mm":$mode}"""

  private fun verify(name: String, shapes: String, moving: Boolean = true) {
    val json = animation(shapes).replace("\"op\":40", "\"op\":10.01")
    compare(
      json,
      "mergescope_$name",
      animated = moving,
      enableMergePaths = true,
      maxForegroundError = 0.02,
      referenceJson = bakedReference(json),
    )
  }

  // MergePathsContent v6.7.1 mutates cached child paths when applying group matrices. Baking
  // this fixture's uniform transforms avoids cumulative reference distortion. RC still gets
  // the original grouped JSON. This is independent scalar arithmetic, not production helpers.
  private fun bakedReference(json: String): String {
    fun property(value: JsonElement, scale: Double, translate: Double): JsonElement {
      val p = value.jsonObject
      fun coordinates(v: JsonElement) =
        JsonArray(v.jsonArray.map { JsonPrimitive(it.jsonPrimitive.double * scale + translate) })
      val k =
        if (p.getValue("a").jsonPrimitive.content == "0") coordinates(p.getValue("k"))
        else
          JsonArray(
            p.getValue("k").jsonArray.map { keyframe ->
              JsonObject(
                keyframe.jsonObject.mapValues { (key, value) ->
                  if (key == "s" || key == "e") coordinates(value) else value
                }
              )
            }
          )
      return JsonObject(p + ("k" to k))
    }
    fun shapes(values: JsonArray, scale: Double, translate: Double): JsonArray =
      JsonArray(
        values.map { value ->
          val shape = value.jsonObject
          when (shape["ty"]?.jsonPrimitive?.content) {
            "gr" -> {
              val children = shape.getValue("it").jsonArray
              // Every group authored by group() has the same uniform scale/translation.
              JsonObject(
                shape +
                  ("it" to
                    shapes(
                      JsonArray(
                        children.filter { it.jsonObject["ty"]?.jsonPrimitive?.content != "tr" }
                      ),
                      scale * 0.9,
                      translate + scale * 2,
                    ))
              )
            }
            "rc" ->
              JsonObject(
                shape +
                  mapOf(
                    "p" to property(shape.getValue("p"), scale, translate),
                    "s" to property(shape.getValue("s"), scale, 0.0),
                  )
              )
            else -> shape
          }
        }
      )
    val root = Json.parseToJsonElement(json).jsonObject
    val layers =
      JsonArray(
        root.getValue("layers").jsonArray.map { value ->
          val layer = value.jsonObject
          JsonObject(layer + ("shapes" to shapes(layer.getValue("shapes").jsonArray, 1.0, 0.0)))
        }
      )
    return JsonObject(root + ("layers" to layers)).toString()
  }

  @Test
  fun pinnedReferenceMutatesCachedGroupPathsAcrossIdenticalDraws() {
    val json = animation("${group(a)},${group("$b,$c")},${merge(2)},$fill")
    fun drawable(input: String) =
      LottieDrawable().apply {
        enableMergePathsForKitKatAndAbove(true)
        setComposition(checkNotNull(LottieCompositionFactory.fromJsonStringSync(input, null).value))
        setBounds(0, 0, 64, 64)
        progress = 0.5f
      }
    fun pixels(drawable: LottieDrawable): IntArray {
      val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)
      canvas.drawColor(android.graphics.Color.WHITE)
      drawable.draw(canvas)
      return IntArray(4096).also {
        bitmap.getPixels(it, 0, 64, 0, 0, 64, 64)
        bitmap.recycle()
      }
    }
    val original = drawable(json)
    val baked = drawable(bakedReference(json))
    val first = pixels(original)
    val expected = pixels(baked)
    // The transformed original initially agrees, but another draw changes its cached paths.
    assertThat(first.indices.count { first[it] != expected[it] }).isLessThan(20)
    val repeated = pixels(original)
    assertThat(first.indices.count { first[it] != repeated[it] }).isGreaterThan(100)
    assertThat(pixels(baked).toList()).isEqualTo(expected.toList())
  }

  @Test fun groupedUnion() = verify("union", "${group(a)},${group("$b,$c")},${merge(2)},$fill")

  @Test
  fun lastCompoundGroupIsSingleSubtractOperand() =
    verify("subtract-group-last", "$a,${group("$b,$c")},${merge(3)},$fill")

  @Test
  fun firstGroupDoesNotMoveToLastOperand() =
    verify("subtract-group-first", "${group(a)},$b,$c,${merge(3)},$fill")

  @Test
  fun interleavedGroupsKeepOperandOrder() =
    verify("subtract-interleaved", "${group(a)},$b,${group(c)},${merge(3)},$fill")

  @Test
  fun compoundGroupIntersection() = verify("intersect", "$a,${group("$b,$c")},${merge(4)},$fill")

  @Test fun compoundGroupXor() = verify("xor", "$a,${group("$b,$c")},${merge(5)},$fill")

  @Test
  fun nestedCompoundGroupIntersection() =
    verify("nested", "${group("$a,${group("$b,$c")},${merge(4)}")},$fill")

  @Test
  fun mergeConsumesEarlierPaintedGroup() =
    verify("owned-group", "${group("$a,$fill")},$b,${merge(3)},$fill")

  @Test
  fun mergeConsumesEarlierDirectPaint() = verify("earlier-paint", "$a,$fill,$b,${merge(3)},$fill")

  @Test
  fun paintBeforeMergeDoesNotPaintConsumedPaths() =
    verify(
      "paint-before",
      "$a,$b,$fill,${merge(3)},${rectangle(fixed("[56,56]"),fixed("[8,8]"))},$fill",
    )

  @Test
  fun singleSubtractOperandProducesEmptyPath() =
    verify(
      "single-subtract",
      "$a,${merge(3)},${rectangle(fixed("[56,56]"),fixed("[8,8]"))},$fill",
      false,
    )

  @Test
  fun hiddenMergeConsumesAndHidesItsOperands() =
    verify(
      "hidden",
      "$a,$b,{\"ty\":\"mm\",\"mm\":2,\"hd\":true},${rectangle(fixed("[56,56]"),fixed("[8,8]"))},$fill",
      false,
    )

  @Test
  fun singleIntersectionOperandProducesEmptyPath() =
    verify(
      "single-intersect",
      "$a,${merge(4)},${rectangle(fixed("[56,56]"),fixed("[8,8]"))},$fill",
      false,
    )

  @Test
  fun emptyLastGroupRemainsAnIntersectionOperand() =
    verify(
      "empty-group",
      "$a,${group("")},${merge(4)},${rectangle(fixed("[56,56]"),fixed("[8,8]"))},$fill",
      false,
    )

  @Test
  fun staticCompoundGroupSubtract() =
    verify(
      "static-subtract",
      "${rectangle(fixed("[28,28]"),fixed("[32,32]"))},${group("$b,$c")},${merge(3)},$fill",
      false,
    )

  @Test
  fun simpleMergeConcatenatesGroupedContours() =
    verify("concatenate", "$a,${group("$b,$c")},${merge(1)},$fill")
}

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

package com.google.android.horologist.remotecompose.lottie.format

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A graphic element in a Lottie animation.
 *
 * Graphic elements are the building blocks of a Lottie animation. They can be shapes (which get
 * rendered to screen), styles (which control the look of shapes - e.g. the fill color), or grouping
 * mechanisms (including transforms).
 */
@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class GraphicElement {
  abstract val name: String?
  abstract val hidden: Boolean?
  abstract val type: ShapeType

  // Shapes

  /** Draw a path following a bezier curve. */
  @JsonClass(generateAdapter = true)
  data class Path(
    @param:Json(name = "nm") override val name: String? = "",
    @param:Json(name = "hd") override val hidden: Boolean? = false,
    @param:Json(name = "ty") override val type: ShapeType = ShapeType.Path,
    @param:Json(name = "ks") val shape: BaseBezierProperty,
  ) : GraphicElement()

  // Grouping

  /** A group of other graphic elements. This allows transforms to be nested. */
  @JsonClass(generateAdapter = true)
  data class Group(
    @param:Json(name = "nm") override val name: String? = "",
    @param:Json(name = "hd") override val hidden: Boolean? = false,
    @param:Json(name = "ty") override val type: ShapeType = ShapeType.Group,
    @param:Json(name = "np") val numberOfProperties: Int,
    @param:Json(name = "it") val shapes: List<GraphicElement>,
  ) : GraphicElement()

  /**
   * A transform that can be applied to other graphic elements. Transforms must always be in a
   * Group, and must always be the last element in the array.
   */
  @JsonClass(generateAdapter = true)
  data class Transform(
    @param:Json(name = "nm") override val name: String? = "",
    @param:Json(name = "hd") override val hidden: Boolean? = false,
    @param:Json(name = "ty") override val type: ShapeType = ShapeType.Transform,
    @param:Json(name = "a") val anchorPoint: StaticPositionProperty,
    @param:Json(name = "p")
    val positionTranslation: StaticPositionProperty =
      StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @param:Json(name = "r") val rotation: StaticScalarProperty = StaticScalarProperty(value = 0f),
    @param:Json(name = "s")
    val scale: BaseVectorProperty = StaticVectorProperty(value = floatArrayOf(100f, 100f)),
    @param:Json(name = "o") val opacity: StaticScalarProperty = StaticScalarProperty(value = 100f),
  ) : GraphicElement()

  // Styles

  /** Solid fill color */
  @JsonClass(generateAdapter = true)
  data class Fill(
    @param:Json(name = "nm") override val name: String? = "",
    @param:Json(name = "hd") override val hidden: Boolean? = false,
    @param:Json(name = "ty") override val type: ShapeType = ShapeType.Fill,
    @param:Json(name = "o") val opacity: StaticScalarProperty = StaticScalarProperty(value = 100f),
    @param:Json(name = "c") val color: StaticColorProperty,
  ) : GraphicElement()
}

enum class ShapeType(val value: String) {
  Fill("fl"),
  Group("gr"),
  Path("sh"),
  Transform("tr");

  companion object {
    fun fromValueOrNull(value: String): ShapeType? {
      return values().firstOrNull { it.value == value }
    }
  }
}

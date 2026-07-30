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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A graphic element in a Lottie animation.
 *
 * Graphic elements are the building blocks of a Lottie animation. They can be shapes (which get
 * rendered to screen), styles (which control the look of shapes - e.g. the fill color), or grouping
 * mechanisms (including transforms).
 */
@Serializable(with = GraphicElementSerializer::class)
sealed class GraphicElement {
  abstract val name: String?
  abstract val hidden: Boolean?
  abstract val type: ShapeType

  // Shapes

  /** Draw a path following a bezier curve. */
  @Serializable
  data class Path(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.Path,
    @SerialName("ks") val shape: BaseBezierProperty,
  ) : GraphicElement()

  // Grouping

  /** A group of other graphic elements. This allows transforms to be nested. */
  @Serializable
  data class Group(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.Group,
    @SerialName("np") val numberOfProperties: Int? = null,
    @SerialName("it") val shapes: List<GraphicElement>,
  ) : GraphicElement()

  /**
   * A transform that can be applied to other graphic elements. Transforms must always be in a
   * Group, and must always be the last element in the array.
   */
  @Serializable
  data class Transform(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.Transform,
    @SerialName("a") val anchorPoint: StaticPositionProperty,
    @SerialName("p")
    val positionTranslation: StaticPositionProperty =
      StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("r") val rotation: StaticScalarProperty = StaticScalarProperty(value = 0f),
    @SerialName("s")
    val scale: BaseVectorProperty = StaticVectorProperty(value = floatArrayOf(100f, 100f)),
    @SerialName("o") val opacity: StaticScalarProperty = StaticScalarProperty(value = 100f),
  ) : GraphicElement()

  // Styles

  /** Solid fill color */
  @Serializable
  data class Fill(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.Fill,
    @SerialName("o") val opacity: StaticScalarProperty = StaticScalarProperty(value = 100f),
    @SerialName("c") val color: StaticColorProperty,
  ) : GraphicElement()
}

@Serializable(with = ShapeTypeSerializer::class)
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

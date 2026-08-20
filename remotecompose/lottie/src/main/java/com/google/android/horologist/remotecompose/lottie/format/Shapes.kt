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
internal sealed class GraphicElement {
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

  /** A rectangle parametric shape. */
  @Serializable
  data class Rectangle(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.Rectangle,
    @SerialName("d") val direction: Int? = null,
    @SerialName("p")
    val position: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("s")
    val size: BaseVectorProperty = StaticVectorProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("r") val cornerRadius: BaseScalarProperty = StaticScalarProperty(value = 0f),
  ) : GraphicElement()

  /** An ellipse parametric shape. */
  @Serializable
  data class Ellipse(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.Ellipse,
    @SerialName("d") val direction: Int? = null,
    @SerialName("p")
    val position: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("s")
    val size: BaseVectorProperty = StaticVectorProperty(value = floatArrayOf(0f, 0f)),
  ) : GraphicElement()

  /** A polystar (star or regular polygon) parametric shape. */
  @Serializable
  data class PolyStar(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.PolyStar,
    @SerialName("sy") val starType: PolyStarType = PolyStarType.Star,
    @SerialName("pt") val points: BaseScalarProperty = StaticScalarProperty(value = 5f),
    @SerialName("p")
    val position: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("r") val rotation: BaseScalarProperty = StaticScalarProperty(value = 0f),
    @SerialName("or") val outerRadius: BaseScalarProperty = StaticScalarProperty(value = 0f),
    @SerialName("os") val outerRoundedness: BaseScalarProperty = StaticScalarProperty(value = 0f),
    @SerialName("ir") val innerRadius: BaseScalarProperty? = null,
    @SerialName("is") val innerRoundedness: BaseScalarProperty? = null,
    @SerialName("d") val direction: Int? = null,
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
    @SerialName("a")
    val anchorPoint: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("p")
    val positionTranslation: BasePositionProperty =
      StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("r") val rotation: BaseScalarProperty = StaticScalarProperty(value = 0f),
    @SerialName("s")
    val scale: BaseVectorProperty = StaticVectorProperty(value = floatArrayOf(100f, 100f)),
    @SerialName("o") val opacity: BaseScalarProperty = StaticScalarProperty(value = 100f),
  ) : GraphicElement()

  // Styles

  /** Solid fill color */
  @Serializable
  data class Fill(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.Fill,
    @SerialName("o") val opacity: BaseScalarProperty = StaticScalarProperty(value = 100f),
    @SerialName("c") val color: BaseColorProperty,
  ) : GraphicElement()

  /** Gradient fill */
  @Serializable
  data class GradientFill(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.GradientFill,
    @SerialName("o") val opacity: BaseScalarProperty = StaticScalarProperty(value = 100f),
    @SerialName("r") val fillRule: Int? = 1,
    @SerialName("s")
    val startPoint: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("e")
    val endPoint: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("t") val gradientType: Int = 1,
    @SerialName("g") val gradient: BaseGradientProperty,
    @SerialName("h") val highlightLength: BaseScalarProperty? = null,
    @SerialName("a") val highlightAngle: BaseScalarProperty? = null,
  ) : GraphicElement()

  /** Gradient stroke */
  @Serializable
  data class GradientStroke(
    @SerialName("nm") override val name: String? = "",
    @SerialName("hd") override val hidden: Boolean? = false,
    @SerialName("ty") override val type: ShapeType = ShapeType.GradientStroke,
    @SerialName("o") val opacity: BaseScalarProperty = StaticScalarProperty(value = 100f),
    @SerialName("w") val strokeWidth: BaseScalarProperty = StaticScalarProperty(value = 1f),
    @SerialName("s")
    val startPoint: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("e")
    val endPoint: BasePositionProperty = StaticPositionProperty(value = floatArrayOf(0f, 0f)),
    @SerialName("t") val gradientType: Int = 1,
    @SerialName("g") val gradient: BaseGradientProperty,
    @SerialName("h") val highlightLength: BaseScalarProperty? = null,
    @SerialName("a") val highlightAngle: BaseScalarProperty? = null,
    @SerialName("lc") val lineCap: Int? = 1,
    @SerialName("lj") val lineJoin: Int? = 1,
    @SerialName("ml") val miterLimit: Float? = 4f,
  ) : GraphicElement()
}

@Serializable(with = ShapeTypeSerializer::class)
internal enum class ShapeType(val value: String) {
  Ellipse("el"),
  Fill("fl"),
  GradientFill("gf"),
  GradientStroke("gs"),
  Group("gr"),
  Path("sh"),
  PolyStar("sr"),
  Rectangle("rc"),
  Transform("tr");

  companion object {
    fun fromValueOrNull(value: String): ShapeType? {
      return values().firstOrNull { it.value == value }
    }
  }
}

@Serializable(with = PolyStarTypeSerializer::class)
internal enum class PolyStarType(val value: Int) {
  Star(1),
  Polygon(2);

  companion object {
    fun fromValueOrNull(value: Int): PolyStarType? {
      return values().firstOrNull { it.value == value }
    }
  }
}

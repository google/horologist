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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseGradientProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Shape Element representing a gradient outline stroke, conforming to
 * [Lottie Gradient Stroke](https://lottie.github.io/lottie-spec/latest/specs/shapes/#gradient-stroke),
 * [Base Stroke](https://lottie.github.io/lottie-spec/latest/specs/shapes/#base-stroke), and
 * [Base Gradient](https://lottie.github.io/lottie-spec/latest/specs/shapes/#base-gradient).
 *
 * Schema Specification:
 * - Required Fields: "ty" (const "gs"), "o" (Opacity), "w" (Stroke width), "g" (Colors), "s" (Start
 *   point), "e" (End point), "t" (Gradient type).
 * - Optional Fields with Schema Defaults:
 *     - "lc": Line cap (schema default: 2 -> [LineCap.Round]).
 *     - "lj": Line join (schema default: 2 -> [LineJoin.Round]).
 *     - "ml": Numeric miter limit (schema default: 0 -> 0f).
 * - Optional Fields without Schema Defaults:
 *     - "nm": Human-readable name (default: null).
 *     - "hd": Hidden boolean flag (default: null).
 *     - "ml2": Animatable miter limit (default: null).
 *     - "d": Dash pattern array (default: null).
 *     - "h": Highlight length (default: null).
 *     - "a": Highlight angle (default: null).
 *
 * @property name Human-readable element name.
 * @property hidden When true, suppresses rendering of this stroke.
 * @property type Shape type discriminator, strictly [ShapeType.GradientStroke].
 * @property opacity Animatable stroke opacity on [0.0, 100.0]. Required in schema.
 * @property strokeWidth Animatable stroke width. Required in schema.
 * @property colors Gradient stops and color definitions. Required in schema.
 * @property startPoint Starting point coordinate for the gradient. Required in schema.
 * @property endPoint Ending point coordinate for the gradient. Required in schema.
 * @property gradientType Type of gradient (linear or radial). Required in schema.
 * @property lineCap Style at the end of stroked lines. Defaults to [LineCap.Round] per schema.
 * @property lineJoin Style at sharp corners of stroked lines. Defaults to [LineJoin.Round] per
 *   schema.
 * @property miterLimit Maximum miter limit before beveling. Defaults to 0f per schema.
 * @property miterLimitAnimatable Animatable scalar alternative to miterLimit.
 * @property dashes Optional list of dash segments, gaps, and offsets.
 * @property highlightLength Radial highlight length as a percentage between start and end points.
 * @property highlightAngle Radial highlight angle in clockwise degrees.
 */
@Serializable
internal data class GradientStroke(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.GradientStroke,
  @SerialName("o") override val opacity: BaseScalarProperty,
  @SerialName("w") val strokeWidth: BaseScalarProperty,
  @SerialName("g") val colors: BaseGradientProperty,
  @SerialName("s") val startPoint: BasePositionProperty,
  @SerialName("e") val endPoint: BasePositionProperty,
  @SerialName("t") val gradientType: GradientType,
  @SerialName("lc") val lineCap: LineCap = LineCap.Round,
  @SerialName("lj") val lineJoin: LineJoin = LineJoin.Round,
  @SerialName("ml") val miterLimit: Float = 0f,
  @SerialName("ml2") val miterLimitAnimatable: BaseScalarProperty? = null,
  @SerialName("d") val dashes: List<StrokeDash>? = null,
  @SerialName("h") val highlightLength: BaseScalarProperty? = null,
  @SerialName("a") val highlightAngle: BaseScalarProperty? = null,
) : ShapeStyle

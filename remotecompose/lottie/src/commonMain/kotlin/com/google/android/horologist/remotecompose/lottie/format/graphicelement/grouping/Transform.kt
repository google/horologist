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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping

import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Group transform element applying affine spatial transformations to surrounding graphic elements,
 * conforming to
 * [Lottie Transform Shape](https://lottie.github.io/lottie-spec/latest/specs/shapes/#transform)
 * (`#/$defs/shapes/transform`).
 *
 * Essential Invariants:
 * - Mathematical Order of Application: Transformation components are concatenated to form the local
 *   matrix M using standard 2D affine composition: M = Translate(p) * Rotate(r) * Skew(sk, sa) *
 *   Scale(s) * Translate(-a) Points transformed by M are first shifted by the negative anchor point
 *   (-a), scaled, skewed, rotated, and finally translated by the position offset (p).
 * - Opacity Concatenation: Opacity scales the alpha channel of all preceding elements within the
 *   current group scope: Alpha_effective = Alpha_element * (opacity / 100.0).
 * - Shape Group Placement: Within a shape [Group], Transform must be the trailing element in the
 *   child shapes array.
 */
@Serializable
internal data class Transform(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.Transform,
  @SerialName("a")
  val anchorPoint: BasePositionProperty = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
  @SerialName("p")
  val positionTranslation: BasePositionProperty =
    StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
  @SerialName("r") val rotation: BaseScalarProperty = StaticScalarProperty(value = 0f.rf),
  @SerialName("rz") val rz: BaseScalarProperty? = null,
  @SerialName("s")
  val scale: BaseVectorProperty = StaticVectorProperty(value = listOf(100f.rf, 100f.rf)),
  @SerialName("o") val opacity: BaseScalarProperty = StaticScalarProperty(value = 100f.rf),
  @SerialName("sk") val skew: BaseScalarProperty? = null,
  @SerialName("sa") val skewAxis: BaseScalarProperty? = null,
) : GraphicElement {
  val effectiveRotation: BaseScalarProperty
    get() = rz ?: rotation
}

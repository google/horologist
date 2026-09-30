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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers

import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Repeater affine transform with independent start/end opacity percentages (`so`/`eo`). Unlike a
 * layer transform it does not require `o`. [legacyOpacity] retains compatibility with older inputs
 * accepted by this renderer and supplies only missing ramp endpoints; otherwise endpoints are 100%.
 */
@Serializable
internal data class RepeaterTransform(
  @SerialName("a")
  val anchorPoint: BasePositionProperty = StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
  @SerialName("p")
  val positionTranslation: BasePositionProperty =
    StaticPositionProperty(value = Point(0f.rf, 0f.rf)),
  @SerialName("r") val rotation: BaseScalarProperty = StaticScalarProperty(value = 0f.rf),
  @SerialName("s")
  val scale: BaseVectorProperty =
    StaticVectorProperty(animated = false.rb, value = listOf(100f.rf, 100f.rf)),
  @SerialName("so") val startOpacity: BaseScalarProperty? = null,
  @SerialName("eo") val endOpacity: BaseScalarProperty? = null,
  @SerialName("o") val legacyOpacity: BaseScalarProperty? = null,
  @SerialName("sk") val skew: BaseScalarProperty? = null,
  @SerialName("sa") val skewAxis: BaseScalarProperty? = null,
) {
  /** Returns the geometry transform; the repeater renderer applies the opacity ramp per copy. */
  fun toTransform(): Transform =
    Transform(
      anchorPoint = anchorPoint,
      positionTranslation = positionTranslation,
      rotation = rotation,
      scale = scale,
      opacity = legacyOpacity ?: StaticScalarProperty(value = 100f.rf),
      skew = skew,
      skewAxis = skewAxis,
    )
}

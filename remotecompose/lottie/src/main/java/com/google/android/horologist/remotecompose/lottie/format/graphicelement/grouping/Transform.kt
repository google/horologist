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

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticPositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A transform that can be applied to other graphic elements. Transforms must always be in a Group,
 * and must always be the last element in the array.
 */
@Serializable
internal data class Transform(
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
) : GraphicElement

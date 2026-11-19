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

package com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.ShapeType
import com.google.android.horologist.remotecompose.lottie.format.properties.BasePositionProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Parametric ellipse shape conforming to
 * [Ellipse Shape](https://lottie.github.io/lottie-spec/latest/specs/shapes/#ellipse).
 *
 * Defined parametrically by its center position [position] and total size [size] (horizontal and
 * vertical diameter).
 *
 * Schema Specification:
 * - Required Fields: `"ty"` (`"el"`), `"p"` (position), `"s"` (size).
 * - Optional Fields without Schema Default:
 *     - `"nm"` (name, default: `null`)
 *     - `"hd"` (hidden flag, default: `null`)
 *     - `"d"` (shape direction, default: `null`)
 *
 * Invariants:
 * - [position]: Center coordinates of the ellipse. Required; no schema default.
 * - [size]: Vector `[width, height]` defining diameter. Required; no schema default.
 * - [direction]: Drawing direction (`"d"`). Nullable when omitted.
 */
@Serializable
internal data class Ellipse(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.Ellipse,
  @SerialName("d") override val direction: Int? = null,
  @SerialName("p") val position: BasePositionProperty,
  @SerialName("s") val size: BaseVectorProperty,
) : GeometryShape

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
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Parametric rectangle shape conforming to
 * [Rectangle Shape](https://lottie.github.io/lottie-spec/latest/specs/shapes/#rectangle).
 *
 * Represents an axis-aligned rectangle with optional rounded corners, centered at [position].
 *
 * Schema Specification:
 * - Required Fields: `"ty"` (`"rc"`), `"p"` (position), `"s"` (size).
 * - Optional Fields without Schema Default:
 *     - `"r"` (corner roundness, default: `null`)
 *     - `"nm"` (name, default: `null`)
 *     - `"hd"` (hidden flag, default: `null`)
 *     - `"d"` (shape direction, default: `null`)
 *
 * Invariants:
 * - [position]: Center coordinates `[x, y]`. Required; no schema default.
 * - [size]: Total dimensions `[width, height]`. Required; no schema default.
 * - [cornerRadius]: Corner rounding radius (`"r"`). Optional; no schema default. Nullable when
 *   omitted.
 * - [direction]: Drawing direction (`"d"`). Nullable when omitted.
 */
@Serializable
internal data class Rectangle(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.Rectangle,
  @SerialName("d") override val direction: Int? = null,
  @SerialName("p") val position: BasePositionProperty,
  @SerialName("s") val size: BaseVectorProperty,
  @SerialName("r") val cornerRadius: BaseScalarProperty? = null,
) : GeometryShape

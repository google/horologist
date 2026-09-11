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
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseBezierProperty
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Freeform Bézier path shape conforming to
 * [Path Shape](https://lottie.github.io/lottie-spec/latest/specs/shapes/#path).
 *
 * Represents an explicit cubic Bézier curve containing ordered vertices, in-tangent control
 * handles, out-tangent control handles, and a closed flag.
 *
 * Schema Specification:
 * - Required Fields: `"ty"` (`"sh"`), `"ks"` (Bézier property).
 * - Optional Fields without Schema Default:
 *     - `"nm"` (name, default: `null`)
 *     - `"hd"` (hidden flag, default: `null`)
 *     - `"d"` (shape direction, default: `null`)
 *
 * Invariants:
 * - [shape]: Animatable Bézier curve geometry ([BaseBezierProperty]). Required; no schema default.
 * - [direction]: Drawing direction (`"d"`). Nullable when omitted.
 */
@Serializable
internal data class Path(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.Path,
  @SerialName("d") override val direction: Int? = null,
  @SerialName("ks") val shape: BaseBezierProperty,
) : GeometryShape

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
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteFloat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A group of graphic elements providing isolated styling and transformation scoping, conforming to
 * [Lottie Group](https://lottie.github.io/lottie-spec/latest/specs/shapes/#group)
 * (`#/$defs/shapes/group`).
 *
 * Essential Invariants:
 * - Scoping Boundary: A Group acts as an isolated visual and spatial container. Shapes defined
 *   within [shapes] are styled exclusively by styles preceding them within the same group, and
 *   transformed by the group's trailing [Transform] element.
 * - Array Ordering: The items array [shapes] is evaluated in reverse order during rendering:
 *   trailing transforms apply to all preceding elements, and styles apply to all preceding geometry
 *   shapes.
 * - Transform Locality: A Group must contain at most one trailing [Transform] element. If present,
 *   it must be the final entry in [shapes].
 * - Nested Hierarchy: Groups may be nested arbitrarily to compose hierarchical transformation
 *   matrices and compound vector shapes.
 *
 * Schema Specification:
 * - Required Fields:
 *     - `"ty"`: Shape type discriminator, constantly `"gr"`.
 *     - `"it"`: Ordered array of child graphic elements.
 * - Optional Fields without Schema Defaults:
 *     - `"nm"`: Display name of the group.
 *     - `"hd"`: Hidden boolean flag suppressing rendering.
 *     - `"np"`: Number of properties within the group.
 *
 * @property name Human readable name of the group.
 * @property hidden When true, suppresses rendering of this group and all its children.
 * @property type Shape discriminator, constantly [ShapeType.Group].
 * @property numberOfProperties Number of properties contained in this group.
 * @property shapes Ordered collection of child graphic elements (geometries, styles, and
 *   transform).
 */
@Serializable
internal data class Group(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean? = null,
  @SerialName("ty") override val type: ShapeType = ShapeType.Group,
  @SerialName("np") val numberOfProperties: SerializableRemoteFloat? = null,
  @SerialName("it") val shapes: List<GraphicElement>,
) : GraphicElement

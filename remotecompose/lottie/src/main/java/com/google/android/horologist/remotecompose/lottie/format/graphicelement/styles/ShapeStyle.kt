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

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.properties.BaseScalarProperty

/**
 * Sealed category interface representing all visual styling elements in a Lottie shape hierarchy,
 * conforming to
 * [Lottie Shape Style](https://lottie.github.io/lottie-spec/latest/specs/shapes/#shape-style).
 *
 * Essential Invariants:
 * - Scoping: Styles define the visual appearance (such as fill color, stroke width, or gradients)
 *   of all preceding shape geometries within the current group scope.
 * - Multi-Style Stacking: When multiple styles apply to the same shape, the shape is rendered
 *   repeatedly for each style in reverse array order (bottom-to-top).
 * - opacity: Animatable scalar controlling overall style opacity, normalized on [0.0, 100.0].
 *   Defined as required in the Lottie JSON schema without a default value.
 */
internal sealed interface ShapeStyle : GraphicElement {
  val opacity: BaseScalarProperty
}

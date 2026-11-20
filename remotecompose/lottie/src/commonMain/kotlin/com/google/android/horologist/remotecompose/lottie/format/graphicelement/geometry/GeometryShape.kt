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

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement

/**
 * Sealed category interface for drawable geometry shapes conforming to
 * [Lottie Shapes](https://lottie.github.io/lottie-spec/latest/specs/shapes/#shape).
 *
 * Defines the contour curve geometry without visual styling information.
 *
 * Essential Invariants:
 * - Inherits visual element metadata ([name], [hidden], [type]) from [GraphicElement].
 * - [direction]: Drawing direction of the shape curve (`"d"`), conforming to
 *   [Shape Direction](https://lottie.github.io/lottie-spec/1.0.1/specs/constants/#shape-direction):
 *   `1` for Normal (clockwise), `3` for Reversed (counter-clockwise). Nullable when omitted from
 *   JSON.
 */
internal sealed interface GeometryShape : GraphicElement {
  val direction: Int?
}

/**
 * Drawing direction of a shape curve conforming to
 * [Shape Direction](https://lottie.github.io/lottie-spec/1.0.1/specs/constants/#shape-direction).
 *
 * Values:
 * - [Normal] (`1`): Usually clockwise drawing direction.
 * - [Reversed] (`3`): Usually counter-clockwise drawing direction.
 */
internal enum class ShapeDirection(val value: Int) {
  Normal(1),
  Reversed(3);

  companion object {
    fun fromValueOrNull(value: Int): ShapeDirection? = entries.firstOrNull { it.value == value }
  }
}

/**
 * Resolves the effective [ShapeDirection] for this geometry shape, defaulting to
 * [ShapeDirection.Normal] when [GeometryShape.direction] is null or unrecognized.
 */
internal val GeometryShape.shapeDirection: ShapeDirection
  get() = direction?.let { ShapeDirection.fromValueOrNull(it) } ?: ShapeDirection.Normal

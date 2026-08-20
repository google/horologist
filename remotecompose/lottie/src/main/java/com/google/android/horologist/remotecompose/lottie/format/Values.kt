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

@file:Suppress("TopLevelName") // Matching name in Lottie spec.

package com.google.android.horologist.remotecompose.lottie.format

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A path defined by a set of bezier curves. */
@Serializable
internal data class BezierValue(
  @SerialName("c") val closed: Boolean,
  @SerialName("i") val inTangents: List<FloatArray>,
  @SerialName("o") val outTangents: List<FloatArray>,
  @SerialName("v") val vertices: List<FloatArray>,
)

/**
 * A gradient represented by sequential color and opacity stops in a flattened float array.
 *
 * Each color stop has 4 floats: [offset, r, g, b]. Optional opacity stops have 2 floats:
 * [offset, alpha].
 */
@Serializable(with = GradientValueSerializer::class)
internal data class GradientValue(val numberOfColors: Int, val stops: FloatArray) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as GradientValue
    if (numberOfColors != other.numberOfColors) return false
    if (!stops.contentEquals(other.stops)) return false
    return true
  }

  override fun hashCode(): Int {
    var result = numberOfColors
    result = 31 * result + stops.contentHashCode()
    return result
  }
}

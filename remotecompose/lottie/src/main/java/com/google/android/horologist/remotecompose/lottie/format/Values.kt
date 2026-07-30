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
data class BezierValue(
  @SerialName("c") val closed: Boolean,
  @SerialName("i") val inTangents: List<List<Float>>,
  @SerialName("o") val outTangents: List<List<Float>>,
  @SerialName("v") val vertices: List<List<Float>>,
)

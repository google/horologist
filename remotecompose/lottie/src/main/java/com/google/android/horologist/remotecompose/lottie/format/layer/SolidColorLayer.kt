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

package com.google.android.horologist.remotecompose.lottie.format.layer

import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A layer rendering a solid color rectangle. */
@Serializable
internal data class SolidColorLayer(
  @SerialName("nm") override val name: String? = "",
  @SerialName("hd") override val hidden: Boolean? = false,
  @SerialName("ty") override val type: LayerType = LayerType.Solid,
  @SerialName("ind") override val index: Int? = null,
  @SerialName("parent") override val parent: Int? = null,
  @SerialName("ip") override val startFrame: Int? = null,
  @SerialName("op") override val endFrame: Int? = null,
  @SerialName("ks") override val transform: Transform? = null,
  @SerialName("sc") val solidColor: String = "#000000",
  @SerialName("sw") val solidWidth: Float = 0f,
  @SerialName("sh") val solidHeight: Float = 0f,
) : Layer()

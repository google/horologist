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

import androidx.compose.remote.creation.compose.state.rb
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.mask.Mask
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableHexColor
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteFloat
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A visual layer rendering a solid color rectangle conforming to
 * [Solid Layer](https://lottie.github.io/lottie-spec/1.0.1/specs/layers/#solid-layer).
 *
 * Renders a rectangular region of dimensions [solidWidth] by [solidHeight] filled with
 * [solidColor].
 */
@Serializable
internal data class SolidColorLayer(
  @SerialName("nm") override val name: String? = null,
  @SerialName("hd") override val hidden: SerializableBoolean = false.rb,
  @SerialName("ty") override val type: LayerType = LayerType.Solid,
  @SerialName("ind") override val index: Int? = null,
  @SerialName("parent") override val parent: Int? = null,
  @SerialName("ip") override val startFrame: SerializableRemoteFloat,
  @SerialName("op") override val endFrame: SerializableRemoteFloat,
  @SerialName("ks") override val transform: Transform? = null,
  @SerialName("ao") override val autoOrient: SerializableRemoteBoolean = false.rb,
  @SerialName("tt") override val matteMode: MatteMode = MatteMode.Normal,
  @SerialName("tp") override val matteParent: Int? = null,
  @SerialName("masksProperties") override val masks: List<Mask>? = null,
  @SerialName("sw") val solidWidth: SerializableRemoteInt,
  @SerialName("sh") val solidHeight: SerializableRemoteInt,
  @SerialName("sc") val solidColor: SerializableHexColor,
) : Layer()

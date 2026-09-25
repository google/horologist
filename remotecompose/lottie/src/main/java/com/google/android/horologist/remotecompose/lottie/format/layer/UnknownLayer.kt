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
import androidx.compose.remote.creation.compose.state.rf
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.mask.Mask
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteBoolean
import com.google.android.horologist.remotecompose.lottie.format.values.SerializableRemoteFloat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Safe fallback for unrecognized layer types, preserving transform hierarchy for child layers. */
@Serializable
internal data class UnknownLayer(
  @SerialName("nm") override val name: String? = "",
  @SerialName("hd") override val hidden: SerializableBoolean = false.rb,
  @SerialName("ty") override val type: LayerType = LayerType.Unknown,
  @SerialName("ind") override val index: Int? = null,
  @SerialName("parent") override val parent: Int? = null,
  @SerialName("ip") override val startFrame: SerializableRemoteFloat = 0f.rf,
  @SerialName("op") override val endFrame: SerializableRemoteFloat = Float.MAX_VALUE.rf,
  @SerialName("st") override val startTime: Float? = 0f,
  @SerialName("sr") override val timeStretch: Float? = 1f,
  @SerialName("ks") override val transform: Transform? = null,
  @SerialName("ao") override val autoOrient: SerializableRemoteBoolean = false.rb,
  @SerialName("bm") override val blendMode: BlendMode? = BlendMode.Normal,
  @SerialName("tt") override val matteMode: MatteMode = MatteMode.Normal,
  @SerialName("tp") override val matteParent: Int? = null,
  @SerialName("td") override val matteTarget: Int? = 0,
  @SerialName("ddd") override val is3d: Int? = 0,
  @SerialName("masksProperties") override val masks: List<Mask>? = null,
) : Layer()

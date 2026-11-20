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

package com.google.android.horologist.remotecompose.lottie.format

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A named marker defining a cue point or segment on the animation timeline.
 *
 * @property name The name or comment for this marker.
 * @property time The start frame of the marker.
 * @property duration The duration of the marker in frames.
 */
@Serializable
internal data class Marker(
  @SerialName("cm") val name: String? = "",
  @SerialName("tm") val time: Float? = 0f,
  @SerialName("dr") val duration: Float? = 0f,
)

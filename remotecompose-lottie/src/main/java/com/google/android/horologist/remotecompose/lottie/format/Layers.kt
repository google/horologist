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

import com.google.android.horologist.remotecompose.lottie.format.GraphicElement.Transform
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A layer in a Lottie animation.
 *
 * Layer parenting provides a way for layer transforms to be applied to child layers. This allows
 * for a single set of transforms to be applied to multiple layers.
 *
 * @property name The name of the layer.
 * @property type The type of the layer.
 * @property index The index of the layer.
 * @property parent The parent of the layer.
 */
@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class Layer {
  abstract val name: String?
  abstract val type: LayerType
  abstract val index: Int?
  abstract val parent: Int?
  abstract val transform: Transform?

  /** A layer with no data. Usually used as a parent to apply a transform. */
  @JsonClass(generateAdapter = true)
  data class NullLayer(
    @Json("nm") override val name: String? = "",
    @Json("hd") val hidden: Boolean? = false,
    @Json("ty") override val type: LayerType = LayerType.Null,
    @Json("ind") override val index: Int? = null,
    @Json("parent") override val parent: Int? = null,
    @Json("ip") val startFrame: Int? = null,
    @Json("op") val endFrame: Int? = null,
    @Json("ks") override val transform: Transform? = null,
  ) : Layer()

  /** A layer containing Shapes. */
  @JsonClass(generateAdapter = true)
  data class ShapeLayer(
    @Json("nm") override val name: String? = "",
    @Json("hd") val hidden: Boolean? = false,
    @Json("ty") override val type: LayerType = LayerType.Shape,
    @Json("ind") override val index: Int? = null,
    @Json("parent") override val parent: Int? = null,
    @Json("ip") val startFrame: Int? = null,
    @Json("op") val endFrame: Int? = null,
    @Json("ks") override val transform: Transform? = null,
    @Json("shapes") val shapes: List<GraphicElement>,
  ) : Layer()
}

enum class LayerType(val value: Int) {
  Null(3),
  Shape(4);

  companion object {
    fun fromValueOrNull(value: Int): LayerType? {
      return values().firstOrNull { it.value == value }
    }
  }
}

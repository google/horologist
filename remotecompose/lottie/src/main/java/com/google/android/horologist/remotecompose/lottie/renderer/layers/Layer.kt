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

package com.google.android.horologist.remotecompose.lottie.renderer.layers

import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.runtime.Composable
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.Layer
import com.google.android.horologist.remotecompose.lottie.format.layer.LayerType
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer

/** A Layer in the Lottie composition */
@Composable
@RemoteComposable
internal fun Layer(
  layer: Layer,
  parentTransforms: Map<Int, List<Transform>>,
  transform: Transform?,
) {
  val ancestorStack = parentTransforms[layer.index] ?: emptyList()

  val completeStack =
    if (transform != null) {
      listOf(transform) + ancestorStack
    } else {
      ancestorStack
    }

  when (layer.type) {
    LayerType.Null -> {}
    LayerType.Solid -> SolidColorLayer(layer as SolidColorLayer, completeStack)
    LayerType.Shape -> ShapeLayer(layer as ShapeLayer, completeStack)
    else -> {}
  }
}

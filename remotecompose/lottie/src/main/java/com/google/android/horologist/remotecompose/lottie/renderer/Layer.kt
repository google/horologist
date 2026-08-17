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

package com.google.android.horologist.remotecompose.lottie.renderer

import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.runtime.Composable
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.format.Layer
import com.google.android.horologist.remotecompose.lottie.format.LayerType

/** A Layer in the Lottie composition */
@Composable
@RemoteComposable
internal fun LayerNode(layer: Layer, childrenMap: Map<Int?, List<Layer>>) {
  if (layer.hidden == true) {
    return
  }

  val transform = layer.transform
  val modifier = if (transform != null) {
      transform.toModifier(LocalAnimationSettings.current)
  } else {
      RemoteModifier
  }

  RemoteBox(modifier = modifier, contentAlignment = RemoteAlignment.Center) {
    when (layer.type) {
      LayerType.Null -> {} // No-op - null layers are used to apply parent transforms.
      LayerType.Shape -> ShapeLayer(layer as Layer.ShapeLayer)
    }

    // Recursively add children
    childrenMap[layer.index]?.forEach { childLayer ->
      LayerNode(childLayer, childrenMap)
    }
  }
}

/** A Layer containing Shapes */
@Composable
@RemoteComposable
internal fun ShapeLayer(layer: Layer.ShapeLayer) {
  // TODO: Check start & end frame to see if we should be rendering
  RenderShapes(layer.shapes, emptyList())
}

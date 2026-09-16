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

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.renderer.RenderShapes

/** A Layer containing Shapes */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun ShapeLayer(
  layer: ShapeLayer,
  transformStack: List<Transform> = emptyList(),
  matteContext: MatteContext? = null,
  layerVisibility: RemoteFloat = 1f.rf,
) {
  if (layer.hidden?.constantValue == true) {
    return
  }

  val updatedTransformStack =
    if (layer.transform != null) transformStack + layer.transform else transformStack

  RenderShapes(
    shapes = layer.shapes,
    transformStack = updatedTransformStack,
    matteContext = matteContext,
    layerVisibility = layerVisibility,
    masks = layer.masksProperties,
  )
}

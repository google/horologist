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
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.renderer.RenderShapes

/** A Layer containing Shapes */
@Composable
@RemoteComposable
internal fun ShapeLayer(layer: ShapeLayer, transformStack: List<Transform?>? = null) {
  if (layer.hidden == true) {
    return
  }

  val safeStack = transformStack?.filterNotNull() ?: emptyList()
  val updatedTransformStack =
    if (layer.transform != null) safeStack + layer.transform else safeStack

  // TODO: Check start & end frame to see if we should be rendering
  RenderShapes(layer.shapes, updatedTransformStack)
}

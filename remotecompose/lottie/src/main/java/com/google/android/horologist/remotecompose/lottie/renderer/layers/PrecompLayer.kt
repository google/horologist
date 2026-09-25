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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.buildAncestorTransforms
import com.google.android.horologist.remotecompose.lottie.format.asset.PrecompAsset
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.PrecompLayer
import com.google.android.horologist.remotecompose.lottie.isLayerMatteSource
import com.google.android.horologist.remotecompose.lottie.resolveLayerMatteContext
import com.google.android.horologist.remotecompose.lottie.resolveMatteSourceIndices

/**
 * Renders the referenced precomposition on its local timeline. [transformStack] already includes
 * this layer's transform and is bound to the containing timelines by [Layer].
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun PrecompLayer(
  layer: PrecompLayer,
  transformStack: List<Transform> = emptyList(),
  matteContext: MatteContext? = null,
) {
  if (layer.hidden?.constantValue == true) {
    return
  }

  val animationSettings = LocalAnimationSettings.current
  // Guard against recursion cycles (e.g. self-referencing or cyclic precomps)
  if (layer.refId in animationSettings.activePrecomps) {
    return
  }

  val asset = animationSettings.assets[layer.refId] as? PrecompAsset ?: return
  if (asset.layers.isEmpty()) {
    return
  }

  val childAncestorTransforms =
    remember(asset.layers, transformStack) { buildAncestorTransforms(asset.layers, transformStack) }

  val matteSourceIndices = remember(asset.layers) { resolveMatteSourceIndices(asset.layers) }

  val nextSettings =
    animationSettings.copy(activePrecomps = animationSettings.activePrecomps + layer.refId)

  CompositionLocalProvider(LocalAnimationSettings provides nextSettings) {
    for (i in asset.layers.indices.reversed()) {
      val childLayer = asset.layers[i]
      if (isLayerMatteSource(asset.layers, i, matteSourceIndices)) {
        continue
      }
      val childMatteContext =
        resolveLayerMatteContext(
          layers = asset.layers,
          index = i,
          ancestorTransforms = childAncestorTransforms,
          fallbackTransforms = transformStack,
        )

      Layer(
        layer = childLayer,
        parentTransforms = childAncestorTransforms,
        transform = null,
        matteContext = childMatteContext ?: matteContext,
      )
    }
  }
}

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

import androidx.compose.remote.creation.RemotePath
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer
import com.google.android.horologist.remotecompose.lottie.format.mask.MaskMode
import com.google.android.horologist.remotecompose.lottie.renderer.applyLayerMasks
import com.google.android.horologist.remotecompose.lottie.renderer.applyMatteClip
import com.google.android.horologist.remotecompose.lottie.renderer.inverseTransform
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.transform

/** A Layer rendering a solid color rectangle. */
@Suppress("RestrictedApi") // remote-creation, RemoteCanvas, RemoteDrawScope.remoteCanvas
@Composable
@RemoteComposable
internal fun SolidColorLayer(
  layer: SolidColorLayer,
  transformStack: List<Transform> = emptyList(),
  matteContext: MatteContext? = null,
  layerVisibility: RemoteFloat = 1f.rf,
) {
  val solidWidth = layer.solidWidth.constantValue.toFloat()
  val solidHeight = layer.solidHeight.constantValue.toFloat()
  if (solidWidth <= 0f || solidHeight <= 0f) {
    return
  }

  val animationSettings = LocalAnimationSettings.current
  val updatedTransformStack =
    if (layer.transform != null) transformStack + layer.transform else transformStack

  val layerOpacity =
    (updatedTransformStack.lastOrNull()?.opacity?.let {
      animateScalar(it, animationSettings) / 100f.rf
    } ?: 1f.rf) * layerVisibility

  val color = layer.solidColor
  val paint = RemotePaint { this.color = color.copy(alpha = color.alpha * layerOpacity) }

  val path =
    RemotePath().apply {
      reset()
      moveTo(0f, 0f)
      lineTo(solidWidth, 0f)
      lineTo(solidWidth, solidHeight)
      lineTo(0f, solidHeight)
      close()
    }

  val hasMasks = layer.masksProperties.any { it.mode != MaskMode.None && it.path != null }
  val needsSave = matteContext != null || hasMasks
  val effectSurface = LocalEffectSurface.current

  RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
    remoteCanvas.withContainingSurface(effectSurface) {
      if (needsSave) {
        remoteCanvas.save()
      }

      if (matteContext != null) {
        applyMatteClip(matteContext, animationSettings, remoteCanvas)
      }

      if (hasMasks) {
        for (transform in updatedTransformStack) {
          transform(transform, null, animationSettings, remoteCanvas)
        }
        applyLayerMasks(layer.masksProperties, animationSettings, remoteCanvas)
        for (transform in updatedTransformStack.reversed()) {
          inverseTransform(transform, animationSettings, remoteCanvas)
        }
      }

      for (transform in updatedTransformStack) {
        remoteCanvas.save()
        transform(transform, null, animationSettings, remoteCanvas)
      }

      usePaint(paint) { remoteCanvas.drawPath(path) }

      for (transform in updatedTransformStack) {
        remoteCanvas.restore()
      }

      if (needsSave) {
        remoteCanvas.restore()
      }
    }
  }
}

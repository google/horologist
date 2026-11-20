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

import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.format.asset.ImageAsset
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.ImageLayer
import com.google.android.horologist.remotecompose.lottie.format.mask.MaskMode
import com.google.android.horologist.remotecompose.lottie.renderer.applyLayerMasks
import com.google.android.horologist.remotecompose.lottie.renderer.applyMatteClip
import com.google.android.horologist.remotecompose.lottie.renderer.inverseTransform
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.transform

internal data class DecodedImage(
  val bitmap: RemoteImageBitmap,
  val nativeWidth: Float,
  val nativeHeight: Float,
)

@Composable internal expect fun rememberDecodedImage(asset: ImageAsset): DecodedImage?

/** A Layer rendering an image asset referenced by [ImageLayer.refId]. */
@Suppress("RestrictedApi") // RemoteCanvas, RemoteDrawScope.remoteCanvas, RemoteFloat.div(Float)
@Composable
@RemoteComposable
internal fun ImageLayer(
  layer: ImageLayer,
  transformStack: List<Transform> = emptyList(),
  matteContext: MatteContext? = null,
  layerVisibility: RemoteFloat = 1f.rf,
) {
  if (layer.hidden?.constantValue == true) {
    return
  }

  val animationSettings = LocalAnimationSettings.current
  val asset = animationSettings.assets[layer.refId] as? ImageAsset ?: return

  val decodedImage = rememberDecodedImage(asset) ?: return
  val remoteBitmap = decodedImage.bitmap
  val nativeWidth =
    if (decodedImage.nativeWidth > 0f) decodedImage.nativeWidth else (asset.width ?: 0f)
  val nativeHeight =
    if (decodedImage.nativeHeight > 0f) decodedImage.nativeHeight else (asset.height ?: 0f)

  val updatedTransformStack =
    if (layer.transform != null) transformStack + layer.transform else transformStack

  val layerOpacity =
    (updatedTransformStack.lastOrNull()?.opacity?.let {
      animateScalar(it, animationSettings) / 100f.rf
    } ?: 1f.rf) * layerVisibility

  val imageWidth = asset.width ?: 0f
  val imageHeight = asset.height ?: 0f

  val paint = RemotePaint { this.color = 0xFFFFFFFF.toInt().rc.copy(alpha = layerOpacity) }

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

      if (imageWidth > 0f && imageHeight > 0f) {
        remoteCanvas.drawScaledBitmap(
          bitmap = remoteBitmap,
          srcLeft = 0f.rf,
          srcTop = 0f.rf,
          srcRight = nativeWidth.rf,
          srcBottom = nativeHeight.rf,
          dstLeft = 0f.rf,
          dstTop = 0f.rf,
          dstRight = imageWidth.rf,
          dstBottom = imageHeight.rf,
          scaleType = 0,
          scaleFactor = 1f.rf,
          contentDescription = null,
          paint = paint,
        )
      } else {
        remoteCanvas.drawBitmap(bitmap = remoteBitmap, left = 0f.rf, top = 0f.rf, paint = paint)
      }

      for (transform in updatedTransformStack) {
        remoteCanvas.restore()
      }

      if (needsSave) {
        remoteCanvas.restore()
      }
    }
  }
}

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
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.rb
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
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

/**
 * Decodes an [ImageAsset] into a [DecodedImage] supporting Base64 data URLs, HTTP/HTTPS URLs, and
 * local assets with native bitmap dimensions.
 */
internal fun decodeImageAsset(asset: ImageAsset, context: Context? = null): DecodedImage? {
  val path = asset.path.orEmpty()
  val dir = asset.directory.orEmpty()
  if (path.isEmpty() && dir.isEmpty()) {
    return null
  }

  val fullPath =
    if (
      path.startsWith("/") ||
        path.startsWith("http://", ignoreCase = true) ||
        path.startsWith("https://", ignoreCase = true) ||
        path.startsWith("data:", ignoreCase = true)
    ) {
      path
    } else {
      dir + path
    }

  if (
    fullPath.startsWith("data:image", ignoreCase = true) ||
      fullPath.startsWith("data:", ignoreCase = true) ||
      asset.embedded == 1
  ) {
    try {
      val base64Data = if (fullPath.contains(",")) fullPath.substringAfter(",") else fullPath
      if (base64Data.isBlank()) return null
      val bytes = Base64.decode(base64Data, Base64.DEFAULT)
      if (bytes == null || bytes.isEmpty()) return null
      val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
      if (bitmap != null) {
        return DecodedImage(
          bitmap = bitmap.asImageBitmap().rb,
          nativeWidth = bitmap.width.toFloat(),
          nativeHeight = bitmap.height.toFloat(),
        )
      }
    } catch (_: Exception) {
      return null
    }
    return null
  }

  if (
    fullPath.startsWith("http://", ignoreCase = true) ||
      fullPath.startsWith("https://", ignoreCase = true)
  ) {
    val declaredWidth = asset.width ?: 0f
    val declaredHeight = asset.height ?: 0f
    return DecodedImage(
      bitmap = RemoteImageBitmap(fullPath),
      nativeWidth = declaredWidth,
      nativeHeight = declaredHeight,
    )
  }

  if (context != null && fullPath.isNotEmpty()) {
    try {
      context.assets.open(fullPath).use { stream ->
        val bitmap = BitmapFactory.decodeStream(stream)
        if (bitmap != null) {
          return DecodedImage(
            bitmap = bitmap.asImageBitmap().rb,
            nativeWidth = bitmap.width.toFloat(),
            nativeHeight = bitmap.height.toFloat(),
          )
        }
      }
    } catch (_: Exception) {}

    try {
      val bitmap = BitmapFactory.decodeFile(fullPath)
      if (bitmap != null) {
        return DecodedImage(
          bitmap = bitmap.asImageBitmap().rb,
          nativeWidth = bitmap.width.toFloat(),
          nativeHeight = bitmap.height.toFloat(),
        )
      }
    } catch (_: Exception) {}
  }

  return null
}

/** A Layer rendering an image asset referenced by [ImageLayer.refId]. */
@SuppressLint("RestrictedApi")
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
  val context = LocalContext.current

  val decodedImage = remember(asset) { decodeImageAsset(asset, context) } ?: return
  val remoteBitmap = decodedImage.bitmap
  val nativeWidth =
    if (decodedImage.nativeWidth > 0f) decodedImage.nativeWidth else (asset.width ?: 0f)
  val nativeHeight =
    if (decodedImage.nativeHeight > 0f) decodedImage.nativeHeight else (asset.height ?: 0f)

  val updatedTransformStack =
    if (layer.transform != null) transformStack + layer.transform else transformStack

  val layerOpacity =
    (updatedTransformStack.lastOrNull()?.opacity?.let {
      animateScalar(it, animationSettings) / 100f
    } ?: 1f.rf) * layerVisibility

  val imageWidth = asset.width ?: 0f
  val imageHeight = asset.height ?: 0f

  val paint = RemotePaint { this.color = Color.White.rc.copy(alpha = layerOpacity) }

  val hasMasks = layer.masksProperties.any { it.mode != MaskMode.None && it.path != null }
  val needsSave = matteContext != null || hasMasks

  RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
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

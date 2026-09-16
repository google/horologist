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

@file:Suppress("RestrictedApi", "INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.google.android.horologist.remotecompose.lottie.renderer.layers

import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.drawWithContent
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.abs
import androidx.compose.remote.creation.compose.state.clamp
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.BlendMode as LottieBlendMode
import com.google.android.horologist.remotecompose.lottie.format.layer.ImageLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.Layer
import com.google.android.horologist.remotecompose.lottie.format.layer.MatteMode
import com.google.android.horologist.remotecompose.lottie.format.layer.PrecompLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.TextLayer
import com.google.android.horologist.remotecompose.lottie.format.mask.MaskMode
import com.google.android.horologist.remotecompose.lottie.renderer.buildRemotePathFromBezier
import com.google.android.horologist.remotecompose.lottie.renderer.inverseTransform
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.transform

private val LocalEffectSurface = staticCompositionLocalOf<RemoteImageBitmap?> { null }

/**
 * Each component has a separate recorder, so carry its containing bitmap across component
 * boundaries.
 */
private fun RemoteCanvas.withContainingSurface(surface: RemoteImageBitmap?, block: () -> Unit) {
  val recorder = internalCanvas
  val previous = recorder.currentDrawToBitmapId
  recorder.currentDrawToBitmapId = surface?.getIdForCreationState(recorder.creationState) ?: 0
  try {
    block()
  } finally {
    recorder.currentDrawToBitmapId = previous
  }
}

/** Applies effects to a rendered layer composite, not to its individual child paints. */
@Composable
@RemoteComposable
internal fun LayerEffects(
  layer: Layer,
  parentTransforms: Map<Int?, List<Transform>>,
  extraTransform: Transform?,
  matte: MatteContext?,
) {
  check(matte?.matteMode !in listOf(MatteMode.Luma, MatteMode.InvertedLuma)) {
    "Luminance mattes need software color-matrix compositing, which the RC alpha19 player does not support"
  }
  val settings = LocalAnimationSettings.current
  val transforms =
    listOfNotNull(extraTransform) +
      (parentTransforms[layer.index] ?: parentTransforms[null].orEmpty()) +
      listOfNotNull(layer.transform)
  val masks = layer.masksProperties.filter { it.mode != MaskMode.None && it.path != null }
  if (
    layer is PrecompLayer &&
      layer.width != null &&
      layer.height != null &&
      matte == null &&
      masks.isEmpty() &&
      (layer.blendMode == null || layer.blendMode == LottieBlendMode.Normal)
  ) {
    // Clipping alone needs no bitmap: retain vector resolution at the player's actual scale.
    RemoteBox(
      modifier =
        RemoteModifier.fillMaxSize().drawWithContent {
          val canvas = remoteCanvas
          canvas.save()
          for (t in transforms) transform(t, null, settings, canvas)
          canvas.clipRect(0f.rf, 0f.rf, layer.width.rf, layer.height.rf)
          for (t in transforms.reversed()) inverseTransform(t, settings, canvas)
          drawContent()
          canvas.restore()
        }
    ) {
      Layer(layer, parentTransforms, extraTransform, effectsApplied = true)
    }
    return
  }
  val containingSurface = LocalEffectSurface.current
  val width = settings.width.toInt()
  val height = settings.height.toInt()
  require(width in 1..32767 && height in 1..32767 && width.toLong() * height <= 16_777_216) {
    "Layer effects require a positive offscreen surface of at most 16 megapixels"
  }
  val target = remember(layer) { RemoteImageBitmap.createOffscreenRemoteBitmap(width, height) }
  val maskBitmap = remember(layer) { RemoteImageBitmap.createOffscreenRemoteBitmap(width, height) }
  val maskPart = remember(layer) { RemoteImageBitmap.createOffscreenRemoteBitmap(width, height) }
  val matteBitmap = remember(matte) { RemoteImageBitmap.createOffscreenRemoteBitmap(width, height) }
  val withoutMasks =
    when (layer) {
      is ShapeLayer -> layer.copy(masks = null)
      is SolidColorLayer -> layer.copy(masks = null)
      is ImageLayer -> layer.copy(masks = null)
      is TextLayer -> layer.copy(masks = null)
      is PrecompLayer -> layer.copy(masks = null)
      else -> layer
    }
  val opaque = RemotePaint { color = Color.White.rc }
  val keepAlpha = RemotePaint {
    color = Color.White.rc
    blendMode = BlendMode.DstIn
  }
  val removeAlpha = RemotePaint {
    color = Color.White.rc
    blendMode = BlendMode.DstOut
  }
  // Offscreen bitmaps do not have recording-time platform image dimensions.
  fun RemoteCanvas.drawSurface(bitmap: RemoteImageBitmap, paint: RemotePaint) =
    drawScaledBitmap(
      bitmap,
      0f.rf,
      0f.rf,
      width.rf,
      height.rf,
      0f.rf,
      0f.rf,
      width.rf,
      height.rf,
      RemoteComposeWriter.IMAGE_SCALE_FILL_BOUNDS,
      1f.rf,
      null,
      paint,
    )
  RemoteBox(modifier = RemoteModifier.fillMaxSize()) {
    if (matte != null) {
      RemoteBox(
        modifier =
          RemoteModifier.fillMaxSize().drawWithContent {
            remoteCanvas.withContainingSurface(containingSurface) {
              remoteCanvas.internalCanvas.drawToOffscreenBitmap(matteBitmap, 0) { drawContent() }
            }
          }
      ) {
        CompositionLocalProvider(LocalEffectSurface provides matteBitmap) {
          Layer(matte.matteLayer, mapOf(matte.matteLayer.index to matte.matteTransforms))
        }
      }
    }
    RemoteBox(
      modifier =
        RemoteModifier.fillMaxSize().drawWithContent {
          val canvas = remoteCanvas
          canvas.withContainingSurface(containingSurface) {
            canvas.internalCanvas.drawToOffscreenBitmap(target, 0) {
              canvas.save()
              if (layer is PrecompLayer && layer.width != null && layer.height != null) {
                for (t in transforms) transform(t, null, settings, canvas)
                canvas.clipRect(0f.rf, 0f.rf, layer.width.rf, layer.height.rf)
                for (t in transforms.reversed()) inverseTransform(t, settings, canvas)
              }
              drawContent()
              canvas.restore()
            }
            if (masks.isNotEmpty()) {
              val initial =
                if (masks.first().mode in listOf(MaskMode.Subtract, MaskMode.Intersect)) -1 else 0
              canvas.internalCanvas.drawToOffscreenBitmap(maskBitmap, initial) {}
              for (mask in masks) {
                val opacity = clamp(animateScalar(mask.opacity, settings) / 100f, 0f.rf, 1f.rf)
                val expansion = animateScalar(mask.expansion, settings)
                canvas.internalCanvas.drawToOffscreenBitmap(
                  maskPart,
                  if (mask.inverted) -1 else 0,
                ) {
                  canvas.save()
                  for (t in transforms) transform(t, null, settings, canvas)
                  val path = canvas.buildRemotePathFromBezier(animateBezier(mask.path!!, settings))
                  val paint = if (mask.inverted) removeAlpha else opaque
                  canvas.drawPath(path, paint)
                  canvas.drawConditionally(expansion.isGreaterThan(0f.rf)) {
                    canvas.drawPath(
                      path,
                      RemotePaint {
                        color = Color.White.rc
                        style = PaintingStyle.Stroke
                        strokeWidth = abs(expansion) * 2f
                        blendMode = if (mask.inverted) BlendMode.DstOut else BlendMode.SrcOver
                      },
                    )
                  }
                  canvas.drawConditionally(expansion.isLessThan(0f.rf)) {
                    canvas.drawPath(
                      path,
                      RemotePaint {
                        color = Color.White.rc
                        style = PaintingStyle.Stroke
                        strokeWidth = abs(expansion) * 2f
                        blendMode = if (mask.inverted) BlendMode.SrcOver else BlendMode.DstOut
                      },
                    )
                  }
                  canvas.restore()
                  // Scale the completed (possibly inverted) mask once, including its transparent
                  // area.
                  canvas.drawRect(
                    0f.rf,
                    0f.rf,
                    width.toFloat().rf,
                    height.toFloat().rf,
                    RemotePaint {
                      color = Color.White.rc.copy(alpha = opacity)
                      blendMode = BlendMode.DstIn
                    },
                  )
                }
                val mode =
                  when (mask.mode) {
                    MaskMode.Subtract -> BlendMode.DstOut
                    MaskMode.Intersect -> BlendMode.DstIn
                    MaskMode.Difference -> BlendMode.Xor
                    MaskMode.Lighten -> BlendMode.Lighten
                    MaskMode.Darken -> BlendMode.Darken
                    else -> BlendMode.SrcOver
                  }
                canvas.internalCanvas.drawToOffscreenBitmap(maskBitmap) {
                  canvas.drawSurface(
                    maskPart,
                    RemotePaint {
                      color = Color.White.rc
                      blendMode = mode
                    },
                  )
                }
              }
              canvas.internalCanvas.drawToOffscreenBitmap(target) {
                canvas.drawSurface(maskBitmap, keepAlpha)
              }
            }
            if (matte != null) {
              val inverted =
                matte.matteMode in listOf(MatteMode.InvertedAlpha, MatteMode.InvertedLuma)
              canvas.internalCanvas.drawToOffscreenBitmap(target) {
                canvas.drawSurface(matteBitmap, if (inverted) removeAlpha else keepAlpha)
              }
            }
            canvas.drawSurface(
              target,
              RemotePaint {
                color = Color.White.rc
                blendMode = layerBlendMode(layer.blendMode)
              },
            )
          }
        }
    ) {
      CompositionLocalProvider(LocalEffectSurface provides target) {
        Layer(withoutMasks, parentTransforms, extraTransform, effectsApplied = true)
      }
    }
  }
}

private fun layerBlendMode(mode: LottieBlendMode?): BlendMode =
  when (mode) {
    null,
    LottieBlendMode.Normal -> BlendMode.SrcOver
    LottieBlendMode.Multiply -> BlendMode.Multiply
    LottieBlendMode.Screen -> BlendMode.Screen
    LottieBlendMode.Overlay -> BlendMode.Overlay
    LottieBlendMode.Darken -> BlendMode.Darken
    LottieBlendMode.Lighten -> BlendMode.Lighten
    LottieBlendMode.ColorDodge -> BlendMode.ColorDodge
    LottieBlendMode.ColorBurn -> BlendMode.ColorBurn
    LottieBlendMode.HardLight -> BlendMode.Hardlight
    LottieBlendMode.SoftLight -> BlendMode.Softlight
    LottieBlendMode.Difference -> BlendMode.Difference
    LottieBlendMode.Exclusion -> BlendMode.Exclusion
    LottieBlendMode.Hue -> BlendMode.Hue
    LottieBlendMode.Saturation -> BlendMode.Saturation
    LottieBlendMode.Color -> BlendMode.Color
    LottieBlendMode.Luminosity -> BlendMode.Luminosity
    LottieBlendMode.Add -> BlendMode.Plus
    LottieBlendMode.HardMix -> error("Hard-mix blending is not supported by the RC player")
  }

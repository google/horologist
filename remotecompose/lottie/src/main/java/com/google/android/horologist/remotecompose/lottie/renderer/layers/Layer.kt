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
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.BlendMode
import com.google.android.horologist.remotecompose.lottie.format.layer.ImageLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.Layer
import com.google.android.horologist.remotecompose.lottie.format.layer.LayerType
import com.google.android.horologist.remotecompose.lottie.format.layer.MatteMode
import com.google.android.horologist.remotecompose.lottie.format.layer.PrecompLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.TextLayer
import com.google.android.horologist.remotecompose.lottie.renderer.bindToTimeline
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar

/** Matte context for paired track matte layer masking */
internal data class MatteContext(
  val matteLayer: Layer,
  val matteTransforms: List<Transform>,
  val matteMode: MatteMode = MatteMode.Alpha,
)

/** Calculates local layer time $t_{\text{local}} = (t - st) / sr$ for layer properties. */
@SuppressLint("RestrictedApi")
internal fun calculateLocalFrame(
  currentFrame: RemoteFloat,
  startTime: Float?,
  timeStretch: Float?,
): RemoteFloat {
  val st = startTime ?: 0f
  val sr = timeStretch ?: 1f
  if (st == 0f && sr == 1f) {
    return currentFrame
  }
  val safeSr = if (sr == 0f) 1f else sr
  return (currentFrame - st.rf) / safeSr.rf
}

/**
 * Extends layer out-point by 0.01f when it reaches or exceeds composition end to preserve
 * visibility at progress=1.0f.
 */
internal fun calculateEffectiveEndFrame(endFrame: Float, compositionEndFrame: Float): Float {
  return if (endFrame >= compositionEndFrame) {
    endFrame + 0.01f
  } else {
    endFrame
  }
}

/**
 * Evaluates dynamic RemoteFloat timeline visibility for a layer given its [startFrame, endFrame)
 * bounds.
 */
@SuppressLint("RestrictedApi")
internal fun calculateLayerVisibility(
  currentFrame: RemoteFloat,
  startFrame: Float,
  effectiveEndFrame: Float,
  baseVisibility: RemoteFloat = 1f.rf,
): RemoteFloat {
  val isAfterStart = selectIfLt(currentFrame, startFrame.rf, 0f.rf, 1f.rf)
  val isBeforeEnd = selectIfLt(currentFrame, effectiveEndFrame.rf, 1f.rf, 0f.rf)
  return baseVisibility * (isAfterStart * isBeforeEnd)
}

/** A Layer in the Lottie composition */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun Layer(
  layer: Layer,
  parentTransforms: Map<Int?, List<Transform>>,
  transform: Transform? = null,
  matteContext: MatteContext? = null,
  effectsApplied: Boolean = false,
) {
  if (layer.hidden?.constantValue == true) {
    return
  }

  if (
    !effectsApplied &&
      (matteContext != null ||
        (layer.blendMode != null && layer.blendMode != BlendMode.Normal) ||
        layer.masksProperties.isNotEmpty() ||
        (layer is PrecompLayer && layer.width != null && layer.height != null))
  ) {
    LayerEffects(layer, parentTransforms, transform, matteContext)
    return
  }

  val startFrame = layer.startFrame.constantValue
  val endFrame = layer.endFrame.constantValue
  val parentSettings = LocalAnimationSettings.current
  val compositionEndFrame = parentSettings.endFrame

  // Root progress clamps before its out-point; child/layer intervals remain half-open.
  val effectiveEndFrame = calculateEffectiveEndFrame(endFrame, compositionEndFrame)

  val currentFrame = parentSettings.currentFrame
  val constFrame = currentFrame.constantValueOrNull
  if (constFrame != null && (constFrame < startFrame || constFrame >= effectiveEndFrame)) {
    return
  }

  val layerVisibility =
    calculateLayerVisibility(currentFrame, startFrame, effectiveEndFrame, parentSettings.visibility)

  val ancestorStack = parentTransforms[layer.index] ?: parentTransforms[null] ?: emptyList()

  val completeStack =
    if (transform != null) {
      listOf(transform) + ancestorStack
    } else {
      ancestorStack
    }

  // Ordinary-layer keyframes and visibility are authored in the containing composition's
  // time, even when st/sr metadata is present. Only precomposition content changes timelines;
  // applying (frame - st) / sr to ordinary layers would retime their exported keys twice.
  when (layer.type) {
    LayerType.Solid ->
      SolidColorLayer(layer as SolidColorLayer, completeStack, matteContext, layerVisibility)
    LayerType.Shape -> ShapeLayer(layer as ShapeLayer, completeStack, matteContext, layerVisibility)
    LayerType.Image -> ImageLayer(layer as ImageLayer, completeStack, matteContext, layerVisibility)
    LayerType.Text -> TextLayer(layer as TextLayer, completeStack, matteContext, layerVisibility)
    LayerType.Precomposition -> {
      val precompLayer = layer as PrecompLayer
      val boundTransforms =
        (completeStack + listOfNotNull(layer.transform)).map { it.bindToTimeline(parentSettings) }
      val localFrame =
        if (precompLayer.timeRemap != null) {
          animateScalar(precompLayer.timeRemap, parentSettings) * parentSettings.frameRate.rf
        } else {
          calculateLocalFrame(currentFrame, layer.startTime, layer.timeStretch)
        }
      val precompOpacity =
        layer.transform?.opacity?.let { animateScalar(it, parentSettings) / 100f } ?: 1f.rf
      val localSettings =
        parentSettings.copy(
          currentFrame = localFrame,
          visibility = layerVisibility * precompOpacity,
        )
      CompositionLocalProvider(LocalAnimationSettings provides localSettings) {
        PrecompLayer(layer = precompLayer, transformStack = boundTransforms)
      }
    }
    LayerType.Null,
    LayerType.Audio,
    LayerType.Unknown -> {}
  }
}

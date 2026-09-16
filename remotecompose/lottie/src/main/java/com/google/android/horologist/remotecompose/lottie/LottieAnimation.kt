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

package com.google.android.horologist.remotecompose.lottie

import android.annotation.SuppressLint
import androidx.annotation.RawRes
import androidx.compose.remote.creation.Rc.Time.ANIMATION_TIME
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.drawWithContent
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.floor
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.FontChar
import com.google.android.horologist.remotecompose.lottie.format.FontList
import com.google.android.horologist.remotecompose.lottie.format.asset.Asset
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.Layer
import com.google.android.horologist.remotecompose.lottie.format.layer.MatteMode
import com.google.android.horologist.remotecompose.lottie.renderer.layers.Layer
import com.google.android.horologist.remotecompose.lottie.renderer.layers.MatteContext

/**
 * Settings for the Lottie animation player.
 *
 * @property currentFrame The current frame to display.
 * @property slotMap Mapping of slot IDs to values for dynamic theming.
 * @property assets Mapping of asset IDs to root assets.
 * @property visibility Compound layer visibility multiplier.
 * @property activePrecomps Set of precomposition asset IDs currently rendering (recursion guard).
 * @property frameRate The composition frame rate in frames per second.
 * @property fonts The fonts table defined in the animation root.
 * @property chars Vector glyph definitions defined in the animation root.
 */
internal data class LottieSettings(
  val currentFrame: RemoteFloat,
  val slotMap: SlotMap = SlotMap.Empty,
  val width: Float = 0f,
  val height: Float = 0f,
  val endFrame: Float = Float.MAX_VALUE,
  val assets: Map<String, Asset> = emptyMap(),
  val visibility: RemoteFloat = 1f.rf,
  val activePrecomps: Set<String> = emptySet(),
  val frameRate: Float = 30f,
  val fonts: FontList? = null,
  val chars: List<FontChar> = emptyList(),
)

/** CompositionLocal for [LottieSettings]. */
internal val LocalAnimationSettings =
  staticCompositionLocalOf<LottieSettings> {
    LottieSettings(0.rf, SlotMap.Empty, 0f, 0f, Float.MAX_VALUE)
  }

/**
 * A RemoteComposable that loads and renders a Lottie animation from a raw resource ID.
 *
 * @param rawRes The raw resource ID of the Lottie JSON file.
 * @param modifier The modifier to apply to the Lottie layout.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
fun LottieAnimation(
  @RawRes rawRes: Int,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap.Empty,
  progress: RemoteFloat? = null,
) {
  val context = LocalContext.current
  val animation = remember(rawRes) { Animation.load(rawRes, context) }
  LottieAnimation(animation, modifier, slotMap, progress)
}

/**
 * A RemoteComposable that loads and renders a Lottie animation from a JSON string.
 *
 * @param json The JSON string of the Lottie animation.
 * @param modifier The modifier to apply to the Lottie layout.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
fun LottieAnimation(
  json: String,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap.Empty,
  progress: RemoteFloat? = null,
) {
  val animation = remember(json) { Animation.decodeFromString(json) }
  LottieAnimation(animation, modifier, slotMap, progress)
}

/**
 * A RemoteComposable that renders a Lottie animation to a RemoteCompose document.
 *
 * @param animation The Lottie animation to render.
 * @param modifier The modifier to apply to the Lottie layout.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun LottieAnimation(
  animation: Animation,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap.Empty,
  progress: RemoteFloat? = null,
) {
  // Cache validation with the immutable model, before emitting any recording operations.
  remember(animation) { animation.also { it.validateForRecording() } }
  // Total span of frames across the animation timeline.
  val totalFrames = animation.endFrame - animation.startFrame
  val startFrameRf = animation.startFrame.rf

  // Determine current frame: if progress [0.0, 1.0] is provided (e.g. via a named variable
  // or user-driven state), map it directly to frames; otherwise drive it continuously
  // from the Remote Compose document animation clock time.
  val currentFrame =
    if (progress != null) {
      // Progress 1 displays the last representable frame, while layer out-points stay exclusive.
      min(startFrameRf + (progress * totalFrames), Math.nextDown(animation.endFrame).rf)
    } else {
      startFrameRf + (floor(RemoteFloat(ANIMATION_TIME) * animation.frameRate) % totalFrames)
    }
  val assetMap = remember(animation.assets) { animation.assets.associateBy { it.id } }
  val animationSettings =
    LottieSettings(
      currentFrame = currentFrame,
      slotMap = slotMap,
      width = animation.width.toFloat(),
      height = animation.height.toFloat(),
      endFrame = animation.endFrame,
      assets = assetMap,
      frameRate = animation.frameRate,
      fonts = animation.fonts,
      chars = animation.chars,
    )

  CompositionLocalProvider(LocalAnimationSettings provides animationSettings) {
    // We remember this topologically sorted map because traversing the graph and tracking
    // hierarchical lists is an expensive allocation operation. Caching it guarantees it executes
    // strictly once per Lottie load, preventing heavy GC churn during recompositions.
    val ancestorTransforms =
      remember(animation.layers) { buildAncestorTransforms(animation.layers) }

    val lottieWidth = animation.width.rf
    val lottieHeight = animation.height.rf

    val scaleModifier = RemoteModifier.drawWithContent {
      val canvasWidth = size.width
      val canvasHeight = size.height

      val scaleX = canvasWidth / lottieWidth
      val scaleY = canvasHeight / lottieHeight
      val scale = min(scaleX, scaleY)

      val scaledWidth = lottieWidth * scale
      val scaledHeight = lottieHeight * scale
      val dx = (canvasWidth - scaledWidth) / 2.rf
      val dy = (canvasHeight - scaledHeight) / 2.rf

      translate(dx, dy) { scale(scale, scale) { drawContent() } }
    }

    RemoteBox(
      modifier = modifier.then(scaleModifier),
      // TODO: 496943072 - ANDROID_NATIVE player doesn't support clipping yet, so we need to avoid
      // clipping for now until it does. coming in cl/893506559
      // .clip(RemoteRectangleShape)
      contentAlignment = RemoteAlignment.Center,
    ) {
      DeclarePlaybackState(currentFrame)
      val matteTargetIndices =
        remember(animation.layers) {
          animation.layers
            .mapIndexedNotNull { index, layer ->
              if (layer.matteParent != null) {
                layer.matteParent
              } else if (
                layer.matteMode != null && layer.matteMode != MatteMode.Normal && index > 0
              ) {
                animation.layers[index - 1].index
              } else {
                null
              }
            }
            .toSet()
        }
      for (i in animation.layers.indices.reversed()) {
        val layer = animation.layers[i]
        val isMatteSource =
          layer.matteTarget == 1 ||
            (layer.index != null && layer.index in matteTargetIndices) ||
            (i < animation.layers.size - 1 &&
              animation.layers[i + 1].matteMode != null &&
              animation.layers[i + 1].matteMode != MatteMode.Normal &&
              animation.layers[i + 1].matteParent == null)
        if (isMatteSource) {
          continue
        }
        val matteContext =
          if (
            (layer.matteMode != null && layer.matteMode != MatteMode.Normal) ||
              layer.matteParent != null
          ) {
            val matteLayer =
              if (layer.matteParent != null) {
                animation.layers.firstOrNull { it.index == layer.matteParent }
              } else if (i > 0) {
                animation.layers[i - 1]
              } else {
                null
              }
            if (matteLayer != null) {
              val matteMode =
                if (layer.matteMode != null && layer.matteMode != MatteMode.Normal) {
                  layer.matteMode!!
                } else {
                  MatteMode.Alpha
                }
              val transforms =
                ancestorTransforms[matteLayer.index] ?: ancestorTransforms[null] ?: emptyList()
              MatteContext(matteLayer, transforms, matteMode)
            } else {
              null
            }
          } else {
            null
          }
        Layer(layer, ancestorTransforms, matteContext = matteContext)
      }
    }
  }
}

internal fun buildAncestorTransforms(
  layers: List<Layer>,
  baseStack: List<Transform> = emptyList(),
): Map<Int?, List<Transform>> {
  val map = mutableMapOf<Int?, List<Transform>>()
  val childrenMap = layers.groupBy { it.parent }

  val roots = childrenMap[null] ?: emptyList()
  for (layer in roots) {
    populateAncestorTransforms(layer, baseStack, emptySet(), childrenMap, map)
  }
  if (baseStack.isNotEmpty()) {
    map[null] = baseStack
  }
  return map
}

private fun populateAncestorTransforms(
  layer: Layer,
  currentStack: List<Transform>,
  visited: Set<Int>,
  childrenMap: Map<Int?, List<Layer>>,
  outMap: MutableMap<Int?, List<Transform>>,
) {
  val layerIndex = layer.index
  if (layerIndex != null) {
    if (layerIndex in visited) {
      return
    }
    outMap[layerIndex] = currentStack
  }

  val layerTransform = layer.transform
  val nextStack =
    if (layerTransform != null) {
      currentStack + layerTransform
    } else {
      currentStack
    }

  val nextVisited = if (layerIndex != null) visited + layerIndex else visited
  val children = if (layerIndex != null) childrenMap[layerIndex] ?: emptyList() else emptyList()
  for (child in children) {
    populateAncestorTransforms(child, nextStack, nextVisited, childrenMap, outMap)
  }
}

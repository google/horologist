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

import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.asset.PrecompAsset
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Group
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.BlendMode
import com.google.android.horologist.remotecompose.lottie.format.layer.MatteMode
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.mask.MaskMode
import com.google.android.horologist.remotecompose.lottie.format.properties.AnimatedVectorProperty
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticVectorProperty

/** Rejects invalid recording dimensions, timelines and scale vectors before emitting operations. */
internal fun Animation.validateForRecording() {
  require(width > 0 && height > 0) { "Lottie width and height must be positive" }
  require(frameRate.isFinite() && frameRate > 0f) {
    "Lottie frame rate must be finite and positive"
  }
  require(
    startFrame.isFinite() &&
      endFrame.isFinite() &&
      endFrame > startFrame &&
      (endFrame - startFrame).isFinite()
  ) {
    "Lottie frame interval must be finite and nonempty"
  }

  fun validateTransform(transform: Transform) {
    val values =
      when (val scale = transform.scale) {
        is StaticVectorProperty -> listOf(scale.value)
        is AnimatedVectorProperty -> scale.keyframes.map { it.value }
      }
    require(values.isNotEmpty() && values.all { it.size >= 2 }) {
      "Lottie transform scale must contain at least two components in every keyframe"
    }
  }
  fun validateShapes(shapes: List<GraphicElement>) {
    for (shape in shapes) when (shape) {
      is Transform -> validateTransform(shape)
      is Group -> validateShapes(shape.shapes)
      else -> Unit
    }
  }
  for (layer in layers + assets.filterIsInstance<PrecompAsset>().flatMap { it.layers }) {
    require(layer.is3d != 1) { "3D Lottie layers are not supported" }
    require(!layer.autoOrient.constantValue) {
      "Lottie layer auto-orientation is not yet supported"
    }
    require(layer.matteMode !in listOf(MatteMode.Luma, MatteMode.InvertedLuma)) {
      "Luminance mattes require software color-matrix compositing unavailable in RC alpha19"
    }
    require(layer.blendMode != BlendMode.HardMix) {
      "Hard-mix blending is not supported by the RC player"
    }
    require(
      layer.masksProperties.all {
        it.mode in listOf(MaskMode.None, MaskMode.Add, MaskMode.Subtract, MaskMode.Intersect)
      }
    ) {
      "Only add, subtract, intersect and none mask modes are supported"
    }
    layer.transform?.let(::validateTransform)
    if (layer is ShapeLayer) validateShapes(layer.shapes)
  }
}

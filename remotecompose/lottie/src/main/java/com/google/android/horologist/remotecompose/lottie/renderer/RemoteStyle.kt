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

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.google.android.horologist.remotecompose.lottie.renderer

import android.annotation.SuppressLint
import androidx.compose.remote.core.operations.paint.PaintBundle
import androidx.compose.remote.core.operations.paint.PaintPathEffects
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.shaders.RemoteLinearShader
import androidx.compose.remote.creation.compose.shaders.RemoteRadialShader
import androidx.compose.remote.creation.compose.shaders.RemoteShader
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.selectIfLt
import androidx.compose.remote.creation.compose.state.sqrt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.FillRule
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.GradientType
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.LineCap
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.LineJoin
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.StrokeDash
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteGradientValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar

internal interface RemoteStyle {
  val dashPattern: RemoteDash?
    get() = null

  val miterLimit: RemoteFloat?
    get() = null

  fun getPaint(inheritedOpacity: RemoteFloat = 1f.rf): RemotePaint
}

@SuppressLint("RestrictedApi")
internal class RemoteStyleWithOpacity(
  val baseStyle: RemoteStyle,
  val opacityMultiplier: RemoteFloat,
) : RemoteStyle {
  override val dashPattern: RemoteDash?
    get() = baseStyle.dashPattern

  override val miterLimit: RemoteFloat?
    get() = baseStyle.miterLimit

  override fun getPaint(inheritedOpacity: RemoteFloat): RemotePaint {
    return baseStyle.getPaint(inheritedOpacity * opacityMultiplier)
  }
}

@SuppressLint("RestrictedApi")
internal class RemoteFill(
  val fillColor: RemoteColor,
  val opacity: RemoteFloat = 100f.rf,
  val fillRule: FillRule = FillRule.NonZero,
) : RemoteStyle {
  override fun getPaint(inheritedOpacity: RemoteFloat): RemotePaint {
    return RemotePaint {
      val effectiveAlpha = fillColor.alpha * (opacity / 100f) * inheritedOpacity
      this.color = fillColor.copy(alpha = effectiveAlpha)
    }
  }
}

@SuppressLint("RestrictedApi")
internal class RemoteStroke(
  val strokeColor: RemoteColor,
  val strokeWidth: RemoteFloat,
  val opacity: RemoteFloat,
  val lineCap: LineCap = LineCap.Round,
  val lineJoin: LineJoin = LineJoin.Round,
  override val miterLimit: RemoteFloat? = null,
  override val dashPattern: RemoteDash? = null,
) : RemoteStyle {
  override fun getPaint(inheritedOpacity: RemoteFloat): RemotePaint {
    return RemotePaint {
      val baseAlpha = strokeColor.alpha * (opacity / 100f) * inheritedOpacity
      val effectiveAlpha = selectIfLt(this@RemoteStroke.strokeWidth, 0.001f.rf, 0f.rf, baseAlpha)
      this.color = strokeColor.copy(alpha = effectiveAlpha)
      this.style = PaintingStyle.Stroke
      this.strokeWidth = this@RemoteStroke.strokeWidth
      this.strokeCap =
        when (lineCap) {
          LineCap.Butt -> StrokeCap.Butt
          LineCap.Round -> StrokeCap.Round
          LineCap.Square -> StrokeCap.Square
        }
      this.strokeJoin =
        when (lineJoin) {
          LineJoin.Miter -> StrokeJoin.Miter
          LineJoin.Round -> StrokeJoin.Round
          LineJoin.Bevel -> StrokeJoin.Bevel
        }
    }
  }
}

@SuppressLint("RestrictedApi")
internal class RemoteGradientFill(
  val gradient: RemoteGradientValue,
  val startPoint: Point,
  val endPoint: Point,
  val gradientType: GradientType,
  val opacity: RemoteFloat,
  val fillRule: FillRule = FillRule.NonZero,
) : RemoteStyle {
  override fun getPaint(inheritedOpacity: RemoteFloat): RemotePaint {
    return RemotePaint {
      this.style = PaintingStyle.Fill
      this.shader =
        createGradientShader(
          gradient = gradient,
          startPoint = startPoint,
          endPoint = endPoint,
          gradientType = gradientType,
          opacity = opacity,
          inheritedOpacity = inheritedOpacity,
        )
    }
  }
}

@SuppressLint("RestrictedApi")
internal class RemoteGradientStroke(
  val gradient: RemoteGradientValue,
  val startPoint: Point,
  val endPoint: Point,
  val gradientType: GradientType,
  val opacity: RemoteFloat,
  val strokeWidth: RemoteFloat,
  val lineCap: LineCap = LineCap.Round,
  val lineJoin: LineJoin = LineJoin.Round,
  override val miterLimit: RemoteFloat? = null,
  override val dashPattern: RemoteDash? = null,
) : RemoteStyle {
  override fun getPaint(inheritedOpacity: RemoteFloat): RemotePaint {
    return RemotePaint {
      val effectiveOpacity =
        selectIfLt(this@RemoteGradientStroke.strokeWidth, 0.001f.rf, 0f.rf, 1f.rf) * opacity
      this.style = PaintingStyle.Stroke
      this.strokeWidth = this@RemoteGradientStroke.strokeWidth
      this.strokeCap =
        when (lineCap) {
          LineCap.Butt -> StrokeCap.Butt
          LineCap.Round -> StrokeCap.Round
          LineCap.Square -> StrokeCap.Square
        }
      this.strokeJoin =
        when (lineJoin) {
          LineJoin.Miter -> StrokeJoin.Miter
          LineJoin.Round -> StrokeJoin.Round
          LineJoin.Bevel -> StrokeJoin.Bevel
        }
      this.shader =
        createGradientShader(
          gradient = gradient,
          startPoint = startPoint,
          endPoint = endPoint,
          gradientType = gradientType,
          opacity = effectiveOpacity,
          inheritedOpacity = inheritedOpacity,
        )
    }
  }
}

/** Dashes are serialized explicitly: alpha19's Compose paint tracker drops PathEffect. */
internal data class RemoteDash(val intervals: List<RemoteFloat>, val phase: RemoteFloat)

@SuppressLint("RestrictedApi")
internal fun RemoteCanvas.applyStrokeDetails(style: RemoteStyle?) {
  val dash = style?.dashPattern
  val miter = style?.miterLimit ?: 4f.rf
  val canvas = internalCanvas
  val op = canvas.recordRenderingOp {
    val bundle = PaintBundle()
    // RemotePaint does not expose miter limits. Serialize the live value explicitly,
    // including a reset for other styles so nested/repeated content cannot inherit it.
    bundle.setStrokeMiter(miter.getFloatIdForCreationState(canvas.creationState))
    bundle.setPathEffect(
      dash?.let {
        PaintPathEffects.encode(
          PaintPathEffects.Dash(
            it.phase.getFloatIdForCreationState(canvas.creationState),
            *it.intervals
              .map { value -> value.getFloatIdForCreationState(canvas.creationState) }
              .toFloatArray(),
          )
        )
      }
    )
    canvas.document.buffer.addPaint(bundle)
  }
  canvas.buffer.addRoots(op, miter)
  if (dash != null) canvas.buffer.addRoots(op, dash.phase, *dash.intervals.toTypedArray())
}

@SuppressLint("RestrictedApi")
internal fun createDashPathEffect(
  dashes: List<StrokeDash>?,
  animationSettings: LottieSettings,
): RemoteDash? {
  if (dashes.isNullOrEmpty()) return null
  val intervals = mutableListOf<RemoteFloat>()
  var phase = 0f.rf
  for (dash in dashes) {
    val property = dash.length ?: continue
    val value = animateScalar(property, animationSettings)
    if (dash.type.value?.startsWith("o") == true) phase = value
    else intervals += max(value, 0.1f.rf)
  }
  if (intervals.isEmpty()) return null
  return RemoteDash(if (intervals.size % 2 == 0) intervals else intervals + intervals, phase)
}

@SuppressLint("RestrictedApi")
private fun createGradientShader(
  gradient: RemoteGradientValue,
  startPoint: Point,
  endPoint: Point,
  gradientType: GradientType,
  opacity: RemoteFloat,
  inheritedOpacity: RemoteFloat,
): RemoteShader {
  val (colors, positions) = extractGradientColorsAndPositions(gradient, opacity, inheritedOpacity)
  return when (gradientType) {
    GradientType.Linear -> {
      RemoteLinearShader(
        x0 = startPoint.x,
        y0 = startPoint.y,
        x1 = endPoint.x,
        y1 = endPoint.y,
        colors = colors,
        positions = positions,
        tileMode = TileMode.Clamp,
      )
    }
    GradientType.Radial -> {
      val dx = endPoint.x - startPoint.x
      val dy = endPoint.y - startPoint.y
      val radius = sqrt((dx * dx) + (dy * dy))
      RemoteRadialShader(
        centerX = startPoint.x,
        centerY = startPoint.y,
        radius = radius,
        colors = colors,
        positions = positions,
        tileMode = TileMode.Clamp,
      )
    }
  }
}

@SuppressLint("RestrictedApi")
private fun extractGradientColorsAndPositions(
  gradient: RemoteGradientValue,
  opacity: RemoteFloat,
  inheritedOpacity: RemoteFloat,
): Pair<List<RemoteColor>, List<RemoteFloat>> {
  val effectiveBaseOpacity = (opacity / 100f) * inheritedOpacity
  val values = gradient.values
  val requestedCount = if (gradient.numberOfColors > 0) gradient.numberOfColors else values.size / 4

  if (requestedCount <= 0 || values.size < 4) {
    val transparent = Color.Transparent.rc
    return Pair(listOf(transparent, transparent), listOf(0f.rf, 1f.rf))
  }

  val maxPossibleColors = values.size / 4
  val colorCount = requestedCount.coerceAtMost(maxPossibleColors)
  if (colorCount <= 0) {
    val transparent = Color.Transparent.rc
    return Pair(listOf(transparent, transparent), listOf(0f.rf, 1f.rf))
  }

  val totalColorFloats = colorCount * 4
  val alphaFloats = values.size - totalColorFloats
  val alphaCount = if (alphaFloats >= 2) alphaFloats / 2 else 0

  val colorPositions = List(colorCount) { values[it * 4] }
  val rgbChannels = List(3) { channel -> List(colorCount) { values[it * 4 + channel + 1] } }
  val alphaPositions = List(alphaCount) { values[totalColorFloats + it * 2] }
  val alphas = List(alphaCount) { values[totalColorFloats + it * 2 + 1] }

  // Both sets contribute stops: sampling opacity only at RGB stops loses intermediate valleys.
  val stops =
    colorPositions.mapIndexed { index, offset ->
      GradientShaderStop(
        offset,
        rgbChannels.map { it[index] } + sampleGradientChannel(offset, alphaPositions, alphas),
      )
    } +
      alphaPositions.mapIndexed { index, offset ->
        GradientShaderStop(
          offset,
          rgbChannels.map { sampleGradientChannel(offset, colorPositions, it) } + alphas[index],
        )
      }
  val sorted = if (alphaCount == 0) stops else sortGradientStops(stops)
  val colors = sorted.map { stop ->
    RemoteColor(
      red = stop.rgba[0],
      green = stop.rgba[1],
      blue = stop.rgba[2],
      alpha = stop.rgba[3] * effectiveBaseOpacity,
    )
  }
  // Android shaders require at least two stops, even for a constant one-color gradient.
  return if (colors.size == 1) {
    listOf(colors.single(), colors.single()) to listOf(0f.rf, 1f.rf)
  } else {
    colors to sorted.map { it.offset }
  }
}

/** A shader stop with four unpremultiplied channels in RGBA order. */
private data class GradientShaderStop(val offset: RemoteFloat, val rgba: List<RemoteFloat>)

/**
 * Samples a channel with equally sized values and nondecreasing positions. Empty means opaque;
 * out-of-range samples clamp to the endpoints, and coincident stops select the later value.
 */
@SuppressLint("RestrictedApi")
private fun sampleGradientChannel(
  offset: RemoteFloat,
  positions: List<RemoteFloat>,
  values: List<RemoteFloat>,
): RemoteFloat {
  if (values.isEmpty()) return 1f.rf
  var result = values.last()
  for (index in positions.size - 2 downTo 0) {
    val width = positions[index + 1] - positions[index]
    // Protect even unselected expression branches from division by zero at coincident stops.
    val safeWidth = selectIfLt(0f.rf, width, width, 1f.rf)
    val fraction = (offset - positions[index]) / safeWidth
    val interpolated = lerp(values[index], values[index + 1], fraction)
    result = selectIfLt(offset, positions[index + 1], interpolated, result)
  }
  return selectIfLt(offset, positions.first(), values.first(), result)
}

/** Keeps the union ordered during playback, including when an opacity stop crosses an RGB stop. */
@SuppressLint("RestrictedApi")
private fun sortGradientStops(stops: List<GradientShaderStop>): List<GradientShaderStop> {
  if (stops.all { it.offset.constantValueOrNull != null }) {
    return stops.sortedBy { it.offset.constantValue }
  }
  // An insertion sorting network carries channels with their offsets. Stable ties retain hard
  // stops.
  val sorted = stops.toMutableList()
  for (index in 1 until sorted.size) {
    for (j in index downTo 1) {
      val left = sorted[j - 1]
      val right = sorted[j]
      fun select(a: RemoteFloat, b: RemoteFloat) = selectIfLt(right.offset, left.offset, b, a)
      sorted[j - 1] =
        GradientShaderStop(
          select(left.offset, right.offset),
          left.rgba.zip(right.rgba) { a, b -> select(a, b) },
        )
      sorted[j] =
        GradientShaderStop(
          select(right.offset, left.offset),
          right.rgba.zip(left.rgba) { a, b -> select(a, b) },
        )
    }
  }
  return sorted
}

internal class NoopStyle() : RemoteStyle {
  override fun getPaint(inheritedOpacity: RemoteFloat): RemotePaint {
    return RemotePaint()
  }
}

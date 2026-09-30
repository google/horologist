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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.remote.core.RemoteClock
import androidx.compose.remote.creation.RemotePath
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.drawWithContent
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.min
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.remotePath
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteComposePlayerFlags
import androidx.compose.remote.player.compose.embedded.RcPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.remotecompose.lottie.format.values.Point
import com.google.android.horologist.remotecompose.lottie.renderer.lookupValueInBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.gammaLerp
import com.google.android.horologist.remotecompose.lottie.renderer.properties.sampleSpatialPosition
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.repeaterCopyOpacity
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.repeaterCopyScale
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.roundedStraightParameter
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.sliceCubic
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.straightParameter
import ee.schimke.composeai.preview.AnimatedPreview
import kotlin.math.hypot
import kotlin.math.pow

internal const val GRAPH_VIRTUAL_SIZE = 120f
internal const val GRAPH_PADDING = 12f

/**
 * Renders an XY graph preview for a complex [RemoteFloat] expression where X is time/progress
 * (`0..1`) and Y is the expression's value at that progress:
 * - A static reference curve ([expectedY]) is drawn underneath across `[0, 1]`.
 * - A live-evaluated [RemoteFloat] marker ([liveY]) animates on top from left to right as
 *   `progress` advances, updating the compiled RemoteCompose document via a named float without
 *   recomposition.
 */
@SuppressLint("RestrictedApi")
@Composable
internal fun ExpressionGraphPreview(
  expectedY: (Float) -> Float,
  liveY: (RemoteFloat) -> RemoteFloat,
  modifier: Modifier = Modifier.size(120.dp),
  progress: Float = 0.5f,
  yMin: Float = 0f,
  yMax: Float = 1f,
  samples: Int = 64,
  clock: RemoteClock = RemoteClock.SYSTEM,
) {
  RemoteComposePlayerFlags.isEmbeddedPlayerEnabled = true
  val doc =
    rememberRemoteDocument(clock = clock) {
      val progressVar = rememberNamedRemoteFloat("progress") { 0f.rf }
      val ySpan = (yMax - yMin).coerceAtLeast(1e-4f)
      val virtualSize = GRAPH_VIRTUAL_SIZE.rf
      val scaleModifier = RemoteModifier.drawWithContent {
        val scaleX = size.width / virtualSize
        val scaleY = size.height / virtualSize
        val scale = min(scaleX, scaleY)
        val dx = (size.width - virtualSize * scale) / 2f.rf
        val dy = (size.height - virtualSize * scale) / 2f.rf
        translate(dx, dy) { scale(scale, scale) { drawContent() } }
      }

      RemoteBox(modifier = RemoteModifier.fillMaxSize().then(scaleModifier)) {
        RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
          val pad = GRAPH_PADDING
          val plotW = GRAPH_VIRTUAL_SIZE - pad * 2f
          val plotH = GRAPH_VIRTUAL_SIZE - pad * 2f

          fun mapX(x: Float): RemoteFloat = (pad + plotW * x).rf
          fun mapY(y: Float): RemoteFloat {
            val norm = ((y - yMin) / ySpan).coerceIn(-0.25f, 1.25f)
            return (pad + plotH * (1f - norm)).rf
          }
          fun mapLiveX(x: RemoteFloat): RemoteFloat = pad.rf + plotW.rf * x
          fun mapLiveY(y: RemoteFloat): RemoteFloat {
            val norm = (y - yMin.rf) / ySpan.rf
            return pad.rf + plotH.rf * (1f.rf - norm)
          }

          val gridPaint = RemotePaint {
            this.color = Color(0xFF2A2E37.toInt()).rc
            this.style = PaintingStyle.Stroke
            this.strokeWidth = 1f.rf
          }
          val gridPath = remotePath {
            moveTo(mapX(0f), mapY(yMin))
            lineTo(mapX(1f), mapY(yMin))
            lineTo(mapX(1f), mapY(yMax))
            lineTo(mapX(0f), mapY(yMax))
            close()
            val midY = (yMin + yMax) * 0.5f
            moveTo(mapX(0f), mapY(midY))
            lineTo(mapX(1f), mapY(midY))
            moveTo(mapX(0.5f), mapY(yMin))
            lineTo(mapX(0.5f), mapY(yMax))
            if (yMin < 0f && yMax > 0f) {
              moveTo(mapX(0f), mapY(0f))
              lineTo(mapX(1f), mapY(0f))
            }
            if (yMin < 1f && yMax > 1f) {
              moveTo(mapX(0f), mapY(1f))
              lineTo(mapX(1f), mapY(1f))
            }
          }
          usePaint(gridPaint) { remoteCanvas.drawPath(gridPath) }

          val referencePaint = RemotePaint {
            this.color = Color(0xFF38BDF8.toInt()).rc
            this.style = PaintingStyle.Stroke
            this.strokeWidth = 2.5f.rf
            this.strokeCap = StrokeCap.Round
            this.strokeJoin = StrokeJoin.Round
          }
          val referencePath: RemotePath = remotePath {
            for (i in 0..samples) {
              val t = i.toFloat() / samples.toFloat()
              val px = mapX(t)
              val py = mapY(expectedY(t))
              if (i == 0) {
                moveTo(px, py)
              } else {
                lineTo(px, py)
              }
            }
          }
          usePaint(referencePaint) { remoteCanvas.drawPath(referencePath) }

          val currentX = mapLiveX(progressVar)
          val currentY = mapLiveY(liveY(progressVar))

          val scanlinePaint = RemotePaint {
            this.color = Color(0x66FFB300).rc
            this.style = PaintingStyle.Stroke
            this.strokeWidth = 1.25f.rf
          }
          val scanlinePath = remotePath {
            moveTo(currentX, pad.rf)
            lineTo(currentX, (GRAPH_VIRTUAL_SIZE - pad).rf)
          }
          usePaint(scanlinePaint) { remoteCanvas.drawPath(scanlinePath) }

          val radius = 4.5f.rf
          val k = (4.5f * 0.55228475f).rf
          val markerPath = remotePath {
            moveTo(currentX, currentY - radius)
            curveTo(
              currentX + k,
              currentY - radius,
              currentX + radius,
              currentY - k,
              currentX + radius,
              currentY,
            )
            curveTo(
              currentX + radius,
              currentY + k,
              currentX + k,
              currentY + radius,
              currentX,
              currentY + radius,
            )
            curveTo(
              currentX - k,
              currentY + radius,
              currentX - radius,
              currentY + k,
              currentX - radius,
              currentY,
            )
            curveTo(
              currentX - radius,
              currentY - k,
              currentX - k,
              currentY - radius,
              currentX,
              currentY - radius,
            )
            close()
          }
          val markerFillPaint = RemotePaint {
            this.color = Color(0xFFFF5252.toInt()).rc
            this.style = PaintingStyle.Fill
          }
          val markerStrokePaint = RemotePaint {
            this.color = Color.White.rc
            this.style = PaintingStyle.Stroke
            this.strokeWidth = 1.5f.rf
          }
          usePaint(markerFillPaint) { remoteCanvas.drawPath(markerPath) }
          usePaint(markerStrokePaint) { remoteCanvas.drawPath(markerPath) }
        }
      }
    }

  doc.value?.let { document ->
    document.setNamedFloat("progress", progress)
    SideEffect { document.setNamedFloat("progress", progress) }
    RcPlayer(document = document, modifier = modifier)
  }
}

@Composable
internal fun ExpressionGraphAnimatedPreview(
  expectedY: (Float) -> Float,
  liveY: (RemoteFloat) -> RemoteFloat,
  modifier: Modifier = Modifier.size(120.dp),
  yMin: Float = 0f,
  yMax: Float = 1f,
  durationMillis: Int = 1000,
) {
  val progress = remember(durationMillis) { Animatable(0f) }
  LaunchedEffect(progress) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing),
    )
  }
  ExpressionGraphPreview(
    expectedY = expectedY,
    liveY = liveY,
    modifier = modifier,
    progress = progress.value,
    yMin = yMin,
    yMax = yMax,
  )
}

internal object ComplexExpressionSpecs {
  fun cubicBezierSolveY(ax: Float, ay: Float, bx: Float, by: Float, x: Float): Float {
    val clampedX = x.coerceIn(0f, 1f)
    if (clampedX <= 0f) return 0f
    if (clampedX >= 1f) return 1f
    var lo = 0f
    var hi = 1f
    for (iter in 0 until 24) {
      val mid = (lo + hi) * 0.5f
      val u = 1f - mid
      val bezX = 3f * u * u * mid * ax + 3f * u * mid * mid * bx + mid * mid * mid
      if (bezX < clampedX) lo = mid else hi = mid
    }
    val t = (lo + hi) * 0.5f
    val u = 1f - t
    return 3f * u * u * t * ay + 3f * u * t * t * by + t * t * t
  }

  fun expectedGammaLerp(start: Float, stop: Float, fraction: Float): Float {
    fun linear(v: Float): Float =
      if (v <= 0.04045f) v / 12.92f else ((v + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    val f = fraction.coerceIn(0f, 1f)
    val a = linear(start)
    val b = linear(stop)
    val blended = a + (b - a) * f
    val srgb =
      if (blended < 0.0031308f) {
        blended * 12.92f
      } else {
        blended.coerceAtLeast(0f).toDouble().pow(1.0 / 2.4).toFloat() * 1.055f - 0.055f
      }
    return srgb.coerceIn(0f, 1f)
  }

  fun expectedSpatialArcLengthX(progress: Float): Float {
    val p0x = 0f
    val p0y = 0f
    val p1x = 85f
    val p1y = 0f
    val p2x = 100f
    val p2y = 15f
    val p3x = 100f
    val p3y = 100f
    val steps = 256
    val xs = FloatArray(steps + 1)
    val arc = FloatArray(steps + 1)
    var prevX = p0x
    var prevY = p0y
    var acc = 0f
    for (i in 0..steps) {
      val t = i.toFloat() / steps.toFloat()
      val u = 1f - t
      val x = u * u * u * p0x + 3f * u * u * t * p1x + 3f * u * t * t * p2x + t * t * t * p3x
      val y = u * u * u * p0y + 3f * u * u * t * p1y + 3f * u * t * t * p2y + t * t * t * p3y
      if (i > 0) {
        acc += hypot((x - prevX).toDouble(), (y - prevY).toDouble()).toFloat()
      }
      xs[i] = x
      arc[i] = acc
      prevX = x
      prevY = y
    }
    val target = progress.coerceIn(0f, 1f) * acc
    for (i in 0 until steps) {
      val a0 = arc[i]
      val a1 = arc[i + 1]
      if (target <= a1 || i == steps - 1) {
        val local = if (a1 > a0) ((target - a0) / (a1 - a0)).coerceIn(0f, 1f) else 0f
        return (xs[i] + (xs[i + 1] - xs[i]) * local) / 100f
      }
    }
    return 1f
  }

  fun expectedStraightParameter(targetU: Float): Float {
    val u = targetU.coerceIn(0f, 1f)
    var lo = 0f
    var hi = 1f
    for (i in 0 until 24) {
      val mid = (lo + hi) * 0.5f
      val poly = mid * mid * (3f - 2f * mid)
      if (poly < u) lo = mid else hi = mid
    }
    return (lo + hi) * 0.5f
  }

  fun expectedRoundedStraightParameter(targetU: Float): Float {
    val u = targetU.coerceIn(0f, 1f)
    val ts = floatArrayOf(0f, 0.125f, 0.25f, 0.5f, 0.75f, 0.875f, 1f)
    var result = 0f
    for (i in 0 until ts.lastIndex) {
      val t0 = ts[i]
      val t1 = ts[i + 1]
      val s0 = t0 * t0 * (3f - 2f * t0)
      val s1 = t1 * t1 * (3f - 2f * t1)
      val dt = t1 - t0
      val localU = ((u - s0) / (s1 - s0)).coerceIn(0f, 1f)
      result += localU * dt
    }
    return result
  }

  fun expectedRepeaterCompound(progress: Float): Float {
    // Compound scale (1.5^(progress*2)) modulated by repeaterCopyOpacity(progress*3, 4, 100, 20)
    val p = progress.coerceIn(0f, 1f)
    val scale = 1.5.pow((p * 2f).toDouble()).toFloat()
    val fraction = (p * 3f) / 4f
    val opacity = (100f + (20f - 100f) * fraction) / 100f
    return (scale * opacity) / 2.25f
  }

  fun expectedSliceCubicEndY(progress: Float): Float {
    val t = progress.coerceIn(0f, 1f)
    val u = 1f - t
    // Cubic with p0y=0, c1y=1.2, c2y=-0.2, p3y=1.0 evaluated at t
    return 3f * u * u * t * 1.2f + 3f * u * t * t * (-0.2f) + t * t * t * 1.0f
  }
}

// 1. Cubic Bezier S-Curve Easing
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun CubicEasingSCurveGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.cubicBezierSolveY(0.42f, 0f, 0.58f, 1f, x) },
    liveY = { progress -> lookupValueInBezier(0.42f.rf, 0f.rf, 0.58f.rf, 1f.rf, 1f, progress) },
    progress = 0.35f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun CubicEasingSCurveGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.cubicBezierSolveY(0.42f, 0f, 0.58f, 1f, x) },
    liveY = { progress -> lookupValueInBezier(0.42f.rf, 0f.rf, 0.58f.rf, 1f.rf, 1f, progress) },
  )
}

// 2. Overshooting Cubic Bezier Easing
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun CubicEasingOvershootGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.cubicBezierSolveY(0.34f, 1.56f, 0.64f, 1f, x) },
    liveY = { progress -> lookupValueInBezier(0.34f.rf, 1.56f.rf, 0.64f.rf, 1f.rf, 1f, progress) },
    progress = 0.45f,
    yMin = -0.05f,
    yMax = 1.25f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun CubicEasingOvershootGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.cubicBezierSolveY(0.34f, 1.56f, 0.64f, 1f, x) },
    liveY = { progress -> lookupValueInBezier(0.34f.rf, 1.56f.rf, 0.64f.rf, 1f.rf, 1f, progress) },
    yMin = -0.05f,
    yMax = 1.25f,
  )
}

// 3. Linear-Light sRGB Gamma Color Channel Interpolation
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun GammaColorLerpGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedGammaLerp(0.05f, 0.95f, x) },
    liveY = { progress -> gammaLerp(0.05f.rf, 0.95f.rf, progress) },
    progress = 0.5f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun GammaColorLerpGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedGammaLerp(0.05f, 0.95f, x) },
    liveY = { progress -> gammaLerp(0.05f.rf, 0.95f.rf, progress) },
  )
}

// 4. Spatial Cubic Bezier Arc-Length Reparameterization
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun SpatialBezierArcLengthGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedSpatialArcLengthX(x) },
    liveY = { progress ->
      sampleSpatialPosition(
          start = Point(0f.rf, 0f.rf),
          end = Point(100f.rf, 100f.rf),
          outgoing = Point(85f.rf, 0f.rf),
          incoming = Point(0f.rf, (-85f).rf),
          progress = progress,
        )
        .x / 100f.rf
    },
    progress = 0.4f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun SpatialBezierArcLengthGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedSpatialArcLengthX(x) },
    liveY = { progress ->
      sampleSpatialPosition(
          start = Point(0f.rf, 0f.rf),
          end = Point(100f.rf, 100f.rf),
          outgoing = Point(85f.rf, 0f.rf),
          incoming = Point(0f.rf, (-85f).rf),
          progress = progress,
        )
        .x / 100f.rf
    },
  )
}

// 5. Straight Zero-Handle Cubic Arc-Length Parameter Inversion
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun StraightTrimParameterGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedStraightParameter(x) },
    liveY = { progress -> straightParameter(progress) },
    progress = 0.25f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun StraightTrimParameterGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedStraightParameter(x) },
    liveY = { progress -> straightParameter(progress) },
  )
}

// 6. Rounded-Corner Straight Cubic Piecewise Inversion
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun RoundedStraightTrimParameterGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedRoundedStraightParameter(x) },
    liveY = { progress -> roundedStraightParameter(progress) },
    progress = 0.5f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun RoundedStraightTrimParameterGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedRoundedStraightParameter(x) },
    liveY = { progress -> roundedStraightParameter(progress) },
  )
}

// 7. Repeater Compound Scale & Opacity Ramp
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun RepeaterCompoundScaleOpacityGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedRepeaterCompound(x) },
    liveY = { progress ->
      val scale = repeaterCopyScale(150f.rf, progress * 2f.rf)
      val opacity = repeaterCopyOpacity(progress * 3f.rf, 4f.rf, 100f.rf, 20f.rf)
      (scale * opacity) / 2.25f.rf
    },
    progress = 0.6f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun RepeaterCompoundScaleOpacityGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedRepeaterCompound(x) },
    liveY = { progress ->
      val scale = repeaterCopyScale(150f.rf, progress * 2f.rf)
      val opacity = repeaterCopyOpacity(progress * 3f.rf, 4f.rf, 100f.rf, 20f.rf)
      (scale * opacity) / 2.25f.rf
    },
  )
}

// 8. De Casteljau Cubic Subdivision Trajectory
@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@Composable
fun DeCasteljauSliceCubicGraphPreview() {
  ExpressionGraphPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedSliceCubicEndY(x) },
    liveY = { progress ->
      val points = listOf(0f.rf, 0f.rf, 0.25f.rf, 1.2f.rf, 0.75f.rf, (-0.2f).rf, 1f.rf, 1f.rf)
      sliceCubic(points, 0f.rf, progress)[7]
    },
    progress = 0.5f,
    yMin = -0.1f,
    yMax = 1.1f,
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff111318, showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun DeCasteljauSliceCubicGraphAnimatedPreview() {
  ExpressionGraphAnimatedPreview(
    expectedY = { x -> ComplexExpressionSpecs.expectedSliceCubicEndY(x) },
    liveY = { progress ->
      val points = listOf(0f.rf, 0f.rf, 0.25f.rf, 1.2f.rf, 0.75f.rf, (-0.2f).rf, 1f.rf, 1f.rf)
      sliceCubic(points, 0f.rf, progress)[7]
    },
    yMin = -0.1f,
    yMax = 1.1f,
  )
}

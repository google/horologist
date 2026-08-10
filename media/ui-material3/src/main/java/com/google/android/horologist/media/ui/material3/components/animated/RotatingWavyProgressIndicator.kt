/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components.animated

import android.graphics.Path as AndroidPath
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.MutableCubic
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.TransformResult
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.google.android.horologist.media.ui.material3.components.ButtonGroupLayoutDefaults
import com.google.android.horologist.media.ui.material3.components.PlayPauseButtonDefaults
import com.google.android.horologist.media.ui.material3.util.LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.MIDDLE_BUTTON_PROGRESS_AND_BUTTON_GAP
import com.google.android.horologist.media.ui.material3.util.MIDDLE_BUTTON_PROGRESS_STROKE_WIDTH
import com.google.android.horologist.media.ui.material3.util.SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
import com.google.android.horologist.media.ui.material3.util.faster
import com.google.android.horologist.media.ui.material3.util.isLargeScreen
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** Progress indicator with rotating wavy progress. */
@Composable
internal fun RotatingWavyProgressIndicator(
  playing: Boolean,
  progress: State<Float>,
  shapeMorphProgress: State<Float>,
  rotationProgress: State<Float>,
  strokeWidth: Dp,
  colorScheme: ColorScheme = MaterialTheme.colorScheme,
  modifier: Modifier = Modifier,
) {
  val coercedProgress by remember { derivedStateOf { progress.value.coerceIn(0f, 1f) } }

  val indicatorColor by
    remember(colorScheme) { derivedStateOf { colorScheme.onSecondaryContainer } }
  val trackColor by remember(colorScheme) { derivedStateOf { colorScheme.surfaceContainerLow } }

  // Map to store 4 polygons of morph
  val morphState = remember { mutableStateMapOf<Pair<Boolean, Boolean>, Morph>() }

  val density = LocalDensity.current
  val configuration = LocalConfiguration.current

  val stroke =
    remember(density, strokeWidth) {
      Stroke(with(density) { strokeWidth.toPx() }, cap = StrokeCap.Round)
    }

  val scallopShapeSize =
    remember(configuration) {
      if (configuration.isLargeScreen) {
        LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
      } else {
        SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
      }
    }

  val scallopHeight =
    remember(density, scallopShapeSize, strokeWidth) {
      with(density) { (scallopShapeSize - strokeWidth).toPx() }
    }

  val scallopPolygon =
    remember(density, scallopShapeSize) {
      PlayPauseButtonDefaults.outerScallopPolygon(density, scallopShapeSize)
        .scaleToSize(scallopHeight)
    }

  val expansionWidthPx =
    remember(density) { with(density) { ButtonGroupLayoutDefaults.ExpansionWidth.toPx() } }

  val circlePolygon =
    remember(scallopHeight) { PlayPauseButtonDefaults.pillPolygon(scallopHeight, scallopHeight) }
  val expandedPill =
    remember(scallopHeight, expansionWidthPx) {
      PlayPauseButtonDefaults.pillPolygon(scallopHeight + expansionWidthPx, scallopHeight)
    }
  val shrunkPill =
    remember(scallopHeight, expansionWidthPx) {
      PlayPauseButtonDefaults.pillPolygon(scallopHeight - expansionWidthPx, scallopHeight)
    }

  val indicatorPath = remember { Path() } // To draw the indicator
  val trackPath = remember { Path() } // To draw the track
  val postPath = remember {
    Path()
  } // To draw the part of indicator path at the end due its rotation
  val preTrack = remember { Path() } // To draw the part of track path at the start due its rotation
  val androidPathInstance = remember {
    AndroidPath()
  } // Path instance to be used (and reused) for morphing
  val mutableCubic = remember {
    MutableCubic()
  } // Cubic instance to be used (and reused) for morphing
  val pathMeasurer = remember { PathMeasure() }

  Spacer(
    modifier
      .clearAndSetSemantics {}
      .fillMaxSize()
      .focusable()
      .drawWithCache {
        val isWidthGreater = size.width > size.height
        val morph =
          morphState.getOrPut(Pair(isWidthGreater, playing)) {
            Morph(
              if (isWidthGreater) expandedPill else shrunkPill,
              if (playing) scallopPolygon else circlePolygon,
            )
          }
        val path =
          morph
            .toPath(shapeMorphProgress.value, androidPathInstance, mutableCubic)
            .asComposePath()
            .apply { translate(Offset(size.width / 2f, size.height / 2f)) }

        pathMeasurer.setPath(path, true)
        val length = pathMeasurer.length

        val gap = (length * 0.006f) + stroke.width

        indicatorPath.reset()
        pathMeasurer.getSegment(
          startDistance = (length * abs(rotationProgress.value)) + (gap / 2f),
          stopDistance =
            max(
              (length * (coercedProgress - rotationProgress.value)) - (gap / 2f),
              (length * abs(rotationProgress.value)) + (gap / 2f) + 0.1f,
            ),
          destination = indicatorPath,
        )

        trackPath.reset()
        pathMeasurer.getSegment(
          startDistance =
            max(
              (length * (coercedProgress - rotationProgress.value)) + (gap / 2f),
              (length * abs(rotationProgress.value)) + (gap * 1.5f),
            ),
          stopDistance = min(length * (1f - rotationProgress.value) - (gap / 2f), length),
          destination = trackPath,
        )

        postPath.reset()
        pathMeasurer.getSegment(
          startDistance = 0f,
          stopDistance = (length * (coercedProgress - rotationProgress.value - 1)) - (gap / 2f),
          destination = postPath,
        )

        preTrack.reset()
        pathMeasurer.getSegment(
          startDistance = 0f,
          stopDistance = (length * abs(rotationProgress.value)) - (gap / 2f),
          destination = preTrack,
        )

        onDrawBehind {
          rotate(degrees = 360f * rotationProgress.value) {
            drawPath(path = preTrack, color = trackColor, style = stroke)

            drawPath(path = trackPath, color = trackColor, style = stroke)

            drawPath(path = indicatorPath, color = indicatorColor, style = stroke)

            drawPath(path = postPath, color = indicatorColor, style = stroke)
          }
        }
      }
  )
}

@Stable
internal class RotatingMorphedScallopShape(
  private val playing: Boolean,
  private val isLargeScreen: Boolean,
  private val morphProgress: State<Float>,
  private val rotationProgress: State<Float>,
  private val morphState: MutableMap<Pair<Boolean, Boolean>, Morph> =
    mutableStateMapOf<Pair<Boolean, Boolean>, Morph>(),
  private val useInnerPolygon: Boolean = true,
) : Shape {

  private val matrix = Matrix()
  private val path = AndroidPath()
  private val mutableCubic = MutableCubic()

  private val scallopSize =
    if (isLargeScreen) {
      LARGE_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
    } else {
      SMALL_DEVICE_PLAYER_SCREEN_MIDDLE_BUTTON_SIZE
    }

  override fun createOutline(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density,
  ): Outline {
    val isWidthGreater = size.width > size.height
    val morph =
      morphState.getOrPut(Pair(isWidthGreater, playing)) {
        val scallopHeight =
          with(density) {
            (scallopSize -
                if (useInnerPolygon) {
                  MIDDLE_BUTTON_PROGRESS_AND_BUTTON_GAP * 2
                } else {
                  MIDDLE_BUTTON_PROGRESS_STROKE_WIDTH
                })
              .toPx()
          }
        val scallopPolygon =
          if (useInnerPolygon) {
              PlayPauseButtonDefaults.innerScallopPolygon(density, scallopSize)
            } else {
              PlayPauseButtonDefaults.outerScallopPolygon(density, scallopSize)
            }
            .scaleToSize(scallopHeight)

        val expansionWidthPx = with(density) { ButtonGroupLayoutDefaults.ExpansionWidth.toPx() }

        val circlePolygon = PlayPauseButtonDefaults.pillPolygon(scallopHeight, scallopHeight)
        val expandedPill =
          PlayPauseButtonDefaults.pillPolygon(scallopHeight + expansionWidthPx, scallopHeight)
        val shrunkPill =
          PlayPauseButtonDefaults.pillPolygon(scallopHeight - expansionWidthPx, scallopHeight)

        Morph(
          if (isWidthGreater) expandedPill else shrunkPill,
          if (playing) scallopPolygon else circlePolygon,
        )
      }
    matrix.reset()
    path.reset()
    matrix.rotateZ(rotationProgress.value * 360f)
    val morphPath =
      morph.toPath(morphProgress.value, path, mutableCubic).asComposePath().apply {
        transform(matrix)
        translate(Offset(size.width / 2f, size.height / 2f))
      }

    return Outline.Generic(morphPath)
  }
}

@Composable
internal fun animatedScallopShapeProgress(
  transition: Transition<Boolean>,
  onPressAnimationSpec: FiniteAnimationSpec<Float> =
    MaterialTheme.motionScheme.fastSpatialSpec<Float>().faster(100f),
  onReleaseAnimationSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.slowSpatialSpec(),
): State<Float> =
  transition.animateFloat(
    label = "Scallop Shape Progress",
    transitionSpec = {
      when {
        false isTransitioningTo true -> onPressAnimationSpec
        else -> onReleaseAnimationSpec
      }
    },
  ) { pressedTarget ->
    if (pressedTarget) 0f else 1f
  }

internal fun RoundedPolygon.scaleToSize(sizePx: Float): RoundedPolygon {
  val scallopBounds = calculateMaxBounds()
  val scallopHeight = scallopBounds[3] - scallopBounds[1]
  val scaleFactor = sizePx / scallopHeight
  return transformed { x, y -> TransformResult(x * scaleFactor, y * scaleFactor) }
}

private fun Morph.toPath(
  progress: Float,
  path: AndroidPath = AndroidPath(),
  mutableCubic: MutableCubic = MutableCubic(),
): AndroidPath {
  // The first/last mechanism here ensures that the final anchor point in the shape
  // exactly matches the first anchor point. There can be rendering artifacts introduced
  // by those points being slightly off, even by much less than a pixel
  path.rewind()

  var firstX = 0f
  var firstY = 0f
  var prevControl0X = 0f
  var prevControl0Y = 0f
  var prevControl1X = 0f
  var prevControl1Y = 0f
  var prevAnchor1X = 0f
  var prevAnchor1Y = 0f
  var first = true
  forEachCubic(progress, mutableCubic) {
    if (first) {
      path.moveTo(it.anchor0X, it.anchor0Y)
      firstX = it.anchor0X
      firstY = it.anchor0Y
      first = false
    } else {
      // We delay using the current cubic, because we need to do something special for the
      // last one and we can't detect it is the last one until the loop ends.
      path.cubicTo(
        prevControl0X,
        prevControl0Y,
        prevControl1X,
        prevControl1Y,
        prevAnchor1X,
        prevAnchor1Y,
      )
    }
    prevControl0X = it.control0X
    prevControl0Y = it.control0Y
    prevControl1X = it.control1X
    prevControl1Y = it.control1Y
    prevAnchor1X = it.anchor1X
    prevAnchor1Y = it.anchor1Y
  }
  if (!first) {
    path.cubicTo(prevControl0X, prevControl0Y, prevControl1X, prevControl1Y, firstX, firstY)
  }
  path.close()
  return path
}

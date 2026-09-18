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

package com.google.android.horologist.remotecompose.lottie.renderer

import android.annotation.SuppressLint
import androidx.compose.remote.creation.RemotePath
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.remotePath
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ClipOp
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.asset.PrecompAsset
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.GraphicElement
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Ellipse
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.GeometryShape
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Path
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.PolyStar
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.geometry.Rectangle
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Group
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergePaths
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.OffsetPath
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.PuckerBloat
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.Repeater
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.RoundedCorners
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.TrimPath
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.Twist
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.ZigZag
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.Fill
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.FillRule
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.GradientFill
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.GradientStroke
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.Stroke
import com.google.android.horologist.remotecompose.lottie.format.layer.MatteMode
import com.google.android.horologist.remotecompose.lottie.format.layer.PrecompLayer
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.format.mask.Mask
import com.google.android.horologist.remotecompose.lottie.format.mask.MaskMode
import com.google.android.horologist.remotecompose.lottie.renderer.layers.MatteContext
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateBezier
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateColor
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateGradient
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animatePosition
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.RepeatedShapeInstance
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateEllipse
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateMergePaths
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateOffsetPath
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluatePath
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluatePolyStar
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluatePuckerBloat
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateRectangle
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateRepeater
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateRoundedCorners
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateTrimPaths
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateTwist
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.evaluateZigZag
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.individualTrimModifier
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.transformRemoteShape

internal data class StyledShapes(val shapes: List<RemoteShape>, val style: RemoteStyle)

@SuppressLint("RestrictedApi")
private fun evaluateGeometry(shape: GeometryShape, settings: LottieSettings): RemoteLottiePath? =
  (when (shape) {
      is Path -> evaluatePath(shape, settings)
      is Rectangle -> evaluateRectangle(shape, settings)
      is Ellipse -> evaluateEllipse(shape, settings)
      is PolyStar -> evaluatePolyStar(shape, settings)
    })
    ?.withIdentity(GeometryIdentity(shape))

/** Renders a list of Lottie Shapes to the RemoteCanvas. */
@SuppressLint("RestrictedApi")
@Composable
@RemoteComposable
internal fun RenderShapes(
  shapes: List<GraphicElement>,
  transformStack: List<Transform>,
  matteContext: MatteContext? = null,
  layerVisibility: RemoteFloat = 1f.rf,
  masks: List<Mask> = emptyList(),
) {
  val animationSettings = LocalAnimationSettings.current
  val shapeGroups = gatherShapes(shapes, animationSettings)

  // Aspect-ratio scaling and centering is applied once, at the top level, by the
  // drawWithContent modifier in LottieAnimation - shapes draw in raw Lottie coordinates here.
  RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
    val hasMasks = masks.any { it.mode != MaskMode.None && it.path != null }
    val needsSave = matteContext != null || hasMasks
    if (needsSave) {
      remoteCanvas.save()
    }

    if (matteContext != null) {
      applyMatteClip(matteContext, animationSettings, remoteCanvas)
    }

    if (hasMasks) {
      for (transform in transformStack) {
        transform(transform, null, animationSettings, remoteCanvas)
      }
      applyLayerMasks(masks, animationSettings, remoteCanvas)
      for (transform in transformStack.reversed()) {
        inverseTransform(transform, animationSettings, remoteCanvas)
      }
    }

    val layerOpacity =
      (transformStack.lastOrNull()?.opacity?.let { animateScalar(it, animationSettings) / 100f }
        ?: 1f.rf) * layerVisibility

    for (shapeGroup in shapeGroups) {
      val paint = shapeGroup.style.getPaint(layerOpacity)

      for (transform in transformStack) {
        remoteCanvas.save()
        transform(transform, null, animationSettings, remoteCanvas)
      }

      usePaint(paint) {
        remoteCanvas.applyStrokeDetails(shapeGroup.style)
        for (shape in shapeGroup.shapes) {
          shape.draw(this, remoteCanvas, layerOpacity)
        }
      }
      remoteCanvas.applyStrokeDetails(null)

      for (transform in transformStack) {
        remoteCanvas.restore()
      }
    }

    if (needsSave) {
      remoteCanvas.restore()
    }
  }
}

@SuppressLint("RestrictedApi")
internal fun gatherShapes(
  shapes: List<GraphicElement>,
  animationSettings: LottieSettings,
  inheritedStyle: RemoteStyle? = null,
): List<StyledShapes> =
  resolveStyledGeometry(gatherShapesUnresolved(shapes, animationSettings, inheritedStyle))

/** Resolve only at the outer collection boundary, after all ancestor modifiers have run. */
private fun resolveStyledGeometry(groups: List<StyledShapes>): List<StyledShapes> =
  groups.flatMap { group ->
    val shapes =
      group.shapes.map { shape ->
        when (shape) {
          is RemoteLottiePath -> shape.resolveGeometry()
          is RemoteGroup ->
            RemoteGroup(
              resolveStyledGeometry(shape.childShapes),
              shape.animationSettings,
              shape.transform,
              shape.opacityMultiplier,
            )
          else -> shape
        }
      }
    val baseStyle = (group.style as? RemoteStyleWithOpacity)?.baseStyle ?: group.style
    if (baseStyle is RemoteStroke || baseStyle is RemoteGradientStroke) {
      // A collapsed open contour can still draw a round cap. Hide inactive copy slots
      // through stroke opacity as well; fills instead share the combined visible contours.
      shapes.mapIndexed { index, shape ->
        val visibility = (group.shapes[index] as? RemoteLottiePath)?.geometryVisibility
        val style =
          if (visibility != null && visibility.constantValueOrNull != 1f)
            RemoteStyleWithOpacity(group.style, visibility)
          else group.style
        val strokeShape =
          if (
            shape is RemoteLottiePath && shape.path.any { it.visibility.constantValueOrNull != 1f }
          )
            RemoteContourStroke(shape)
          else shape
        StyledShapes(listOf(strokeShape), style)
      }
    } else listOf(group.copy(shapes = compoundFillShapes(shapes, group.style)))
  }

@SuppressLint("RestrictedApi")
private fun gatherShapesUnresolved(
  shapes: List<GraphicElement>,
  animationSettings: LottieSettings,
  inheritedStyle: RemoteStyle? = null,
): List<StyledShapes> {
  val shapeGroups = mutableListOf<StyledShapes>()
  var currentGeometries = mutableListOf<RepeatedShapeInstance>()
  var currentGroups = mutableListOf<RemoteShape>()
  // Greedy modifiers consume authored path operands, not paint views. A compound group or
  // repeater is one operand, even when it contains multiple contours and painted runs.
  var pathOperands = mutableListOf<List<RemoteShape>>()
  var hasEmittedStyle = false

  fun modifyEarlierGeometry(modify: (List<RemoteShape>) -> List<RemoteShape>) {
    // Paints are views of the preceding geometry, not scope boundaries for later modifiers.
    // Update each view independently: paths are immutable, and two paints may own the same path.
    for (i in shapeGroups.indices) {
      val styled = shapeGroups[i]
      shapeGroups[i] = styled.copy(shapes = modify(styled.shapes))
    }
    currentGeometries =
      currentGeometries
        .flatMap { instance -> modify(listOf(instance.shape)).map { instance.copy(shape = it) } }
        .toMutableList()
    currentGroups = modify(currentGroups).toMutableList()
    pathOperands = pathOperands.map(modify).toMutableList()
  }

  for (shape in shapes) {
    // A modifier affects content earlier in its authored list, not later siblings.
    when (shape) {
      is TrimPath -> {
        if (shape.mode == TrimMode.Individually) {
          modifyEarlierGeometry(
            individualTrimModifier(pathOperands.flatten(), shape, animationSettings)
          )
        } else modifyEarlierGeometry { evaluateTrimPaths(it, shape, animationSettings) }
      }
      is RoundedCorners -> {
        if (shape.hidden?.constantValue != true) {
          modifyEarlierGeometry { evaluateRoundedCorners(it, shape, animationSettings) }
        }
      }
      is Repeater -> {
        if (shape.hidden?.constantValue != true) {
          // Exported repeaters follow their fills/strokes. Repeat the already-styled content as
          // a unit so the ramp reaches its paints and the original is not also drawn unmodified.
          if (shapeGroups.isNotEmpty()) {
            val content = RemoteGroup(shapeGroups.reversed(), animationSettings, null)
            val repeated = evaluateRepeater(listOf(content), shape, animationSettings)
            shapeGroups.clear()
            for (instance in repeated) {
              val group = (instance.shape as RemoteGroup).withOpacity(instance.opacityMultiplier)
              shapeGroups.add(StyledShapes(listOf(group), NoopStyle()))
            }
          }
          if (pathOperands.isNotEmpty()) {
            val baseShapes = pathOperands.flatten()
            currentGeometries =
              evaluateRepeater(baseShapes, shape, animationSettings, pathOnly = true)
                .toMutableList()
            currentGroups.clear()
            pathOperands = mutableListOf(currentGeometries.map { it.shape })
          }
        }
      }
      is MergePaths -> {
        val mergedShapes =
          evaluateMergePaths(
            pathOperands.map { mergeOperand(it, animationSettings) },
            shape,
            animationSettings,
          )
        // MergePaths absorbs preceding PathContent, including groups' owned paints. Paints
        // before it no longer have those source paths; later paints consume the merge result.
        shapeGroups.clear()
        currentGroups.clear()
        currentGeometries = mergedShapes.map { RepeatedShapeInstance(it) }.toMutableList()
        pathOperands = mutableListOf(mergedShapes)
        hasEmittedStyle = false
      }
      is ZigZag -> {
        if (shape.hidden?.constantValue != true) {
          modifyEarlierGeometry { evaluateZigZag(it, shape, animationSettings) }
        }
      }
      is PuckerBloat -> {
        if (shape.hidden?.constantValue != true) {
          modifyEarlierGeometry { evaluatePuckerBloat(it, shape, animationSettings) }
        }
      }
      is Twist -> {
        if (shape.hidden?.constantValue != true) {
          modifyEarlierGeometry { evaluateTwist(it, shape, animationSettings) }
        }
      }
      is OffsetPath -> {
        if (shape.hidden?.constantValue != true) {
          modifyEarlierGeometry { evaluateOffsetPath(it, shape, animationSettings) }
        }
      }
      is GeometryShape -> {
        if (hasEmittedStyle) {
          currentGeometries = mutableListOf()
          currentGroups = mutableListOf()
          hasEmittedStyle = false
        }
        val remoteShape = evaluateGeometry(shape, animationSettings)
        if (remoteShape != null) {
          currentGeometries.add(RepeatedShapeInstance(remoteShape))
          pathOperands.add(listOf(remoteShape))
        }
      }
      is Group -> {
        if (hasEmittedStyle) {
          currentGeometries = mutableListOf()
          currentGroups = mutableListOf()
          hasEmittedStyle = false
        }
        val groupShape = group(shape, animationSettings, inheritedStyle)
        if (groupShape != null) {
          shapeGroups.add(StyledShapes(listOf(groupShape), inheritedStyle ?: NoopStyle()))
        }
        // Keep the group's modifier scope from its own position, not the later fill/stroke's.
        val geometries = evaluateGroupGeometries(shape, animationSettings)
        currentGroups.addAll(geometries)
        pathOperands.add(geometries)
      }
      is Fill -> {
        if (shape.hidden?.constantValue != true) {
          val fill = fill(shape, animationSettings)
          emitStyledShapes(shapeGroups, currentGeometries, currentGroups, fill)
          hasEmittedStyle = true
        }
      }
      is Stroke -> {
        if (shape.hidden?.constantValue != true) {
          val stroke = stroke(shape, animationSettings)
          emitStyledShapes(shapeGroups, currentGeometries, currentGroups, stroke)
          hasEmittedStyle = true
        }
      }
      is GradientFill -> {
        if (shape.hidden?.constantValue != true) {
          val gradientFill = gradientFill(shape, animationSettings)
          emitStyledShapes(shapeGroups, currentGeometries, currentGroups, gradientFill)
          hasEmittedStyle = true
        }
      }
      is GradientStroke -> {
        if (shape.hidden?.constantValue != true) {
          val gradientStroke = gradientStroke(shape, animationSettings)
          emitStyledShapes(shapeGroups, currentGeometries, currentGroups, gradientStroke)
          hasEmittedStyle = true
        }
      }
      else -> {} // Transform, other modifiers, unknown elements
    }
  }

  if (inheritedStyle != null && currentGeometries.isNotEmpty()) {
    emitStyledShapes(shapeGroups, currentGeometries, emptyList(), inheritedStyle)
  }

  // In Lottie, elements at higher array indices are at the bottom of the stack and drawn first;
  // elements at lower array indices are at the top of the stack and drawn last.
  return shapeGroups.reversed()
}

@SuppressLint("RestrictedApi")
private fun emitStyledShapes(
  shapeGroups: MutableList<StyledShapes>,
  currentGeometries: List<RepeatedShapeInstance>,
  currentGroups: List<RemoteShape>,
  style: RemoteStyle,
) {
  val fillRule =
    (style as? RemoteFill)?.fillRule
      ?: (style as? RemoteGradientFill)?.fillRule
      ?: (style as? RemoteStyleWithOpacity)?.let {
        (it.baseStyle as? RemoteFill)?.fillRule ?: (it.baseStyle as? RemoteGradientFill)?.fillRule
      }
      ?: FillRule.NonZero

  val styledGeometries =
    if (fillRule != FillRule.NonZero) {
      currentGeometries.map {
        RepeatedShapeInstance(it.shape.withFillRule(fillRule), it.opacityMultiplier)
      }
    } else {
      currentGeometries
    }

  val hasVaryingOpacity = styledGeometries.any { it.opacityMultiplier.constantValueOrNull != 1f }
  val baseStyle = if (style is RemoteStyleWithOpacity) style.baseStyle else style
  val isFill = baseStyle is RemoteFill || baseStyle is RemoteGradientFill
  val styledGroupShapes = currentGroups.map {
    if (fillRule != FillRule.NonZero) it.withFillRule(fillRule) else it
  }
  if (hasVaryingOpacity) {
    for (instance in styledGeometries.reversed()) {
      val instanceStyle =
        if (instance.opacityMultiplier.constantValueOrNull == 1f) {
          style
        } else {
          RemoteStyleWithOpacity(style, instance.opacityMultiplier)
        }
      shapeGroups.add(StyledShapes(listOf(instance.shape), instanceStyle))
    }
  } else if (styledGeometries.isNotEmpty() || (isFill && styledGroupShapes.isNotEmpty())) {
    val contours =
      styledGeometries.map { it.shape } + if (isFill) styledGroupShapes else emptyList()
    shapeGroups.add(StyledShapes(contours, style))
  }

  if (styledGroupShapes.isNotEmpty() && (hasVaryingOpacity || !isFill)) {
    shapeGroups.add(StyledShapes(styledGroupShapes, style))
  }
}

/** One fill operation owns all compatible contours, including their shared winding and opacity. */
private fun compoundFillShapes(shapes: List<RemoteShape>, style: RemoteStyle): List<RemoteShape> {
  val baseStyle = if (style is RemoteStyleWithOpacity) style.baseStyle else style
  if (baseStyle !is RemoteFill && baseStyle !is RemoteGradientFill) return shapes
  val result = mutableListOf<RemoteShape>()
  var pending = mutableListOf<RemoteBezierValue>()
  var rule: FillRule? = null
  fun flush() {
    if (pending.isNotEmpty()) result.add(RemoteLottiePath(pending.toList(), checkNotNull(rule)))
    pending = mutableListOf()
    rule = null
  }
  for (shape in shapes) {
    if (shape is RemoteLottiePath && shape.trim == null) {
      if (pending.isNotEmpty() && shape.fillRule != rule) flush()
      rule = shape.fillRule
      pending.addAll(shape.path)
    } else {
      flush()
      result.add(shape)
    }
  }
  flush()
  return result
}

@SuppressLint("RestrictedApi")
internal fun gatherShapesForTest(
  shapes: List<GraphicElement>,
  animationSettings: LottieSettings,
  inheritedStyle: RemoteStyle? = null,
): List<StyledShapes> = gatherShapes(shapes, animationSettings, inheritedStyle = inheritedStyle)

/** Keep a compound group's contour list as one boolean operand without duplicating its paints. */
private fun mergeOperand(shapes: List<RemoteShape>, settings: LottieSettings): RemoteShape =
  shapes.singleOrNull() ?: RemoteGroup(listOf(StyledShapes(shapes, NoopStyle())), settings, null)

@SuppressLint("RestrictedApi")
private fun evaluateGroupGeometries(
  group: Group,
  animationSettings: LottieSettings,
): List<RemoteShape> {
  if (group.hidden?.constantValue == true) return emptyList()
  val groupTransform = group.shapes.filterIsInstance<Transform>().firstOrNull()
  var operands = mutableListOf<List<RemoteShape>>()
  fun modify(modifier: (List<RemoteShape>) -> List<RemoteShape>) {
    operands = operands.map(modifier).toMutableList()
  }
  for (shape in group.shapes) {
    when (shape) {
      is GeometryShape -> {
        val remoteShape = evaluateGeometry(shape, animationSettings)
        if (remoteShape != null) {
          operands.add(listOf(remoteShape))
        }
      }
      is Group -> {
        val nestedGeometries = evaluateGroupGeometries(shape, animationSettings)
        operands.add(nestedGeometries)
      }
      is TrimPath -> {
        if (shape.mode == TrimMode.Individually) {
          modify(individualTrimModifier(operands.flatten(), shape, animationSettings))
        } else modify { evaluateTrimPaths(it, shape, animationSettings) }
      }
      is RoundedCorners -> {
        if (shape.hidden?.constantValue != true) {
          modify { evaluateRoundedCorners(it, shape, animationSettings) }
        }
      }
      is Repeater -> {
        if (shape.hidden?.constantValue != true && operands.isNotEmpty()) {
          operands =
            mutableListOf(
              evaluateRepeater(operands.flatten(), shape, animationSettings, pathOnly = true).map {
                it.shape
              }
            )
        }
      }
      is MergePaths -> {
        operands =
          mutableListOf(
            evaluateMergePaths(
              operands.map { mergeOperand(it, animationSettings) },
              shape,
              animationSettings,
            )
          )
      }
      is ZigZag -> {
        if (shape.hidden?.constantValue != true) {
          modify { evaluateZigZag(it, shape, animationSettings) }
        }
      }
      is PuckerBloat -> {
        if (shape.hidden?.constantValue != true) {
          modify { evaluatePuckerBloat(it, shape, animationSettings) }
        }
      }
      is Twist -> {
        if (shape.hidden?.constantValue != true) {
          modify { evaluateTwist(it, shape, animationSettings) }
        }
      }
      is OffsetPath -> {
        if (shape.hidden?.constantValue != true) {
          modify { evaluateOffsetPath(it, shape, animationSettings) }
        }
      }
      else -> {}
    }
  }
  val geometries = operands.flatten()
  if (groupTransform != null) {
    return geometries.map { transformRemoteShape(it, groupTransform, animationSettings) }
  }
  return geometries
}

@SuppressLint("RestrictedApi")
private fun group(
  group: Group,
  animationSettings: LottieSettings,
  inheritedStyle: RemoteStyle? = null,
): RemoteGroup? {
  if (group.hidden?.constantValue == true) {
    return null
  }

  val transform = group.shapes.filterIsInstance<Transform>().firstOrNull()
  val contentShapes = group.shapes.filter { it !is Transform }
  val styledShapes = gatherShapesUnresolved(contentShapes, animationSettings, inheritedStyle)
  if (styledShapes.isEmpty()) {
    return null
  }
  return RemoteGroup(styledShapes, animationSettings, transform)
}

@SuppressLint("RestrictedApi")
private fun fill(fill: Fill, animationSettings: LottieSettings): RemoteFill {
  val fillColor = animateColor(fill.color, animationSettings)
  val opacity = animateScalar(fill.opacity, animationSettings)
  return RemoteFill(fillColor, opacity, fill.fillRule ?: FillRule.NonZero)
}

@SuppressLint("RestrictedApi")
private fun stroke(stroke: Stroke, animationSettings: LottieSettings): RemoteStroke {
  val strokeColor = animateColor(stroke.color, animationSettings)
  val strokeWidth = animateScalar(stroke.strokeWidth, animationSettings)
  val opacity = animateScalar(stroke.opacity, animationSettings)
  val miterLimit =
    stroke.miterLimitAnimatable?.let { animateScalar(it, animationSettings) }
      ?: stroke.miterLimit.rf
  val dashPattern = createDashPathEffect(stroke.dashes, animationSettings)
  return RemoteStroke(
    strokeColor = strokeColor,
    strokeWidth = strokeWidth,
    opacity = opacity,
    lineCap = stroke.lineCap,
    lineJoin = stroke.lineJoin,
    miterLimit = miterLimit,
    dashPattern = dashPattern,
  )
}

@SuppressLint("RestrictedApi")
private fun gradientFill(
  fill: GradientFill,
  animationSettings: LottieSettings,
): RemoteGradientFill {
  val startPoint = animatePosition(fill.startPoint, animationSettings)
  val endPoint = animatePosition(fill.endPoint, animationSettings)
  val gradient = animateGradient(fill.colors, animationSettings)
  val opacity = animateScalar(fill.opacity, animationSettings)
  return RemoteGradientFill(
    gradient = gradient,
    startPoint = startPoint,
    endPoint = endPoint,
    gradientType = fill.gradientType,
    opacity = opacity,
    fillRule = fill.fillRule ?: FillRule.NonZero,
  )
}

@SuppressLint("RestrictedApi")
private fun gradientStroke(
  stroke: GradientStroke,
  animationSettings: LottieSettings,
): RemoteGradientStroke {
  val startPoint = animatePosition(stroke.startPoint, animationSettings)
  val endPoint = animatePosition(stroke.endPoint, animationSettings)
  val gradient = animateGradient(stroke.colors, animationSettings)
  val opacity = animateScalar(stroke.opacity, animationSettings)
  val strokeWidth = animateScalar(stroke.strokeWidth, animationSettings)
  val miterLimit =
    stroke.miterLimitAnimatable?.let { animateScalar(it, animationSettings) }
      ?: stroke.miterLimit.rf
  val dashPattern = createDashPathEffect(stroke.dashes, animationSettings)
  return RemoteGradientStroke(
    gradient = gradient,
    startPoint = startPoint,
    endPoint = endPoint,
    gradientType = stroke.gradientType,
    opacity = opacity,
    strokeWidth = strokeWidth,
    lineCap = stroke.lineCap,
    lineJoin = stroke.lineJoin,
    miterLimit = miterLimit,
    dashPattern = dashPattern,
  )
}

private fun MutableList<RemoteShape>.addIfNotNull(shape: RemoteShape?) {
  if (shape != null) {
    this.add(shape)
  }
}

@SuppressLint("RestrictedApi")
internal fun applyLayerMasks(
  masks: List<Mask>,
  animationSettings: LottieSettings,
  canvas: RemoteCanvas,
) {
  val nonInvertedAddSubpaths = mutableListOf<RemoteBezierValue>()

  for (mask in masks) {
    if (mask.mode == MaskMode.None) continue
    val maskPath = mask.path ?: continue
    val bezierList = animateBezier(maskPath, animationSettings)
    if (bezierList.isEmpty()) continue

    if (mask.mode == MaskMode.Add && !mask.inverted) {
      nonInvertedAddSubpaths.addAll(bezierList)
    } else {
      val rcPath = canvas.buildRemotePathFromBezier(bezierList)
      val clipOp =
        when (mask.mode) {
          MaskMode.Subtract -> if (mask.inverted) ClipOp.Intersect else ClipOp.Difference
          MaskMode.Add -> ClipOp.Difference
          MaskMode.Intersect -> if (mask.inverted) ClipOp.Difference else ClipOp.Intersect
          MaskMode.Difference -> ClipOp.Difference
          MaskMode.Lighten,
          MaskMode.Darken,
          MaskMode.None,
          MaskMode.Unknown -> continue
        }
      canvas.clipPath(rcPath, clipOp)
    }
  }

  if (nonInvertedAddSubpaths.isNotEmpty()) {
    val compositeAddPath = canvas.buildRemotePathFromBezier(nonInvertedAddSubpaths)
    canvas.clipPath(compositeAddPath, ClipOp.Intersect)
  }
}

@SuppressLint("RestrictedApi")
internal fun applyMatteClip(
  matteContext: MatteContext,
  animationSettings: LottieSettings,
  canvas: RemoteCanvas,
) {
  val matteLayer = matteContext.matteLayer
  val matteTransform = matteLayer.transform
  val layerTransforms =
    if (matteTransform != null) {
      matteContext.matteTransforms + matteTransform
    } else {
      matteContext.matteTransforms
    }

  for (transform in layerTransforms) {
    transform(transform, null, animationSettings, canvas)
  }

  val clipOp =
    if (
      matteContext.matteMode == MatteMode.InvertedAlpha ||
        matteContext.matteMode == MatteMode.InvertedLuma
    ) {
      ClipOp.Difference
    } else {
      ClipOp.Intersect
    }

  when (matteLayer) {
    is ShapeLayer -> clipShapes(matteLayer.shapes, animationSettings, canvas, clipOp)
    is com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer -> {
      val rcPath = RemotePath()
      rcPath.reset()
      rcPath.moveTo(0f, 0f)
      rcPath.lineTo(matteLayer.solidWidth.constantValue.toFloat(), 0f)
      rcPath.lineTo(
        matteLayer.solidWidth.constantValue.toFloat(),
        matteLayer.solidHeight.constantValue.toFloat(),
      )
      rcPath.lineTo(0f, matteLayer.solidHeight.constantValue.toFloat())
      rcPath.close()
      canvas.clipPath(rcPath, clipOp)
    }
    is PrecompLayer -> {
      val asset = animationSettings.assets[matteLayer.refId] as? PrecompAsset
      if (asset != null) {
        for (childLayer in asset.layers) {
          if (childLayer.hidden?.constantValue == true) continue
          if (childLayer is ShapeLayer) {
            val childTransforms = childLayer.transform?.let { listOf(it) } ?: emptyList()
            for (t in childTransforms) {
              transform(t, null, animationSettings, canvas)
            }
            clipShapes(childLayer.shapes, animationSettings, canvas, clipOp)
            for (t in childTransforms.reversed()) {
              inverseTransform(t, animationSettings, canvas)
            }
          } else if (
            childLayer
              is com.google.android.horologist.remotecompose.lottie.format.layer.SolidColorLayer
          ) {
            val childTransforms = childLayer.transform?.let { listOf(it) } ?: emptyList()
            for (t in childTransforms) {
              transform(t, null, animationSettings, canvas)
            }
            val rcPath =
              RemotePath().apply {
                reset()
                moveTo(0f, 0f)
                lineTo(childLayer.solidWidth.constantValue.toFloat(), 0f)
                lineTo(
                  childLayer.solidWidth.constantValue.toFloat(),
                  childLayer.solidHeight.constantValue.toFloat(),
                )
                lineTo(0f, childLayer.solidHeight.constantValue.toFloat())
                close()
              }
            canvas.clipPath(rcPath, clipOp)
            for (t in childTransforms.reversed()) {
              inverseTransform(t, animationSettings, canvas)
            }
          }
        }
      }
    }
    else -> {}
  }

  for (transform in layerTransforms.reversed()) {
    inverseTransform(transform, animationSettings, canvas)
  }
}

@SuppressLint("RestrictedApi")
private fun clipShapes(
  shapes: List<GraphicElement>,
  animationSettings: LottieSettings,
  canvas: RemoteCanvas,
  clipOp: ClipOp = ClipOp.Intersect,
) {
  for (shape in shapes) {
    if (shape.hidden?.constantValue == true) continue
    when (shape) {
      is Rectangle -> {
        val lottiePath = evaluateRectangle(shape, animationSettings)
        if (lottiePath != null) {
          val rcPath = canvas.buildRemotePathFromBezier(lottiePath.path)
          canvas.clipPath(rcPath, clipOp)
        }
      }
      is Path -> {
        val lottiePath = evaluatePath(shape, animationSettings, null)
        if (lottiePath != null) {
          val rcPath = canvas.buildRemotePathFromBezier(lottiePath.path)
          canvas.clipPath(rcPath, clipOp)
        }
      }
      is Ellipse -> {
        val lottiePath = evaluateEllipse(shape, animationSettings)
        if (lottiePath != null) {
          val rcPath = canvas.buildRemotePathFromBezier(lottiePath.path)
          canvas.clipPath(rcPath, clipOp)
        }
      }
      is PolyStar -> {
        val lottiePath = evaluatePolyStar(shape, animationSettings)
        if (lottiePath != null) {
          val rcPath = canvas.buildRemotePathFromBezier(lottiePath.path)
          canvas.clipPath(rcPath, clipOp)
        }
      }
      is Group -> {
        val groupTransform = shape.shapes.filterIsInstance<Transform>().firstOrNull()
        if (groupTransform != null) {
          transform(groupTransform, null, animationSettings, canvas)
          clipShapes(shape.shapes.filter { it !is Transform }, animationSettings, canvas, clipOp)
          inverseTransform(groupTransform, animationSettings, canvas)
        } else {
          clipShapes(shape.shapes, animationSettings, canvas, clipOp)
        }
      }
      else -> {}
    }
  }
}

@SuppressLint("RestrictedApi")
/** Records a clipping path while retaining vertex and tangent dependencies for playback. */
internal fun RemoteCanvas.buildRemotePathFromBezier(path: List<RemoteBezierValue>): RemotePath =
  remotePath {
    for (subpath in path) {
      val vertices = subpath.vertices
      val inTangents = subpath.inTangents
      val outTangents = subpath.outTangents

      if (vertices.isEmpty()) continue

      val startX = vertices[0].getOrElse(0) { 0f.rf }
      val startY = vertices[0].getOrElse(1) { 0f.rf }
      moveTo(startX, startY)

      val maxIndex = if (subpath.closed) vertices.size else vertices.size - 1
      for (i in 0 until maxIndex) {
        val p0 = vertices[i]
        val lastIndex = if (i == vertices.size - 1 && subpath.closed) 0 else i + 1
        val p4 = vertices[lastIndex]
        val inTangent = inTangents.getOrNull(lastIndex)
        val outTangent = outTangents.getOrNull(i)

        val p0x = p0.getOrElse(0) { 0f.rf }
        val p0y = p0.getOrElse(1) { 0f.rf }
        val p4x = p4.getOrElse(0) { 0f.rf }
        val p4y = p4.getOrElse(1) { 0f.rf }

        val inTangentX = inTangent?.getOrElse(0) { 0f.rf } ?: 0f.rf
        val inTangentY = inTangent?.getOrElse(1) { 0f.rf } ?: 0f.rf
        val outTangentX = outTangent?.getOrElse(0) { 0f.rf } ?: 0f.rf
        val outTangentY = outTangent?.getOrElse(1) { 0f.rf } ?: 0f.rf

        val p1x = p0x + outTangentX
        val p1y = p0y + outTangentY
        val p2x = p4x + inTangentX
        val p2y = p4y + inTangentY

        curveTo(p1x, p1y, p2x, p2y, p4x, p4y)
      }

      if (subpath.closed) {
        close()
      }
    }
  }

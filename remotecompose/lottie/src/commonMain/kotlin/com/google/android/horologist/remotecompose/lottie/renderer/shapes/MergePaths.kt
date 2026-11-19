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

package com.google.android.horologist.remotecompose.lottie.renderer.shapes

import androidx.compose.remote.core.operations.PathCombine
import com.google.android.horologist.remotecompose.lottie.LottieSettings
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergeMode
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.modifiers.MergePaths
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.FillRule
import com.google.android.horologist.remotecompose.lottie.renderer.GeometryIdentity
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteBooleanPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteCompiledPath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteGroup
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteShape
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue

/**
 * Evaluates a [MergePaths] modifier across a collection of [RemoteShape] geometries, performing
 * either contour concatenation (Merge) or boolean path operations (Add, Subtract, Intersect,
 * ExcludeIntersections).
 */
@Suppress("RestrictedApi")
internal fun evaluateMergePaths(
  shapes: List<RemoteShape>,
  mergePaths: MergePaths,
  animationSettings: LottieSettings,
): List<RemoteShape> {
  if (mergePaths.hidden?.constantValue == true || shapes.isEmpty()) return emptyList()

  val fillRule =
    (shapes.firstOrNull() as? RemoteLottiePath)?.fillRule
      ?: (shapes.firstOrNull() as? RemoteCompiledPath)?.fillRule
      ?: FillRule.NonZero

  return (when (mergePaths.mode) {
      MergeMode.Merge -> {
        listOf(concatenateOperands(shapes.map(::normalizeMergeOperand), fillRule))
      }
      MergeMode.Add,
      MergeMode.Subtract,
      MergeMode.Intersect,
      MergeMode.ExcludeIntersections -> {
        val operands = shapes.map(::normalizeMergeOperand)
        // Lottie combines the last authored operand with the concatenation of all earlier
        // contours. It does not reduce a sequence of pairwise boolean operations.
        val remainder = concatenateOperands(operands.dropLast(1))
        val last = operands.last()
        val live = operands.any { operand ->
          operand is RemoteBooleanPath ||
            (operand as RemoteLottiePath).path.any { path ->
              (path.vertices + path.inTangents + path.outTangents).flatten().any {
                it.constantValueOrNull == null
              }
            }
        }
        if (live) {
          val remoteOp =
            when (mergePaths.mode) {
              MergeMode.Add -> PathCombine.OP_UNION
              MergeMode.Subtract -> PathCombine.OP_DIFFERENCE
              MergeMode.Intersect -> PathCombine.OP_INTERSECT
              MergeMode.ExcludeIntersections -> PathCombine.OP_XOR
              else -> error("Not a boolean operation")
            }
          return listOf(RemoteBooleanPath(remainder, last, remoteOp))
        }
        evaluateStaticBooleanMergePaths(mergePaths.mode, remainder, last)
      }
    })
    .map { if (it is RemoteLottiePath) it.withIdentity(GeometryIdentity(mergePaths)) else it }
}

internal expect fun evaluateStaticBooleanMergePaths(
  mode: MergeMode,
  remainder: RemoteShape,
  last: RemoteShape,
): List<RemoteShape>

/** Retains operation trees and group boundaries without extracting live result vertices. */
private fun normalizeMergeOperand(shape: RemoteShape): RemoteShape =
  when (shape) {
    is RemoteBooleanPath -> shape
    is RemoteLottiePath -> {
      val source = shape.materializeTrim()
      require(source.trim == null) { "Merges of playback-trimmed paths are not yet supported" }
      source.resolveGeometry()
    }
    is RemoteGroup -> {
      val operand =
        concatenateOperands(shape.childShapes.flatMap { it.shapes }.map(::normalizeMergeOperand))
      if (shape.transform == null) operand
      else
        transformRemoteShape(operand, shape.transform, shape.animationSettings)
          .let(::normalizeMergeOperand)
    }
    else -> RemoteLottiePath(shapeToBezierValues(shape))
  }

private fun concatenateOperands(
  shapes: List<RemoteShape>,
  fillRule: FillRule = FillRule.NonZero,
): RemoteShape {
  if (shapes.size == 1) return shapes.single()
  // Union is not contour concatenation: winding and cancellation may differ. Alpha19's
  // pathAppend accepts coordinates, not the path IDs produced by pathCombine.
  require(shapes.all { it is RemoteLottiePath }) {
    "Runtime Boolean contour concatenation is not yet supported"
  }
  return RemoteLottiePath(shapes.flatMap { (it as RemoteLottiePath).path }, fillRule)
}

@Suppress("RestrictedApi")
internal fun shapeToBezierValues(shape: RemoteShape): List<RemoteBezierValue> {
  return when (shape) {
    is RemoteBooleanPath -> error("A live boolean result cannot be converted to Bezier vertices")
    is RemoteLottiePath -> shape.resolveGeometry().path
    is RemoteGroup -> {
      val subpaths = mutableListOf<RemoteBezierValue>()
      for (group in shape.childShapes) {
        for (child in group.shapes) {
          subpaths.addAll(shapeToBezierValues(child))
        }
      }
      if (shape.transform == null) subpaths
      else subpaths.map { transformBezierValue(it, shape.transform, shape.animationSettings) }
    }
    else -> emptyList()
  }
}

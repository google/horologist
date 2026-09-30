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
import androidx.compose.remote.creation.compose.layout.RemoteDrawScope
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.styles.FillRule

/** Combines live path coordinates on the player, without converting them to a CPU snapshot. */
@SuppressLint("RestrictedApi")
internal class RemoteBooleanPath(
  val remainder: RemoteShape,
  val last: RemoteShape,
  val operation: Byte,
) : RemoteShape {
  @Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
  override fun draw(
    drawScope: RemoteDrawScope,
    canvas: RemoteCanvas,
    inheritedOpacity: RemoteFloat,
  ) {
    // Build leaves before recording and register every one as a dependency. A nested
    // combine consumes the earlier combine's live path ID, not a recording-time snapshot.
    val leaves = mutableMapOf<RemoteLottiePath, RemotePath>()
    fun prepare(shape: RemoteShape) {
      when (shape) {
        is RemoteLottiePath -> leaves.getOrPut(shape) { shape.asRemotePath(drawScope) }
        is RemoteBooleanPath -> {
          prepare(shape.remainder)
          prepare(shape.last)
        }
        else -> error("Boolean operands require normalized path geometry")
      }
    }
    prepare(this)
    val op =
      canvas.internalCanvas.recordRenderingOp(null as RemotePaint?) {
        fun record(shape: RemoteShape): Int =
          when (shape) {
            // Keep operand winding separate from the later fill style's winding.
            is RemoteLottiePath ->
              canvas.document.addPathData(
                leaves.getValue(shape),
                if (shape.fillRule == FillRule.EvenOdd) 1 else 0,
              )
            is RemoteBooleanPath ->
              canvas.document.pathCombine(
                record(shape.remainder),
                record(shape.last),
                shape.operation,
              )
            else -> error("Boolean operands require normalized path geometry")
          }
        canvas.document.drawPath(record(this@RemoteBooleanPath))
      }
    canvas.internalCanvas.buffer.addRoots(op, *leaves.values.toTypedArray())
  }

  // The player retains the boolean operation's result winding. Reinterpreting its contours
  // with the fill style's winding can fill holes which the operation explicitly excluded.
}

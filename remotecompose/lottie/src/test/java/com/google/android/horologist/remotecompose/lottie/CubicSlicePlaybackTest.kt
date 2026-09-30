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

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteState
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.remote.player.view.RemoteComposePlayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.cubicSliceFunction
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test

class CubicSlicePlaybackTest : MotionPixelHarness() {
  @Test
  fun chainedSlicesPreserveEightOutputsThroughBackwardAndEmptySeeks() {
    val curve = listOf(0f, 0f, 4f, 12f, 8f, -6f, 12f, 3f)
    var player: RemoteComposePlayer? = null
    var recordings = 0
    fun name(call: Int, coordinate: Int) =
      RemoteState.Domain.User.prefixed("slice-$call-$coordinate")
    composeRule.setContent {
      val document =
        rememberRemoteDocument(profile = LottieProfiles.NoRuntimeShaders) {
          val start = rememberNamedRemoteFloat("start") { 0f.rf }
          val end = rememberNamedRemoteFloat("end") { 1f.rf }
          RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
            val canvas = remoteCanvas.internalCanvas
            val operation = canvas.recordRenderingOp {
              recordings++
              val writer = canvas.creationState.document
              val full = cubicSliceFunction(curve.map { it.rf } + listOf(start, end))
              val half = cubicSliceFunction(full + listOf(0f.rf, .5f.rf))
              for ((call, values) in listOf(full, half).withIndex()) {
                for ((coordinate, value) in values.withIndex()) {
                  writer.setFloatName(
                    value.getIdForCreationState(canvas.creationState),
                    name(call, coordinate),
                  )
                }
              }
              writer.drawRect(0f, 0f, 8f, 8f)
            }
            canvas.buffer.addRoots(operation, start, end)
          }
        }
      Box(Modifier.size(64.dp).testTag("motion")) {
        document.value?.let {
          RemoteDocumentPlayer(
            it,
            modifier = Modifier.size(64.dp),
            documentWidth = 64,
            documentHeight = 64,
            init = { player = it },
          )
        }
      }
    }
    capture("motion").recycle()
    for ((start, end) in listOf(0f to 1f, .2f to .8f, .4f to .4f, 1f to 0f, 0f to 1f, .2f to .8f)) {
      composeRule.runOnIdle {
        checkNotNull(player).setUserLocalFloat("start", start)
        checkNotNull(player).setUserLocalFloat("end", end)
      }
      capture("motion").recycle()
      for ((call, cutEnd) in listOf(end, (start + end) / 2f).withIndex()) {
        val expected = bernsteinSlice(curve, start.toDouble(), cutEnd.toDouble())
        for (coordinate in expected.indices) {
          assertWithMessage("call=$call, range=$start..$cutEnd, coordinate=$coordinate")
            .that(checkNotNull(player).getNamedFloat(name(call, coordinate)))
            .isWithin(.0001f)
            .of(expected[coordinate].toFloat())
        }
      }
    }
    assertThat(recordings).isEqualTo(1)
  }

  // Independent double-precision Bernstein evaluation, not the production de Casteljau operations.
  private fun bernsteinSlice(p: List<Float>, start: Double, end: Double): DoubleArray {
    val output = DoubleArray(8)
    for (axis in 0..1) {
      fun point(t: Double): Double {
        val u = 1 - t
        return p[axis] * u * u * u +
          3.0 * p[axis + 2] * t * u * u +
          3.0 * p[axis + 4] * t * t * u +
          p[axis + 6] * t * t * t
      }
      fun tangent(t: Double): Double {
        val u = 1 - t
        return 3.0 * (p[axis + 2] - p[axis]) * u * u +
          6.0 * (p[axis + 4] - p[axis + 2]) * t * u +
          3.0 * (p[axis + 6] - p[axis + 4]) * t * t
      }
      output[axis] = point(start)
      output[axis + 2] = point(start) + tangent(start) * (end - start) / 3
      output[axis + 4] = point(end) - tangent(end) * (end - start) / 3
      output[axis + 6] = point(end)
    }
    return output
  }
}

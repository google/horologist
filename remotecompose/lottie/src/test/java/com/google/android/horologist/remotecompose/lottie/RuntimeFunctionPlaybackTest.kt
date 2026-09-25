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
import androidx.compose.remote.core.operations.FloatExpression
import androidx.compose.remote.core.operations.Utils
import androidx.compose.remote.core.operations.utilities.AnimatedFloatExpression
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
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteFloatFunction
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test

/** RC execution-order contracts needed by a bounded, shared geometry solver. */
class RuntimeFunctionPlaybackTest : MotionPixelHarness() {
  @Test fun functionCallsPreserveEarlierResults() = verify(loop = false)

  @Test fun functionLoopsRefreshMutableState() = verify(loop = true)

  @Test
  fun wrappedFunctionSharesItsDefinitionAndSnapshotsMultipleOutputs() =
    verify(loop = false, wrapped = true)

  private fun verify(loop: Boolean, wrapped: Boolean = false) {
    var player: RemoteComposePlayer? = null
    var recordings = 0
    var definitions = 0
    val wrappedFunction =
      RemoteFloatFunction(1, 2) { writer, args ->
        with(writer) {
          definitions++
          val first = floatExpression(args[0], 2f, AnimatedFloatExpression.MUL)
          floatArrayOf(first, floatExpression(first, 2f, AnimatedFloatExpression.ADD))
        }
      }
    fun name(suffix: String) = RemoteState.Domain.User.prefixed("function-$suffix")
    composeRule.setContent {
      val doc =
        rememberRemoteDocument(profile = LottieProfiles.NoRuntimeShaders) {
          val input = rememberNamedRemoteFloat("input") { 0f.rf }
          RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
            val canvas = remoteCanvas.internalCanvas
            val op = canvas.recordRenderingOp {
              recordings++
              val writer = canvas.creationState.document
              if (wrapped) {
                val results = wrappedFunction(listOf(input))
                val composed = wrappedFunction(listOf(results[0]))[0]
                for ((suffix, value) in
                  listOf("first" to results[0], "second" to results[1], "composed" to composed)) {
                  writer.setFloatName(
                    value.getIdForCreationState(canvas.creationState),
                    name(suffix),
                  )
                }
                writer.drawRect(
                  0f,
                  0f,
                  results[0].getFloatIdForCreationState(canvas.creationState),
                  8f,
                )
                return@recordRenderingOp
              }
              val x = input.getFloatIdForCreationState(canvas.creationState)
              val args = FloatArray(1)
              val function = writer.createFloatFunction(args)
              val result =
                if (loop) {
                  val accumulator = writer.createFloatId()
                  FloatExpression.apply(
                    writer.buffer.buffer,
                    Utils.idFromNan(accumulator),
                    floatArrayOf(0f),
                    null,
                  )
                  val index = writer.startLoopVar(0f, 1f, 4f)
                  FloatExpression.apply(
                    writer.buffer.buffer,
                    Utils.idFromNan(accumulator),
                    floatArrayOf(
                      accumulator,
                      args[0],
                      AnimatedFloatExpression.ADD,
                      index,
                      AnimatedFloatExpression.ADD,
                    ),
                    null,
                  )
                  writer.endLoop()
                  accumulator
                } else writer.floatExpression(args[0], 2f, AnimatedFloatExpression.MUL)
              writer.endFloatFunction()
              writer.callFloatFunction(function, x)
              val first = writer.floatExpression(result)
              writer.setFloatName(Utils.idFromNan(first), name("first"))
              val shifted = writer.floatExpression(x, 1f, AnimatedFloatExpression.ADD)
              writer.callFloatFunction(function, shifted)
              val second = writer.floatExpression(result)
              writer.setFloatName(Utils.idFromNan(second), name("second"))
              writer.callFloatFunction(function, first)
              val composed = writer.floatExpression(result)
              writer.setFloatName(Utils.idFromNan(composed), name("composed"))
              writer.drawRect(0f, 0f, first, 8f)
              writer.drawRect(0f, 12f, second, 20f)
            }
            canvas.buffer.addRoots(op, input)
          }
        }
      Box(Modifier.size(64.dp).testTag("motion")) {
        doc.value?.let {
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
    fun expected(x: Float) = if (loop) 4f * x + 6f else 2f * x
    for (x in listOf(0f, 3f, -2f, 3f, 0f, 0f)) {
      composeRule.runOnIdle { checkNotNull(player).setUserLocalFloat("input", x) }
      capture("motion").recycle()
      for ((suffix, value) in
        listOf(
          "first" to expected(x),
          "second" to expected(x + 1f),
          "composed" to expected(expected(x)),
        )) {
        assertWithMessage("loop=$loop, input=$x, output=$suffix")
          .that(checkNotNull(player).getNamedFloat(name(suffix)))
          .isWithin(0.00001f)
          .of(value)
      }
    }
    assertWithMessage("playback must not rerecord the function").that(recordings).isEqualTo(1)
    if (wrapped) assertWithMessage("one definition per recording").that(definitions).isEqualTo(1)
  }
}

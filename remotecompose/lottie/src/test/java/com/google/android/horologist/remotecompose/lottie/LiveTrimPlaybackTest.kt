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

@file:Suppress("RestrictedApi")

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.RemoteState
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.remote.player.view.RemoteComposePlayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.RemotePathTrim
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue
import com.google.android.horologist.remotecompose.lottie.renderer.shapes.materializeTrim
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test

/** Checks actual player floats against an analytic cut, independently of raster tolerances. */
class LiveTrimPlaybackTest : MotionPixelHarness() {
  @Test
  fun symmetricMovingCurveCutsAtItsAnalyticMidpoint() {
    var player: RemoteComposePlayer? = null
    val names = mutableSetOf<String>()
    composeRule.setContent {
      val document =
        rememberRemoteDocument(profile = LottieProfiles.NoRuntimeShaders) {
          val f = rememberNamedRemoteFloat("shape") { 0f.rf }
          // Reflection symmetry makes half arc length exactly t=1/2 at every live shape.
          val source =
            RemoteBezierValue(
              false,
              listOf(listOf(0f.rf, 0f.rf), listOf(-8f.rf * f, -24f.rf + 8f.rf * f)),
              listOf(listOf(8f.rf * f, -24f.rf + 8f.rf * f), listOf(0f.rf, 0f.rf)),
              listOf(listOf(8f.rf, 32f.rf), listOf(56f.rf, 32f.rf)),
            )
          val result =
            RemoteLottiePath(listOf(source), trim = RemotePathTrim(0f.rf, .5f.rf, 0f.rf))
              .materializeTrim()
              .path
              .single()
          fun named(kind: String, points: List<List<RemoteFloat>>) = points.mapIndexed { i, p ->
            p.mapIndexed { j, value ->
              val name = "$kind-$i-$j"
              if (value.constantValueOrNull == null) names.add(name)
              RemoteFloat.createNamedRemoteFloatExpression(name, RemoteState.Domain.User) { value }
            }
          }
          val output =
            RemoteLottiePath(
              listOf(
                result.copy(
                  vertices = named("v", result.vertices),
                  inTangents = named("i", result.inTangents),
                  outTangents = named("o", result.outTangents),
                )
              )
            )
          val paint = RemotePaint { color = Color.Red.rc }
          RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
            usePaint(paint) { output.draw(this, remoteCanvas) }
          }
        }
      Box(Modifier.size(64.dp).testTag("motion")) {
        document.value?.let {
          RemoteDocumentPlayer(
            it,
            modifier = Modifier.size(64.dp),
            documentWidth = 64,
            documentHeight = 64,
            init = { view ->
              player = view
              view.setShaderControl { false }
            },
          )
        }
      }
    }
    capture("motion").recycle()
    assertWithMessage("coordinates must remain live").that(names).isNotEmpty()
    val errors = mutableListOf<String>()
    for (f in listOf(0f, .125f, .5f, .99f, 1f, .5f, .125f, 0f, 0f)) {
      composeRule.runOnIdle { checkNotNull(player).setUserLocalFloat("shape", f) }
      capture("motion").recycle()
      // de Casteljau at t=1/2: C1=(8+4f,20+4f), C2=(20+2f,14+6f), E=(32,14+6f).
      val expected =
        mapOf(
          "v" to listOf(listOf(8f, 32f), listOf(32f, 14f + 6f * f)),
          "i" to listOf(listOf(0f, 0f), listOf(-12f + 2f * f, 0f)),
          "o" to listOf(listOf(4f * f, -12f + 4f * f), listOf(0f, 0f)),
        )
      for ((kind, points) in expected) for ((i, point) in points.withIndex()) for ((j, value) in
        point.withIndex()) {
        val name = "$kind-$i-$j"
        if (name !in names) continue
        val actual = checkNotNull(player).getNamedFloat(RemoteState.Domain.User.prefixed(name))
        if (!actual.isFinite() || kotlin.math.abs(actual - value) > .0002f)
          errors.add("$f/$name: expected=$value actual=$actual")
      }
    }
    assertWithMessage(errors.joinToString("\n")).that(errors).isEmpty()
  }
}

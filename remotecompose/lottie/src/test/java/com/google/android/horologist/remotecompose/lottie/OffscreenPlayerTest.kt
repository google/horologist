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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.drawWithContent
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.junit.Test

class OffscreenPlayerTest : MotionPixelHarness() {
  @Test fun drawsControlRectangle() = checkOffscreen(false, true)

  @Test fun drawsDirectOffscreenRectangle() = checkOffscreen(false)

  @Test fun drawsComponentContentOffscreen() = checkOffscreen(true)

  @Test fun updatesComponentContentOffscreen() = checkOffscreen(true, dynamic = true)

  @Test fun updatesDirectContentOffscreen() = checkOffscreen(false, dynamic = true)

  private fun checkOffscreen(content: Boolean, control: Boolean = false, dynamic: Boolean = false) {
    val opacity = mutableFloatStateOf(if (dynamic) 0f else 1f)
    composeRule.setContent {
      val doc = rememberRemoteDocument {
        val bitmap = RemoteImageBitmap.createOffscreenRemoteBitmap(64, 64)
        val alpha = rememberNamedRemoteFloat("alpha") { 0f.rf }
        val red = RemotePaint { color = Color.Red.rc.copy(alpha = alpha) }
        val white = RemotePaint { color = Color.White.rc }
        RemoteBox(
          modifier =
            RemoteModifier.fillMaxSize().drawWithContent {
              scale(size.width / 64f, size.height / 64f) { drawContent() }
            }
        ) {
          if (content) {
            RemoteBox(
              modifier =
                RemoteModifier.fillMaxSize().drawWithContent {
                  remoteCanvas.drawToOffscreenBitmap(bitmap, 0) { drawContent() }
                  remoteCanvas.drawScaledBitmap(
                    bitmap,
                    0f.rf,
                    0f.rf,
                    64f.rf,
                    64f.rf,
                    0f.rf,
                    0f.rf,
                    64f.rf,
                    64f.rf,
                    6,
                    1f.rf,
                    null,
                    white,
                  )
                }
            ) {
              RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
                remoteCanvas.drawRect(8f.rf, 8f.rf, 56f.rf, 56f.rf, red)
              }
            }
          } else {
            RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
              if (control) {
                remoteCanvas.drawRect(8f.rf, 8f.rf, 56f.rf, 56f.rf, red)
              } else {
                remoteCanvas.drawToOffscreenBitmap(bitmap, 0) {
                  remoteCanvas.drawRect(8f.rf, 8f.rf, 56f.rf, 56f.rf, red)
                }
                remoteCanvas.drawScaledBitmap(
                  bitmap,
                  0f.rf,
                  0f.rf,
                  64f.rf,
                  64f.rf,
                  0f.rf,
                  0f.rf,
                  64f.rf,
                  64f.rf,
                  6,
                  1f.rf,
                  null,
                  white,
                )
              }
            }
          }
        }
      }
      Box(Modifier.size(64.dp).testTag("motion")) {
        doc.value?.let {
          RemoteDocumentPlayer(
            it,
            modifier = Modifier.size(64.dp),
            documentWidth = 64,
            documentHeight = 64,
            update = { player -> player.setUserLocalFloat("alpha", opacity.floatValue) },
          )
        }
      }
    }
    composeRule.runOnIdle { opacity.floatValue = 1f }
    assertPixels(Probe(32, 32, 1f))
  }
}

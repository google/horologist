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

package com.google.android.horologist.sample.lottie

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.remote.tooling.preview.RemoteContentPreview
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import com.google.android.horologist.remotecompose.lottie.format.LottieDeserializer
import com.google.android.horologist.remotecompose.lottie.renderer.SlotMap

import com.google.android.horologist.sample.R

class LottieSampleActivity : ComponentActivity() {
  @SuppressLint("RestrictedApi")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val asset = applicationContext.resources.openRawResource(R.raw.geometry).readBytes()
      val deserializer = LottieDeserializer.jsonAdapter
      val animation = deserializer.fromJson(asset.decodeToString())!!

      val doc =  rememberRemoteDocument {
        AnimationDemo(animation, SlotMap(emptyMap())).Render()
      }
      doc.value?.let {
        RemoteDocumentPlayer(
          document = it,
          modifier = Modifier.fillMaxSize(),
          documentWidth = LocalWindowInfo.current.containerSize.width,
          documentHeight = LocalWindowInfo.current.containerSize.height
        )
      }
    }
  }
}

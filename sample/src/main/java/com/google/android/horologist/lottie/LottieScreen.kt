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

package com.google.android.horologist.lottie

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize as remoteFillMaxSize
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import com.google.android.horologist.remotecompose.lottie.LottieAnimation
import com.google.android.horologist.sample.R

// Builds and plays the document here rather than calling the library's LottiePreview, which lives
// in remotecompose/lottie's `debug` source set: this screen is a navigation destination in
// SampleWearApp, so it is part of the release variant, where that function does not exist.
@SuppressLint("RestrictedApi")
@Composable
fun LottieScreen(modifier: Modifier = Modifier) {
  val document = rememberRemoteDocument {
    LottieAnimation(R.raw.geometry, modifier = RemoteModifier.remoteFillMaxSize())
  }
  val containerSize = LocalWindowInfo.current.containerSize
  document.value?.let {
    RemoteDocumentPlayer(
      document = it,
      modifier = modifier.fillMaxSize(),
      documentWidth = containerSize.width,
      documentHeight = containerSize.height,
    )
  }
}

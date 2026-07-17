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

import android.annotation.SuppressLint
import androidx.annotation.RawRes
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteArrangement
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.background
import androidx.compose.remote.creation.compose.modifier.height
import androidx.compose.remote.creation.compose.modifier.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.LottieDeserializer
import com.google.android.horologist.remotecompose.lottie.renderer.SlotMap

/**
 * Displays a Lottie animation using Remote Compose.
 *
 * @param animation The parsed Lottie animation to play.
 * @param slotMap Optional mapping of slot IDs to values for dynamic theming.
 */
@SuppressLint("RestrictedApi")
@Composable
fun LottiePreview(
  animation: Animation,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap(emptyMap()),
) {
  RemoteColumn(
    modifier = modifier.background(Color.Black),
    horizontalAlignment = RemoteAlignment.CenterHorizontally,
    verticalArrangement = RemoteArrangement.Center,
  ) {
    LottieAnimation(
      animation,
      slotMap = slotMap,
      modifier = RemoteModifier.width(animation.width).height(animation.height),
    )
  }
}

/**
 * Displays a Lottie animation from a raw resource ID using Remote Compose.
 *
 * @param animationResId The raw resource ID of the Lottie JSON file.
 * @param slotMap Optional mapping of slot IDs to values for dynamic theming.
 */
@Composable
fun LottiePreview(
  @RawRes animationResId: Int,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap(emptyMap()),
) {
  val resources = LocalResources.current
  val animation =
    remember(animationResId) {
      val resource = resources.openRawResource(animationResId).readBytes()
      LottieDeserializer.jsonAdapter.fromJson(resource.decodeToString())!!
    }
  LottiePreview(animation, modifier, slotMap)
}

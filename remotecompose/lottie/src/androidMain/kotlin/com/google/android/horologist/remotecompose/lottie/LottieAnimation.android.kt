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

@file:JvmName("LottieAnimationKt")
@file:JvmMultifileClass

package com.google.android.horologist.remotecompose.lottie

import androidx.annotation.RawRes
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.load

/**
 * A RemoteComposable that loads and renders a Lottie animation from a raw resource ID.
 *
 * @param rawRes The raw resource ID of the Lottie JSON file.
 * @param modifier The modifier to apply to the Lottie layout.
 * @param slotMap Mapping of slot IDs to values for dynamic theming.
 * @param progress Optional progress value to drive animation frame instead of clock time.
 */
@Composable
@RemoteComposable
fun LottieAnimation(
  @RawRes rawRes: Int,
  modifier: RemoteModifier = RemoteModifier,
  slotMap: SlotMap = SlotMap.Empty,
  progress: RemoteFloat? = null,
) {
  val context = LocalContext.current
  val animation = remember(rawRes) { Animation.load(rawRes, context) }
  LottieAnimation(animation, modifier, slotMap, progress)
}

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
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.height
import androidx.compose.remote.creation.compose.modifier.width
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.tooling.preview.RemoteContentPreview
import androidx.compose.runtime.Composable
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.remotecompose.lottie.LottiePreview
import com.google.android.horologist.remotecompose.lottie.format.StaticColorProperty
import com.google.android.horologist.remotecompose.lottie.renderer.SlotMap
import com.google.android.horologist.sample.R

@WearPreviewDevices
@Composable
fun GeometryPreview() {
  RemoteContentPreview {
    LottiePreview(animationResId = R.raw.geometry)
  }
}

@SuppressLint("RestrictedApi")
@WearPreviewDevices
@Composable
fun TintGeometryPreview() {
  RemoteContentPreview {
    LottiePreview(
      animationResId = R.raw.geometry,
      modifier = RemoteModifier.width(100).height(100),
      slotMap = SlotMap(mapOf("color.primary" to StaticColorProperty(value = RemoteColor(0xFF00FF00.toInt())))),
    )
  }
}

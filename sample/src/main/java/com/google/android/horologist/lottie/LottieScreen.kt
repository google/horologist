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
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import com.google.android.horologist.remotecompose.lottie.format.LottieDeserializer
import com.google.android.horologist.remotecompose.lottie.renderer.SlotMap
import com.google.android.horologist.sample.R

@SuppressLint("RestrictedApi")
@Composable
fun LottieScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val asset = remember { context.resources.openRawResource(R.raw.geometry).readBytes() }
    val animation = remember(asset) { LottieDeserializer.jsonAdapter.fromJson(asset.decodeToString())!! }

    val doc = rememberRemoteDocument {
        AnimationDemo(animation, SlotMap(emptyMap())).Render()
    }
    doc.value?.let { document ->
        RemoteDocumentPlayer(
            document = document,
            modifier = modifier.fillMaxSize(),
            documentWidth = LocalWindowInfo.current.containerSize.width,
            documentHeight = LocalWindowInfo.current.containerSize.height,
        )
    }
}

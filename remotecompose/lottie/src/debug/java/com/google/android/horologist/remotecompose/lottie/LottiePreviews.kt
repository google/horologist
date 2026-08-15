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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.AnimatedPreview

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun LottieGeometryPreview() {
  LottiePreview(animationResId = R.raw.geometry, modifier = Modifier.size(100.dp))
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100)
@AnimatedPreview(durationMs = 3000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieGeometryAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.geometry, modifier = Modifier.size(100.dp))
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun LottieTintGeometryPreview() {
  LottiePreview(
    animationResId = R.raw.geometry,
    modifier = Modifier.size(100.dp),
    slotMap = SlotMap(mapOf("color.primary" to 0xFF00FF00.toInt())),
  )
}

@SuppressLint("RestrictedApi")
@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100)
@AnimatedPreview(durationMs = 3000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieTintGeometryAnimatedPreview() {
  LottieAnimatedPreview(
    animationResId = R.raw.geometry,
    modifier = Modifier.size(100.dp),
    slotMap = SlotMap(mapOf("color.primary" to 0xFF00FF00.toInt())),
  )
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 64, heightDp = 64)
@Composable
fun LottiePlayPausePreview() {
  LottiePreview(animationResId = R.raw.play_pause, modifier = Modifier.size(64.dp), progress = 0f)
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 64, heightDp = 64)
@Composable
fun LottiePlayPausePauseStatePreview() {
  LottiePreview(animationResId = R.raw.play_pause, modifier = Modifier.size(64.dp), progress = 1f)
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 64, heightDp = 64)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottiePlayPauseAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.play_pause, modifier = Modifier.size(64.dp))
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 64, heightDp = 64)
@Composable
fun LottieM3PlayPausePreview() {
  LottiePreview(
    animationResId = R.raw.m3_play_pause,
    modifier = Modifier.size(64.dp),
    progress = 0f,
  )
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 64, heightDp = 64)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieM3PlayPauseAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.m3_play_pause, modifier = Modifier.size(64.dp))
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 64, heightDp = 64)
@Composable
fun LottieNextPreview() {
  LottiePreview(animationResId = R.raw.next, modifier = Modifier.size(64.dp), progress = 0f)
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 64, heightDp = 64)
@AnimatedPreview(durationMs = 1000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieNextAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.next, modifier = Modifier.size(64.dp))
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun LottiePositionStaticPreview() {
  LottiePreview(animationResId = R.raw.position_static, modifier = Modifier.size(100.dp))
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun LottiePositionAnimatedPreview() {
  LottiePreview(
    animationResId = R.raw.position_animated,
    modifier = Modifier.size(100.dp),
    progress = 0f,
  )
}

@Preview(backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottiePositionAnimatedLivePreview() {
  LottieAnimatedPreview(animationResId = R.raw.position_animated, modifier = Modifier.size(100.dp))
}

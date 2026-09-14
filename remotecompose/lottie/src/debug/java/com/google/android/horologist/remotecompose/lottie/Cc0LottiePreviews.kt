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

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.AnimatedPreview

// Unmodified CC0 fixtures; sources and checksums are in docs/sample-provenance.txt.

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@Composable
fun LottieCc0ColorEasePreview() {
  LottiePreview(
    animationResId = R.raw.cc0_color_ease,
    modifier = Modifier.size(160.dp),
    progress = 0.25f,
  )
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieCc0ColorEaseAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.cc0_color_ease, modifier = Modifier.size(160.dp))
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@Composable
fun LottieCc0GradientAlphaPreview() {
  LottiePreview(
    animationResId = R.raw.cc0_gradient_alpha,
    modifier = Modifier.size(160.dp),
    progress = 0.25f,
  )
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieCc0GradientAlphaAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.cc0_gradient_alpha, modifier = Modifier.size(160.dp))
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@Composable
fun LottieCc0MultidimensionalPreview() {
  LottiePreview(
    animationResId = R.raw.cc0_multidimensional,
    modifier = Modifier.size(160.dp),
    progress = 0.25f,
  )
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieCc0MultidimensionalAnimatedPreview() {
  LottieAnimatedPreview(
    animationResId = R.raw.cc0_multidimensional,
    modifier = Modifier.size(160.dp),
  )
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@Composable
fun LottieCc0PositionHoldPreview() {
  LottiePreview(
    animationResId = R.raw.cc0_position_hold,
    modifier = Modifier.size(160.dp),
    progress = 0.25f,
  )
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
// The changed position lasts only one 60 Hz frame before looping; 100 ms sampling misses it.
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 10, showCurves = false)
@Composable
fun LottieCc0PositionHoldAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.cc0_position_hold, modifier = Modifier.size(160.dp))
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@Composable
fun LottieCc0PositionPathPreview() {
  LottiePreview(
    animationResId = R.raw.cc0_position_path,
    modifier = Modifier.size(160.dp),
    progress = 0.25f,
  )
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieCc0PositionPathAnimatedPreview() {
  LottieAnimatedPreview(animationResId = R.raw.cc0_position_path, modifier = Modifier.size(160.dp))
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@Composable
fun LottieCc0PrecompStretchPreview() {
  LottiePreview(
    animationResId = R.raw.cc0_precomp_stretch,
    modifier = Modifier.size(160.dp),
    progress = 0.25f,
  )
}

@Preview(backgroundColor = 0xffffffff, showBackground = true, widthDp = 160, heightDp = 160)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun LottieCc0PrecompStretchAnimatedPreview() {
  LottieAnimatedPreview(
    animationResId = R.raw.cc0_precomp_stretch,
    modifier = Modifier.size(160.dp),
  )
}

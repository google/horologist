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

import org.junit.Test

/**
 * Screenshot tests comparing RemoteCompose Lottie rendering with `lottie-android` reference output.
 *
 * Test cases for parametric shapes (`rect_ellipse`, `polystar`) are sourced from the
 * [Lottie Format Feature Support & Sample Test Suite](https://docs.google.com/document/d/1jXj3kbXL57kxjRc0soUqst2poa2-Lrc2qZAIzEmbB8w/edit).
 */
class LottieFeatureDiffScreenshotTest : LottieDiffScreenshotTest() {

  @Test
  fun positionStatic() {
    runLottieDiffTest(R.raw.position_static)
  }

  @Test
  fun positionAnimated() {
    runLottieDiffTest(R.raw.position_animated) {
      captureFrame(frame = 0f)
      captureFrame(frame = 20f)
      captureFrame(frame = 40f)
      captureFrame(frame = 60f)
    }
  }

  /** Tests parametric rectangle, rounded rectangle, ellipse, and circle shapes. */
  @Test
  fun rectEllipse() {
    runLottieDiffTest(R.raw.rect_ellipse)
  }

  /** Tests parametric star, rounded star, polygon, and rounded polygon shapes. */
  @Test
  fun polystar() {
    runLottieDiffTest(R.raw.polystar)
  }

  /**
   * Tests layer parenting more than one level deep.
   *
   * `parent_chain` is 20 dot layers chained child -> parent -> grandparent -> ..., each applying
   * the same relative delta (translate 30px, rotate 25 degrees, scale 93%). Accumulating those
   * deltas down the chain draws a shrinking, fading spiral.
   *
   * Only the layer's immediate parent transform is applied, so every layer from the third down
   * loses its ancestors' transforms and collapses onto a single point.
   *
   * See https://github.com/google/horologist/issues/2795.
   */
  @Test
  fun parentChain() {
    runLottieDiffTest(R.raw.parent_chain)
  }
}

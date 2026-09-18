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

class MediaLottieDiffScreenshotTest : LottieDiffScreenshotTest() {

  @Test
  fun geometry() {
    runLottieDiffTest(R.raw.geometry, suffix = "_progress0")
  }

  @Test
  fun playPause() {
    runLottieDiffTest(R.raw.play_pause, suffix = "_progress0")
  }

  @Test
  fun next() {
    runLottieDiffTest(R.raw.next, suffix = "_progress0")
  }

  @Test
  fun m3PlayPause() {
    runLottieDiffTest(R.raw.m3_play_pause, suffix = "_progress0")
  }

  @Test
  fun m3Next() {
    runLottieDiffTest(R.raw.m3_next, suffix = "_progress0")
  }

  @Test
  fun volumeUp() {
    runLottieDiffTest(R.raw.volume_up, suffix = "_progress0")
  }

  @Test
  fun volumeDown() {
    runLottieDiffTest(R.raw.volume_down, suffix = "_progress0")
  }

  @Test
  fun muteToUnmute() {
    runLottieDiffTest(R.raw.mute_to_unmute) {
      captureProgress(0f)
      captureProgress(0.25f)
      captureProgress(0.5f)
      captureProgress(1f)
    }
  }

  @Test
  fun unmuteToMute() {
    runLottieDiffTest(R.raw.unmute_to_mute) {
      captureProgress(0f)
      captureProgress(0.5f)
      captureProgress(1f)
    }
  }
}

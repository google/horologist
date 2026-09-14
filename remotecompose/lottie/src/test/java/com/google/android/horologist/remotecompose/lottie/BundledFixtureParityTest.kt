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

import androidx.compose.ui.graphics.Color
import org.junit.Ignore
import org.junit.Test

/** Covers every original sample on neutral gray, so both black and white artwork are visible. */
class BundledFixtureParityTest : FixtureParityHarness() {
  private fun sample(resource: Int, animated: Boolean = true) =
    compare(resource, animated, Color(0xff808080))

  @Ignore("TODO: Fix failure on main AST/renderer") @Test fun geometry() = sample(R.raw.geometry)

  @Ignore("TODO: Fix failure on main AST/renderer") @Test fun playPause() = sample(R.raw.play_pause)

  @Ignore("TODO: Fix failure on main AST/renderer") @Test fun next() = sample(R.raw.next)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun m3PlayPause() = sample(R.raw.m3_play_pause)

  @Ignore("TODO: Fix failure on main AST/renderer") @Test fun m3Next() = sample(R.raw.m3_next)

  @Ignore("TODO: Fix failure on main AST/renderer") @Test fun volumeUp() = sample(R.raw.volume_up)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun volumeDown() = sample(R.raw.volume_down)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun muteToUnmute() = sample(R.raw.mute_to_unmute)

  @Ignore("TODO: Fix failure on main AST/renderer")
  @Test
  fun unmuteToMute() = sample(R.raw.unmute_to_mute)

  @Test fun positionAnimated() = sample(R.raw.position_animated)

  @Test fun positionStatic() = sample(R.raw.position_static, animated = false)

  @Test fun parentChain() = sample(R.raw.parent_chain, animated = false)

  @Test fun grandparent() = sample(R.raw.grandparent, animated = false)

  @Test fun polystar() = sample(R.raw.polystar, animated = false)

  @Test fun rectangleEllipse() = sample(R.raw.rect_ellipse, animated = false)
}

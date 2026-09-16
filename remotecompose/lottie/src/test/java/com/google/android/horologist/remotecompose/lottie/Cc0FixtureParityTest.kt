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

import org.junit.Ignore
import org.junit.Test

/** Real CC0 exports compared numerically with the pinned lottie-android renderer. */
class Cc0FixtureParityTest : FixtureParityHarness() {
  @Test fun colorEase() = compare(R.raw.cc0_color_ease)

  @Ignore("Animated gradient opacity stop interpolation parity")
  @Test
  fun gradientAlpha() = compare(R.raw.cc0_gradient_alpha)

  @Test fun multidimensional() = compare(R.raw.cc0_multidimensional)

  @Test fun positionHold() = compare(R.raw.cc0_position_hold)

  @Test fun positionPath() = compare(R.raw.cc0_position_path)

  @Test fun precompStretch() = compare(R.raw.cc0_precomp_stretch, maxForegroundError = 0.01)
}

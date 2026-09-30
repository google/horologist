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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LottieScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun catalogContainsExpectedAnimations() {
    assertThat(LottieDemoCatalog).isNotEmpty()
    assertThat(LottieDemoCatalog.size).isAtLeast(20)

    for (item in LottieDemoCatalog) {
      assertThat(item.title).isNotEmpty()
      assertThat(item.subtitle).isNotEmpty()
      assertThat(item.category).isNotEmpty()
      assertThat(item.rawRes).isNotEqualTo(0)
    }
  }

  @Test
  fun catalogHasAllFourCategories() {
    val categories = LottieDemoCatalog.map { it.category }.toSet()
    assertThat(categories)
      .containsExactly(
        "Media & Controls",
        "Shapes & Hierarchies",
        "Gradients & Strokes",
        "Modifiers & Paths",
      )
  }

  @Test
  fun playbackRegimesAreDefined() {
    assertThat(PlaybackRegime.entries).containsExactly(PlaybackRegime.TIME, PlaybackRegime.CROWN)
  }

  @Test
  fun viewModesAreDefined() {
    val gallery: LottieViewMode = LottieViewMode.Gallery
    val detail: LottieViewMode = LottieViewMode.Detail(0)
    val demo: LottieViewMode = LottieViewMode.Demo(0)

    assertThat(gallery).isNotNull()
    assertThat((detail as LottieViewMode.Detail).index).isEqualTo(0)
    assertThat((demo as LottieViewMode.Demo).index).isEqualTo(0)
  }

  @Test
  fun galleryListRendersAndNavigatesToDetail() {
    composeTestRule.setContent { LottieScreen() }

    // Verify Gallery header
    composeTestRule.onNodeWithText("Lottie Gallery (${LottieDemoCatalog.size})").assertIsDisplayed()

    // Verify Start Demo Mode button exists
    composeTestRule.onNodeWithText("Start Demo Mode").assertIsDisplayed()

    // Click first item (Geometry)
    composeTestRule.onNodeWithText("Geometry").performClick()

    // Detail view should display title, subtitle, counter, and regime controls
    composeTestRule.onNodeWithText("Geometry").assertIsDisplayed()
    composeTestRule
      .onNodeWithText("1 / ${LottieDemoCatalog.size} • Media & Controls")
      .assertIsDisplayed()
    composeTestRule.onNodeWithText("⏰ Time").assertIsDisplayed()
    composeTestRule.onNodeWithText("⚙️ Crown").assertIsDisplayed()
  }

  @Test
  fun detailPlayerSwitchesRegimes() {
    composeTestRule.setContent { LottieScreen() }

    // Open detail
    composeTestRule.onNodeWithText("Geometry").performClick()

    // Switch to Crown regime
    composeTestRule.onNodeWithText("⚙️ Crown").performClick()

    // Scrubber percentage should appear in Crown mode
    composeTestRule.onNodeWithText("0%").assertExists()

    // Switch back to Time regime
    composeTestRule.onNodeWithText("⏰ Time").performClick()

    // 0% should no longer be displayed
    composeTestRule.onNodeWithText("0%").assertDoesNotExist()
  }

  @Test
  fun demoModeStartsAndDisplaysHeader() {
    composeTestRule.setContent { LottieScreen() }

    // Click Start Demo Mode
    composeTestRule.onNodeWithText("Start Demo Mode").performClick()

    // Should display Demo header and exit prompt
    composeTestRule.onNodeWithText("DEMO • 1 / ${LottieDemoCatalog.size}").assertIsDisplayed()
    composeTestRule.onNodeWithText("Tap anywhere to exit").assertIsDisplayed()

    // Tap anywhere to exit back to gallery
    composeTestRule.onNodeWithText("Tap anywhere to exit").performClick()

    // Should be back on Gallery
    composeTestRule.onNodeWithText("Lottie Gallery (${LottieDemoCatalog.size})").assertIsDisplayed()
  }
}

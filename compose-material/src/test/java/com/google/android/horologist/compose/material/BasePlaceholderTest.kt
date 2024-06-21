/*
 * Copyright 2022 The Android Open Source Project
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

@file:OptIn(ExperimentalRoborazziApi::class)

package com.google.android.horologist.compose.material

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.wear.compose.material.PlaceholderState
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.compose.layout.rememberActivePlaceholderState
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable
import com.google.android.horologist.images.base.paintable.PaintableIcon
import org.junit.Rule
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@Config(
    sdk = [33],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound,
)
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class BasePlaceholderTest {
    @get:Rule
    public val composeRule: ComposeContentTestRule = createComposeRule()

    @get:Rule
    public val testInfo: TestName = TestName()

    fun testName(suffix: String): String =
        "src/test/snapshots/images/${this.javaClass.simpleName}_${testInfo.methodName}$suffix.png"

    fun runPlaceholderTest(
        item: Item,
        component: @Composable (Item?, PlaceholderState) -> Unit,
    ) {
        composeRule.mainClock.autoAdvance = false

        lateinit var placeholderState: PlaceholderState
        val itemState = mutableStateOf<Item?>(null)

        composeRule.setContent {
            placeholderState = rememberActivePlaceholderState {
                itemState.value != null
            }

            Box(modifier = Modifier.background(Color.Black)) {
                component(itemState.value, placeholderState)
            }
        }

        captureScreenshot(placeholderState, "_1_initial")

        composeRule.mainClock.advanceTimeUntil { placeholderState.placeholderProgression > 100f }
//        captureScreenshot(placeholderState, "_2_animating")

        itemState.value = item
        composeRule.mainClock.advanceTimeUntil { placeholderState.isWipeOff }
//        captureScreenshot(placeholderState, "_3_wipeOff")

        composeRule.mainClock.advanceTimeUntil { placeholderState.isShowContent }
        captureScreenshot(placeholderState, "_4_showContent")
    }

    public fun captureScreenshot(placeholderState: PlaceholderState, suffix: String = "") {
        println("Capturing $suffix at progression=${placeholderState.placeholderProgression} wipeOff=${placeholderState.isWipeOff}")
        composeRule.onRoot().captureRoboImage(
            filePath = testName(suffix),
        )
    }

    data class Item(
        val label: String,
        val secondaryLabel: String? = null,
        val icon: PaintableIcon? = null,
        val time: String? = null,
        val content: String? = null,
    ) {

        companion object {
            val RedPaintable = ImageVectorPaintable(Icons.Default.Recycling)

            val Full = Item(
                label = "Bobby Bonson",
                secondaryLabel = "bobby@bonson.bx",
                icon = RedPaintable,
                time = "12:05",
                content = "This is sample\ncontent.",
            )
            val LabelOnly = Item(label = "Bobby Bonson", secondaryLabel = null, icon = null)
        }
    }
}

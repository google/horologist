/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.tile

import android.os.Looper.getMainLooper
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.compose.tools.requestParams
import com.google.android.horologist.compose.tools.resourceParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import java.time.Duration


@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound
)
@LooperMode(LooperMode.Mode.PAUSED)
class TilePreviewTest {
    @get:Rule
    val composeTestRule = createComposeRule() as AndroidComposeTestRule<*, *>

    @get:Rule
    val roborazziRule = RoborazziRule(
        composeRule = composeTestRule as AndroidComposeTestRule<*, *>,
        captureRoot = composeTestRule.onRoot(),
        options = RoborazziRule.Options(
            captureType = RoborazziRule.CaptureType.LastImage,
            outputDirectoryPath = "src/test/snapshots/images",
            roborazziOptions = RoborazziOptions(
                recordOptions = RoborazziOptions.RecordOptions(
                    pixelBitConfig = RoborazziOptions.PixelBitConfig.Argb8888
                )
            )
        )
    )

    @Test
    fun givenHeadingModifierIsNOTOverridden_thenHeadingModifierIsPresent() {
        // given
        composeTestRule.setContent {
            Box {
                SampleAnimatedTilePreview()
            }
        }

        shadowOf(getMainLooper()).idle()

        // then
        onView(withText("Anchor angle")).check(matches(isDisplayed()))

        onView(isRoot()).captureRoboImage("build/tile1.png")

        shadowOf(getMainLooper()).idleFor(Duration.ofSeconds(1))

        onView(isRoot()).captureRoboImage("build/tile2.png")
    }

    @Test
    fun viewBasedPreview() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val tileRenderer = androidx.wear.tiles.renderer.TileRenderer(
            /* uiContext = */ context,
            /* loadActionExecutor = */ Dispatchers.IO.asExecutor(),
            /* loadActionListener = */ {}
        )

        val renderer  = SampleAnimatedTileRenderer(context)
        val resources = context.resources
        val requestParams = requestParams(resources)
        val tile = renderer.renderTimeline(Unit, requestParams)
        val resourceParams = resourceParams(resources, tile.resourcesVersion)
        val tileResources = renderer.produceRequestedResources(Unit, resourceParams)

        // Returning a future
        tileRenderer.inflateAsync(
            tile.tileTimeline?.timelineEntries?.first()?.layout!!,
            tileResources,
            composeTestRule.activity.findViewById(android.R.id.content)
        ).get()

        shadowOf(getMainLooper()).idle()

        // then
        onView(withText("Anchor angle")).check(matches(isDisplayed()))

        onView(isRoot()).captureRoboImage("build/tileb1.png")

        shadowOf(getMainLooper()).idleFor(Duration.ofSeconds(1))

        onView(isRoot()).captureRoboImage("build/tileb2.png")
    }
}

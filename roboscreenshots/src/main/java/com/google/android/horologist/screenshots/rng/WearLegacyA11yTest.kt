/*
 * Copyright 2024 The Android Open Source Project
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

@file:OptIn(ExperimentalRoborazziApi::class, ExperimentalCoilApi::class, ExperimentalCoilApi::class)

package com.google.android.horologist.screenshots.rng

import android.app.Application
import android.graphics.Bitmap
import android.os.Looper
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Root
import androidx.test.espresso.base.RootsOracle_Factory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.wear.compose.material.MaterialTheme
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoboComponent
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.screenshots.FixedTimeSource
import com.google.android.horologist.screenshots.a11y.A11ySnapshotTransformer
import com.google.android.horologist.screenshots.rng.WearScreenshotTest.Companion.CorrectLayout
import com.google.android.horologist.screenshots.rng.WearScreenshotTest.Companion.useHardwareRenderer
import com.google.android.horologist.screenshots.rng.WearScreenshotTest.Companion.withImageLoader
import org.junit.Rule
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@Config(
    sdk = [33],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound,
)
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public abstract class WearLegacyA11yTest {
    @get:Rule
    public val composeRule: ComposeContentTestRule = createComposeRule()

    @get:Rule
    public val testInfo: TestName = TestName()

    // Allow for individual tolerances to be set on each test, should be between 0.0 and 1.0
    public open val tolerance: Float = 0.0f

    public open val imageLoader: FakeImageLoaderEngine? = null

    public fun runScreenTest(
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            TestScaffold {
                content()
            }
        }

        captureScreenshot()
    }

    public fun runComponentTest(
        background: Color? = Color.Black.copy(alpha = 0.3f),
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            withImageLoader(imageLoader) {
                Box(
                    modifier = Modifier.run {
                        if (background != null) {
                            background(background)
                        } else {
                            this
                        }
                    },
                ) {
                    ComponentScaffold {
                        content()
                    }
                }
            }
        }
        captureScreenshot()
    }

    public fun captureScreenshot(suffix: String = "") {
        captureScreenA11yRoboImage(
            filePath = testName(suffix),
            roborazziOptions = RoborazziOptions(
                recordOptions = RoborazziOptions.RecordOptions(
                    applyDeviceCrop = true,
                ),
                compareOptions = RoborazziOptions.CompareOptions(
                    resultValidator = ThresholdValidator(tolerance),
                ),
            ),
        )
    }

    public open fun testName(suffix: String): String = "src/test/snapshots/images/" +
        "${this.javaClass.`package`?.name}_${this.javaClass.simpleName}_" +
        "${testInfo.methodName}$suffix.png"

    public fun captureScreenA11yRoboImage(
        filePath: String,
        roborazziOptions: RoborazziOptions,
    ) {
        Espresso.onIdle()
        val screenImage = captureScreenImageToBitmap(roborazziOptions)
        val annotatedImage =
            A11ySnapshotTransformer().transform(composeRule.onRoot(), screenImage)
        annotatedImage.captureRoboImage(filePath, roborazziOptions)
    }

    @Suppress("INACCESSIBLE_TYPE")
    private fun captureScreenImageToBitmap(roborazziOptions: RoborazziOptions): Bitmap {
        val rootsOracle = RootsOracle_Factory { Looper.getMainLooper() }.get()
        val listActiveRoots = rootsOracle.javaClass.getMethod("listActiveRoots")
        listActiveRoots.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val roots: List<Root> =
            listActiveRoots.invoke(rootsOracle) as List<Root>

        val rootComponent = RoboComponent.Screen(
            rootsOrderByDepth = roots.sortedBy { it.windowLayoutParams.get()?.type },
            roborazziOptions = roborazziOptions,
        )

        val image = rootComponent.image!!
        return image
    }

    @Composable
    public open fun TestScaffold(content: @Composable () -> Unit) {
        CorrectLayout {
            AppScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                timeText = { ResponsiveTimeText(timeSource = FixedTimeSource) },
            ) {
                content()
            }
        }
    }

    @Composable
    public open fun ComponentScaffold(content: @Composable () -> Unit) {
        CorrectLayout {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                content()
            }
        }
    }

    public companion object {
        public fun enableTouchExploration() {
            val applicationContext = ApplicationProvider.getApplicationContext<Application>()
            val a11yManager = applicationContext.getSystemService(AccessibilityManager::class.java)
            val shadow = Shadows.shadowOf(a11yManager)

            shadow.setEnabled(true)
            shadow.setTouchExplorationEnabled(true)
        }

        init {
            useHardwareRenderer()
        }
    }
}

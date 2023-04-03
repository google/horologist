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

@file:OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalHorologistApi::class,
    ExperimentalTestApi::class
)
@file:Suppress("UnstableApiUsage")

package com.google.android.horologist.screenshots

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import coil.compose.LocalImageLoader
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
import com.google.android.horologist.screenshots.a11y.A11ySnapshotTransformer
import com.quickbird.snapshot.Diffing
import com.quickbird.snapshot.Snapshotting
import com.quickbird.snapshot.fileSnapshotting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path
import org.junit.Rule
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.GraphicsMode.Mode.NATIVE

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30],
    qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav"
)
@GraphicsMode(NATIVE)
@ExperimentalHorologistApi
public abstract class ScreenshotTest {
    public var tolerance: Float = 0.1f

    @get:Rule
    public val rule: ComposeContentTestRule = createComposeRule()

    @get:Rule
    public val testName: TestName = TestName()

    public var testLabel: String? = null

    // Flip to true to record
    public var record: Boolean = false

    public var fakeImageLoader: FakeImageLoader = FakeImageLoader.Never

    internal var snapshotTransformer: SnapshotTransformer = SnapshotTransformer.None

    public var screenTimeText: @Composable () -> Unit = {
        TimeText(
            timeSource = FixedTimeSource
        )
    }

    public val resources: Resources
        get() = applicationContext.resources

    public val applicationContext: Context
        get() = ApplicationProvider.getApplicationContext<Application>()

    @Suppress("DEPRECATION")
    @Composable
    internal fun FakeImageLoader.apply(content: @Composable () -> Unit) {
        // Not sure why this is needed, but Coil has improved
        // test support in next release
        this.override {
            CompositionLocalProvider(LocalImageLoader provides this) {
                content()
            }
        }
    }

    public fun takeScreenshot(
        round: Boolean = resources.configuration.isScreenRound,
        timeText: @Composable () -> Unit = screenTimeText,
        positionIndicator: @Composable () -> Unit = {
        },
        checks: suspend () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        runTest {
            lateinit var view: View

            rule.setContent {
                view = LocalView.current
                ScreenshotDefaults(round, timeText, positionIndicator, content)
            }

            rule.awaitIdle()

            checks()

            val snapshotting = Snapshotting(
                diffing = Diffing.bitmapWithTolerance(
                    tolerance = tolerance,
                    colorDiffing = Diffing.highlightWithRed
                ),
                snapshot = { node: SemanticsNodeInteraction ->
                    val bitmap =
                        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                            .apply {
                                view.draw(Canvas(this))
                            }
                    snapshotTransformer.transform(node, bitmap)
                }
            ).fileSnapshotting

            snapshotting.snapshot(
                rule.onRoot(),
                testName = testName.methodName + (if (testLabel != null) "_$testLabel" else ""),
                record = record,
                testClass = this@ScreenshotTest.javaClass
            )
        }
    }

    @Composable
    public open fun ScreenshotDefaults(
        round: Boolean,
        timeText: @Composable () -> Unit,
        positionIndicator: @Composable () -> Unit,
        content: @Composable () -> Unit
    ) {
        fakeImageLoader.apply {
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
            ) {
                DefaultTheme {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .run {
                                if (round) {
                                    clip(CircleShape)
                                } else {
                                    this
                                }
                            }
                            .background(Color.Black),
                        timeText = {
                            timeText()
                        },
                        positionIndicator = positionIndicator
                    ) {
                        content()
                    }
                }
            }
        }
    }

    public fun takeComponentScreenshot(
        checks: suspend () -> Unit = {},
        content: @Composable BoxScope.() -> Unit
    ) {
        runTest {
            lateinit var view: View

            rule.setContent {
                view = LocalView.current
                ComponentDefaults(content)
            }

            rule.awaitIdle()

            checks()

            val snapshotting = Snapshotting(
                diffing = Diffing.bitmapWithTolerance(
                    tolerance = tolerance,
                    colorDiffing = Diffing.highlightWithRed
                ),
                snapshot = { _: SemanticsNodeInteraction ->
                    Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).apply {
                        view.draw(Canvas(this))
                    }
                }
            ).fileSnapshotting

            snapshotting.snapshot(
                rule.onRoot(),
                record = record,
                testName = testName.methodName + (if (testLabel != null) "_$testLabel" else ""),
                testClass = this@ScreenshotTest.javaClass
            )
        }
    }

    @Composable
    public open fun ComponentDefaults(content: @Composable BoxScope.() -> Unit) {
        fakeImageLoader.override {
            Box(
                modifier = Modifier.wrapContentSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                DefaultTheme {
                    content()
                }
            }
        }
    }

    @Composable
    public open fun DefaultTheme(content: @Composable () -> Unit) {
        content()
    }

    public fun takeScrollableScreenshot(
        round: Boolean = resources.configuration.isScreenRound,
        timeTextMode: TimeTextMode,
        columnStateFactory: ScalingLazyColumnState.Factory = ScalingLazyColumnDefaults.belowTimeText(),
        checks: suspend (columnState: ScalingLazyColumnState) -> Unit = {},
        content: @Composable (columnState: ScalingLazyColumnState) -> Unit
    ) {
        lateinit var columnState: ScalingLazyColumnState

        takeScreenshot(
            round,
            timeText = {
                if (timeTextMode != TimeTextMode.Off) {
                    TimeText(
                        timeSource = FixedTimeSource,
                        modifier = if (timeTextMode == TimeTextMode.Scrolling) {
                            Modifier.scrollAway(columnState.state)
                        } else {
                            Modifier
                        }
                    )
                }
            },
            positionIndicator = {
                PositionIndicator(scalingLazyListState = columnState.state)
            },
            checks = {
                checks(columnState)
            }
        ) {
            columnState = columnStateFactory.create()

            content(columnState)
        }
    }

    public fun enableA11yTest() {
        // allow more tolerance as A11y tests are mainly for illustrating the
        // current observable behaviour
        tolerance = 0.10f

        snapshotTransformer = A11ySnapshotTransformer()
    }

    public enum class TimeTextMode {
        OnTop,
        Off,
        Scrolling
    }

    public companion object {
        public fun loadTestBitmap(path: Path): Bitmap = FileSystem.RESOURCES.read(path) {
            BitmapFactory.decodeStream(this.inputStream())
        }
    }
}

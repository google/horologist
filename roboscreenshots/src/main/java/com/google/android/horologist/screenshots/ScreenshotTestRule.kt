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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.screenshots

import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.core.graphics.applyCanvas
import androidx.test.core.app.ApplicationProvider
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.images.coil.FakeImageLoader
import com.google.android.horologist.screenshots.RobolectricTempHelpers.capture
import com.google.android.horologist.screenshots.ScreenshotTestRule.RecordMode.Companion.defaultRecordMode
import com.google.android.horologist.screenshots.a11y.A11ySnapshotTransformer
import com.quickbird.snapshot.Diffing
import com.quickbird.snapshot.FileSnapshotting
import com.quickbird.snapshot.Snapshotting
import com.quickbird.snapshot.fileSnapshotting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.robolectric.RobolectricTestRunner
import kotlin.math.min

/**
 * A [TestRule] that allows you to run screenshot tests of your composable components.
 *
 * This rule requires robolectric, so the test suite should run with [RobolectricTestRunner]. See
 * [ScreenshotBaseTest] for a basic configuration.
 */
@ExperimentalHorologistApi
public class ScreenshotTestRule(
    private val params: ScreenshotTestRuleParams = screenshotTestRuleParams { },
) : TestRule {

    private val testClassInfoRule: TestClassInfoRule = TestClassInfoRule()
    private val composeContentTestRule: ComposeContentTestRule = createComposeRule()

    private val snapshotTransformer: SnapshotTransformer = if (params.enableA11y) {
        A11ySnapshotTransformer()
    } else {
        SnapshotTransformer.None
    }
    private val applicationContext = ApplicationProvider.getApplicationContext<Application>()
    private val resources: Resources =
        applicationContext.resources

    private var testContent: View? = null
    private var snapshotCount: Int = 0
    private var isComponent: Boolean = false

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    RuleChain.outerRule(testClassInfoRule)
                        .around(composeContentTestRule)
                        .apply(base, description)
                        .evaluate()
                } finally {
                    testContent = null
                    snapshotCount = 0
                    isComponent = false
                }
            }
        }
    }

    public fun setContent(
        isComponent: Boolean = false,
        componentDefaultContent: (@Composable (content: @Composable (() -> Unit)) -> Unit)? = null,
        takeScreenshot: Boolean = false,
        roundScreen: Boolean? = null,
        timeText: @Composable () -> Unit = params.screenTimeText,
        positionIndicator: @Composable () -> Unit = { },
        fakeImageLoader: FakeImageLoader = FakeImageLoader.Never,
        composable: @Composable () -> Unit,
    ) {
        this.isComponent = isComponent
        val round = roundScreen ?: resources.configuration.isScreenRound

        composeContentTestRule.setContent {
            testContent = LocalView.current

            if (isComponent) {
                if (componentDefaultContent == null) {
                    ComponentDefaults(fakeImageLoader, composable)
                } else {
                    componentDefaultContent(composable)
                }
            } else {
                ScreenshotDefaults(fakeImageLoader, round, timeText, positionIndicator, composable)
            }
        }

        runTest {
            composeContentTestRule.awaitIdle()
        }

        if (takeScreenshot) {
            takeScreenshot()
        }
    }

    // should not give [ComposeContentTestRule] as scope, as we don't want clients to call
    // [ComposeContentTestRule.setContent]
    public fun interact(block: ComposeTestRule.() -> Unit = {}) {
        block(composeContentTestRule)
    }

    public fun takeScreenshot() {
        val snapshotting = Snapshotting(
            diffing = Diffing.bitmapWithTolerance(
                tolerance = params.tolerance,
                colorDiffing = Diffing.highlightWithRed,
            ),
            snapshot = { node: SemanticsNodeInteraction ->
                val view = getView()

                val bitmap = captureBitmap(view)

                if (isComponent) {
                    bitmap
                } else {
                    snapshotTransformer.transform(node, bitmap)
                }
            },
        ).fileSnapshotting

        runTest {
            saveSnapshot(snapshotting)
        }
    }

    private fun captureBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888,
        )

        @Suppress("DEPRECATION")
        val isFullScreen = view.height == view.display.height && view.width == view.display.width

        if (isFullScreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            capture(view, bitmap)
        } else {
            view.draw(Canvas(bitmap))
        }

        val shouldClip = when (params.clipMode) {
            ClipMode.Round -> true

            ClipMode.Auto -> {
                resources.configuration.isScreenRound && isFullScreen
            }

            ClipMode.None -> false
        }
        return if (shouldClip) {
            circularClip(bitmap)
        } else {
            bitmap
        }
    }

    internal fun circularClip(image: Bitmap): Bitmap {
        // From https://github.com/coil-kt/coil/blob/2.0.0-rc01/coil-base/src/main/java/coil/transform/CircleCropTransformation.kt
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val minSize = min(image.width, image.height)
        val radius = minSize / 2f
        val output = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        output.applyCanvas {
            drawCircle(radius, radius, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawBitmap(image, radius - image.width / 2f, radius - image.height / 2f, paint)
        }

        return output
    }

    private fun getView(): View = testContent!!

    private suspend fun saveSnapshot(snapshotting: FileSnapshotting<SemanticsNodeInteraction, Bitmap>) {
        snapshotting.snapshot(
            composeContentTestRule.onRoot(),
            testName = getTestName(),
            record = params.record,
            testClass = checkNotNull(testClassInfoRule.testClass) { "Could not retrieve information from test class" },
        )
        snapshotCount++
    }

    private fun getTestName(): String {
        val label = if (params.testLabel != null) "_${params.testLabel}" else ""
        val suffix = if (snapshotCount > 0) {
            "_${snapshotCount + 1}"
        } else {
            ""
        }
        return testClassInfoRule.methodName + label + suffix
    }

    @Composable
    private fun ScreenshotDefaults(
        fakeImageLoader: FakeImageLoader,
        round: Boolean,
        timeText: @Composable () -> Unit,
        positionIndicator: @Composable () -> Unit,
        content: @Composable () -> Unit,
    ) {
        fakeImageLoader.apply {
            Box(
                modifier = Modifier
                    .background(Color.Transparent),
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
                        positionIndicator = positionIndicator,
                    ) {
                        content()
                    }
                }
            }
        }
    }

    @Composable
    private fun ComponentDefaults(
        fakeImageLoader: FakeImageLoader,
        content: @Composable () -> Unit,
    ) {
        fakeImageLoader.override {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center,
            ) {
                DefaultTheme {
                    content()
                }
            }
        }
    }

    @Composable
    private fun DefaultTheme(content: @Composable () -> Unit) {
        content()
    }

    public data class ScreenshotTestRuleParams(
        public val tolerance: Float,
        public val enableA11y: Boolean,
        public val screenTimeText: @Composable () -> Unit,
        public val testLabel: String?,
        public val record: RecordMode,
        public val clipMode: ClipMode,
    ) {

        public class Builder internal constructor() {
            public var tolerance: Float = 0.1f
            public var enableA11y: Boolean = false
            public var screenTimeText: @Composable () -> Unit = defaultScreenTimeText()
            public var testLabel: String? = null
            public var record: RecordMode = defaultRecordMode()

            public var clipMode: ClipMode = ClipMode.Auto

            public fun build(): ScreenshotTestRuleParams {
                if (enableA11y) {
                    tolerance = 0.10f
                }

                return ScreenshotTestRuleParams(
                    tolerance = tolerance,
                    enableA11y = enableA11y,
                    screenTimeText = screenTimeText,
                    testLabel = testLabel,
                    record = record,
                    clipMode = clipMode,
                )
            }
        }
    }

    public enum class RecordMode {
        Test, Record, Repair;

        public companion object {
            public fun fromProperty(property: String?): RecordMode = when (property?.lowercase()) {
                "test", "false" -> Test
                "record", "true" -> Record
                "repair" -> Repair
                else -> Test
            }

            public fun defaultRecordMode(): RecordMode = RecordMode.fromProperty(
                System.getProperty("screenshot.record"),
            )
        }
    }

    public enum class ClipMode {
        Round, None, Auto
    }

    public companion object {
        private fun defaultScreenTimeText(): @Composable () -> Unit = {
            TimeText(
                timeSource = FixedTimeSource,
            )
        }

        public fun builder(): ScreenshotTestRuleParams.Builder = ScreenshotTestRuleParams.Builder()

        public inline fun screenshotTestRuleParams(block: ScreenshotTestRuleParams.Builder.() -> Unit): ScreenshotTestRuleParams {
            return builder().apply { block() }.build()
        }
    }
}

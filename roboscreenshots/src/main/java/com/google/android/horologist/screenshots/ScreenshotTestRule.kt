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

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import coil.compose.LocalImageLoader
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.coil.FakeImageLoader
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

/**
 * A [TestRule] that allows you to run screenshot tests of your composable components.
 *
 * This rule requires robolectric, so the test suite should run with [RobolectricTestRunner]. See
 * [ScreenshotBaseTest] for a basic configuration.
 */
@ExperimentalHorologistApi
public class ScreenshotTestRule(
    private val params: ScreenshotTestRuleParams = screenshotTestRuleParams { }
) : TestRule {

    private val testClassInfoRule: TestClassInfoRule = TestClassInfoRule()
    private val composeContentTestRule: ComposeContentTestRule = createComposeRule()

    private val snapshotTransformer: SnapshotTransformer = if (params.enableA11y) {
        A11ySnapshotTransformer()
    } else {
        SnapshotTransformer.None
    }
    private val resources: Resources =
        ApplicationProvider.getApplicationContext<Application>().resources

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
        composable: @Composable () -> Unit
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
                colorDiffing = Diffing.highlightWithRed
            ),
            snapshot = { node: SemanticsNodeInteraction ->
                val view = getView()
                val bitmap = Bitmap.createBitmap(
                    view.width,
                    view.height,
                    Bitmap.Config.ARGB_8888
                ).apply {
                    view.draw(Canvas(this))
                }

                if (isComponent) {
                    bitmap
                } else {
                    snapshotTransformer.transform(node, bitmap)
                }
            }
        ).fileSnapshotting

        runTest {
            saveSnapshot(snapshotting)
        }
    }

    private fun getView(): View = testContent!!

    private suspend fun saveSnapshot(snapshotting: FileSnapshotting<SemanticsNodeInteraction, Bitmap>) {
        snapshotting.snapshot(
            composeContentTestRule.onRoot(),
            testName = getTestName(),
            record = params.record,
            testClass = checkNotNull(testClassInfoRule.testClass) { "Could not retrieve information from test class" }
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

    @SuppressLint("ComposableNaming")
    @Suppress("DEPRECATION")
    @Composable
    private fun FakeImageLoader.apply(content: @Composable () -> Unit) {
        // Not sure why this is needed, but Coil has improved
        // test support in next release
        this.override {
            CompositionLocalProvider(LocalImageLoader provides this) {
                content()
            }
        }
    }

    @Composable
    private fun ScreenshotDefaults(
        fakeImageLoader: FakeImageLoader,
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

    @Composable
    private fun ComponentDefaults(
        fakeImageLoader: FakeImageLoader,
        content: @Composable () -> Unit
    ) {
        fakeImageLoader.override {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
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
        public val record: Boolean
    ) {

        public class Builder internal constructor() {
            public var tolerance: Float = 0.1f
            public var enableA11y: Boolean = false
            public var screenTimeText: @Composable () -> Unit = defaultScreenTimeText()
            public var testLabel: String? = null
            public var record: Boolean = false

            public fun build(): ScreenshotTestRuleParams {
                if (enableA11y) {
                    tolerance = 0.10f
                }

                return ScreenshotTestRuleParams(
                    tolerance = tolerance,
                    enableA11y = enableA11y,
                    screenTimeText = screenTimeText,
                    testLabel = testLabel,
                    record = record
                )
            }
        }
    }

    public companion object {
        private fun defaultScreenTimeText(): @Composable () -> Unit = {
            TimeText(
                timeSource = FixedTimeSource
            )
        }

        public fun builder(): ScreenshotTestRuleParams.Builder = ScreenshotTestRuleParams.Builder()

        public inline fun screenshotTestRuleParams(block: ScreenshotTestRuleParams.Builder.() -> Unit): ScreenshotTestRuleParams {
            return builder().apply { block() }.build()
        }
    }
}

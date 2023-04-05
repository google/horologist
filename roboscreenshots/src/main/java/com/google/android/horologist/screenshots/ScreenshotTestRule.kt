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
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
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
import org.junit.rules.TestName
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.robolectric.RobolectricTestRunner

/**
 * A [TestRule] that allows you to run screenshot tests of your composable components.
 *
 * This rule requires robolectric, so the test suite should run with [RobolectricTestRunner]. See
 * [ScreenShotBaseTest] for a basic configuration.
 */
@ExperimentalHorologistApi
public class ScreenshotTestRule(
    private val params: ScreenshotTestRuleParams = screenshotTestRuleParams { }
) : TestRule {

    private val testName: TestName = TestName()
    private val composeContentTestRule: ComposeContentTestRule = createComposeRule()

    private val snapshotTransformer: SnapshotTransformer = if (params.enableA11y) {
        A11ySnapshotTransformer()
    } else {
        SnapshotTransformer.None
    }
    private val resources: Resources =
        ApplicationProvider.getApplicationContext<Application>().resources

    override fun apply(base: Statement, description: Description): Statement {
        return RuleChain.outerRule(testName)
            .around(composeContentTestRule)
            .apply(base, description)
    }

    public fun takeScreenshot(
        roundScreen: Boolean? = null,
        timeText: @Composable () -> Unit = params.screenTimeText,
        positionIndicator: @Composable () -> Unit = { },
        fakeImageLoader: FakeImageLoader = FakeImageLoader.Never,
        checks: suspend ComposeContentTestRule.() -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val round = roundScreen ?: resources.configuration.isScreenRound

        runTest {
            lateinit var view: View

            composeContentTestRule.setContent {
                view = LocalView.current
                ScreenshotDefaults(fakeImageLoader, round, timeText, positionIndicator, content)
            }

            composeContentTestRule.awaitIdle()

            checks(composeContentTestRule)

            val snapshotting = Snapshotting(
                diffing = Diffing.bitmapWithTolerance(
                    tolerance = params.tolerance,
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

            saveSnapshot(snapshotting)
        }
    }

    private suspend fun saveSnapshot(snapshotting: FileSnapshotting<SemanticsNodeInteraction, Bitmap>) {
        snapshotting.snapshot(
            composeContentTestRule.onRoot(),
            testName = testName.methodName + (if (params.testLabel != null) "_${params.testLabel}" else ""),
            record = params.record,
            testClass = this@ScreenshotTestRule.javaClass
        )
    }

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

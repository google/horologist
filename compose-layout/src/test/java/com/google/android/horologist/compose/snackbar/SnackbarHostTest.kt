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

package com.google.android.horologist.compose.snackbar

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@MediumTest
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [30],
    qualifiers = "w227dp-h227dp-small-notlong-round-watch-xhdpi-keyshidden-nonav",
)
class SnackbarHostTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun snackbarHost_observePushedData() {
        var resultedInvocation = ""
        val hostState = SnackbarHostState()
        lateinit var scope: CoroutineScope
        rule.setContent {
            scope = rememberCoroutineScope()
            SnackbarHost(hostState) { data ->
                LaunchedEffect(data) {
                    resultedInvocation += data.message
                    data.dismiss()
                }
            }
        }
        val job = scope.launch {
            hostState.showSnackbar("1")
            Truth.assertThat(resultedInvocation).isEqualTo("1")
            hostState.showSnackbar("2")
            Truth.assertThat(resultedInvocation).isEqualTo("12")
            hostState.showSnackbar("3")
            Truth.assertThat(resultedInvocation).isEqualTo("123")
        }

        rule.waitUntil { job.isCompleted }
    }

    @Test
    fun snackbarHost_fifoQueueContract() {
        var resultedInvocation = ""
        val hostState = SnackbarHostState()
        lateinit var scope: CoroutineScope
        rule.setContent {
            scope = rememberCoroutineScope()
            SnackbarHost(hostState) { data ->
                LaunchedEffect(data) {
                    resultedInvocation += data.message
                    launch {
                        delay(30L)
                        data.dismiss()
                    }
                }
            }
        }
        val parent = SupervisorJob()
        repeat(10) {
            scope.launch(parent) {
                delay(it * 10L)
                hostState.showSnackbar(it.toString())
            }
        }

        rule.waitUntil { parent.children.all { it.isCompleted } }
        Truth.assertThat(resultedInvocation).isEqualTo("0123456789")
    }

    @Test
    @Ignore("ComposeTimeoutException: Condition still not satisfied after 5000 ms")
    fun snackbarHost_returnedResult() {
        val hostState = SnackbarHostState()
        lateinit var scope: CoroutineScope
        rule.setContent {
            scope = rememberCoroutineScope()
            SnackbarHost(hostState) { data ->
                TestSnackbar(data)
            }
        }
        val job1 = scope.launch {
            val result = hostState.showSnackbar("1", actionLabel = "press")
            Truth.assertThat(result).isEqualTo(SnackbarResult.ActionPerformed)
        }
        rule.onNodeWithText("press")
            .performClick()

        rule.waitUntil { job1.isCompleted }

        val job2 = scope.launch {
            val result = hostState.showSnackbar(
                message = "1",
                actionLabel = "do not press",
            )
            Truth.assertThat(result).isEqualTo(SnackbarResult.Dismissed)
        }

        rule.waitUntil(timeoutMillis = 5_000) { job2.isCompleted }
    }

    @Test
    @Ignore("Failing and Snackbar is not a recommended pattern")
    fun snackbarHost_scopeLifecycleRespect() {
        val switchState = mutableStateOf(true)
        val hostState = SnackbarHostState()
        lateinit var scope: CoroutineScope
        rule.setContent {
            if (switchState.value) {
                scope = rememberCoroutineScope()
            }
            SnackbarHost(hostState) { data ->
                TestSnackbar(data)
            }
        }
        val job1 = scope.launch {
            hostState.showSnackbar("1")
            Truth.assertWithMessage("Result shouldn't happen due to cancellation").fail()
        }
        val job2 = scope.launch {
            delay(10)
            switchState.value = false
        }

        rule.waitUntil { job1.isCompleted && job2.isCompleted }
    }

    @Ignore("Failing and Snackbar is not a recommended pattern")
    @Test
    fun snackbarHost_semantics() {
        val hostState = SnackbarHostState()
        lateinit var scope: CoroutineScope
        rule.setContent {
            scope = rememberCoroutineScope()
            SnackbarHost(hostState) { data ->
                TestSnackbar(data)
            }
        }
        val job1 = scope.launch {
            val result = hostState.showSnackbar("1", actionLabel = "press")
            Truth.assertThat(result).isEqualTo(SnackbarResult.Dismissed)
        }
        rule.onNodeWithText("1").onParent().onParent().onParent()
            .assert(
                SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite),
            )
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Dismiss)

        rule.waitUntil { job1.isCompleted }
    }

    @Ignore("Failing and Snackbar is not a recommended pattern")
    @Test
    fun snackbarDuration_toMillis_nonNullAccessibilityManager() {
        val mockDurationControl = 10000L
        val mockDurationNonControl = 5000L
        val accessibilityManager: AccessibilityManager = object : AccessibilityManager {
            override fun calculateRecommendedTimeoutMillis(
                originalTimeoutMillis: Long,
                containsIcons: Boolean,
                containsText: Boolean,
                containsControls: Boolean,
            ): Long = if (originalTimeoutMillis == Long.MAX_VALUE) {
                Long.MAX_VALUE
            } else if (containsControls == true) {
                mockDurationControl
            } else {
                mockDurationNonControl
            }
        }
        assertEquals(
            Long.MAX_VALUE,
            SnackbarDuration.Indefinite.toMillis(true, accessibilityManager),
        )
        assertEquals(
            Long.MAX_VALUE,
            SnackbarDuration.Indefinite.toMillis(false, accessibilityManager),
        )
        assertEquals(
            mockDurationControl,
            SnackbarDuration.Long.toMillis(true, accessibilityManager),
        )
        assertEquals(
            mockDurationNonControl,
            SnackbarDuration.Long.toMillis(false, accessibilityManager),
        )
        assertEquals(
            mockDurationControl,
            SnackbarDuration.Short.toMillis(true, accessibilityManager),
        )
        assertEquals(
            mockDurationNonControl,
            SnackbarDuration.Short.toMillis(false, accessibilityManager),
        )
    }

    @Test
    fun snackbarDuration_toMillis_nullAccessibilityManager() {
        assertEquals(
            Long.MAX_VALUE,
            SnackbarDuration.Indefinite.toMillis(true, null),
        )
        assertEquals(
            Long.MAX_VALUE,
            SnackbarDuration.Indefinite.toMillis(false, null),
        )
        assertEquals(
            10000L,
            SnackbarDuration.Long.toMillis(true, null),
        )
        assertEquals(
            10000L,
            SnackbarDuration.Long.toMillis(false, null),
        )
        assertEquals(
            4000L,
            SnackbarDuration.Short.toMillis(true, null),
        )
        assertEquals(
            4000L,
            SnackbarDuration.Short.toMillis(false, null),
        )
    }
}

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

package com.google.android.horologist.compose.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.whenResumed
import androidx.wear.compose.material.Text
import com.google.accompanist.pager.PagerState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class PagerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavScaffoldNavigation() = runTest {
        // Test pager sends the right events to each page
        // on changes

        var resumedScreen by mutableStateOf(0)

        val state = PagerState()

        composeTestRule.setContent {
            PagerScreen(modifier = Modifier.fillMaxSize(), count = 5, state = state) { i ->
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(Unit) {
                    lifecycleOwner.whenResumed {
                        resumedScreen = i
                    }
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(modifier = Modifier.testTag("text$i"), text = "Text $i")
                }
            }
        }

        assertThat(state.currentPage).isEqualTo(0)
        assertThat(state.pageCount).isEqualTo(5)

        val text0 = composeTestRule.onNodeWithTag("text0")
        val text1 = composeTestRule.onNodeWithTag("text1")
        val text2 = composeTestRule.onNodeWithTag("text2")
        val text3 = composeTestRule.onNodeWithTag("text3")
        val text4 = composeTestRule.onNodeWithTag("text4")

        text0.assertIsDisplayed()
        text1.assertIsNotDisplayed()
        text2.assertDoesNotExist()
        text3.assertDoesNotExist()
        text4.assertDoesNotExist()
        assertThat(resumedScreen).isEqualTo(0)

        state.scrollToPage(page = 1)
        composeTestRule.awaitIdle()

        text0.assertIsNotDisplayed()
        text1.assertIsDisplayed()
        text2.assertIsNotDisplayed()
        text3.assertDoesNotExist()
        text4.assertDoesNotExist()
        assertThat(resumedScreen).isEqualTo(1)
    }
}

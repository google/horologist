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
    ExperimentalFoundationApi::class,
    ExperimentalWearFoundationApi::class,
)

package com.google.android.horologist.compose.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.test.filters.MediumTest
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
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
@Ignore("Failing with robolectric")
class PagerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavScaffoldNavigation() = runTest {
        lateinit var pagerState: PagerState

        composeTestRule.setContent {
            pagerState = rememberPagerState {
                5
            }
            PagerScreen(modifier = Modifier.fillMaxSize(), state = pagerState) { i ->
                val focusRequester = rememberActiveFocusRequester()
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotaryWithScroll(scrollState, focusRequester)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(modifier = Modifier.testTag("text$i"), text = "Text $i")
                }
            }
        }

        assertThat(pagerState.currentPage).isEqualTo(0)
//        assertThat(state.pageCount).isEqualTo(5)

        val text0 = composeTestRule.onNodeWithTag("text0")
//        val text1 = composeTestRule.onNodeWithTag("text1")
        val text2 = composeTestRule.onNodeWithTag("text2")
        val text3 = composeTestRule.onNodeWithTag("text3")
        val text4 = composeTestRule.onNodeWithTag("text4")

        text0.onParent().assertIsFocused()
        text0.assertIsDisplayed()
        // No longer optimistically created in compose 1.4?
//        text1.onParent().assertIsNotFocused()
//        text1.assertIsNotDisplayed()
        text2.assertDoesNotExist()
        text3.assertDoesNotExist()
        text4.assertDoesNotExist()

        withContext(Dispatchers.Main) {
            pagerState.scrollToPage(page = 1)
        }
        composeTestRule.awaitIdle()
        assertThat(pagerState.currentPage).isEqualTo(1)

        text0.assertIsNotDisplayed()
        // No longer optimistically created in compose 1.4?
//        text1.assertIsDisplayed()
//        text2.assertIsNotDisplayed()
        text3.assertDoesNotExist()
        text4.assertDoesNotExist()
    }
}

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

@file:OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalHorologistComposeLayoutApi::class,
    ExperimentalFoundationApi::class
)

package com.google.android.horologist.compose.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.test.filters.MediumTest
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.focus.RequestFocusWhenActive
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithFling
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test

@MediumTest
class PagerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavScaffoldNavigation() = runTest {
        val state = PagerState()

        composeTestRule.setContent {
            PagerScreen(modifier = Modifier.fillMaxSize(), count = 5, state = state) { i ->
                val focusRequester = remember { FocusRequester() }
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotaryWithFling(focusRequester, scrollState)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(modifier = Modifier.testTag("text$i"), text = "Text $i")
                }
                RequestFocusWhenActive(focusRequester)
            }
        }

        assertThat(state.currentPage).isEqualTo(0)
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
            state.scrollToPage(page = 1)
        }
        composeTestRule.awaitIdle()
        assertThat(state.currentPage).isEqualTo(1)

        text0.assertIsNotDisplayed()
        // No longer optimistically created in compose 1.4?
//        text1.assertIsDisplayed()
//        text2.assertIsNotDisplayed()
        text3.assertDoesNotExist()
        text4.assertDoesNotExist()
    }
}

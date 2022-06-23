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

@file:OptIn(ExperimentalPagerApi::class)

package com.google.android.horologist.media.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.pager.FocusOnResume
import com.google.android.horologist.compose.pager.PagerScreen

@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
fun PlayerLibraryPagerScreen(
    pagerState: PagerState,
    playerScreen: @Composable (FocusRequester) -> Unit,
    libraryScreen: @Composable (FocusRequester) -> Unit,
    modifier: Modifier = Modifier,
) {
    PagerScreen(
        modifier = modifier,
        count = 2,
        state = pagerState
    ) { page ->
        when (page) {
            0 -> {
                val playerFocusRequester = remember { FocusRequester() }

                playerScreen(playerFocusRequester)

                FocusOnResume(playerFocusRequester)
            }
            1 -> {
                val libraryFocusRequester = remember { FocusRequester() }

                libraryScreen(libraryFocusRequester)

                FocusOnResume(libraryFocusRequester)
            }
        }
    }
}
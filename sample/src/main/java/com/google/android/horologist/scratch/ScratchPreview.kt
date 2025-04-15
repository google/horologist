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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.scratch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.expandableItem
import androidx.wear.compose.foundation.hierarchicalFocusRequester
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rememberExpandableState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults.behavior
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound

@WearPreviewLargeRound
@Composable
fun ScratchPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val state = rememberScalingLazyListState()
        val expandableState = rememberExpandableState()
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .hierarchicalFocusRequester()
                .rotaryScrollable(
                    behavior = behavior(scrollableState = state),
                    focusRequester = remember { FocusRequester() },
                ),
            state = state,
        ) {
            item {
                ListHeader {
                    Text(text = "Main")
                }
            }
            expandableItem(expandableState) {
                Text(text = "I am $it")
            }
            items(10) {
                Text(text = "Item $it")
            }
        }
    }
}

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

package com.google.android.horologist.navsample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.focus.rememberActiveFocusRequester
import com.google.android.horologist.compose.rotaryinput.rotaryWithFling
import com.google.android.horologist.sample.SampleChip

@Composable
fun NavMenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    scrollState: ScalingLazyListState
) {
    val focusRequester = rememberActiveFocusRequester()

    ScalingLazyColumn(
        modifier = modifier.rotaryWithFling(focusRequester, scrollState),
        state = scrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
        autoCentering = AutoCenteringParams(itemIndex = 0)
    ) {
        item {
            SampleChip(
                onClick = { navigateToRoute(NavScreen.ScalingLazyColumn.route) },
                label = "ScalingLazyColumn"
            )
        }
        item {
            SampleChip(
                onClick = { navigateToRoute(NavScreen.Column.route) },
                label = "Column"
            )
        }
        item {
            SampleChip(
                onClick = { navigateToRoute(NavScreen.Dialog.route) },
                label = "Dialog"
            )
        }
        item {
            SampleChip(
                onClick = { navigateToRoute(NavScreen.Pager.route) },
                label = "Pager"
            )
        }
        item {
            SampleChip(
                onClick = { navigateToRoute(NavScreen.Volume.route) },
                label = "Volume (custom scrolling)"
            )
        }
        item {
            SampleChip(
                onClick = { navigateToRoute(NavScreen.Snackbar.route) },
                label = "Snackbar"
            )
        }
    }
}

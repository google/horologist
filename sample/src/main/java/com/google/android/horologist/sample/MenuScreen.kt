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

package com.google.android.horologist.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.compose.focus.RequestFocusWhenActive
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter
import java.time.LocalDateTime

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    time: LocalDateTime,
    scrollState: ScalingLazyListState = rememberScalingLazyListState()
) {
    val focusRequester = remember { FocusRequester() }

    ScalingLazyColumn(
        modifier = modifier
            .rotaryWithSnap(
                focusRequester,
                scrollState.toRotaryScrollAdapter()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState
    ) {
        item {
            NetworkChip { navigateToRoute(Screen.Network.route) }
        }
        item {
            DataLayerNodesChip { navigateToRoute(Screen.DataLayerNodes.route) }
        }
        item {
            FillMaxRectangleChip(navigateToRoute)
        }
        item {
            VolumeScreenChip(navigateToRoute)
        }
        item {
            ScrollAwayChip("Scroll Away") { navigateToRoute(Screen.ScrollAway.route) }
        }
        item {
            ScrollAwayChip("Scroll Away SLC") { navigateToRoute(Screen.ScrollAwaySLC.route) }
        }
        item {
            ScrollAwayChip("Scroll Away Column") { navigateToRoute(Screen.ScrollAwayColumn.route) }
        }
        item {
            TimePickerChip(time) { navigateToRoute(Screen.TimePicker.route) }
        }
        item {
            DatePickerChip(time) { navigateToRoute(Screen.DatePicker.route) }
        }
        item {
            FromDatePickerChip(time) { navigateToRoute(Screen.FromDatePicker.route) }
        }
        item {
            TimeWithSecondsPickerChip(time) { navigateToRoute(Screen.TimeWithSecondsPicker.route) }
        }
        item {
            TimeWithoutSecondsPickerChip(time) { navigateToRoute(Screen.TimeWithoutSecondsPicker.route) }
        }
        item {
            Chip(
                label = {
                    Text(text = stringResource(id = R.string.sectionedlist_samples_menu))
                },
                modifier = modifier.fillMaxWidth(),
                onClick = { navigateToRoute(Screen.SectionedListMenuScreen.route) },
                colors = ChipDefaults.primaryChipColors()
            )
        }
    }

    RequestFocusWhenActive(focusRequester)
}

@Composable
fun SampleChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    content: (@Composable () -> Unit)? = null
) {
    Chip(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        label = {
            Text(modifier = Modifier.weight(1f), text = label)
            if (content != null) {
                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    content()
                }
            }
        }
    )
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Preview(
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun MenuScreenPreview() {
    MenuScreen(modifier = Modifier.fillMaxSize(), navigateToRoute = {}, time = LocalDateTime.now())
}

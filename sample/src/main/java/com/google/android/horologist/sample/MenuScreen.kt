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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText

@Composable
fun MenuScreen(modifier: Modifier = Modifier, navigateToRoute: (String) -> Unit) {
    Scaffold(timeText = { TimeText() }) {
        ScalingLazyColumn(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            autoCentering = true,
        ) {
            item {
                FillMaxRectangleChip(navigateToRoute)
            }
            item {
                Chip(
                    label = { Text("List UI Demo") },
                    onClick = { navigateToRoute(Screen.ListUi.route) }
                )
            }
        }
    }
}

@Composable
fun SampleChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    content: @Composable () -> Unit
) {
    Chip(
        modifier = modifier,
        onClick = onClick,
        colors = ChipDefaults.primaryChipColors(),
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier.weight(1f), text = label)
            Box(modifier = Modifier.size(36.dp)) {
                content()
            }
        }
    }
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
    MenuScreen(modifier = Modifier.fillMaxSize(), navigateToRoute = {})
}

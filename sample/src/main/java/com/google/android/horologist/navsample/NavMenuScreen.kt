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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.navsample

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@Composable
fun NavMenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    columnState: ScalingLazyColumnState,
) {
    ScalingLazyColumn(
        modifier = modifier,
        columnState = columnState,
    ) {
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(NavScreen.ScalingLazyColumn.route) },
                label = {
                    Text(modifier = Modifier.weight(1f), text = "ScalingLazyColumn")
                },
            )
        }
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(NavScreen.Column.route) },
                label = {
                    Text(modifier = Modifier.weight(1f), text = "Column")
                },
            )
        }
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(NavScreen.Dialog.route) },
                label = {
                    Text(modifier = Modifier.weight(1f), text = "Dialog")
                },
            )
        }
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(NavScreen.Pager.route) },
                label = {
                    Text(modifier = Modifier.weight(1f), text = "Pager")
                },
            )
        }
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(NavScreen.Volume.route) },
                label = {
                    Text(modifier = Modifier.weight(1f), text = "Volume (custom scrolling)")
                },
            )
        }
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToRoute(NavScreen.Snackbar.route) },
                label = {
                    Text(modifier = Modifier.weight(1f), text = "Snackbar")
                },
            )
        }
    }
}

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

package com.google.android.horologist.networks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@Composable
fun NetworkScreen(
    viewModel: NetworkScreenViewModel = viewModel(factory = NetworkScreenViewModel.Factory),
    columnConfig: ScalingLazyColumnState
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        columnConfig = columnConfig
    ) {
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.makeRequests() },
                label = {
                    Text("Requests")
                }
            )
        }
        items(uiState.responses.entries.toList()) { (name, response) ->
            Text(text = "$name: $response")
        }

        item {
            ListHeader {
                Text("Networks")
            }
        }
        items(uiState.networks.networks) {
            val downloads = uiState.dataUsage.dataByType[it.networkInfo.type] ?: 0
            Chip(

                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "${it.id} ${it.networkInfo.type} ${it.status}")
                },
                onClick = { },
                secondaryLabel = {
                    Text(text = "bytes: $downloads")
                }
            )
        }

        item {
            ListHeader {
                Text("Events")
            }
        }
        items(uiState.requests.takeLast(5).reversed()) {
            if (it is InMemoryStatusLogger.Event.NetworkResponse) {
                Text(text = "Network: ${it.networkInfo.type} ${it.bytesTransferred}")
            } else {
                Text(text = it.message)
            }
        }
    }
}

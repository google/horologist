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

package com.google.android.horologist.datalayer.sample.screens.nodes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Chip

@Composable
fun DataLayerNodesScreen(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState,
    viewModel: DataLayerNodesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            ListHeader {
                Text("Nodes")
            }
        }
        items(state.nodes) {
            Chip(
                label = "${it.displayName}(${it.id}) ${if (it.isNearby) "NEAR" else ""}",
                onClick = { },
            )
        }
        item {
            ListHeader {
                Text("Data")
            }
        }
        item {
            val thisData = state.thisData
            if (thisData != null) {
                Chip(
                    label = "This Value: ${thisData.value} (${thisData.name})",
                    onClick = {
                        viewModel.increment()
                    },
                )
            } else {
                Text("This Value: None")
            }
        }
        items(state.protoMap.entries.toList()) { (id, data) ->
            Text("$id Value: ${data.value} (${data.name})")
        }
    }
}

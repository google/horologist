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

package com.google.android.horologist.datalayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@Composable
fun DataLayerNodesScreen(
    viewModel: DataLayerNodesViewModel,
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScalingLazyColumn(
        modifier = modifier,
        columnState = columnState
    ) {
        item {
            ListHeader {
                Text("Nodes")
            }
        }
        items(state.nodes) {
            Chip(
                onClick = { },
                label = {
                    // REMOVEME: comment to check issues in CI
                    Text("${it.displayName}(${it.id}) ${if (it.isNearby) "NEAR" else ""}")
                }
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
                Button(onClick = {
                    viewModel.increment()
                }) {
                    Text("This Value: ${thisData.value} (${thisData.name})")
                }
            } else {
                Text("This Value: None")
            }
        }
        items(state.protoMap.entries.toList()) { (id, data) ->
            Text("$id Value: ${data.value} (${data.name})")
        }
    }
}

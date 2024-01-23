/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.datalayer.sample.screens.nodeslistener

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.gms.wearable.Node
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.datalayer.sample.R

@Composable
fun NodesListenerScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: NodesListenerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == NodesListenerScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    NodesListenerScreen(
        state = state,
        columnState = columnState,
        modifier = modifier,
    )
}

@Composable
fun NodesListenerScreen(
    state: NodesListenerScreenState,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            Title(
                textId = R.string.nodes_listener_screen_header,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
        when (state) {
            NodesListenerScreenState.Idle,
            NodesListenerScreenState.Loading,
            -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is NodesListenerScreenState.Loaded -> {
                item {
                    Text(stringResource(id = R.string.nodes_listener_screen_message))
                }

                if (state.nodeList.isNotEmpty()) {
                    items(state.nodeList.toList()) { node ->
                        Chip(
                            label = node.displayName,
                            onClick = { /* do nothing */ },
                            secondaryLabel = node.id,
                        )
                    }
                } else {
                    item {
                        Text(stringResource(id = R.string.nodes_listener_screen_no_nodes))
                    }
                }
            }

            NodesListenerScreenState.ApiNotAvailable -> {
                item {
                    Text(stringResource(id = R.string.wearable_message_api_unavailable))
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun NodesListenerScreenPreviewLoaded() {
    NodesListenerScreen(
        state = NodesListenerScreenState.Loaded(
            nodeList = setOf(
                NodePreviewImpl(
                    displayName = "Google Pixel Watch",
                    id = "903b8371",
                    isNearby = true,
                ),
                NodePreviewImpl(
                    displayName = "Galaxy Watch4 Classic",
                    id = "813d1812",
                    isNearby = false,
                ),
            ),
        ),
        columnState = belowTimeTextPreview(),
    )
}

@WearPreviewDevices
@Composable
fun NodesListenerScreenPreviewEmptyNodes() {
    NodesListenerScreen(
        state = NodesListenerScreenState.Loaded(emptySet()),
        columnState = belowTimeTextPreview(),
    )
}

@WearPreviewDevices
@Composable
fun NodesListenerScreenPreviewApiNotAvailable() {
    NodesListenerScreen(
        state = NodesListenerScreenState.ApiNotAvailable,
        columnState = belowTimeTextPreview(),
    )
}

private class NodePreviewImpl(
    private val displayName: String,
    private val id: String,
    private val isNearby: Boolean,
) : Node {
    override fun getDisplayName(): String = displayName

    override fun getId(): String = id

    override fun isNearby(): Boolean = isNearby
}

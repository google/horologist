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

package com.google.android.horologist.datalayer.sample.screens.nodesactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.CompactChip
import com.google.android.horologist.datalayer.sample.R
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable

@Composable
fun NodesActionsScreen(
    onNodeClick: (nodeId: String, appInstalled: Boolean) -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: NodesActionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == NodesActionScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    NodesActionsScreen(
        state = state,
        onNodeClick = onNodeClick,
        onRefreshClick = viewModel::onRefreshClick,
        columnState = columnState,
        modifier = modifier,
    )
}

@Composable
fun NodesActionsScreen(
    state: NodesActionScreenState,
    onNodeClick: (nodeId: String, appInstalled: Boolean) -> Unit,
    onRefreshClick: () -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            Text(
                text = stringResource(id = R.string.nodes_actions_header),
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
        when (state) {
            NodesActionScreenState.Idle,
            NodesActionScreenState.Loading,
            -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is NodesActionScreenState.Loaded -> {
                if (state.nodeList.isNotEmpty()) {
                    item {
                        Text(stringResource(id = R.string.nodes_actions_message))
                    }
                    items(state.nodeList) { node ->
                        val icon = when (node.type) {
                            NodeTypeUiModel.WATCH -> Icons.Default.Watch
                            NodeTypeUiModel.PHONE -> Icons.Default.PhoneAndroid
                            NodeTypeUiModel.UNKNOWN -> Icons.Default.DeviceUnknown
                        }

                        Chip(
                            label = node.name,
                            onClick = { onNodeClick(node.id, node.appInstalled) },
                            secondaryLabel = if (node.appInstalled) {
                                stringResource(id = R.string.nodes_actions_app_installed_label)
                            } else {
                                stringResource(id = R.string.nodes_actions_app_not_installed_label)
                            },
                            icon = ImageVectorPaintable(imageVector = icon),
                        )
                    }
                } else {
                    item {
                        Text(stringResource(id = R.string.nodes_actions_no_nodes))
                    }
                }

                item {
                    CompactChip(
                        label = stringResource(id = R.string.nodes_actions_refresh_chip_label),
                        onClick = onRefreshClick,
                        icon = ImageVectorPaintable(imageVector = Icons.Default.Refresh),
                    )
                }
            }

            NodesActionScreenState.ApiNotAvailable -> {
                item {
                    Text(stringResource(id = R.string.wearable_message_api_unavailable))
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun NodesActionsScreenPreviewLoaded() {
    NodesActionsScreen(
        state = NodesActionScreenState.Loaded(
            listOf(
                NodeUiModel(
                    id = "123",
                    name = "Pixel Watch",
                    appInstalled = true,
                    type = NodeTypeUiModel.WATCH,
                ),
                NodeUiModel(
                    id = "123",
                    name = "Pixel 6 Pro",
                    appInstalled = true,
                    type = NodeTypeUiModel.PHONE,
                ),
                NodeUiModel(
                    id = "123",
                    name = "Unknown",
                    appInstalled = false,
                    type = NodeTypeUiModel.UNKNOWN,
                ),
            ),
        ),
        onNodeClick = { _, _ -> },
        onRefreshClick = { },
        columnState = rememberResponsiveColumnState(),
    )
}

@WearPreviewDevices
@Composable
fun NodesActionsScreenPreviewEmptyNodes() {
    NodesActionsScreen(
        state = NodesActionScreenState.Loaded(emptyList()),
        onNodeClick = { _, _ -> },
        onRefreshClick = { },
        columnState = rememberResponsiveColumnState(),
    )
}

@WearPreviewDevices
@Composable
fun NodesActionsScreenPreviewApiNotAvailable() {
    NodesActionsScreen(
        state = NodesActionScreenState.ApiNotAvailable,
        onNodeClick = { _, _ -> },
        onRefreshClick = { },
        columnState = rememberResponsiveColumnState(),
    )
}

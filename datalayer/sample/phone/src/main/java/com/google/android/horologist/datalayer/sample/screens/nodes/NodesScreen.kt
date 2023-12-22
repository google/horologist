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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.data.apphelper.AppInstallationStatus
import com.google.android.horologist.data.apphelper.AppInstallationStatusNodeType
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.surfacesInfo
import com.google.android.horologist.data.tileInfo
import com.google.android.horologist.datalayer.sample.R
import com.google.android.horologist.datalayer.sample.util.toProtoTimestamp

@Composable
fun NodesScreen(
    modifier: Modifier = Modifier,
    viewModel: NodesActionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == NodesScreenState.Idle) {
        viewModel.initialize()
    }

    NodesScreen(
        state = state,
        onRefreshClick = viewModel::onRefreshClick,
        onInstallOnNodeClick = viewModel::onInstallOnNodeClick,
        onStartCompanionClick = viewModel::onStartCompanionClick,
        onStartRemoteOwnAppClick = viewModel::onStartRemoteOwnAppClick,
        onStartRemoteActivityClick = viewModel::onStartRemoteActivityClick,
        onDialogDismiss = viewModel::onDialogDismiss,
        modifier = modifier,
    )
}

@Composable
fun NodesScreen(
    state: NodesScreenState,
    onRefreshClick: () -> Unit,
    onInstallOnNodeClick: (nodeId: String) -> Unit,
    onStartCompanionClick: (nodeId: String) -> Unit,
    onStartRemoteOwnAppClick: (nodeId: String) -> Unit,
    onStartRemoteActivityClick: (nodeId: String) -> Unit,
    onDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showFailureDialog by rememberSaveable { mutableStateOf(false) }
    var errorCode by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = stringResource(id = R.string.nodes_screen_header),
                modifier = Modifier.padding(vertical = 10.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        when (state) {
            NodesScreenState.Idle,
            NodesScreenState.Loading,
            NodesScreenState.ActionRunning,
            -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is NodesScreenState.Loaded -> {
                if (state.nodeList.isNotEmpty()) {
                    items(items = state.nodeList) { nodeStatus ->
                        AppHelperNodeStatusCard(
                            nodeStatus = nodeStatus,
                            onInstallOnNodeClick = onInstallOnNodeClick,
                            onStartCompanionClick = onStartCompanionClick,
                            onStartRemoteOwnAppClick = onStartRemoteOwnAppClick,
                            onStartRemoteActivityClick = onStartRemoteActivityClick,
                        )
                    }
                } else {
                    item {
                        Text(stringResource(id = R.string.nodes_screen_no_nodes))
                    }
                }

                item {
                    Button(
                        onClick = onRefreshClick,
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 10.dp),
                            )
                            Text(
                                stringResource(id = R.string.nodes_screen_refresh_button_label),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.CenterVertically),
                            )
                        }
                    }
                }
            }

            NodesScreenState.ApiNotAvailable -> {
                item {
                    Text(stringResource(id = R.string.wearable_message_api_unavailable))
                }
            }

            is NodesScreenState.ActionFailed -> {
                showFailureDialog = true
                errorCode = state.errorCode
            }

            NodesScreenState.ActionSucceeded -> {
                showSuccessDialog = true
            }
        }
    }

    if (showSuccessDialog) {
        NodesActionSucceededDialog(
            message = stringResource(id = R.string.node_screen_success_dialog_message),
            onDismissRequest = {
                showSuccessDialog = false
                onDialogDismiss()
            },
        )
    }

    if (showFailureDialog) {
        NodesActionFailureDialog(
            message = stringResource(id = R.string.node_screen_failure_dialog_message, errorCode),
            onDismissRequest = {
                showFailureDialog = false
                onDialogDismiss()
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NodesScreenPreview() {
    NodesScreen(
        state = NodesScreenState.Loaded(
            listOf(
                AppHelperNodeStatus(
                    id = "a1b2c3d4",
                    displayName = "Pixel Watch",
                    appInstallationStatus = AppInstallationStatus.Installed(
                        nodeType = AppInstallationStatusNodeType.WATCH,
                    ),
                    surfacesInfo = surfacesInfo {
                        tiles.add(
                            tileInfo {
                                name = "MyTile"
                                timestamp = System.currentTimeMillis().toProtoTimestamp()
                            },
                        )
                        complications.add(
                            complicationInfo {
                                name = "MyComplication"
                                instanceId = 101
                                type = "SHORT_TEXT"
                                timestamp = System.currentTimeMillis().toProtoTimestamp()
                            },
                        )
                    },
                ),
            ),
        ),
        onRefreshClick = { },
        onInstallOnNodeClick = { },
        onStartCompanionClick = { },
        onStartRemoteOwnAppClick = { },
        onStartRemoteActivityClick = { },
        onDialogDismiss = { },
    )
}

@Preview(showBackground = true)
@Composable
fun NodesActionsScreenPreviewEmptyNodes() {
    NodesScreen(
        state = NodesScreenState.Loaded(emptyList()),
        onRefreshClick = { },
        onInstallOnNodeClick = { },
        onStartCompanionClick = { },
        onStartRemoteOwnAppClick = { },
        onStartRemoteActivityClick = { },
        onDialogDismiss = { },
    )
}

@Preview(showBackground = true)
@Composable
fun NodesScreenPreviewApiNotAvailable() {
    NodesScreen(
        state = NodesScreenState.ApiNotAvailable,
        onRefreshClick = { },
        onInstallOnNodeClick = { },
        onStartCompanionClick = { },
        onStartRemoteOwnAppClick = { },
        onStartRemoteActivityClick = { },
        onDialogDismiss = { },
    )
}

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

package com.google.android.horologist.datalayer.sample.screens.listnodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
fun ListNodesScreen(
    modifier: Modifier = Modifier,
    viewModel: ListNodesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == ListNodesScreenUiState.Idle) {
        viewModel.initialize()
    }

    ListNodesScreen(
        state = state,
        onListNodesClick = viewModel::onListNodesClick,
        onInstallClick = { viewModel.onInstallClick(it) },
        onLaunchClick = { viewModel.onLaunchClick(it) },
        onCompanionClick = { viewModel.onCompanionClick(it) },
        modifier = modifier,
    )
}

@Composable
fun ListNodesScreen(
    state: ListNodesScreenUiState,
    onListNodesClick: () -> Unit,
    onInstallClick: (String) -> Unit,
    onLaunchClick: (String) -> Unit,
    onCompanionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                onListNodesClick()
            },
            modifier = Modifier.wrapContentHeight(),
            enabled = state != ListNodesScreenUiState.ApiNotAvailable,
        ) { Text(stringResource(R.string.app_helper_button_list_nodes)) }

        when (state) {
            ListNodesScreenUiState.Idle,
            ListNodesScreenUiState.Loading,
            -> {
//                CircularProgressIndicator(
//                    modifier = Modifier.width(64.dp),
//                )
            }

            is ListNodesScreenUiState.Loaded -> {
                state.nodeList.forEach { nodeStatus ->
                    AppHelperNodeStatusCard(
                        nodeStatus = nodeStatus,
                        onInstallClick = onInstallClick,
                        onLaunchClick = onLaunchClick,
                        onCompanionClick = onCompanionClick,
                    )
                }
            }

            ListNodesScreenUiState.ApiNotAvailable -> {
                Text(
                    text = stringResource(R.string.wearable_message_api_unavailable),
                    modifier.fillMaxWidth(),
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListNodesScreenPreview() {
    val nodeList = listOf(
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
    )

    ListNodesScreen(
        state = ListNodesScreenUiState.Loaded(nodeList = nodeList),
        onListNodesClick = { },
        onInstallClick = { },
        onLaunchClick = { },
        onCompanionClick = { },
    )
}

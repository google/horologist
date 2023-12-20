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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.datalayer.sample.R

@Composable
fun NodeDetailsScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: NodeDetailsViewModel = hiltViewModel(),
) {
    NodeDetailsScreen(
        nodeId = viewModel.nodeId,
        onStartCompanionClick = viewModel::onStartCompanionClick,
        onInstallOnNodeClick = viewModel::onInstallOnNodeClick,
        onStartRemoteOwnAppClick = viewModel::onStartRemoteOwnAppClick,
        columnState = columnState,
        modifier = modifier,
    )
}

@Composable
fun NodeDetailsScreen(
    nodeId: String,
    onStartCompanionClick: () -> Unit,
    onInstallOnNodeClick: () -> Unit,
    onStartRemoteOwnAppClick: () -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            Text(
                text = stringResource(id = R.string.node_details_header),
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }

        item {
            Text(
                text = stringResource(id = R.string.node_details_id, nodeId),
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.node_details_start_companion_chip_label),
                onClick = onStartCompanionClick,
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.node_details_install_on_node_chip_label),
                onClick = onInstallOnNodeClick,
            )
        }
        item {
            Chip(
                label = stringResource(id = R.string.node_details_start_remote_own_app_chip_label),
                onClick = onStartRemoteOwnAppClick,
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun NodeDetailsScreenPreview() {
    NodeDetailsScreen(
        nodeId = "12345",
        onStartCompanionClick = { },
        onInstallOnNodeClick = { },
        onStartRemoteOwnAppClick = { },
        columnState = belowTimeTextPreview(),
    )
}

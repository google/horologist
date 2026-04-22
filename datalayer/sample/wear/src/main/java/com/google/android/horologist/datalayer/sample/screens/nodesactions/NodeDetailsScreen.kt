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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Confirmation
import com.google.android.horologist.compose.material.Icon
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.datalayer.sample.R
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable

@Composable
fun NodeDetailsScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: NodeDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NodeDetailsScreen(
        nodeId = viewModel.nodeId,
        appInstalled = viewModel.appInstalled,
        state = state,
        onStartCompanionClick = viewModel::onStartCompanionClick,
        onInstallOnNodeClick = viewModel::onInstallOnNodeClick,
        onStartRemoteOwnAppClick = viewModel::onStartRemoteOwnAppClick,
        onStartRemoteActivityClick = viewModel::onStartRemoteActivityClick,
        onDialogDismiss = viewModel::onDialogDismiss,
        columnState = columnState,
        modifier = modifier,
    )
}

@Composable
fun NodeDetailsScreen(
    nodeId: String,
    appInstalled: Boolean,
    state: NodeDetailsScreenState,
    onStartCompanionClick: () -> Unit,
    onInstallOnNodeClick: () -> Unit,
    onStartRemoteOwnAppClick: () -> Unit,
    onStartRemoteActivityClick: () -> Unit,
    onDialogDismiss: () -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showFailureDialog by rememberSaveable { mutableStateOf(false) }
    var errorCode by rememberSaveable { mutableStateOf("") }

    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            Title(
                textId = R.string.node_details_header,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }

        item {
            Text(
                text = stringResource(id = R.string.node_details_id, nodeId),
            )
        }
        when (state) {
            NodeDetailsScreenState.Idle -> {
                item {
                    Chip(
                        label = stringResource(id = R.string.node_details_start_companion_chip_label),
                        onClick = onStartCompanionClick,
                        enabled = appInstalled,
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
                        enabled = appInstalled,
                    )
                }
                item {
                    Chip(
                        label = stringResource(id = R.string.node_details_start_remote_activity_chip_label),
                        onClick = onStartRemoteActivityClick,
                        enabled = appInstalled,
                    )
                }
            }

            NodeDetailsScreenState.ActionRunning -> {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 10.dp))
                }
            }

            NodeDetailsScreenState.ActionSucceeded -> {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 10.dp))
                }

                showSuccessDialog = true
            }

            is NodeDetailsScreenState.ActionFailed -> {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 10.dp))
                }

                showFailureDialog = true
                errorCode = state.errorCode
            }
        }
    }

    Dialog(
        showDialog = showSuccessDialog,
        onDismissRequest = {
            showSuccessDialog = false
            onDialogDismiss()
        },
    ) {
        Confirmation(onTimeout = {
            showSuccessDialog = false
            onDialogDismiss()
        }) {
            Icon(
                paintable = ImageVectorPaintable(imageVector = Icons.Default.Done),
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally),
                tint = Color.Green,
            )
            Text(
                stringResource(id = R.string.node_details_success_dialog_message),
                textAlign = TextAlign.Center,
            )
        }
    }

    Dialog(
        showDialog = showFailureDialog,
        onDismissRequest = {
            showFailureDialog = false
            onDialogDismiss()
        },
    ) {
        Confirmation(onTimeout = {
            showFailureDialog = false
            onDialogDismiss()
        }) {
            Icon(
                paintable = ImageVectorPaintable(imageVector = Icons.Default.Close),
                contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally),
                tint = Color.Red,
            )
            Text(
                stringResource(id = R.string.node_details_failure_dialog_message, errorCode),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun NodeDetailsScreenPreview() {
    NodeDetailsScreen(
        nodeId = "12345",
        appInstalled = true,
        state = NodeDetailsScreenState.Idle,
        onStartCompanionClick = { },
        onInstallOnNodeClick = { },
        onStartRemoteOwnAppClick = { },
        onStartRemoteActivityClick = { },
        onDialogDismiss = { },
        columnState = rememberResponsiveColumnState(),
    )
}

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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.data.UsageStatus
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.data.apphelper.AppInstallationStatus
import com.google.android.horologist.data.apphelper.AppInstallationStatusNodeType
import com.google.android.horologist.data.apphelper.appInstalled
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.surfacesInfo
import com.google.android.horologist.data.tileInfo
import com.google.android.horologist.data.usageInfo
import com.google.android.horologist.datalayer.sample.R
import com.google.android.horologist.datalayer.sample.ui.theme.HorologistTheme
import com.google.android.horologist.datalayer.sample.util.toProtoTimestamp

@Composable
fun AppHelperNodeStatusCard(
    nodeStatus: AppHelperNodeStatus,
    onInstallOnNodeClick: (String) -> Unit,
    onStartCompanionClick: (String) -> Unit,
    onStartRemoteOwnAppClick: (String) -> Unit,
    onStartRemoteActivityClick: (nodeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),

            ) {
                Text(stringResource(R.string.node_status_node_name_label, nodeStatus.displayName))
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(R.string.node_status_node_id_label, nodeStatus.id),
                )
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(
                        R.string.node_status_is_app_installed_label,
                        nodeStatus.appInstalled,
                    ),
                )
                val nodeType = if (nodeStatus.appInstalled) {
                    (nodeStatus.appInstallationStatus as AppInstallationStatus.Installed).nodeType
                } else {
                    stringResource(id = R.string.node_status_node_type_unknown_label)
                }
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(R.string.node_status_node_type_label, nodeType),
                )
                if (nodeStatus.surfacesInfo.complicationsList.isNotEmpty()) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.node_status_complications_label,
                            nodeStatus.surfacesInfo.complicationsList.joinToString { it.name },
                        ),
                    )
                }
                if (nodeStatus.surfacesInfo.tilesList.isNotEmpty()) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.node_status_tiles_label,
                            nodeStatus.surfacesInfo.tilesList.joinToString { it.name },
                        ),
                    )
                }
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(
                        R.string.node_status_usage_status,
                        nodeStatus.surfacesInfo.usageInfo.usageStatus.name,
                    ),
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = { onStartCompanionClick(nodeStatus.id) },
                        modifier = Modifier.wrapContentHeight(),
                    ) {
                        Text(
                            stringResource(id = R.string.node_status_start_companion_button_label),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Button(
                        onClick = { onInstallOnNodeClick(nodeStatus.id) },
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                    ) {
                        Text(
                            stringResource(id = R.string.node_status_install_on_node_button_label),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = { onStartRemoteOwnAppClick(nodeStatus.id) },
                        modifier = Modifier.wrapContentHeight(),
                        enabled = nodeStatus.appInstalled,
                    ) {
                        Text(
                            stringResource(id = R.string.node_status_start_own_app_button_label),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Button(
                        onClick = { onStartRemoteActivityClick(nodeStatus.id) },
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(start = 10.dp),
                        enabled = nodeStatus.appInstalled,
                    ) {
                        Text(
                            stringResource(id = R.string.node_status_start_remote_activity_button_label),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NodeCardPreview() {
    val nodeStatus = AppHelperNodeStatus(
        displayName = "Pixel Watch",
        id = "a1b2c3",
        appInstallationStatus = AppInstallationStatus.Installed(
            nodeType = AppInstallationStatusNodeType.WATCH,
        ),
        surfacesInfo = surfacesInfo {
            tiles.add(
                tileInfo {
                    name = "Horologist Tile"
                    timestamp = System.currentTimeMillis().toProtoTimestamp()
                },
            )
            complications.add(
                complicationInfo {
                    type = "SHORT_TEXT"
                    instanceId = 123
                    name = "Horologist Complication"
                    timestamp = System.currentTimeMillis().toProtoTimestamp()
                },
            )
            usageInfo = usageInfo {
                usageStatus = UsageStatus.USAGE_STATUS_LAUNCHED_ONCE
                timestamp = System.currentTimeMillis().toProtoTimestamp()
            }
        },
    )
    HorologistTheme {
        AppHelperNodeStatusCard(
            nodeStatus = nodeStatus,
            onStartCompanionClick = { },
            onInstallOnNodeClick = { },
            onStartRemoteOwnAppClick = { },
            onStartRemoteActivityClick = { },
        )
    }
}

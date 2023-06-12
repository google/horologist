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

package com.google.android.horologist.auth.sample

import android.os.Build
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.auth.sample.ui.theme.HorologistTheme
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.data.apphelper.AppHelperNodeType
import com.google.android.horologist.data.apphelper.Util.toProtoTimestamp
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.surfacesInfo
import com.google.android.horologist.data.tileInfo
import java.time.Instant

@Composable
fun AppHelperNodeStatusCard(
    nodeStatus: AppHelperNodeStatus,
    onInstallClick: (String) -> Unit,
    onLaunchClick: (String) -> Unit,
    onCompanionClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()

            ) {
                Text(stringResource(R.string.app_helper_node_name_label, nodeStatus.displayName))
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(R.string.app_helper_node_id_label, nodeStatus.id)
                )
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(R.string.app_helper_node_type_label, nodeStatus.nodeType)
                )
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(
                        R.string.app_helper_is_app_installed_label,
                        nodeStatus.isAppInstalled
                    )
                )
                if (nodeStatus.surfacesInfo.complicationsList.isNotEmpty()) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.app_helper_complications_label,
                            nodeStatus.surfacesInfo.complicationsList.joinToString { it.name }
                        )
                    )
                }
                if (nodeStatus.surfacesInfo.tilesList.isNotEmpty()) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.app_helper_tiles_label,
                            nodeStatus.surfacesInfo.tilesList.joinToString { it.name }
                        )
                    )
                }
                if (nodeStatus.surfacesInfo.activityLaunched.activityLaunchedOnce) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.app_has_been_opened,
                            nodeStatus.surfacesInfo.activityLaunched.activityLaunchedOnce
                        )
                    )
                }
                if (nodeStatus.nodeInfo.hasApp()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(
                                R.string.update_time_label,
                                Instant.ofEpochSecond(nodeStatus.nodeInfo.app.updateTime.seconds)
                            )
                        )
                    }
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.app_version_label,
                            nodeStatus.nodeInfo.app.appVersion
                        )
                    )
                }
                if (nodeStatus.nodeInfo.hasDevice()) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.api_version_label,
                            nodeStatus.nodeInfo.device.apiVersion
                        )
                    )
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.device_info_label,
                            nodeStatus.nodeInfo.device.manufacturer,
                            nodeStatus.nodeInfo.device.model,
                            nodeStatus.nodeInfo.device.device,
                            nodeStatus.nodeInfo.device.product
                        )
                    )
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.locale_label,
                            nodeStatus.nodeInfo.device.locale
                        )
                    )
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.features_label,
                            nodeStatus.nodeInfo.device.featureList.size
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.wrapContentHeight(),
                        onClick = { onInstallClick(nodeStatus.id) }
                    ) {
                        Text(stringResource(id = R.string.app_helper_install_button_label))
                    }
                    Button(
                        modifier = Modifier.wrapContentHeight(),
                        onClick = { onLaunchClick(nodeStatus.id) }
                    ) {
                        Text(stringResource(id = R.string.app_helper_launch_button_label))
                    }
                    Button(
                        modifier = Modifier.wrapContentHeight(),
                        onClick = { onCompanionClick(nodeStatus.id) }
                    ) {
                        Text(stringResource(id = R.string.app_helper_companion_button_label))
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
        isAppInstalled = true,
        nodeType = AppHelperNodeType.WATCH,
        surfacesInfo = surfacesInfo {
            tiles.add(
                tileInfo {
                    name = "Horologist Tile"
                    timestamp = System.currentTimeMillis().toProtoTimestamp()
                }
            )
            complications.add(
                complicationInfo {
                    type = "SHORT_TEXT"
                    instanceId = 123
                    name = "Horologist Complication"
                    timestamp = System.currentTimeMillis().toProtoTimestamp()
                }
            )
        }
    )
    HorologistTheme {
        AppHelperNodeStatusCard(
            nodeStatus = nodeStatus,
            onCompanionClick = {},
            onInstallClick = {},
            onLaunchClick = {}
        )
    }
}

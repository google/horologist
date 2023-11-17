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

package com.google.android.horologist.datalayer.sample

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.data.UsageStatus
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.data.apphelper.AppHelperNodeType
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.surfacesInfo
import com.google.android.horologist.data.tileInfo
import com.google.android.horologist.data.usageInfo
import com.google.android.horologist.datalayer.sample.prompt.InstallAppDialog
import com.google.android.horologist.datalayer.sample.ui.theme.HorologistTheme
import com.google.protobuf.Timestamp

@Composable
fun AppHelperNodeStatusCard(
    nodeStatus: AppHelperNodeStatus,
    onInstallClick: (String) -> Unit,
    onLaunchClick: (String) -> Unit,
    onCompanionClick: (String) -> Unit,
    onInstallAppClick: (packageName: String) -> Unit,
    onAddTileClick: (String) -> Unit,
) {
    var showInstallAppDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
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
                Text(stringResource(R.string.app_helper_node_name_label, nodeStatus.displayName))
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(R.string.app_helper_node_id_label, nodeStatus.id),
                )
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(R.string.app_helper_node_type_label, nodeStatus.nodeType),
                )
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(
                        R.string.app_helper_is_app_installed_label,
                        nodeStatus.isAppInstalled,
                    ),
                )
                if (nodeStatus.surfacesInfo.complicationsList.isNotEmpty()) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.app_helper_complications_label,
                            nodeStatus.surfacesInfo.complicationsList.joinToString { it.name },
                        ),
                    )
                }
                if (nodeStatus.surfacesInfo.tilesList.isNotEmpty()) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(
                            R.string.app_helper_tiles_label,
                            nodeStatus.surfacesInfo.tilesList.joinToString { it.name },
                        ),
                    )
                }
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(
                        R.string.app_helper_usage_status,
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
                        modifier = Modifier.wrapContentHeight(),
                        onClick = { onInstallClick(nodeStatus.id) },
                    ) {
                        Text(stringResource(id = R.string.app_helper_install_button_label))
                    }
                    Button(
                        modifier = Modifier.wrapContentHeight(),
                        onClick = { onLaunchClick(nodeStatus.id) },
                    ) {
                        Text(stringResource(id = R.string.app_helper_launch_button_label))
                    }
                    Button(
                        modifier = Modifier.wrapContentHeight(),
                        onClick = { onCompanionClick(nodeStatus.id) },
                    ) {
                        Text(stringResource(id = R.string.app_helper_companion_button_label))
                    }
                }
                /*
                This button should only be displayed when AppHelperNodeStatus.isAppInstalled returns
                false. However, given our sample app is not published on Play, we are using Gmail as
                a "sample" in order to display the full flow.
                 */
                Button(
                    modifier = Modifier.wrapContentHeight(),
                    onClick = {
                        showInstallAppDialog = true
                    },
                ) {
                    Text(stringResource(id = R.string.app_helper_install_app_prompt_button_label))
                }

                Button(
                    modifier = Modifier.wrapContentHeight(),
                    onClick = {
                        onAddTileClick(nodeStatus.displayName)
                    },
                ) {
                    Text(stringResource(id = R.string.app_helper_add_tile_prompt_button_label))
                }
            }
        }

        if (showInstallAppDialog) {
            val sampleAppPackage =
                stringResource(id = R.string.app_helper_install_app_prompt_sample_app_package)

            InstallAppDialog(
                appName = stringResource(id = R.string.app_helper_install_app_prompt_sample_app_name),
                watchName = nodeStatus.displayName,
                message = stringResource(id = R.string.app_helper_install_app_prompt_sample_message),
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.sample_app_wearos_screenshot),
                        contentDescription = null,
                    )
                },
                onDismissRequest = {
                    showInstallAppDialog = false
                },
                onConfirmation = {
                    showInstallAppDialog = false

                    onInstallAppClick(sampleAppPackage)
                },
            )
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
            onCompanionClick = { },
            onInstallClick = { },
            onLaunchClick = { },
            onInstallAppClick = { },
            onAddTileClick = { },
        )
    }
}

fun Long.toProtoTimestamp(): Timestamp {
    return Timestamp.newBuilder()
        .setSeconds(this / 1000)
        .setNanos((this % 1000).toInt() * 1000000)
        .build()
}

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

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.horologist.data.ProtoDataStoreHelper.protoDataStore
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.WearableApiAvailability
import com.google.android.horologist.data.apphelper.AppHelperNodeStatus
import com.google.android.horologist.data.apphelper.AppInstallationStatus
import com.google.android.horologist.data.apphelper.AppInstallationStatusNodeType
import com.google.android.horologist.data.complicationInfo
import com.google.android.horologist.data.surfacesInfo
import com.google.android.horologist.data.tileInfo
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import com.google.android.horologist.datalayer.phone.ui.PhoneUiDataLayerHelper
import com.google.android.horologist.datalayer.sample.shared.CounterValueSerializer
import com.google.android.horologist.datalayer.sample.shared.grpc.GrpcDemoProto.CounterValue
import com.google.android.horologist.datalayer.sample.shared.grpc.copy
import com.google.android.horologist.datalayer.sample.ui.theme.HorologistTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var phoneDataLayerAppHelper: PhoneDataLayerAppHelper
    private lateinit var phoneUiDataLayerHelper: PhoneUiDataLayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val registry = WearDataLayerRegistry.fromContext(
            application = this@MainActivity.applicationContext,
            coroutineScope = lifecycleScope,
        ).apply {
            registerSerializer(CounterValueSerializer)
        }

        phoneDataLayerAppHelper = PhoneDataLayerAppHelper(
            context = this,
            registry = registry,
        )
        phoneUiDataLayerHelper = PhoneUiDataLayerHelper()

        val counterDataStore = registry.protoDataStore<CounterValue>(lifecycleScope)

        setContent {
            HorologistTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    var nodeList by remember { mutableStateOf<List<AppHelperNodeStatus>>(emptyList()) }
                    var apiAvailable by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            apiAvailable = WearableApiAvailability.isAvailable(registry.dataClient)
                            nodeList =
                                if (apiAvailable) phoneDataLayerAppHelper.connectedNodes() else listOf()
                        }
                    }

                    val counterState by counterDataStore.data.collectAsState(initial = CounterValue.getDefaultInstance())

                    val sampleAppName =
                        stringResource(id = R.string.app_helper_install_app_prompt_sample_app_name)
                    val sampleAppMessage =
                        stringResource(id = R.string.app_helper_install_app_prompt_sample_message)

                    val installAppPromptLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.StartActivityForResult(),
                    ) { result ->
                        Log.d(TAG, "Did user push install? ${result.resultCode == RESULT_OK}")
                    }

                    MainScreen(
                        apiAvailable = apiAvailable,
                        nodeList = nodeList,
                        onListNodes = {
                            coroutineScope.launch {
                                nodeList = phoneDataLayerAppHelper.connectedNodes()
                            }
                        },
                        onInstallClick = { nodeId ->
                            coroutineScope.launch {
                                phoneDataLayerAppHelper.installOnNode(nodeId)
                            }
                        },
                        onLaunchClick = { nodeId ->
                            coroutineScope.launch {
                                phoneDataLayerAppHelper.startRemoteOwnApp(nodeId)
                            }
                        },
                        onCompanionClick = { nodeId ->
                            coroutineScope.launch {
                                phoneDataLayerAppHelper.startCompanion(nodeId)
                            }
                        },
                        counterState = counterState,
                        onCounterIncrement = {
                            coroutineScope.launch {
                                counterDataStore.updateData {
                                    it.copy {
                                        value += 1
                                        updated = System.currentTimeMillis().toProtoTimestamp()
                                    }
                                }
                            }
                        },
                        onInstallAppPromptClick = { watchName ->
                            installAppPromptLauncher.launch(
                                phoneUiDataLayerHelper.getInstallPromptIntent(
                                    context = this@MainActivity,
                                    appName = sampleAppName,
                                    watchName = watchName,
                                    message = sampleAppMessage,
                                    image = R.drawable.sample_app_wearos_screenshot,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }

    private companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}

@Composable
fun MainScreen(
    apiAvailable: Boolean,
    nodeList: List<AppHelperNodeStatus>,
    onListNodes: () -> Unit,
    onInstallClick: (String) -> Unit,
    onLaunchClick: (String) -> Unit,
    onCompanionClick: (String) -> Unit,
    counterState: CounterValue,
    onCounterIncrement: () -> Unit,
    onInstallAppPromptClick: (watchName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                onListNodes()
            },
            modifier = Modifier.wrapContentHeight(),
            enabled = apiAvailable,
        ) { Text(stringResource(R.string.app_helper_button_list_nodes)) }

        nodeList.forEach { nodeStatus ->
            AppHelperNodeStatusCard(
                nodeStatus = nodeStatus,
                onInstallClick = onInstallClick,
                onLaunchClick = onLaunchClick,
                onCompanionClick = onCompanionClick,
                onInstallAppPromptClick = onInstallAppPromptClick,
            )
        }

        if (!apiAvailable) {
            Text(
                text = stringResource(R.string.wearable_message_api_unavailable),
                modifier.fillMaxWidth(),
                color = Color.Red,
                textAlign = TextAlign.Center,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.app_helper_counter_label, counterState.value))
            Button(
                onClick = onCounterIncrement,
                modifier = Modifier.padding(start = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.app_helper_counter_increase_btn_content_description),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
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
    HorologistTheme {
        MainScreen(
            apiAvailable = true,
            nodeList = nodeList,
            onListNodes = { },
            onInstallClick = { },
            onLaunchClick = { },
            onCompanionClick = { },
            counterState = CounterValue.getDefaultInstance(),
            onCounterIncrement = { },
            onInstallAppPromptClick = { },
        )
    }
}

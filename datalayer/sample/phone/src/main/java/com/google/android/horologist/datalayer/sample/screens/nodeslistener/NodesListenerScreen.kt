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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wearable.Node
import com.google.android.horologist.datalayer.sample.R

@Composable
fun NodesListenerScreen(
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
        modifier = modifier,
    )
}

@Composable
fun NodesListenerScreen(
    state: NodesListenerScreenState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = stringResource(id = R.string.nodes_listener_screen_header),
                modifier = Modifier.padding(vertical = 10.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (state != NodesListenerScreenState.ApiNotAvailable) {
            item {
                Text(
                    text = stringResource(id = R.string.nodes_listener_screen_message),
                    modifier = Modifier.padding(vertical = 10.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
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
                if (state.nodeList.isNotEmpty()) {
                    items(items = state.nodeList.toList()) { node ->
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
                                    Text(
                                        stringResource(
                                            R.string.nodes_listener_screen_node_name_label,
                                            node.displayName,
                                        ),
                                    )
                                    Text(
                                        style = MaterialTheme.typography.labelMedium,
                                        text = stringResource(
                                            R.string.nodes_listener_screen_node_id_label,
                                            node.id,
                                        ),
                                    )
                                }
                            }
                        }
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

@Preview(showBackground = true)
@Composable
fun NodesListenerScreenPreview() {
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

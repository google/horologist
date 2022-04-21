/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.scratch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.scrollableColumn

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val navController = rememberSwipeDismissableNavController()

    WearNavScaffold(
        startDestination = "Menu",
        navController = navController,
    ) {
        scalingLazyColumnComposable(
            route = "Menu",
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollableColumn(
                        focusRequester = it.viewModel.focusRequester,
                        scrollableState = it.scrollableState
                    ),
                state = it.scrollableState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Chip(
                        label = { Text("Example 0") },
                        onClick = { navController.navigate("Example0") }
                    )
                }
                item {
                    Chip(
                        label = { Text("Example 1") },
                        onClick = { navController.navigate("Example1") }
                    )
                }
                item {
                    Chip(
                        label = { Text("Example 2") },
                        onClick = { navController.navigate("Example2") }
                    )
                }
                item {
                    Chip(
                        label = { Text("Example 3") },
                        onClick = { navController.navigate("Example3") }
                    )
                }
                item {
                    Chip(
                        label = { Text("Toggle 0") },
                        onClick = { navController.navigate("Toggle0") }
                    )
                }
                item {
                    Chip(
                        label = { Text("Toggle 1") },
                        onClick = { navController.navigate("Toggle1") }
                    )
                }
                item {
                    Chip(
                        label = { Text("Toggle 2") },
                        onClick = { navController.navigate("Toggle2") }
                    )
                }
                item {
                    Chip(
                        label = { Text("Toggle 3") },
                        onClick = { navController.navigate("Toggle3") }
                    )
                }
            }
        }
        scalingLazyColumnComposable(
            route = "Example0",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
        ) {
            ExampleScreen(Modifier.fillMaxSize(), it.viewModel.focusRequester, it.scrollableState)
        }
        scalingLazyColumnComposable(
            route = "Example1",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 1) }
        ) {
            ExampleScreen(Modifier.fillMaxSize(), it.viewModel.focusRequester, it.scrollableState)
        }
        scalingLazyColumnComposable(
            route = "Example2",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 2) }
        ) {
            ExampleScreen(Modifier.fillMaxSize(), it.viewModel.focusRequester, it.scrollableState)
        }
        scalingLazyColumnComposable(
            route = "Example3",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 3) }
        ) {
            ExampleScreen(Modifier.fillMaxSize(), it.viewModel.focusRequester, it.scrollableState)
        }
        scalingLazyColumnComposable(
            route = "Toggle0",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
        ) {
            ExampleToggleScreen(
                Modifier.fillMaxSize(),
                it.viewModel.focusRequester,
                it.scrollableState
            )
        }
        scalingLazyColumnComposable(
            route = "Toggle1",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 1) }
        ) {
            ExampleToggleScreen(
                Modifier.fillMaxSize(),
                it.viewModel.focusRequester,
                it.scrollableState
            )
        }
        scalingLazyColumnComposable(
            route = "Toggle2",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 2) }
        ) {
            ExampleToggleScreen(
                Modifier.fillMaxSize(),
                it.viewModel.focusRequester,
                it.scrollableState
            )
        }
        scalingLazyColumnComposable(
            route = "Toggle3",
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 3) }
        ) {
            ExampleToggleScreen(
                Modifier.fillMaxSize(),
                it.viewModel.focusRequester,
                it.scrollableState
            )
        }
    }
}

@Composable
private fun ExampleScreen(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    scrollableState: ScalingLazyListState,
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(
                focusRequester = focusRequester,
                scrollableState = scrollableState
            ),
        state = scrollableState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CardI(0)
        }
        item {
            CardI(1)
        }
        item {
            CardI(2)
        }
        item {
            CardI(3)
        }
    }
}

@Composable
private fun ExampleToggleScreen(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    scrollableState: ScalingLazyListState,
) {
    val showAll = remember { mutableStateOf(false) }
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .scrollableColumn(
                focusRequester = focusRequester,
                scrollableState = scrollableState
            ),
        state = scrollableState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showAll.value) {
            item {
                key(0) {
                    CardI(0, onClick = { showAll.value = true })
                }
            }
            item {
                key(1) {
                    CardI(1, onClick = { showAll.value = true })
                }
            }
            item {
                key(2) {
                    CardI(2, onClick = { showAll.value = true })
                }
            }
        }
        item {
            key(3) {
                CardI(3, onClick = { showAll.value = true })
            }
        }
    }
}

@Composable
private fun CardI(i: Int, onClick: () -> Unit = {}) {
    Card(onClick = onClick) {
        Column() {
            Text(text = "Item $i")
        }
    }
}

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.compose.focus.rememberActiveFocusRequester
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.scrollable
import com.google.android.horologist.compose.rotaryinput.rotaryWithFling

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
    WearNavScaffold(startDestination = "menu", navController = navController) {
        // new Horologist scrollable DSL method
        scrollable("menu") {
            com.google.android.horologist.compose.layout.ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                columnState = it.columnState
            ) {
                item {
                    ListHeader {
                        Text("scrollable")
                    }
                }
                item {
                    StandardChip(
                        label = "composable",
                        onClick = { navController.navigate("composable") })
                }
                item {
                    StandardChip(
                        label = "scalingLazyColumnComposable",
                        onClick = { navController.navigate("scalingLazyColumnComposable") })
                }
                items(100) {
                    Text("Item $it")
                }
            }
        }
        // The standard composable DSL method
        composable("composable") {
            val focusRequester = rememberActiveFocusRequester()

            val listState = rememberScalingLazyListState()

            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize().rotaryWithFling(focusRequester, listState),
                state = listState
            ) {
                item {
                    ListHeader {
                        Text("composable")
                    }
                }
                items(100) {
                    Text("Item $it")
                }
            }
        }
        // The deprecated Horologist method
        scalingLazyColumnComposable("scalingLazyColumnComposable", scrollStateBuilder = {
            ScalingLazyListState()
        }) {
            val focusRequester = rememberActiveFocusRequester()

            val listState = it.scrollableState

            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize().rotaryWithFling(focusRequester, listState),
                state = listState
            ) {
                item {
                    ListHeader {
                        Text("scalingLazyColumn")
                    }
                }
                items(100) {
                    Text("Item $it")
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.material.scrollAway

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
    var expanded by remember { mutableIntStateOf(0) }
    val state = rememberScalingLazyListState()
    Scaffold(
        positionIndicator = { PositionIndicator(state) },
        timeText = {
            TimeText(modifier = Modifier.scrollAway(state),
                startCurvedContent = {
                    curvedText("Pos ${state.centerItemIndex}/${state.centerItemScrollOffset}")
                },
                endCurvedContent = {
                    curvedText("Scrolling ${state.isScrollInProgress}")
                })
        }
    ) {
        ScalingLazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
            item {
                Chip(label = { Text("10 Items") }, modifier = Modifier.fillMaxWidth(), onClick = {
                    expanded = 10
                })
            }
            items(expanded) {
                Chip(
                    label = { Text("${it + 1} Items") },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        expanded = it + 1
                    })
            }
        }
    }
}

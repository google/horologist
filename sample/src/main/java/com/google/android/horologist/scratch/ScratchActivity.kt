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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.compose.layout.fadeAwayScalingLazyList
import com.google.android.horologist.compose.pager.PageScreenIndicatorState

class ScratchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

data class Offsets(
    val index: Int = 0,
    val offset: Int = 0,
)

@Composable
fun WearApp() {
    var initialOffsetsMode by remember { mutableStateOf(0) }
    val initialOffsets = remember {
        listOf(
            Offsets(0, 0),
            Offsets(1, 0),
            Offsets(1, -20),
            Offsets(1, 20),
            Offsets(2, 0)
        )
    }

    var autoCenteringMode by remember { mutableStateOf(1) }
    val autoCenterings = remember {
        listOf(
            Pair("null", null),
            Pair("0/0", AutoCenteringParams(0, 0)),
            Pair("1/0", AutoCenteringParams(1, 0)),
            Pair("2/0", AutoCenteringParams(2, 0)),
            Pair("3/0", AutoCenteringParams(3, 0))
        )
    }

    var itemHeightMode by remember { mutableStateOf(0) }
    val itemHeights = remember { listOf(40, 80, 120) }

    key(initialOffsetsMode) {
        val initialOffset = initialOffsets[initialOffsetsMode]
        val itemHeight = itemHeights[itemHeightMode]
        val autoCentering = autoCenterings[autoCenteringMode]

        val listState = rememberScalingLazyListState(
            initialCenterItemIndex = initialOffset.index,
            initialCenterItemScrollOffset = initialOffset.offset,
        )

        SideEffect {
            println("Auto Centering $autoCentering $autoCenteringMode")
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            timeText = {
                TimeText(
                    modifier = Modifier
                        .fadeAwayScalingLazyList(
                            initialIndex = initialOffset.index, initialOffset = initialOffset.offset
                        ) {
                            listState
                        },
                    startCurvedContent = {
                        curvedText("${listState.centerItemIndex}/${listState.centerItemScrollOffset}")
                    }
                )
            },
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                autoCentering = autoCentering.second
            ) {
                item {
                    val text = "Item Height: $itemHeight"
                    FixedHeightChip(text, itemHeight, onClick = {
                        itemHeightMode = (itemHeightMode + 1) % itemHeights.size
                    })
                }
                item {
                    val text = "Initial Offset: ${initialOffset.index} / ${initialOffset.offset}"
                    FixedHeightChip(text, itemHeight, onClick = {
                        initialOffsetsMode = (initialOffsetsMode + 1) % initialOffsets.size
                    })
                }
                item {
                    val text = "Auto Centering: ${autoCentering.first}"
                    FixedHeightChip(text, itemHeight, onClick = {
                        println(autoCenteringMode)
                        autoCenteringMode = (autoCenteringMode + 1) % autoCenterings.size
                    })
                }
            }
        }
    }
}

@Composable
fun FixedHeightChip(text: String, itemHeight: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(itemHeight.dp)
            .fillMaxWidth()
            .border(1.dp, Color.DarkGray)
    ) {
        Chip(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onClick,
            colors = ChipDefaults.primaryChipColors(),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.caption3
            )
        }
    }
}

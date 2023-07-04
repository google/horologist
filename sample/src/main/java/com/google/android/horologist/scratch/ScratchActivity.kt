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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.material.scrollAway
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberColumnState
import java.text.DecimalFormat

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
    val columnState = ScalingLazyColumnDefaults.belowTimeText().create()
    val scalingLazyListState = columnState.state
    val text by remember { derivedStateOf { explainPositionIndicator(scalingLazyListState) } }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        timeText = {
            TimeText(
                startCurvedContent = {
                    curvedText(text)
                }
            )
        },
        positionIndicator = { PositionIndicator(scalingLazyListState) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            columnState = columnState
        ) {
            val items = listOf(10, 10, 10, 10, 100, 10, 10, 10, 10, 100, 10, 10, 10, 10)

            itemsIndexed(items) { index, height ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height.dp)
                        .background(if (index % 2 == 0) Color.DarkGray else Color.Black)
                )
            }
        }
    }
}

val format = DecimalFormat.getPercentInstance()

fun explainPositionIndicator(state: ScalingLazyListState): String {
    if (state.layoutInfo.visibleItemsInfo.isEmpty()) {
        return ""
    }

    val firstItem = state.layoutInfo.visibleItemsInfo.first().index.toFloat()
    val lastItem = state.layoutInfo.visibleItemsInfo.last().index.toFloat()
    val total = state.layoutInfo.totalItemsCount.toFloat()

    val result = (lastItem - firstItem) / total
    return format.format(result)
}

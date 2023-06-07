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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.material.scrollAway
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberColumnState
import kotlinx.coroutines.launch

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
    val offset: Int = 0
)

@Composable
fun WearApp() {
    var settings by rememberSaveable(stateSaver = Settings.Saver) {
        mutableStateOf(Settings())
    }

    val coroutineScope = rememberCoroutineScope()

    key(settings) {
        val initialOffset = settings.initialOffset
        val itemHeight = settings.itemHeight
        val autoCentering = settings.autoCentering
        val anchorType = settings.anchorType

        val columnState = rememberColumnState(
            ScalingLazyColumnDefaults.scalingLazyColumnDefaults(
                initialCenterIndex = initialOffset.index,
                initialCenterOffset = initialOffset.offset,
                autoCentering = autoCentering,
                anchorType = anchorType
            )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            timeText = {
                TimeText(
                    modifier = Modifier
                        .scrollAway(
                            columnState.state,
                            columnState.initialScrollPosition.index,
                            columnState.initialScrollPosition.offsetPx.dp
                        ),
                    startCurvedContent = {
                        curvedText("${columnState.state.centerItemIndex}/${columnState.state.centerItemScrollOffset}")
                    }
                )
            }
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                columnState = columnState
            ) {
                item {
                    com.google.android.horologist.compose.material.Chip("Scroll to end", onClick = {
                        coroutineScope.launch {
                            columnState.state.scrollToItem(999, 0)
                        }
                    })
                }
                item {
                    val text = "Initial Offset: ${initialOffset.index} / ${initialOffset.offset}"
                    FixedHeightChip(text, itemHeight, onClick = {
                        settings = settings.copy(
                            initialOffsetsMode = (settings.initialOffsetsMode + 1) % Settings.initialOffsets.size
                        )
                    })
                }
                item {
                    val text =
                        "Auto Centering: ${Settings.autoCenterings[settings.autoCenteringMode].first}"
                    FixedHeightChip(text, itemHeight, onClick = {
                        settings = settings.copy(
                            autoCenteringMode = (settings.autoCenteringMode + 1) % Settings.autoCenterings.size
                        )
                    })
                }
                val items = listOf("1", "2", "miss a few", "99", "100")
                items(items) {
                    FixedHeightChip(it, itemHeight, onClick = {
                    })
                }
                item {
                    val text = "Anchor Type: ${Settings.anchorTypes[settings.anchorTypeMode].first}"
                    FixedHeightChip(text, itemHeight, onClick = {
                        settings = settings.copy(
                            anchorTypeMode = (settings.anchorTypeMode + 1) % Settings.anchorTypes.size
                        )
                    })
                }
                item {
                    val text = "Item Height: ${settings.itemHeight}"
                    FixedHeightChip(text, itemHeight, onClick = {
                        settings = settings.copy(
                            itemHeightMode = (settings.itemHeightMode + 1) % Settings.itemHeights.size
                        )
                    })
                }
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    Color.Red,
                    Offset(0f, size.height / 2f),
                    Offset(size.width, size.height / 2f)
                )
            }
        }
    }
}

data class Settings(
    val initialOffsetsMode: Int = 0,
    val autoCenteringMode: Int = 1,
    val itemHeightMode: Int = 0,
    val anchorTypeMode: Int = 0
) {
    val initialOffset: Offsets = initialOffsets[initialOffsetsMode]
    val itemHeight: Int = itemHeights[itemHeightMode]
    val autoCentering: AutoCenteringParams? = autoCenterings[autoCenteringMode].second
    val anchorType: ScalingLazyListAnchorType = anchorTypes[anchorTypeMode].second

    companion object {
        val initialOffsets = listOf(
            Offsets(0, 0),
            Offsets(1, 0),
            Offsets(1, -20),
            Offsets(1, 20),
            Offsets(2, 0)
        )

        val autoCenterings = listOf(
            Pair("null", null),
            Pair("0/0", AutoCenteringParams(0, 0)),
            Pair("1/0", AutoCenteringParams(1, 0)),
            Pair("2/0", AutoCenteringParams(2, 0)),
            Pair("3/0", AutoCenteringParams(3, 0))
        )

        val itemHeights = listOf(40, 80, 120)

        val anchorTypes = listOf(
            Pair("Center", ScalingLazyListAnchorType.ItemCenter),
            Pair("Start", ScalingLazyListAnchorType.ItemStart)
        )

        val Saver = Saver<Settings, List<Int>>(
            save = {
                listOf(
                    it.initialOffsetsMode,
                    it.autoCenteringMode,
                    it.itemHeightMode,
                    it.anchorTypeMode
                )
            },
            restore = {
                Settings(it[0], it[1], it[2], it[3])
            }
        )
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
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            label = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.caption3
                )
            }
        )
    }
}

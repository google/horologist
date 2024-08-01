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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.rotary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.sample.R
import com.google.android.horologist.sample.Screen
import kotlin.random.Random

@Composable
fun RotaryMenuScreen(
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit,
    columnState: ScalingLazyColumnState,
) {
    SectionedList(
        columnState = columnState,
        modifier = modifier.fillMaxSize(),
    ) {
        section(
            listOf(
                Pair(
                    R.string.rotarymenu_scroll_list,
                    Screen.RotaryScrollScreen.route,
                ),
                Pair(
                    R.string.rotarymenu_scroll_list_reversed,
                    Screen.RotaryScrollReversedScreen.route,
                ),
                Pair(
                    R.string.rotarymenu_scroll_with_fling,
                    Screen.RotaryScrollWithFlingScreen.route,
                ),
            ),
        ) {
            header {
                Title(
                    stringResource(R.string.rotarymenu_scroll),
                    Modifier.padding(vertical = 8.dp),
                )
            }

            loaded { item ->
                Chip(
                    label = {
                        Text(text = stringResource(item.first))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(item.second) },
                    colors = ChipDefaults.primaryChipColors(),
                )
            }
        }

        section(
            listOf(
                Pair(
                    R.string.rotarymenu_snap_list,
                    Screen.RotarySnapListScreen.route,
                ),
            ),
        ) {
            header {
                Title(
                    text = stringResource(R.string.rotarymenu_snap),
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            loaded { item ->
                Chip(
                    label = {
                        Text(text = stringResource(item.first))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navigateToRoute(item.second) },
                    colors = ChipDefaults.primaryChipColors(),
                )
            }
        }
    }
}

@Composable
fun RotaryScrollScreen(
    reverseDirection: Boolean = false,
) {
    val columnState =
        rememberColumnState(ScalingLazyColumnDefaults.responsive(reverseLayout = reverseDirection))
    Scaffold(timeText = {
        if (!reverseDirection) {
            TimeText(modifier = Modifier.scrollAway(columnState))
        }
    }) {
        ScalingLazyColumn(
            columnState = columnState,
        ) {
            ChipsList {}
        }
    }
}

@Composable
fun RotaryScrollWithFlingOrSnapScreen(
    rotaryMode: ScalingLazyColumnState.RotaryMode,
) {
    var showList by remember { mutableStateOf(false) }

    var hapticsEnabled by remember { mutableStateOf(true) }
    var itemTypeIndex by remember { mutableStateOf(0) }

    val randomHeights: List<Int> = remember { (0..300).map { Random.nextInt(1, 10) } }
    val tenSmallOneBig: List<Int> = remember { (0..4).map { 1 }.plus(20).plus((0..4).map { 1 }) }
    if (showList) {
        val columnState = rememberColumnState(
            ScalingLazyColumnDefaults.responsive(
                rotaryMode = rotaryMode,
                hapticsEnabled = hapticsEnabled,
            ),
        )

        Scaffold(timeText = { TimeText(modifier = Modifier.scrollAway(columnState)) }) {
            ScalingLazyColumn(
                columnState = columnState,
            ) {
                when (itemTypeIndex) {
                    0 -> ChipsList { showList = false }
                    1 -> CardsList(2) { showList = false }
                    2 -> CardsList(10) { showList = false }
                    3 -> CardsList(10, randomHeights = randomHeights) { showList = false }
                    4 -> CardsList(10, itemCount = 10, randomHeights = tenSmallOneBig) {
                        showList = false
                    }

                    else -> {}
                }
            }
        }
    } else {
        val columnState = rememberColumnState(
            ScalingLazyColumnDefaults.responsive(
                rotaryMode = rotaryMode,
                hapticsEnabled = hapticsEnabled,
            ),
        )
        Scaffold(timeText = { TimeText(modifier = Modifier.scrollAway(columnState)) }) {
            ScrollPreferences(
                columnState = columnState,
                itemTypeIndex = itemTypeIndex,
                hapticsEnabled = hapticsEnabled,
                onShowListClicked = { showList = true },
                onItemTypeIndexChanged = { itemTypeIndex = it },
                onHapticsToggled = { hapticsEnabled = !hapticsEnabled },
            )
        }
    }
}

@Composable
private fun ScrollPreferences(
    columnState: ScalingLazyColumnState,
    itemTypeIndex: Int,
    hapticsEnabled: Boolean,
    onShowListClicked: () -> Unit,
    onItemTypeIndexChanged: (Int) -> Unit,
    onHapticsToggled: () -> Unit,
) {
    val itemTypes = stringArrayResource(R.array.rotarymenu_item_sizes).toList()

    ScalingLazyColumn(
        columnState = columnState,
    ) {
        item {
            Text(
                text = stringResource(R.string.rotarymenu_chips_size),
                textAlign = TextAlign.Center,
            )
        }
        item {
            Chip(
                onClick = {
                    onItemTypeIndexChanged((itemTypeIndex + 1) % itemTypes.size)
                },
                colors = ChipDefaults.primaryChipColors(),
                border = ChipDefaults.chipBorder(),
                content = {
                    Text(
                        text = itemTypes[itemTypeIndex],
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
        item {
            Chip(
                onClick = {
                    onHapticsToggled()
                },
                colors = ChipDefaults.primaryChipColors(),
                border = ChipDefaults.chipBorder(),
                content = {
                    Text(
                        text = stringResource(
                            if (hapticsEnabled) {
                                R.string.rotarymenu_haptics_on
                            } else {
                                R.string.rotarymenu_haptics_off
                            },
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
        item {
            CompactChip(
                onClick = { onShowListClicked() },
                label = {
                    Text(
                        text = stringResource(R.string.rotarymenu_show_list),
                        textAlign = TextAlign.Center,
                    )
                },
            )
        }
    }
}

private fun ScalingLazyListScope.CardsList(
    maxLines: Int = 10,
    itemCount: Int = 300,
    randomHeights: List<Int>? = null,
    onItemClicked: () -> Unit,
) {
    val colors = listOf(Color.Green, Color.Yellow, Color.Cyan, Color.Magenta)

    items(itemCount) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onItemClicked,
        ) {
            Column {
                Row {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(color = colors[it % colors.size]),
                    )
                    Text(text = "#$it")
                }
                Text(
                    maxLines = randomHeights?.let { height -> height[it] } ?: maxLines,
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                        "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
                        "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                        "ex ea commodo consequat. Lorem ipsum dolor sit amet, consectetur " +
                        "adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore " +
                        "magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                        "ullamco laboris nisi ut aliquip ex ea commodo consequat",
                )
            }
        }
    }
}

internal fun ScalingLazyListScope.ChipsList(
    onItemClicked: () -> Unit,
) {
    val colors = listOf(Color.Green, Color.Yellow, Color.Cyan, Color.Magenta)
    items(300) {
        Chip(
            modifier = Modifier.fillMaxWidth(),
            onClick = onItemClicked,
            label = { Text("List item $it") },
            colors = ChipDefaults.secondaryChipColors(),
            icon = {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(color = colors[it % 4]),
                )
            },
        )
    }
}

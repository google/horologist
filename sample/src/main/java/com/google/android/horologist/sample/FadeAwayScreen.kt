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

package com.google.android.horologist.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.compose.layout.fadeAway

@Composable
fun FadeAwayScreenLazyColumn() {
    val scrollState = rememberLazyListState()

    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.fadeAway(scrollState))
        },
        positionIndicator = {
            PositionIndicator(lazyListState = scrollState)
        }
    ) {
        LazyColumn(state = scrollState) {
            items(3) { i ->
                val modifier = Modifier.fillParentMaxHeight(0.5f)
                ExampleCard(modifier = modifier, i = i)
            }
        }
    }
}

@Composable
fun FadeAwayScreenSLC() {
    val scrollState = rememberScalingLazyListState()

    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.fadeAway(scrollState))
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = scrollState)
        }
    ) {
        ScalingLazyColumn(state = scrollState) {
            items(3) { i ->
                ExampleCard(Modifier.fillParentMaxHeight(0.5f), i)
            }
        }
    }
}

@Composable
fun FadeAwayScreenColumn() {
    val scrollState = rememberScrollState()

    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.fadeAway(scrollState))
        },
        positionIndicator = {
            PositionIndicator(scrollState = scrollState)
        }
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            val modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp / 2)
            repeat(3) { i ->
                ExampleCard(modifier, i)
            }
        }
    }
}

@Composable
private fun ExampleCard(modifier: Modifier, i: Int) {
    Card(
        modifier = modifier,
        onClick = { }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Card $i")
        }
    }
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Preview(
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun FadeAwayScreenPreview() {
    FadeAwayScreenLazyColumn()
}
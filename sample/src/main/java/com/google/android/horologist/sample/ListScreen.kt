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

import androidx.annotation.DimenRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.sample.theme.WearAppTheme
import com.google.android.horologist.compose.layout.paddingValues
import com.google.android.horologist.compose.layout.topPaddingForTopmostRect
import com.google.android.horologist.compose.layout.TopPaddingStrategy

private val ITEM_LIST = listOf(
    MenuItem(Icons.Default.Cloud, "Connectivity"),
    MenuItem(Icons.Default.BrightnessHigh, "Display"),
    MenuItem(Icons.Default.Gesture, "Gestures"),
    MenuItem(Icons.Default.Apps, "App & notifications"),
    MenuItem(Icons.Default.SurroundSound, "Sound & vibration"),
)

data class MenuItem(
    val imageVector: ImageVector,
    val label: String
)

/**
 * List UI Demo, which implements Settings app demo.
 * Refer the Figma design resource: https://developer.android.com/training/wearables/design/download
 */
@Composable
fun ListScreen() {
    val scrollState = rememberScalingLazyListState()
    Scaffold(
        positionIndicator = { PositionIndicator(scrollState) },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        ListScreen(scrollState)
    }
}

@Composable
fun ListScreen(
    scrollState: ScalingLazyListState,
) {
    val contentPadding = paddingValues(
        startPercentId = R.dimen.list_horizontal_padding_percent,
        endPercentId = R.dimen.list_horizontal_padding_percent,
        bottomPercentId = R.dimen.list_bottom_padding_percent,
    )
    var topPaddingStrategy by remember { mutableStateOf(TopPaddingStrategy.FixedPadding) }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = contentPadding,
        autoCentering = false,
    ) {
        item {
            ListTextHeader(
                "Settings", R.dimen.list_top_padding_percent, topPaddingStrategy
            )
        }
        item {
            ToggleChipItem(
                checked = (topPaddingStrategy == TopPaddingStrategy.FixedPadding),
                onCheckedChange = {
                    topPaddingStrategy = if (topPaddingStrategy  == TopPaddingStrategy.FixedPadding)
                        TopPaddingStrategy.FitToTopPadding
                    else
                        TopPaddingStrategy.FixedPadding
                }
            )
        }
        items(
            items = ITEM_LIST,
            key = { it.label },
        ) { item ->
            ListChipItem(item)
        }
    }
}

@Composable
fun ListTextHeader(
    text: String,
    @DimenRes topMarginPercentId: Int = R.dimen.list_top_padding_percent,
    paddingStrategy: TopPaddingStrategy = TopPaddingStrategy.FixedPadding
) {
    Text(
        text,
        modifier = Modifier
            .topPaddingForTopmostRect(topMarginPercentId, paddingStrategy)
            .height(48.dp)
            .wrapContentHeight(),
        color = MaterialTheme.colors.onBackground,
        textAlign = TextAlign.Center,
        maxLines = 2,
        style = MaterialTheme.typography.button
    )
}

@Composable
fun ToggleChipItem(checked: Boolean, onCheckedChange: (Boolean) -> Unit,) {
    ToggleChip(
        label = { Text("Fixed top margin") },
        checked = checked,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
fun ListChipItem(item: MenuItem) {
    Chip(
        label = { Text(item.label) },
        onClick = {},
        modifier = Modifier,
        icon = {
            Icon(
                imageVector = item.imageVector,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.White,
            )
        },
        colors = ChipDefaults.primaryChipColors(
            backgroundColor = MaterialTheme.colors.surface,
        )
    )
}

@Preview(name = "Small round watch", device = Devices.WEAR_OS_SMALL_ROUND)
@Preview(name = "Large round watch", device = Devices.WEAR_OS_LARGE_ROUND)
@Preview(name = "Square watch", device = Devices.WEAR_OS_SQUARE)
@Preview(name = "Rectangle watch", device = Devices.WEAR_OS_RECT)
@Composable
fun ListPreview() {
    WearAppTheme {
        ListScreen()
    }
}

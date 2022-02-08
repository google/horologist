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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.WearMaterialTheme
import com.google.android.horologist.compose.layout.rectangleDimensions
import com.google.android.horologist.compose.layout.roundDimensions
import com.google.android.horologist.sample.theme.MaterialAlmondDark

@Composable
fun MarginsDemoScreen() {
    val paddingValues = WearMaterialTheme.dimensions.minMargins.paddingValues()
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .background(MaterialAlmondDark)
    ) {
        Text(
            modifier = Modifier.fillMaxSize().wrapContentSize(),
            text = paddingValues.toString()
        )
    }
}

@Preview(
    name = "Large round",
    device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true,
    backgroundColor = 0xff000000, showBackground = true
)
@Preview(
    name = "Small round",
    device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true,
    backgroundColor = 0xff000000, showBackground = true
)
@Preview(
    name = "Square",
    device = Devices.WEAR_OS_SQUARE, showSystemUi = true,
    backgroundColor = 0xff000000, showBackground = true
)
@Preview(
    name = "Rectangle",
    device = Devices.WEAR_OS_RECT, showSystemUi = true,
    backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun MarginsDemoPreview() {
    WearMaterialTheme(
        dimensions = if (LocalConfiguration.current.isScreenRound)
            roundDimensions()
        else
            rectangleDimensions()
    ) {
        MarginsDemoScreen()
    }
}
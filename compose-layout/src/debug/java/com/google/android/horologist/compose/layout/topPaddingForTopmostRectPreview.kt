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

package com.google.android.horologist.compose.layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text

@Preview(
    name = "Small round (Fixed)", device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true, backgroundColor = 0xff000000, showBackground = true
)
@Preview(
    name = "Large round (Fixed)", device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true, backgroundColor = 0xff000000, showBackground = true
)
@Preview(
    name = "Square (Fixed)", device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true, backgroundColor = 0xff000000, showBackground = true
)
@Preview(
    name = "Rectangle (Fixed)", device = Devices.WEAR_OS_RECT,
    showSystemUi = true, backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun TopPaddingForTopmostRectListPreview() {
    val topPadding = if (LocalConfiguration.current.isScreenRound) 0.0938f else 0.052f
    val paddingStrategy = TopPaddingStrategy.FixedPadding
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Hello World!",
            modifier = Modifier
                .border(1.dp, Color.Green)
                .topPaddingForTopmostRect(topPadding, paddingStrategy)
                .border(1.dp, Color.Blue)
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .wrapContentHeight()
        )
    }
}

@Preview(
    name = "Small round (Fit)", device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true, backgroundColor = 0xff000000, showBackground = true
)
@Preview(
    name = "Large round (Fit)", device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true, backgroundColor = 0xff000000, showBackground = true
)
@Composable
fun TopPaddingForTopmostRectFitPreview() {
    val topPadding = if (LocalConfiguration.current.isScreenRound) 0.0938f else 0.052f
    val paddingStrategy = TopPaddingStrategy.FitToTopPadding
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Hello World!",
            modifier = Modifier
                .border(1.dp, Color.Green)
                .topPaddingForTopmostRect(topPadding, paddingStrategy)
                .border(1.dp, Color.Blue)
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .wrapContentHeight()
        )
    }
}

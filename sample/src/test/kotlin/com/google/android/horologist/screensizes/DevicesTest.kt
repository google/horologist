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

package com.google.android.horologist.screensizes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.tools.Device

class DevicesTest(device: Device) : ScreenSizeTest(device = device, showTimeText = true) {

    @Composable
    override fun Content() {
        Box(modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val width = 192f * density
                val offset = Offset(this.size.width / 2, this.size.height / 2)
                drawCircle(
                    Color.DarkGray,
                    radius = width / 2f,
                    center = offset,
                    style = Stroke()
                )
            }) {
            ScreenSizeInfo(device)
        }
    }
}

@Composable
fun ScreenSizeInfo(device: Device) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val context = LocalContext.current
        val displayMetrics = context.resources.displayMetrics

        Text(
            device.name,
            style = MaterialTheme.typography.title3,
            modifier = Modifier.fillMaxWidth(0.65f),
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        Text(
            "isScreenRound: ${configuration.isScreenRound}",
            style = MaterialTheme.typography.body2
        )
        Text(
            "screenSizePx: ${displayMetrics.widthPixels}x${displayMetrics.heightPixels}",
            style = MaterialTheme.typography.body2
        )
        Text(
            "screenSizeDp: ${configuration.screenWidthDp}x${configuration.screenHeightDp}",
            style = MaterialTheme.typography.body2
        )
        Text("density: ${density.density}", style = MaterialTheme.typography.body2)
        Text("fontScale: ${density.fontScale}", style = MaterialTheme.typography.body2)
    }
}

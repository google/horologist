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

package com.google.android.horologist.audioui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VolumeScreenUxGuide() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val _18dp = 18.dp.toPx()
        val _25dp = 25.dp.toPx()
        val height = this.size.height
        val width = this.size.width

        // 18dp padding on sides
        drawLine(Color.Red, Offset(_18dp, 0f), Offset(_18dp, height))
        drawLine(Color.Red, Offset(width - _18dp, 0f), Offset(width - _18dp, height))

        // 12 + 13 padding to button
        drawLine(Color.Red, Offset(0f, _25dp), Offset(width, _25dp))
        drawLine(Color.Red, Offset(0f, height - _25dp), Offset(width, height - _25dp))
    }
}

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

package com.google.android.horologist.compose.tools

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

/**
 * A repeated preview to allow seeing the same component at multiple widths.
 */
@Composable
public fun WidthConstrainedBox(
    widths: List<Dp> = listOf(192.dp, 227.dp),
    comfortableHeight: Dp = 101.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(widths.maxOrNull()!!)
            .height(widths.size * comfortableHeight)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            widths.forEach { width ->
                Text(width.toString(), style = MaterialTheme.typography.caption3)
                Column(
                    modifier = Modifier
                        .width(width)
                        .padding(vertical = 5.dp)
                        .border(1.dp, Color.Yellow)
                        .padding(vertical = 5.dp)
                ) {
                    content()
                }
            }
        }
    }
}

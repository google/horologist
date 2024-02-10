/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.materialcomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.Card

@Preview
@Composable
fun SampleCardScreenPreview() {
    val state = rememberColumnState()
    SampleCardScreen(columnState = state)
}

@Composable
internal fun SampleCardScreen(
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState,
) {
    ScalingLazyColumn(
        modifier = modifier,
        columnState = columnState,
    ) {
        item {
            Card(
                onClick = { println("Click") },
                onLongClick = { println("LongClick") },
                onDoubleClick = { println("DoubleClick") },
                modifier = Modifier.background(Color.Cyan),
            ) {
                Text("Hello, Card")
            }
        }
        item {
            Card(
                onClick = { println("Click") },
                onLongClick = { println("LongClick") },
                onDoubleClick = { println("DoubleClick") },
                modifier = Modifier.background(Color.Cyan),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Hello, Card")
            }
        }
        item {
            Card(
                onClick = { println("Click") },
                onLongClick = { println("LongClick") },
                onDoubleClick = { println("DoubleClick") },
                backgroundPainter = painterResource(id = android.R.drawable.ic_dialog_alert),
            ) {
                Text("Hello, Card")
            }
        }
        item {
            Card(
                onClick = { println("Click") },
                onLongClick = { println("LongClick") },
                onDoubleClick = { println("DoubleClick") },
                contentPadding = PaddingValues(24.dp),
            ) {
                Text("Hello, Card")
            }
        }
        item {
            Card(
                onClick = { println("Click") },
                onLongClick = { println("LongClick") },
                onDoubleClick = { println("DoubleClick") },
                contentColor = MaterialTheme.colors.primaryVariant,
            ) {
                Text("Hello, Card")
            }
        }
        item {
            Card(
                onClick = { println("Click") },
                onLongClick = { println("LongClick") },
                onDoubleClick = { println("DoubleClick") },
                enabled = false,
            ) {
                Text("Hello, Card")
            }
        }
    }
}

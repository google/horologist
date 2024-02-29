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

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Card

@WearPreviewDevices
@Composable
fun SampleCardScreenPreview() {
    SampleCardScreen()
}

@Composable
internal fun SampleCardScreen(
    modifier: Modifier = Modifier,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            modifier = modifier,
            columnState = columnState,
        ) {
            item {
                Card(
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    enabled = false,
                ) {
                    Text("Hello\nCard")
                }
            }
            item {
                Card(
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    modifier = Modifier.background(Color.Cyan),
                ) {
                    Text("Hello\nCard")
                }
            }
            item {
                Card(
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    modifier = Modifier.background(Color.Cyan),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text("Hello\nCard")
                }
            }
            item {
                Card(
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    backgroundPainter = painterResource(id = R.drawable.ic_dialog_alert),
                ) {
                    Text("Hello\nCard")
                }
            }
            item {
                Card(
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    contentPadding = PaddingValues(24.dp),
                ) {
                    Text("Hello\nCard")
                }
            }
            item {
                Card(
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    contentColor = MaterialTheme.colors.primaryVariant,
                ) {
                    Text("Hello\nCard")
                }
            }
            item {
                Card(
                    onClick = { println("Click") },
                    onLongClick = { println("LongClick") },
                    enabled = false,
                ) {
                    Text("Hello\nCard")
                }
            }
        }
    }
}

/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.m3

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.ColumnItemType.Companion.EdgeButtonPadding
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun M3TLCButtonAndEdgeButton() {
    // Disable other screen scaffold
    com.google.android.horologist.compose.layout.ScreenScaffold(
        timeText = {},
        positionIndicator = {},
    ) {
        AppScaffold {
            val columnState = rememberTransformingLazyColumnState()
            ScreenScaffold(
                scrollState = columnState,
                contentPadding = rememberResponsiveColumnPadding(
                    first = ColumnItemType.IconButton,
                    last = EdgeButtonPadding,
                ),
                edgeButton = {
                    EdgeButton(onClick = { }, buttonSize = EdgeButtonSize.Large) {
                        Text("To top")
                    }
                },
            ) { contentPadding ->
                val transformationSpec = rememberTransformationSpec()

                TransformingLazyColumn(
                    state = columnState,
                    contentPadding = contentPadding,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("TransformingLazyColumn"),
                ) {
                    item {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowUpward,
                                contentDescription = null,
                            )
                        }
                    }
                    items(3) {
                        TitleCard(
                            onClick = { /* Do something */ },
                            title = { Text("Title card") },
                            time = { Text("now") },
                            modifier = Modifier.transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) { Text("Card content") }
                    }
                }
            }
        }
    }
}

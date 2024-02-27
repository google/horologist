/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.spec

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState


@Composable
fun SampleMenu(
    columnState: ScalingLazyColumnState,
    borders: (DrawScope.() -> Unit)? = null,
    atBottom: Boolean = false,
    items: ScalingLazyListScope.() -> Unit,
) {
    SampleTheme {
        AppScaffold(timeText = {
            TimeText(timeSource = object : TimeSource {
                override val currentTime: String
                    @Composable get() = "9:30"
            })
        }) {
            ScreenScaffold(scrollState = columnState) {
                ScalingLazyColumn(columnState = columnState) {
                    items()
                }
            }
        }

        if (borders != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                borders()
            }
        }
    }

    if (atBottom) {
        LaunchedEffect(Unit) {
            columnState.state.scrollToItem(100, 0)
        }
    }
}

@Composable
fun SampleMenu(
    columnState: ScalingLazyColumnState,
    before: @Composable() (() -> Unit)? = null,
    after: @Composable() (() -> Unit)? = null,
    borders: (DrawScope.() -> Unit)? = null,
    atBottom: Boolean = false
) {
    SampleMenu(borders = borders, columnState = columnState, atBottom = atBottom) {
        chipMenu(before, after)
    }
}

fun ScalingLazyListScope.chipMenu(
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null
) {
    if (before != null) {
        item {
            before()
        }
    }
    item {
        ConnectivityChip()
    }
    item {
        AppsChip()
    }
    item {
        SoundChip()
    }
    item {
        AccountsChip()
    }
    item {
        SystemChip()
    }
    if (after != null) {
        item {
            after()
        }
    }
}

@Composable
private fun SampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Color.DarkGray, onPrimary = Color.White
        )
    ) {
        content()
    }
}

fun DrawScope.top(fl: Float) {
    drawRect(
        color = MagentaIsh,
        alpha = 0.4f,
        topLeft = Offset.Zero,
        size = Size(this.size.width, this.size.height * fl)
    )
}

fun DrawScope.bottom(fl: Float) {
    val height = this.size.height * fl
    drawRect(
        color = MagentaIsh,
        alpha = 0.4f,
        topLeft = Offset(x = 0f, y = this.size.height - height),
        size = Size(this.size.width, height)
    )
}

fun DrawScope.side(fl: Float, offset: Float = 0f, color: Color = MagentaIsh) {
    val width = this.size.width * fl
    drawRect(
        color = color,
        alpha = 0.4f,
        topLeft = Offset(x = 0f + offset * this.size.width, y = 0f),
        size = Size(width, this.size.height)
    )
    drawRect(
        color = color,
        alpha = 0.4f,
        topLeft = Offset(x = this.size.width - width - offset * this.size.width, y = 0f),
        size = Size(width, this.size.height)
    )
}

val MagentaIsh = Color.Magenta

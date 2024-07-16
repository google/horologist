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

package com.google.android.horologist.audit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState

@Composable
fun PositionIndicatorAudit(route: AuditNavigation.PositionIndicator.Audit) {
    val (initial, itemCount) = when (route.config) {
        AuditNavigation.PositionIndicator.Config.TopLong -> {
            Pair(0, 100)
        }

        AuditNavigation.PositionIndicator.Config.TopShort -> {
            Pair(0, 10)
        }

        AuditNavigation.PositionIndicator.Config.BottomLong -> {
            Pair(100, 100)
        }

        AuditNavigation.PositionIndicator.Config.BottomShort -> {
            Pair(100, 10)
        }

        AuditNavigation.PositionIndicator.Config.MiddleShort -> {
            Pair(5, 10)
        }

        AuditNavigation.PositionIndicator.Config.MiddleLong -> {
            Pair(50, 100)
        }
    }

    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Chip,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState) {
            items(itemCount) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            Color.DarkGray.copy(alpha = 0.5f),
                        ),
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        columnState.state.scrollToItem(initial + 1)
        columnState.state.scrollToItem(initial)
    }
}

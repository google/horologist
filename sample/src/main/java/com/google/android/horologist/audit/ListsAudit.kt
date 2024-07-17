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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LooksTwo
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.WhereToVote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ResponsiveButton
import com.google.android.horologist.compose.material.responsiveButtonWidth

@Composable
fun ListsAudit(route: AuditNavigation.Lists.Audit) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = when (route.config) {
                AuditNavigation.Lists.Config.OneBottomChip -> ItemType.Chip
                AuditNavigation.Lists.Config.NoBottomButton -> ItemType.BodyText
                AuditNavigation.Lists.Config.OneBottomButton -> ItemType.SingleButton
                AuditNavigation.Lists.Config.TwoBottomRound -> ItemType.MultiButton
            },
        ),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            items(10) {
                Chip("Chip $it", onClick = {})
            }
            item {
                when (route.config) {
                    AuditNavigation.Lists.Config.NoBottomButton -> {
                        Text(
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
                            modifier = Modifier.listTextPadding(),
                            maxLines = 3,
                        )
                    }

                    AuditNavigation.Lists.Config.OneBottomChip -> {
                        Chip("Final Chip", onClick = {}, modifier = Modifier.padding(top = 12.dp))
                    }

                    AuditNavigation.Lists.Config.TwoBottomRound -> {
                        val (buttonSpacedBy, buttonWidth) = responsiveButtonWidth(2)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                buttonSpacedBy,
                                Alignment.CenterHorizontally
                            ),
                        ) {
                            ResponsiveButton(
                                onClick = {},
                                icon = Icons.Default.PlusOne,
                                contentDescription = "",
                                buttonWidth = buttonWidth,
                            )
                            ResponsiveButton(
                                onClick = {},
                                icon = Icons.Default.LooksTwo,
                                contentDescription = "",
                                buttonWidth = buttonWidth,
                            )
                        }
                    }

                    AuditNavigation.Lists.Config.OneBottomButton -> {
                        Button(
                            onClick = {},
                            imageVector = Icons.Default.WhereToVote,
                            contentDescription = "",
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        columnState.state.scrollToItem(100)
    }
}

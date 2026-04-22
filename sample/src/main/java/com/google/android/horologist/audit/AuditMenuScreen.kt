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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.SecondaryTitle
import com.google.android.horologist.compose.material.Title

@Composable
fun AuditMenuScreen(
    columnState: ScalingLazyColumnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    ),
    onClick: (AuditNavigation) -> Unit,
) {
    val screens = remember { AuditNavigation.screens.groupBy { it.parent } }

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                Title("Large Screens")
            }
            item {
                Text(
                    "DP: " + LocalConfiguration.current.screenWidthDp + " FontScale: " + LocalDensity.current.fontScale,
                    modifier = Modifier.listTextPadding(),
                    style = MaterialTheme.typography.caption3,
                )
            }
            screens.forEach { (section, auditNavigations) ->
                if (section != null) {
                    item {
                        SecondaryTitle(section.title)
                    }
                }
                items(auditNavigations) {
                    Chip(it.title, onClick = { onClick(it) })
                }
            }
        }
    }
}

@Composable
@WearPreviewSmallRound
@WearPreviewLargeRound
fun AuditMenuScreenPreview() {
    AppScaffold {
        AuditMenuScreen { }
    }
}

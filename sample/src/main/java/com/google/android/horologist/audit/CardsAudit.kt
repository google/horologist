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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.audit.AuditNavigation.Cards.Config
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.sample.R

@Composable
fun CardsAudit(route: AuditNavigation.Cards.Audit) {

    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            if (route.config == Config.BackgroundImage) {
                item {
                    BackgroundImageCard()
                }
            }
        }
    }
}

@Composable
fun BackgroundImageCard() {
    TitleCard(
        onClick = { /* Do something */ },
        title = { Text("TitleCard With an ImageBackground") },
        backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
            backgroundImagePainter = painterResource(id = R.drawable.backgroundimage)
        ),
        contentColor = MaterialTheme.colors.onSurface,
        titleColor = MaterialTheme.colors.onSurface,
    ) {
        // Apply 24.dp padding in bottom for TitleCard with an ImageBackground.
        // Already 12.dp padding exists. Ref - [CardDefaults.ContentPadding]
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp),
        ) {
            Text("Text coloured to stand out on the image")
        }
    }
}

@Composable
@WearPreviewSmallRound
@WearPreviewLargeRound
fun CardsAuditPreview() {
    AppScaffold {
        CardsAudit(AuditNavigation.Cards.Audit(Config.BackgroundImage))
    }
}
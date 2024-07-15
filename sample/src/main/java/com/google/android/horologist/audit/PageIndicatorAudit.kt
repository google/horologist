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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.audit.AuditNavigation.Cards.Config
import com.google.android.horologist.audit.AuditNavigation.PageIndicator.Config.*
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.pager.PagerScreen

@Composable
fun PageIndicatorAudit(route: AuditNavigation.PageIndicator.Audit) {
    val pagerState = when (route.config) {
        TwoDots -> {
            rememberPagerState { 2 }
        }

        FourDots -> {
            rememberPagerState { 4 }
        }

        Left5Plus -> {
            rememberPagerState(initialPage = 0) { 100 }
        }

        Right5Plus -> {
            rememberPagerState(initialPage = 99) { 100 }
        }
    }

    PagerScreen(pagerState) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("This page intentionally left blank.\npage $it", textAlign = TextAlign.Center)
        }
    }
}

@Composable
@WearPreviewSmallRound
@WearPreviewLargeRound
fun PageIndicatorLeft5PlusAuditPreview() {
    AppScaffold {
        PageIndicatorAudit(AuditNavigation.PageIndicator.Audit(Left5Plus))
    }
}

@Composable
@WearPreviewSmallRound
@WearPreviewLargeRound
fun PageIndicatorRight5PlusAuditPreview() {
    AppScaffold {
        PageIndicatorAudit(AuditNavigation.PageIndicator.Audit(Right5Plus))
    }
}
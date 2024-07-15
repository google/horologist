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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ProgressIndicatorDefaults

@Composable
fun ProgressIndicatorAudit(route: AuditNavigation.ProgressIndicator.Audit) {
    when (route.config) {
        AuditNavigation.ProgressIndicator.Config.GapAtTop -> {

            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize().padding(1.dp),
                startAngle = 270f + (51f / 2f),
                endAngle = 270f - (51f / 2f),
                progress = 0.3f,
                strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth
            )
        }

        AuditNavigation.ProgressIndicator.Config.GapAtBottom -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize().padding(1.dp),
                startAngle = 90f + (51f / 2f),
                endAngle = 90f - (51f / 2f),
                progress = 0.3f,
                strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth
            )
        }

        AuditNavigation.ProgressIndicator.Config.WithoutGap -> {
            CircularProgressIndicator(
                0.40f,
                modifier = Modifier.fillMaxSize().padding(1.dp),
            )
        }
    }

}
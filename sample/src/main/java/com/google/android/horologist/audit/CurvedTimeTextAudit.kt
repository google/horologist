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
import androidx.wear.compose.material.curvedText
import com.google.android.horologist.audit.AuditNavigation.CurvedTimeText.Config
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.screensizes.FixedTimeSource

@Composable
fun CurvedTimeTextAudit(route: AuditNavigation.CurvedTimeText.Audit) {
    ScreenScaffold(
        timeText = {
            when (route.config) {
                Config.H12 -> ResponsiveTimeText(timeSource = FixedTimeSource.H12)
                Config.H24 -> ResponsiveTimeText(timeSource = FixedTimeSource.H24)
                Config.LongerTextString -> ResponsiveTimeText(timeSource = FixedTimeSource, startCurvedContent = {
                    this.curvedText("Network unavailable")
                })
            }
        }
    ) {

    }
}
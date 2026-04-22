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

import android.text.format.DateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.HairlineWidth
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.audit.AuditNavigation.CurvedTimeText.Config
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.screensizes.FixedTimeSource
import java.util.Calendar

@Composable
fun CurvedTimeTextAudit(route: AuditNavigation.CurvedTimeText.Audit) {
    ScreenScaffold(
        timeText = {
            when (route.config) {
                Config.H12 -> ResponsiveTimeText(timeSource = FixedTimeSource.H12)
                Config.H24 -> ResponsiveTimeText(timeSource = FixedTimeSource.H24)
                Config.Tall -> ResponsiveTimeText(
                    timeSource = object : TimeSource {
                        override val currentTime: String
                            @Composable get() = DateFormat.format(
                                "9⎥:⎥0",
                                Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, 21)
                                    set(Calendar.MINUTE, 30)
                                },
                            ).toString()
                    },
                )

                Config.LongerTextString -> ResponsiveTimeText(
                    timeSource = FixedTimeSource,
                    startCurvedContent = {
                        this.curvedText("Network unavailable")
                    },
                )
            }
        },
    ) {
        if (route.config == Config.Tall) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val fromTop = 2.dp.toPx()
                drawCircle(
                    style = Stroke(HairlineWidth),
                    color = Color.White,
                    radius = size.width / 2f - fromTop,
                )
                drawLine(Color.White, Offset(0f, fromTop), Offset(size.width, fromTop))
            }
        }
    }
}

@Composable
@WearPreviewSmallRound
@WearPreviewLargeRound
fun CurvedTimeTextAuditPreview() {
    AppScaffold {
        CurvedTimeTextAudit(AuditNavigation.CurvedTimeText.Audit(AuditNavigation.CurvedTimeText.Config.H24))
    }
}

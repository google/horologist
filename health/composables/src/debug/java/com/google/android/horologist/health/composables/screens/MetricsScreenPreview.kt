/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.health.composables.screens

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.theme.HR_HARD
import com.google.android.horologist.health.composables.theme.HR_MAXIMUM

@WearPreviewDevices
@Composable
fun MetricsScreenPreviewOneMetric() {
    MetricsScreen(
        firstMetric = MetricUiModel(
            text = "21:34",
            bottomRightText = "6"
        ),
        positionIndicator = {
            PositionIndicator(
                value = { 0.7f }
            )
        }
    )
}

@WearPreviewDevices
@Composable
fun MetricsScreenPreviewTwoMetrics() {
    MetricsScreen(
        firstMetric = MetricUiModel(
            text = "21:34",
            bottomRightText = "6"
        ),
        secondMetric = MetricUiModel(
            text = "138",
            bottomRightText = "cal"
        ),
        positionIndicator = {
            PositionIndicator(
                value = { 0.7f }
            )
        }
    )
}

@WearPreviewDevices
@Composable
fun MetricsScreenPreviewThreeMetrics() {
    MetricsScreen(
        firstMetric = MetricUiModel(
            text = "164",
            bottomRightText = "Vigorous",
            color = HR_HARD
        ),
        secondMetric = MetricUiModel(
            text = "2.7",
            bottomRightText = "mi"
        ),
        thirdMetric = MetricUiModel(
            text = "21:34",
            bottomRightText = "6"
        ),
        positionIndicator = {
            PositionIndicator(
                value = { 0.7f }
            )
        }
    )
}

@WearPreviewDevices
@Composable
fun MetricsScreenPreviewFourMetrics() {
    MetricsScreen(
        firstMetric = MetricUiModel(
            text = "198",
            bottomRightText = "Peak",
            color = HR_MAXIMUM
        ),
        secondMetric = MetricUiModel(
            text = "2.7",
            bottomRightText = "mi"
        ),
        thirdMetric = MetricUiModel(
            text = "8'51\"",
            bottomRightText = "pace"
        ),
        fourthMetric = MetricUiModel(
            text = "21:34",
            bottomRightText = "6"
        ),
        positionIndicator = {
            PositionIndicator(
                value = { 0.7f }
            )
        }
    )
}

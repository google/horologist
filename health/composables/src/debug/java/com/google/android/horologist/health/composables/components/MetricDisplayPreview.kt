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

package com.google.android.horologist.health.composables.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.theme.HR_HARD
import com.google.android.horologist.health.composables.theme.HR_LIGHT
import com.google.android.horologist.health.composables.theme.HR_MAXIMUM
import com.google.android.horologist.health.composables.theme.HR_MODERATE

@Preview
@Composable
fun MetricDisplayPreview() {
    MetricDisplay(
        metric = MetricUiModel(
            text = "139",
            topRightText = "Vigorous",
            bottomRightText = "bpm",
            color = HR_HARD
        )
    )
}

@Preview
@Composable
fun MetricDisplayPreviewTextOnly() {
    MetricDisplay(
        metric = MetricUiModel(
            text = "18:52",
            color = HR_LIGHT
        )
    )
}

@Preview
@Composable
fun MetricDisplayPreviewTopRightText() {
    MetricDisplay(
        metric = MetricUiModel(
            text = "8'32\"",
            topRightText = ":15",
            color = HR_MODERATE
        )
    )
}

@Preview
@Composable
fun MetricDisplayPreviewBottomRightText() {
    MetricDisplay(
        metric = MetricUiModel(
            text = "2.1",
            bottomRightText = "/3 mi",
            color = HR_MAXIMUM
        )
    )
}

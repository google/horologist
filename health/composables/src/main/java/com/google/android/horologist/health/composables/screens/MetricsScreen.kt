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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.health.composables.components.MetricDisplay
import com.google.android.horologist.health.composables.model.MetricUiModel

/**
 * A screen to display metrics, e.g. workout metrics.
 * It can display up to four metrics, and it's recommended that at least two metrics should be
 * displayed.
 */
@Composable
public fun MetricsScreen(
    firstMetric: MetricUiModel,
    modifier: Modifier = Modifier,
    secondMetric: MetricUiModel? = null,
    thirdMetric: MetricUiModel? = null,
    fourthMetric: MetricUiModel? = null,
    positionIndicator: @Composable (() -> Unit)? = null,
) {
    Box {
        positionIndicator?.invoke()

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(start = 40.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            MetricDisplay(metric = firstMetric)
            secondMetric?.let { MetricDisplay(metric = it) }
            thirdMetric?.let { MetricDisplay(metric = it) }
            fourthMetric?.let { MetricDisplay(metric = it) }
        }
    }
}

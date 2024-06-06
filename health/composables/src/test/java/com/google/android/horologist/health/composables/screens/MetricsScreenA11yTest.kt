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

import androidx.compose.ui.res.stringResource
import com.google.android.horologist.health.composables.R
import com.google.android.horologist.health.composables.model.MetricUiModel
import com.google.android.horologist.health.composables.theme.HR_MAXIMUM
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test
import org.robolectric.annotation.Config

class MetricsScreenA11yTest : WearLegacyA11yTest() {

    @Test
    fun metricsScreenTwoMetrics() {
        runScreenTest {
            MetricsScreen(
                firstMetric = MetricUiModel(
                    text = "198",
                    bottomRightText = stringResource(R.string.horologist_peak),
                    color = HR_MAXIMUM,
                ),
                secondMetric = MetricUiModel(
                    text = "2.7",
                    bottomRightText = stringResource(R.string.horologist_miles),
                ),
            )
        }
    }

    @Test
    @Config(qualifiers = "+ar-rXB-ldrtl")
    fun metricsScreenTwoMetrics_rtl() {
        runScreenTest {
                MetricsScreen(
                    firstMetric = MetricUiModel(
                        text = "198",
                        bottomRightText = stringResource(R.string.horologist_peak),
                        color = HR_MAXIMUM,
                    ),
                    secondMetric = MetricUiModel(
                        text = "2.7",
                        bottomRightText = stringResource(R.string.horologist_miles),
                    ),
                )
            }
    }
}

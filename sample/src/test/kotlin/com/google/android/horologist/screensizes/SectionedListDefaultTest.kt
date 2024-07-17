/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.screensizes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.tools.Device
import com.google.android.horologist.screenshots.FixedTimeSource
import com.google.android.horologist.sectionedlist.SectionedListMenuScreen

class SectionedListDefaultTest(device: Device) : WearLegacyScreenSizeTest(device = device, showTimeText = true) {

    @Composable
    override fun Content() {
        AppScaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            timeText = { ResponsiveTimeText(timeSource = FixedTimeSource) },
        ) {
            SectionedListMenuScreen(
                navigateToRoute = {},
                columnState = rememberResponsiveColumnState(
                    contentPadding = padding(
                        first = ItemType.Text,
                        last = ItemType.Chip,
                    ),
                ),
            )
        }
    }
}

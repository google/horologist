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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.composables

import androidx.compose.runtime.Composable
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.compose.tools.WearPreviewFontSizes
import java.time.LocalTime

@WearPreviewDevices
@WearPreviewFontSizes
@Composable
@OptIn(ExperimentalHorologistApi::class)
fun TimePicker12hPreview() {
    // Due to a limitation with ScalingLazyColumn,
    // previews only work in interactive mode.
    TimePickerWith12HourClock(
        time = LocalTime.of(10, 10, 0),
        onTimeConfirm = {}
    )
}

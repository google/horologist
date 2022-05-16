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

package com.google.android.horologist.composables

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.google.android.horologist.compose.testing.WearPreviewDevices
import com.google.android.horologist.compose.testing.WearPreviewFontSizes
import java.time.LocalTime

@WearPreviewDevices
@WearPreviewFontSizes
@Composable
@OptIn(ExperimentalHorologistComposablesApi::class)
fun TimePickerPreview() {
    // Due to a limitation with ScalingLazyColumn,
    // previews only work in interactive mode.
    TimePicker(
        buttonIcon = {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "check",
                modifier = Modifier
                    .size(24.dp)
                    .wrapContentSize(align = Alignment.Center),
            )
        },
        initial = LocalTime.of(10, 10, 0),
        onClick = { }
    )
}

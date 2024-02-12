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

import androidx.compose.runtime.Composable
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.compose.tools.Device
import org.junit.Test
import java.time.LocalDate

class DatePickerTest(device: Device) : ScreenSizeTest(device = device, showTimeText = false) {

    private val date: LocalDate = LocalDate.of(2022, 4, 25)

    @Composable
    override fun Content() {
        DatePicker(
            onDateConfirm = {},
            date = date,
        )
    }

    @Test
    fun fromDatePicker() = runTest {
        DatePicker(
            onDateConfirm = {},
            date = date,
            fromDate = date,
        )
    }

    @Test
    fun toDatePicker() = runTest {
        DatePicker(
            onDateConfirm = {},
            date = date,
            toDate = date,
        )
    }
}

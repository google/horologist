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
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun PickersAudit(route: AuditNavigation.Pickers.Audit) {
    when (route.config) {
        AuditNavigation.Pickers.Config.Time12h -> {
            TimePickerWith12HourClock(onTimeConfirm = {}, time = LocalTime.of(10, 10, 0))}
        AuditNavigation.Pickers.Config.Time24Hour -> {
            TimePicker(onTimeConfirm = {}, time = LocalTime.of(10, 10, 0), showSeconds = false)
        }
        AuditNavigation.Pickers.Config.Time24hWithSeconds -> {
            TimePicker(onTimeConfirm = {}, time = LocalTime.of(10, 10, 0), showSeconds = true)
        }
        AuditNavigation.Pickers.Config.Date -> {
            DatePicker(onDateConfirm = {}, date = LocalDate.of(2003, 8, 18))
        }
    }
}
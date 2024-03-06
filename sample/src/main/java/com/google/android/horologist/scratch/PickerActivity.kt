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

package com.google.android.horologist.scratch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.composables.TimePickerWith12HourClock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearPickerApp()
        }
    }
}

@Composable
fun WearPickerApp() {
    val sizes = listOf(190, 200, 210, 220, 230)
    val size = remember { mutableIntStateOf(0) }

    val pickerTypes = arrayOf("Date", "Time12h", "Time24h", "Time hms")
    val pickerType = remember { mutableIntStateOf(0) }

    var time by remember { mutableStateOf(LocalDateTime.now()) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SizedContainer(screenSize = sizes[size.intValue], roundScreen = true) {
            when (pickerType.value) {
                0 -> DatePicker({
                    time = time.toLocalTime().atDate(it)
                })
                1 -> TimePickerWith12HourClock({
                    time = time.toLocalDate().atTime(it)
                }, time = time.toLocalTime())
                2 -> TimePicker({
                    time = time.toLocalDate().atTime(it)
                }, time = time.toLocalTime(), showSeconds = false)
                else -> TimePicker({
                    time = time.toLocalDate().atTime(it)
                }, time = time.toLocalTime(), showSeconds = true)
            }
        }
        ToggleRow(
            title = "Size",
            options = sizes.map { it.toString() }.toTypedArray(),
            selected = size,
            optionWidth = 50.dp,
        )
        ToggleRow(
            title = "Type",
            options = pickerTypes,
            selected = pickerType,
            optionWidth = 80.dp,
        )
        Text(time.format(DateTimeFormatter.ISO_DATE_TIME))
    }
}

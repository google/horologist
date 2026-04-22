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

import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test
import org.robolectric.annotation.Config
import java.time.LocalTime

class TimePicker12hTest : WearLegacyScreenTest() {

    @Test
    fun initial() {
        runTest {
            TimePickerWith12HourClock(
                time = LocalTime.of(10, 10, 0),
                onTimeConfirm = {},
            )
        }
    }

    @Test
    fun midnight() {
        runTest {
            TimePickerWith12HourClock(
                time = LocalTime.of(0, 0, 0),
                onTimeConfirm = {},
            )
        }
    }

    @Test
    @Config(
        fontScale = 1.24f,
    )
    fun largestFontScaling() {
        runTest(applyDeviceConfig = false) {
            TimePickerWith12HourClock(
                time = LocalTime.of(10, 10, 0),
                onTimeConfirm = {},
            )
        }
    }

    @Test
    fun smallDeviceLargeFontBold() {
        runTest(device = WearDevice.GooglePixelWatchLargeFont) {
            TimePickerWith12HourClock(
                time = LocalTime.of(10, 10, 0),
                onTimeConfirm = {},
            )
        }
    }

    @Test
    @Config(qualifiers = "+en-rGB")
    fun localeEnGb() {
        runTest {
            TimePickerWith12HourClock(
                time = LocalTime.of(10, 10, 0),
                onTimeConfirm = {},
            )
        }
    }
}

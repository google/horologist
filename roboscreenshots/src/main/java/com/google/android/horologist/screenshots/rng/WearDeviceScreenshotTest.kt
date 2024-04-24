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

package com.google.android.horologist.screenshots.rng

import androidx.compose.runtime.Composable
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
public abstract class WearDeviceScreenshotTest(override val device: WearDevice) :
    WearScreenshotTest() {
        public override val tolerance: Float = 0.02f

        public fun runTest(content: @Composable () -> Unit) {
            withDrawingEnabled(forceHardware) {
                runTest(suffix = null, content = content)
            }
        }

        public companion object {
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters
            public fun devices(): List<WearDevice> = WearDevice.entries
        }
    }

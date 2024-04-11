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

package com.google.android.horologist.compose.material

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.wear.compose.material.Text
import com.google.android.horologist.screenshots.rng.WearLegacyScreenTest
import org.junit.Test

class StepperTest : WearLegacyScreenTest() {

    @Test
    fun float() {
        runTest {
            var value by remember {
                mutableFloatStateOf(0f)
            }
            Stepper(
                value = value,
                onValueChange = { value = it },
                valueRange = 0f..100f,
                steps = 9,
            ) {
                Text("Value: $value")
            }
        }
    }

    @Test
    fun int() {
        runTest {
            var value by remember {
                mutableIntStateOf(0)
            }
            Stepper(
                value = value,
                onValueChange = { value = it },
                valueProgression = IntProgression.fromClosedRange(0, 100, 10),
            ) {
                Text("Value: $value")
            }
        }
    }
}

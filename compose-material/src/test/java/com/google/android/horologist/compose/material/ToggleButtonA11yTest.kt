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

@file:Suppress("DEPRECATION")

package com.google.android.horologist.compose.material

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirplanemodeInactive
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.junit.Test

class ToggleButtonA11yTest :
    WearLegacyA11yTest() {

    @Test
    fun default() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
            )
        }
    }

    @Test
    fun notChecked() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
            )
        }
    }

    @Test
    fun disabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                enabled = false,
            )
        }
    }

    @Test
    fun notCheckedDisabled() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                checked = false,
                enabled = false,
            )
        }
    }

    @Test
    fun text() {
        runComponentTest {
            ToggleButton(
                text = "Monday",
                onCheckedChanged = {},
            )
        }
    }

    @Test
    fun iconOnly() {
        runComponentTest {
            ToggleButton(
                checkedIcon = Icons.Filled.AirplanemodeActive.asPaintable(),
                notCheckedIcon = Icons.Filled.AirplanemodeInactive.asPaintable(),
                contentDescription = "contentDescription",
                onCheckedChanged = {},
                colors = ToggleButtonDefaults.iconOnlyColors(),
                smallSize = true,
            )
        }
    }
}

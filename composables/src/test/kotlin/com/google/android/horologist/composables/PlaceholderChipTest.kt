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

@file:OptIn(ExperimentalWearFoundationApi::class)

package com.google.android.horologist.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.LocalReduceMotion
import androidx.wear.compose.foundation.ReduceMotion
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.screenshots.rng.WearLegacyComponentTest
import org.junit.Test

class PlaceholderChipTest : WearLegacyComponentTest() {

    @Test
    fun default() {
        runComponentTest {
            PlaceholderChip()
        }
    }

    @Test
    fun secondaryColors() {
        runComponentTest {
            PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
        }
    }

    @Composable
    override fun ComponentScaffold(content: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalReduceMotion provides ReduceMotion {
                true
            },
        ) {
            super.ComponentScaffold(content)
        }
    }
}

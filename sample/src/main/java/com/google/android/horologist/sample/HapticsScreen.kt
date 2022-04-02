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

package com.google.android.horologist.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.devices.LocalHaptics

@Composable
fun HapticsScreen() {
    val haptics = LocalHaptics.current

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(haptics.javaClass.simpleName)
        }
        item {
            Button(onClick = { haptics.performScrollTick() }) {
                Text(text = "Scroll Tick")
            }
        }
        item {
            Button(onClick = { haptics.performScrollClick() }) {
                Text(text = "Scroll Click")
            }
        }
        item {
            Button(onClick = { haptics.performScrollHeavyClick() }) {
                Text(text = "Heavy Click")
            }
        }
    }
}

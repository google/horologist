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

package com.google.android.horologist.media.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.android.horologist.media.ui.util.isLargeScreen

@Composable
public fun ControlButtonLayout(
    leftButton: @Composable () -> Unit,
    middleButton: @Composable () -> Unit,
    rightButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val middleSize = if (LocalConfiguration.current.isLargeScreen) 80.dp else 60.dp
    Row(
        modifier = modifier.fillMaxWidth().height(middleSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.Center,
    ) {
        Box(modifier = Modifier.fillMaxHeight().weight(1.0f)) {
            leftButton()
        }

        Box(modifier = Modifier.size(middleSize)) {
            middleButton()
        }

        Box(modifier = Modifier.fillMaxHeight().weight(1.0f)) {
            rightButton()
        }
    }
}

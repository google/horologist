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

package com.google.android.horologist.materialcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.material.Confirmation

@Composable
internal fun SampleConfirmationScreen(
    modifier: Modifier = Modifier,
) {
    Confirmation(
        modifier = modifier.fillMaxSize(),
        onTimeout = {},
    ) {
        ConfirmationContent()
    }
}

@Composable
internal fun SampleConfirmationLauncher() {
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Chip(
            onClick = { showDialog = true },
            label = { Text("Show dialog") },
            colors = ChipDefaults.secondaryChipColors(),
        )
    }
    Confirmation(
        showDialog = showDialog,
        onTimeout = { showDialog = false },
        icon = {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Completed",
                tint = Color.Green,
            )
        },
        title = "Alarm in 23 hr 59 min",
    )
}

@Composable
private fun ConfirmationContent() {
    Text(text = "Confirmation Content")
}

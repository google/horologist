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

package com.google.android.horologist.materialcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl

@Composable
internal fun SampleAlertDialog() {
    var showBedtimeModeDialog by remember { mutableStateOf(false) }
    var showAllowDebuggingDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ListHeader {
            Text("AlertDialog samples")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Chip(
            onClick = { showBedtimeModeDialog = true },
            label = { Text("Bedtime Mode") },
            colors = ChipDefaults.secondaryChipColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Chip(
            onClick = { showAllowDebuggingDialog = true },
            label = { Text("Allow debugging") },
            colors = ChipDefaults.secondaryChipColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    var bedtimeMode by remember { mutableStateOf(false) }
    AlertDialog(
        onCancel = { showBedtimeModeDialog = false },
        onOk = { showBedtimeModeDialog = false },
        showDialog = showBedtimeModeDialog,
        title = "Turn on Bedtime mode?",
        message = "Watch screen, tilt-to-wake, and touch are turned off. " +
            "Only calls from starred contacts, repeat callers, " +
            "and alarms will notify you.",
        content = {
            item {
                ToggleChip(
                    checked = bedtimeMode,
                    onCheckedChanged = { bedtimeMode = !bedtimeMode },
                    label = "Don't show again",
                    toggleControl = ToggleChipToggleControl.Checkbox,
                )
            }
        },
    )
    AlertDialog(
        showDialog = showAllowDebuggingDialog,
        onDismiss = { showAllowDebuggingDialog = false },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Help,
                contentDescription = "Question",
            )
        },
        title = "Allow debugging?",
        content = {
            item {
                Chip(
                    onClick = { showAllowDebuggingDialog = false },
                    label = { Text("Cancel") },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = { showAllowDebuggingDialog = false },
                    label = { Text("OK") },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = { showAllowDebuggingDialog = false },
                    label = { Text("Always allow") },
                    secondaryLabel = { Text("from this computer") },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}

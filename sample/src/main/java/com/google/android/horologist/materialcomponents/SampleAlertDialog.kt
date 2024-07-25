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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.compose.material.centeredDialogColumnState

@Composable
internal fun SampleAlertDialog(
    modifier: Modifier = Modifier,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )

    var showSimpleDialog by remember { mutableStateOf(false) }
    var showBedtimeModeDialog by remember { mutableStateOf(false) }
    var showAllowDebuggingDialog by remember { mutableStateOf(false) }
    var showCenteredDialog by remember { mutableStateOf(false) }

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = modifier,
        ) {
            item {
                Title("AlertDialog samples")
            }
            item {
                Chip(
                    onClick = { showSimpleDialog = true },
                    label = { Text("Simple alert") },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = { showCenteredDialog = true },
                    label = { Text("Centered alert") },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = { showBedtimeModeDialog = true },
                    label = { Text("Bedtime Mode") },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Chip(
                    onClick = { showAllowDebuggingDialog = true },
                    label = { Text("Allow debugging") },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    // Simple AlertDialog sample with icon, title, message and Ok/Cancel buttons.
    AlertDialog(
        showDialog = showSimpleDialog,
        onCancel = { showSimpleDialog = false },
        onOk = { showSimpleDialog = false },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Help,
                contentDescription = "Question",
            )
        },
        title = "A Simple Dialog",
        message = "Icon and Text dialog use up to 2 lines.",
    )

    // Centered AlertDialog sample with title and Ok/Cancel buttons.
    AlertDialog(
        showDialog = showCenteredDialog,
        onCancel = { showCenteredDialog = false },
        onOk = { showCenteredDialog = false },
        title = "A Centered Dialog",
        state = centeredDialogColumnState(),
    )

    // "Bedtime mode" AlertDialog sample, with Ok/Cancel buttons and extra content.
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

    // "Allow debugging?" AlertDialog sample, with a vertical stack of Chip choices.
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

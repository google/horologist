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

package com.google.android.horologist.spec

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.CompactChip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable

@Composable
fun Title(lines: Int) {
    ResponsiveListHeader(
        contentPadding = ListHeaderDefaults.firstItemPadding(),
    ) {
        Text(text = (1..lines).map { "MMMMMMMM" }.joinToString("\n"), maxLines = lines)
    }
}

val text = "We posted a payment for your credit card ending in 5555. " +
    "If there is anything that does not match your payment details " +
    "please log into your account or contact us with any questions. " +
    "Please contact us if you did not perform this purchase. " +
    "Our customer service team is available 24 hours a day, 7 days a week to help you."

@Composable
fun WarningText() {
    Text(
        text,
        modifier = Modifier.listTextPadding()
    )
}

fun ScalingLazyListScope.warningTextItems() {
    text.split(".").forEach {
        val textLine = it.trim()
        if (textLine.isNotBlank()) {
            item {
                Text(textLine, modifier = Modifier.listTextPadding())
            }
        }
    }
}

@Composable
fun MessagesCard() {
    AppCard(
        onClick = { /*TODO*/ },
        appName = { Text(text = "messages") },
        time = { Text(text = "12m") },
        title = {
            Text(
                text = "Robert Simpson",
            )
        },
    ) {
        Text(text = "Let me know when you leave home")
    }
}

@Composable
fun BofACard() {
    AppCard(
        onClick = { /*TODO*/ },
        appName = { Text(text = "Gmail") },
        time = { Text(text = "15m") },
        title = {
            Text(
                text = "Bank of America",
            )
        },
    ) {
        Text(text = "Reminder: Your payment is now due")
    }
}

@Composable
fun LocationOnButton() {
    Button(
        imageVector = Icons.Filled.LocationOn,
        contentDescription = "",
        onClick = { },
    )
}

@Composable
fun AddCircleButton() {
    Button(
        imageVector = Icons.Filled.AddCircle,
        contentDescription = "",
        onClick = { },
    )
}

@Composable
fun DoneCompactChip() {
    CompactChip(
        icon = Icons.Default.Done.asPaintable(),
        contentDescription = "",
        onClick = { /*TODO*/ },
    )
}

@Composable
fun SystemChip() {
    Chip(
        label = "System",
        onClick = { },
        icon = Icons.Default.Schedule.asPaintable(),
    )
}

@Composable
fun AccountsChip() {
    Chip(
        label = "Accounts & Security",
        onClick = { },
        icon = Icons.Outlined.Badge.asPaintable(),
    )
}

@Composable
fun SoundChip() {
    Chip(
        label = "Sound & vibration",
        onClick = { },
        icon = Icons.Default.Apps.asPaintable(),
    )
}

@Composable
fun AppsChip() {
    Chip(
        label = "Apps & notifications",
        onClick = { },
        icon = Icons.Default.Apps.asPaintable(),
    )
}

@Composable
fun ConnectivityChip() {
    Chip(
        label = "Connectivity",
        onClick = { },
        icon = Icons.Outlined.Cloud.asPaintable(),
    )
}

@Composable
fun VolumeSlider() {
    Chip(label = "TODO", onClick = { /*TODO*/ })
}

@Composable
fun SoundToggleChip() {
    ToggleChip(
        label = "Sound",
        checked = true,
        onCheckedChanged = {},
        toggleControl = ToggleChipToggleControl.Switch,
    )
}

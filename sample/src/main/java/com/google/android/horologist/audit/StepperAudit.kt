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

package com.google.android.horologist.audit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.audio.ui.R
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.Stepper

@Composable
fun StepperAudit(route: AuditNavigation.Stepper.Audit) {
    ScreenScaffold(timeText = {}) {
        when (route.config) {
            AuditNavigation.Stepper.Config.ButtonAndIcon -> {
                Stepper(
                    value = 0.5f,
                    onValueChange = {},
                    steps = 10,
                    increaseIcon = { Icon(Icons.Default.Add, contentDescription = "") },
                    decreaseIcon = {
                        Icon(Icons.Default.Remove, contentDescription = "")
                    }) { }
            }

            AuditNavigation.Stepper.Config.ButtonOnly -> {
                Text("TBC")
            }

            AuditNavigation.Stepper.Config.TextOnly -> {
                Stepper(
                    value = 0.5f,
                    onValueChange = {},
                    steps = 10,
                    increaseIcon = { Text("Plus") },
                    decreaseIcon = {
                        Text("Minus")
                    }) { }
            }

            AuditNavigation.Stepper.Config.VolumeIndicator -> {
                Stepper(value = 0.5f, onValueChange = {}, steps = 10) {
                    Text(
                        stringResource(id = R.string.horologist_volume_screen_volume_label),
                        style = MaterialTheme.typography.button.copy(
                            color = MaterialTheme.colors.onBackground,
                            fontWeight = FontWeight.Normal,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                }
            }
        }
    }
}
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
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.WhereToVote
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.dialog.Dialog
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.NonScrollableAlertContent

@Composable
fun DialogsAudit(route: AuditNavigation.Dialogs.Audit) {
    when (route.config) {
        AuditNavigation.Dialogs.Config.Title -> {
            AlertDialog(showDialog = true, title = "Title", onDismiss = {})
        }

        AuditNavigation.Dialogs.Config.IconAndTitle -> {
            AlertDialog(
                showDialog = true,
                title = "Title",
                onDismiss = {},
                icon = { Icon(Icons.Default.MedicalServices, contentDescription = "") },
            )
        }

        AuditNavigation.Dialogs.Config.OneButtonChip -> {
            AlertDialog(showDialog = true, title = "Title", onDismiss = {}) {
                item {
                    Chip("A Chip", onClick = {})
                }
            }
        }
        AuditNavigation.Dialogs.Config.TwoBottomButtons -> {
            AlertDialog(showDialog = true, title = "Title", onOk = {}, onCancel = {})
        }
        AuditNavigation.Dialogs.Config.NoBottomButton -> {
            AlertDialog(showDialog = true, title = "Title", onDismiss = {})
        }
        AuditNavigation.Dialogs.Config.OneBottomButton -> {
            AlertDialog(showDialog = true, title = "Title", onDismiss = {}) {
                item {
                    Button(
                        onClick = {},
                        imageVector = Icons.Default.WhereToVote,
                        contentDescription = "",
                    )
                }
            }
        }

        AuditNavigation.Dialogs.Config.NonScrollable -> {
            Dialog(
                showDialog = true,
                onDismissRequest = {},
            ) {
                NonScrollableAlertContent(
                    icon = { Icon(Icons.Default.MedicalServices, contentDescription = "") },
                    title = "Title",
                )
            }
        }
    }
}

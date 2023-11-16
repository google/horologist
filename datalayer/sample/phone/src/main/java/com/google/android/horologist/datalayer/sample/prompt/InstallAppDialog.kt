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

package com.google.android.horologist.datalayer.sample.prompt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.datalayer.sample.R

@Composable
fun InstallAppDialog(
    appName: String,
    watchName: String,
    message: String,
    icon: @Composable (() -> Unit),
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        icon = {
            Box(modifier = Modifier.padding(vertical = 20.dp)) {
                icon()
            }
        },
        title = {
            Text(
                text = stringResource(
                    id = R.string.install_app_prompt_dialog_title,
                    appName,
                    watchName,
                ),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center,
            )
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Button(
                onClick = { onConfirmation() },
            ) {
                Text(stringResource(id = R.string.install_app_prompt_dialog_install_btn_label))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismissRequest() },
            ) {
                Text(stringResource(id = R.string.install_app_prompt_dialog_cancel_btn_label))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun InstallAppDialogPreview() {
    InstallAppDialog(
        appName = "Gmail",
        watchName = "Pixel Watch",
        message = "Stay productive and manage emails right from your wrist.",
        icon = { Icon(Icons.Default.Email, contentDescription = null) },
        onDismissRequest = {},
        onConfirmation = {},
    )
}

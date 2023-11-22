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

package com.google.android.horologist.datalayer.phone.ui.prompt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.horologist.datalayer.phone.ui.R

// Definitions from androidx.compose.material3.AlertDialog
private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 560.dp
private val DialogPadding = PaddingValues(all = 24.dp)
private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)

@Composable
public fun InstallAppDialog(
    appName: String,
    watchName: String,
    message: String,
    icon: @Composable (() -> Unit)?,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Content adapted from androidx.compose.material3.AlertDialog
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = modifier
                .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth),
            propagateMinConstraints = true,
        ) {
            Surface(
                modifier = modifier,
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(DialogPadding),
                ) {
                    icon?.let {
                        CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.iconContentColor) {
                            Box(
                                Modifier
                                    .padding(IconPadding)
                                    .align(Alignment.CenterHorizontally),
                            ) {
                                icon()
                            }
                        }
                    }

                    CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.titleContentColor) {
                        // Original: DialogTokens.HeadlineFont
                        val textStyle = MaterialTheme.typography.headlineMedium
                        ProvideTextStyle(textStyle) {
                            Box(
                                // Align the title to the center when an icon is present.
                                Modifier
                                    .padding(TitlePadding)
                                    .align(
                                        if (icon == null) {
                                            Alignment.Start
                                        } else {
                                            Alignment.CenterHorizontally
                                        },
                                    ),
                            ) {
                                Text(
                                    text = stringResource(
                                        id = R.string.horologist_install_app_prompt_dialog_title,
                                        appName,
                                        watchName,
                                    ),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.textContentColor) {
                        // Original: DialogTokens.SupportingTextFont
                        val textStyle = MaterialTheme.typography.bodyMedium
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    // .weight(weight = 1f, fill = false)
                                    .padding(TextPadding)
                                    .align(Alignment.Start),
                            ) {
                                Text(
                                    text = message,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.titleContentColor) {
                            // Original: DialogTokens.SupportingTextFont
                            val textStyle = MaterialTheme.typography.labelMedium
                            ProvideTextStyle(
                                value = textStyle,
                                content = {
                                    Row {
                                        OutlinedButton(
                                            onClick = { onDismissRequest() },
                                        ) {
                                            Text(stringResource(id = R.string.horologist_install_app_prompt_dialog_cancel_btn_label))
                                        }
                                        Spacer(modifier = Modifier.width(20.dp))
                                        Button(
                                            onClick = { onConfirmation() },
                                        ) {
                                            Text(stringResource(id = R.string.horologist_install_app_prompt_dialog_ok_btn_label))
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InstallAppDialogPreview() {
    InstallAppDialog(
        appName = "Gmail",
        watchName = "Pixel Watch",
        message = "Stay productive and manage emails right from your wrist.",
        icon = { Icon(Icons.Default.Email, contentDescription = null) },
        onDismissRequest = { },
        onConfirmation = { },
    )
}

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

package com.google.android.horologist.datalayer.phone.ui.prompt.installtile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.datalayer.phone.ui.R

// Constants from the redlines.
private val PADDING_GREEN = 12.dp
private val PADDING_PINK = 16.dp
private val PADDING_PURPLE = 24.dp
private val PADDING_BLUE = 32.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun InstallTileBottomSheet(
    image: @Composable (() -> Unit)?,
    topMessage: String,
    bottomMessage: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = null,
    ) {
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                InstallTileBottomSheetPortraitContent(
                    image = image,
                    topMessage = topMessage,
                    bottomMessage = bottomMessage,
                    onDismissRequest = onDismissRequest,
                    onConfirmation = onConfirmation,
                )
            }

            else -> {
                InstallTileBottomSheetLandscapeContent(
                    image = image,
                    topMessage = topMessage,
                    bottomMessage = bottomMessage,
                    onDismissRequest = onDismissRequest,
                    onConfirmation = onConfirmation,
                )
            }
        }
    }
}

@Composable
internal fun InstallTileBottomSheetPortraitContent(
    image: @Composable (() -> Unit)?,
    topMessage: String,
    bottomMessage: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(PADDING_PINK)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        image?.let {
            Box(
                modifier = Modifier
                    .padding(top = PADDING_PURPLE)
                    .align(Alignment.CenterHorizontally),
            ) {
                image()
            }
        }

        if (topMessage.isNotBlank()) {
            Text(
                text = topMessage,
                modifier = Modifier
                    .padding(top = PADDING_PURPLE)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (bottomMessage.isNotBlank()) {
            Text(
                text = bottomMessage,
                modifier = Modifier
                    .padding(top = PADDING_PINK)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.height(PADDING_PURPLE))

        Row(
            modifier = Modifier
                .padding(horizontal = PADDING_PINK)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .padding(end = PADDING_GREEN),
            ) {
                Text(stringResource(id = R.string.horologist_install_tile_prompt_cancel_btn_label))
            }

            Button(
                onClick = onConfirmation,
            ) {
                Text(stringResource(id = R.string.horologist_install_tile_prompt_ok_btn_label))
            }
        }
    }
}

@Composable
internal fun InstallTileBottomSheetLandscapeContent(
    image: @Composable (() -> Unit)?,
    topMessage: String,
    bottomMessage: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = PADDING_PURPLE)
            .padding(top = PADDING_BLUE)
            .verticalScroll(rememberScrollState()),
    ) {
        Row {
            image?.let {
                Box(modifier = Modifier.padding(end = PADDING_PURPLE)) {
                    image()
                }
            }

            Column {
                if (topMessage.isNotBlank()) {
                    Text(
                        text = topMessage,
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        maxLines = 3,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                if (bottomMessage.isNotBlank()) {
                    Text(
                        text = bottomMessage,
                        modifier = Modifier
                            .padding(top = PADDING_PINK)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start,
                        maxLines = 3,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(top = PADDING_BLUE, bottom = PADDING_PINK)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .padding(end = PADDING_GREEN),
            ) {
                Text(stringResource(id = R.string.horologist_install_app_prompt_cancel_btn_label))
            }

            Button(
                onClick = onConfirmation,
            ) {
                Text(stringResource(id = R.string.horologist_install_tile_prompt_ok_btn_label))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InstallTileBottomSheetContentPreview() {
    InstallTileBottomSheetPortraitContent(
        image = { Icon(Icons.Default.Email, contentDescription = null) },
        topMessage = "Stay productive and manage emails right from your wrist.",
        bottomMessage = "Add the Gmail app to your Wear OS watch for easy access wherever you are.",
        onDismissRequest = { },
        onConfirmation = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun InstallTileBottomSheetContentPreviewNoIcon() {
    InstallTileBottomSheetPortraitContent(
        image = null,
        topMessage = "Find useful content from your app with a glance.",
        bottomMessage = "Add the Tile to your Wear OS app for accessing data with a glance.",
        onDismissRequest = { },
        onConfirmation = { },
    )
}

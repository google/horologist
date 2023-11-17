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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.datalayer.sample.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTileBottomSheet(
    tileName: String,
    appName: String,
    watchName: String,
    icon: @Composable (() -> Unit),
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        AddTileBottomSheetContent(
            tileName = tileName,
            appName = appName,
            watchName = watchName,
            icon = icon,
            onDismissRequest = onDismissRequest,
            onConfirmation = onConfirmation,
        )
    }
}

@Composable
internal fun AddTileBottomSheetContent(
    tileName: String,
    appName: String,
    watchName: String,
    icon: @Composable (() -> Unit),
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box(
            modifier = Modifier
                .heightIn(min = 0.dp, max = 180.dp)
                .padding(vertical = 20.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            icon()
        }

        Text(
            text = tileName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = stringResource(
                id = R.string.add_tile_prompt_message,
                appName,
                watchName,
            ),
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.End),
        ) {
            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Text(stringResource(id = R.string.add_tile_prompt_cancel_btn_label))
            }

            Button(
                onClick = onConfirmation,
            ) {
                Text(stringResource(id = R.string.add_tile_prompt_add_btn_label))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTileBottomSheetContentPreview() {
    AddTileBottomSheetContent(
        tileName = "Unread Emails",
        appName = "Gmail",
        watchName = "Pixel Watch",
        icon = { Icon(Icons.Default.Email, contentDescription = null) },
        onDismissRequest = {},
        onConfirmation = {},
    )
}

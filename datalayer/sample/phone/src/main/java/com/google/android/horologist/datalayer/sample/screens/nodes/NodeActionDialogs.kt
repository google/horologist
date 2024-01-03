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

package com.google.android.horologist.datalayer.sample.screens.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.horologist.datalayer.sample.R

@Composable
fun NodesActionSucceededDialog(
    message: String,
    onDismissRequest: () -> Unit,
) {
    NodesActionDialog(
        message = message,
        onDismissRequest = onDismissRequest,
        imageVector = Icons.Default.Done,
    )
}

@Composable
fun NodesActionFailureDialog(
    message: String,
    onDismissRequest: () -> Unit,
) {
    NodesActionDialog(
        message = message,
        onDismissRequest = onDismissRequest,
        imageVector = Icons.Default.Close,
    )
}

@Composable
fun NodesActionDialog(
    message: String,
    onDismissRequest: () -> Unit,
    imageVector: ImageVector,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(225.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                Text(
                    text = message,
                    modifier = Modifier.padding(10.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text(stringResource(id = R.string.node_screen_action_dialog_dismiss_button_label))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NodesActionSucceededDialogPreview() {
    NodesActionSucceededDialog(
        message = "Success!",
        onDismissRequest = { },
    )
}

@Preview(showBackground = true)
@Composable
fun NodesActionFailureDialogPreview() {
    NodesActionFailureDialog(
        message = "Failed: RESULT",
        onDismissRequest = { },
    )
}

@Preview(showBackground = true)
@Composable
fun NodesActionDialogPreview() {
    NodesActionDialog(
        message = "This is a dialog with a button and an icon.",
        onDismissRequest = { },
        imageVector = Icons.Default.Done,
    )
}

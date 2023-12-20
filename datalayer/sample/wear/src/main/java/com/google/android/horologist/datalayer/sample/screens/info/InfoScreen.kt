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

package com.google.android.horologist.datalayer.sample.screens.info

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.belowTimeTextPreview
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.datalayer.sample.R

@Composable
fun InfoScreen(
    onDismissClick: () -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    infoScreenViewModel: InfoScreenViewModel = hiltViewModel(),
) {
    InfoScreen(
        message = infoScreenViewModel.message,
        onDismissClick = onDismissClick,
        columnState = columnState,
        modifier = modifier,
    )
}

@Composable
fun InfoScreen(
    message: String,
    onDismissClick: () -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier,
    ) {
        item {
            Text(text = message, modifier = Modifier.padding(top = 20.dp))
        }
        item {
            Button(
                imageVector = Icons.Default.Done,
                contentDescription = stringResource(id = R.string.info_done_button_content_description),
                onClick = onDismissClick,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun InfoScreenPreview() {
    InfoScreen(
        message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
            "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
            "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        onDismissClick = { },
        columnState = belowTimeTextPreview(),
    )
}

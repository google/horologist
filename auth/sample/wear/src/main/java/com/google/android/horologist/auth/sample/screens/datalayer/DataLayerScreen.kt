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

package com.google.android.horologist.auth.sample.screens.datalayer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Title

@Composable
fun DataLayerScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
    viewModel: DataLayerViewModel = viewModel(factory = DataLayerViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ScalingLazyColumn(
        columnState = columnState,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Title(R.string.data_layer_title, Modifier)
        }
        item {
            Text(
                text = stringResource(id = R.string.server_counter_message),
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
        }
        val error = state.error
        if (error != null) {
            item {
                Text(
                    text = stringResource(R.string.data_layer_error_message, error),
                    color = MaterialTheme.colors.error
                )
            }
        }
        val counterValue = state.counterValue
        item {
            if (counterValue != null) {
                Text(text = stringResource(R.string.data_layer_value_message, counterValue.value))
            } else {
                Text(text = stringResource(R.string.data_layer_missing_message))
            }
        }
        item {
            Row {
                Button(
                    imageVector = Icons.Default.PlusOne,
                    contentDescription = "Plus One",
                    onClick = {
                        viewModel.addDelta(1)
                    }
                )
            }
        }
    }
}

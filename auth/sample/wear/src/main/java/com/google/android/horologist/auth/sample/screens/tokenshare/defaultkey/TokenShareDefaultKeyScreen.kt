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

package com.google.android.horologist.auth.sample.screens.tokenshare.defaultkey

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.google.android.horologist.auth.sample.R
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader

@Composable
fun TokenShareDefaultKeyScreen(
    modifier: Modifier = Modifier,
    viewModel: TokenShareDefaultKeyViewModel = viewModel(factory = TokenShareDefaultKeyViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = modifier,
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(stringResource(id = R.string.token_share_default_key_title), modifier = Modifier.listTextPadding())
                }
            }
            item {
                Text(
                    text = stringResource(id = R.string.token_share_default_key_message),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }
            items(state) { tokenBundle ->
                tokenBundle?.let {
                    Chip(
                        label = tokenBundle.accessToken,
                        onClick = { /* do nothing */ },
                        colors = ChipDefaults.secondaryChipColors(),
                        enabled = false,
                    )
                }
            }
        }
    }
}

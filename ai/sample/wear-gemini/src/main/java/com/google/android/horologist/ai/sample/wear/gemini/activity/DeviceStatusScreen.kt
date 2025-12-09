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

package com.google.android.horologist.ai.sample.wear.gemini.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import coil.compose.AsyncImage
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun DeviceStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: DeviceStatusViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val columnState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    val data = uiState.value
    val paragraphs = remember((data as? Loaded)?.description) {
        (data as? Loaded)?.description?.split("[\n,]+".toRegex()).orEmpty()
    }

    ScreenScaffold(
        modifier = modifier,
        contentPadding = rememberResponsiveColumnPadding(
            first = ColumnItemType.IconButton,
            last = ColumnItemType.BodyText,
        ),
        scrollState = columnState,
    ) { contentPadding ->
        TransformingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            contentPadding = contentPadding,
            state = columnState,
        ) {
            item {
                ListHeader(modifier = Modifier.fillMaxWidth()) {
                    Text("Gemini")
                }
            }
            if (data is Loaded) {
                items(paragraphs.take(3)) {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyExtraSmall,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
                item {
                    AsyncImage(
                        model = data.image,
                        contentDescription = null,
                        modifier = Modifier.width(100.dp),
                        contentScale = ContentScale.FillWidth,
                    )
                }
                items(paragraphs.drop(3)) {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyExtraSmall,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
            } else {
                item {
                    Text("Loading...")
                }
            }
        }
    }
}

sealed interface DeviceStatusUiState

data object Loading : DeviceStatusUiState

data class Loaded(
    val image: ByteArray?,
    val description: String?,
) : DeviceStatusUiState

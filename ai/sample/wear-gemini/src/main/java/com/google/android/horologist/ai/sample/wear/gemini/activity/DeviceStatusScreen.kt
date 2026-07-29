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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import coil.compose.AsyncImage

@Composable
fun DeviceStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: DeviceStatusViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    DeviceStatusScreen(uiState = uiState.value, modifier = modifier)
}

/**
 * Stateless overload, which is the one that can be previewed and tested.
 *
 * The stateful overload above reaches for `hiltViewModel()`, and a `@Preview` of it renders
 * nothing: there is no Hilt-enabled activity behind a preview, so resolving the view model throws
 * and the capture produces no image.
 */
@Composable
fun DeviceStatusScreen(
    uiState: DeviceStatusUiState,
    modifier: Modifier = Modifier,
) {
    ScreenScaffold(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (uiState is Loaded) {
                AsyncImage(
                    model = uiState.image,
                    contentDescription = null,
                    modifier = Modifier.width(100.dp),
                    contentScale = ContentScale.FillWidth,
                )
                Text(uiState.description ?: "None", style = MaterialTheme.typography.bodyExtraSmall)
            } else {
                Text("Loading...")
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

@WearPreviewSmallRound
@Composable
fun DeviceStatusScreenLoadingPreview() {
    DeviceStatusScreen(uiState = Loading)
}

// There is deliberately no `Loaded` preview. `AsyncImage` never resolves a painter during an
// offscreen render — coil's load is asynchronous and nothing drives it — so it reports no intrinsic
// size, and with `ContentScale.FillWidth` it then expands to the column's full height and pushes
// the description off the watch face. The result is a solid black capture that says nothing.
// Verified: passing real PNG bytes instead of `null` produces a byte-identical black PNG, so it is
// the unresolved painter and not the missing data.
//
// Previewing that state usefully needs the screen to bound the image's height (or to accept a
// `Painter` the preview can supply directly), which is a change to the screen rather than to its
// preview.

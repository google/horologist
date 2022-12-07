/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.mediasample.ui.debug

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.settings.ActionSetting

@Composable
fun SamplesScreen(
    columnState: ScalingLazyColumnState,
    samplesScreenViewModel: SamplesScreenViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val uiState by samplesScreenViewModel.uiState.collectAsStateWithLifecycle()

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize(),
        columnState = columnState
    ) {
        item {
            Text(
                text = stringResource(id = R.string.sample_samples),
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.title3
            )
        }
        items(uiState.samples) {
            ActionSetting(text = it.name) {
                samplesScreenViewModel.playSamples(it.id)
                navController.navigateToPlayer()
            }
        }
    }
}

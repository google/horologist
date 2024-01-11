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

package com.google.android.horologist.ai.sample.wear.prompt.prompt

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.ai.sample.R
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import com.google.android.horologist.ai.ui.model.TextPromptUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel
import com.google.android.horologist.ai.ui.screens.PromptScreen
import com.google.android.horologist.ai.ui.screens.PromptUiState
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.Button

@Composable
fun SamplePromptScreen(
    modifier: Modifier = Modifier,
    viewModel: SamplePromptViewModel = hiltViewModel(),
    columnState: ScalingLazyColumnState = rememberColumnState(),
    onSettingsClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val voiceLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.let { data ->
                val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val enteredPrompt = results?.get(0)
                if (!enteredPrompt.isNullOrBlank()) {
                    viewModel.askQuestion(enteredPrompt)
                }
            }
        }

    val voiceIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )

        putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            stringResource(R.string.prompt_input),
        )
    }

    SamplePromptScreen(
        uiState = uiState,
        modifier = modifier,
        columnState = columnState,
        onSettingsClick = onSettingsClick,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                Icons.Default.Mic,
                contentDescription = stringResource(R.string.prompt_input),
                onClick = {
                    voiceLauncher.launch(voiceIntent)
                },
            )
        }
    }
}

@Composable
private fun SamplePromptScreen(
    uiState: PromptUiState,
    modifier: Modifier = Modifier,
    columnState: ScalingLazyColumnState = rememberColumnState(),
    onSettingsClick: (() -> Unit)? = null,
    promptEntry: @Composable () -> Unit,
) {
    ScreenScaffold(scrollState = columnState) {
        PromptScreen(
            uiState = uiState,
            columnState = columnState,
            modifier = modifier,
            promptEntry = promptEntry,
            onSettingsClick = onSettingsClick,
        )
    }
}

@WearPreviewLargeRound
@Composable
fun SamplePromptScreenPreviewEmpty() {
    SamplePromptScreen(
        uiState = PromptUiState(),
        promptEntry = {
            Button(
                imageVector = Icons.Default.QuestionAnswer,
                contentDescription = "Ask Again",
                onClick = { },
            )
        },
    )
}

@WearPreviewLargeRound
@Composable
fun SamplePromptScreenPreviewMany() {
    SamplePromptScreen(
        uiState = PromptUiState(
            ModelInstanceUiModel("id", "Demo Model"),
            listOf(
                TextPromptUiModel("why did the chicken cross the road?"),
                TextResponseUiModel("To get to the other side."),
                TextPromptUiModel("why did the chicken cross the road?"),
                TextResponseUiModel(
                    "To get to the other side. " +
                        "To get to the other side. " +
                        "To get to the other side. " +
                        "To get to the other side. " +
                        "To get to the other side.",
                ),
                TextPromptUiModel("why did the chicken cross the road?"),
                TextResponseUiModel("To get to the other side."),
            ),
        ),
        promptEntry = {
            Button(
                imageVector = Icons.Default.QuestionAnswer,
                contentDescription = "Ask Again",
                onClick = { },
            )
        },
    )
}

@WearPreviewLargeRound
@Composable
fun SamplePromptScreenPreviewQuestion() {
    SamplePromptScreen(
        uiState = PromptUiState(
            ModelInstanceUiModel("id", "Demo Model"),
            listOf(
                TextPromptUiModel("why did the chicken cross the road?"),
                TextResponseUiModel("To get to the other side."),
            ),
            TextPromptUiModel("why did the chicken cross the road?"),
        ),
        promptEntry = {
            Button(
                imageVector = Icons.Default.QuestionAnswer,
                contentDescription = "Ask Again",
                onClick = { },
            )
        },
    )
}

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.ai.sample.prompt.R
import com.google.android.horologist.ai.ui.components.PromptOrResponseDisplay
import com.google.android.horologist.ai.ui.model.ModelInstanceUiModel
import com.google.android.horologist.ai.ui.model.PromptOrResponseUiModel
import com.google.android.horologist.ai.ui.model.TextPromptUiModel
import com.google.android.horologist.ai.ui.model.TextResponseUiModel
import com.google.android.horologist.ai.ui.screens.PromptScreen
import com.google.android.horologist.ai.ui.screens.PromptUiState
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography

@Composable
fun SamplePromptScreen(
    modifier: Modifier = Modifier,
    viewModel: SamplePromptViewModel = hiltViewModel(),
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
    onSettingsClick: (() -> Unit)? = null,
    promptEntry: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMarkdownColors provides SampleColors(),
        LocalMarkdownTypography provides SampleTypography(),
    ) {
        PromptScreen(
            uiState = uiState,
            modifier = modifier,
            promptEntry = promptEntry,
            onSettingsClick = onSettingsClick,
            promptDisplay = {
                ModelDisplay(it)
            },
        )
    }
}

@Composable
private fun SampleTypography() = DefaultMarkdownTypography(
    h1 = MaterialTheme.typography.title1,
    h2 = MaterialTheme.typography.title2,
    h3 = MaterialTheme.typography.title3,
    h4 = MaterialTheme.typography.caption1,
    h5 = MaterialTheme.typography.caption2,
    h6 = MaterialTheme.typography.caption3,
    text = MaterialTheme.typography.body1,
    code = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace),
    quote = MaterialTheme.typography.body2.plus(SpanStyle(fontStyle = FontStyle.Italic)),
    paragraph = MaterialTheme.typography.body1,
    ordered = MaterialTheme.typography.body1,
    bullet = MaterialTheme.typography.body1,
    list = MaterialTheme.typography.body1,
)

@Composable
private fun SampleColors() = DefaultMarkdownColors(
    text = Color.White,
    codeText = LocalContentColor.current,
    linkText = Color.Blue,
    codeBackground = MaterialTheme.colors.background,
    inlineCodeBackground = MaterialTheme.colors.background,
    dividerColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
)

@Composable
private fun ModelDisplay(it: PromptOrResponseUiModel) {
    if (it is TextResponseUiModel) {
        SampleTextResponseCard(it)
    } else {
        PromptOrResponseDisplay(
            promptResponse = it,
            onClick = {},
        )
    }
}

@Composable
public fun SampleTextResponseCard(
    textResponseUiModel: TextResponseUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            MaterialTheme.colors.surface,
            MaterialTheme.colors.surface,
        ),
    ) {
        Markdown(
            textResponseUiModel.text,
            colors = SampleColors(),
            typography = SampleTypography(),
        )
    }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
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
@WearPreviewSmallRound
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
@WearPreviewSmallRound
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

@WearPreviewLargeRound
@Composable
fun SamplePromptScreenPreviewMarkdown() {
    SamplePromptScreen(
        uiState = PromptUiState(
            ModelInstanceUiModel("id", "Demo Model"),
            listOf(
                TextPromptUiModel("why did the chicken cross the road?"),
                TextResponseUiModel("To **get** to _the_ other side."),
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

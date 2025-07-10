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
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SurfaceTransformation
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

    val voiceLauncher = rememberLauncherForActivityResult(
        VoiceContract(),
    ) {
        when (it) {
            VoiceContract.Result.Empty -> {
                /* NO-OP */
            }

            is VoiceContract.Result.EnteredPrompt -> {
                viewModel.askQuestion(it.prompt)
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
        EdgeButton(
            onClick = {
                voiceLauncher.launch(voiceIntent)
            },
            buttonSize = EdgeButtonSize.ExtraSmall,
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = stringResource(R.string.prompt_input),
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
            promptDisplay = { model, modifier, spec ->
                ModelDisplay(model, modifier, spec)
            },
        )
    }
}

@Composable
private fun SampleTypography(): DefaultMarkdownTypography {
    val link = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline,
    )
    val text = MaterialTheme.typography.bodyMedium
    return DefaultMarkdownTypography(
        h1 = MaterialTheme.typography.titleLarge,
        h2 = MaterialTheme.typography.titleMedium,
        h3 = MaterialTheme.typography.titleSmall,
        h4 = MaterialTheme.typography.displayLarge,
        h5 = MaterialTheme.typography.displayMedium,
        h6 = MaterialTheme.typography.displaySmall,
        text = text,
        code = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        quote = MaterialTheme.typography.bodyMedium.plus(SpanStyle(fontStyle = FontStyle.Italic)),
        paragraph = MaterialTheme.typography.bodyLarge,
        ordered = MaterialTheme.typography.bodyLarge,
        bullet = MaterialTheme.typography.bodyLarge,
        list = MaterialTheme.typography.bodyLarge,
        link = link,
        inlineCode = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
        textLink = TextLinkStyles(style = link.toSpanStyle()),
        table = text,
    )
}

@Composable
private fun SampleColors() = DefaultMarkdownColors(
    text = Color.White,
    codeText = LocalContentColor.current,
    linkText = Color.Blue,
    codeBackground = MaterialTheme.colorScheme.background,
    inlineCodeBackground = MaterialTheme.colorScheme.background,
    dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    inlineCodeText = LocalContentColor.current,
    tableText = Color.Unspecified,
    tableBackground = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f),
)

@Composable
private fun ModelDisplay(
    model: PromptOrResponseUiModel,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
) {
    if (model is TextResponseUiModel) {
        SampleTextResponseCard(
            model,
            modifier = modifier,
            transformation = transformation,
        )
    } else {
        PromptOrResponseDisplay(
            promptResponse = model,
            modifier = modifier,
            transformation = transformation,
        )
    }
}

@Composable
public fun SampleTextResponseCard(
    textResponseUiModel: TextResponseUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    transformation: SurfaceTransformation? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surfaceContainer,
        ),
        transformation = transformation,
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
            EdgeButton(
                onClick = { },
                buttonSize = EdgeButtonSize.ExtraSmall,
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = "Ask Again",
                )
            }
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
            EdgeButton(
                onClick = { },
                buttonSize = EdgeButtonSize.ExtraSmall,
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = "Ask Again",
                )
            }
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
            EdgeButton(
                onClick = { },
                buttonSize = EdgeButtonSize.ExtraSmall,
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = "Ask Again",
                )
            }
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
            EdgeButton(
                onClick = { },
                buttonSize = EdgeButtonSize.ExtraSmall,
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = "Ask Again",
                )
            }
        },
    )
}

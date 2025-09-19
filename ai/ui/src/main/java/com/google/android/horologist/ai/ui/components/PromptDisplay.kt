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

package com.google.android.horologist.ai.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import com.google.android.horologist.ai.ui.model.TextPromptUiModel

/**
 * A component to display a Prompt.
 */
@Composable
public fun TextPromptDisplay(
    prompt: TextPromptUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    transformation: SurfaceTransformation? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {},
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        transformation = transformation,
    ) {
        Text(text = prompt.prompt)
    }
}

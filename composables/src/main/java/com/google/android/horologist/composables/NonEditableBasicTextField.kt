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

package com.google.android.horologist.composables

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

/**
 * This is a wrapper for [BasicTextField] that when clicked launches the keyboard. It cannot be
 * edited, but works as a helper to open the keyboard.
 */
@Composable
public fun NonEditableBasicTextField(
    modifier: Modifier = Modifier,
    onTextChanged: (value: String) -> Unit,
    value: TextFieldValue,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
) {
    val keyboardLauncher = rememberLauncherForActivityResult(
        KeyBoardContract(),
    ) {
        if (it is KeyBoardContract.Result.TextChanged) {
            onTextChanged(it.text)
        }
    }
    val keyboardIntent = Intent("com.google.android.wearable.action.LAUNCH_KEYBOARD")
    BasicTextField(
        modifier = modifier.clickable {
            keyboardLauncher.launch(keyboardIntent)
        },
        value = value,
        onValueChange = {},
        enabled = false,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = decorationBox,
    )
}

private class KeyBoardContract : ActivityResultContract<Intent, KeyBoardContract.Result>() {
    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): Result {
        return if (resultCode == RESULT_OK) {
            val resultText = intent?.extras?.getString("result_text")
            if (!resultText.isNullOrBlank()) {
                Result.TextChanged(resultText)
            } else {
                Result.Empty
            }
        } else {
            Result.Empty
        }
    }

    sealed class Result {
        data class TextChanged(val text: String) : Result()
        data object Empty : Result()
    }
}

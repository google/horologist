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

package com.google.android.horologist.compose.snackbar

import com.google.android.horologist.compose.navscaffold.ExperimentalComposeLayoutApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@ExperimentalComposeLayoutApi
public class SnackbarManager {
    private val _messages: MutableStateFlow<List<UiMessage>> = MutableStateFlow(emptyList())
    internal val messages: StateFlow<List<UiMessage>> get() = _messages.asStateFlow()

    public fun showMessage(message: UiMessage) {
        _messages.update { currentMessages -> currentMessages + message }
    }

    internal fun setMessageShown(messageId: String) {
        _messages.update { currentMessages -> currentMessages.filterNot { it.id == messageId } }
    }

    public fun showMessage(message: String) {
        showMessage(UiMessage(message = message))
    }
}

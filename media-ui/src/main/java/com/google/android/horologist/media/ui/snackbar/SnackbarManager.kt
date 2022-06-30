/*
 * Copyright 2022 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.media.ui.snackbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Snackbar Manager that coordinates between backend components producing messages for the user
 * and the SnackbarHost in the app Scaffold.
 */
class SnackbarManager {
    private val _messages: MutableStateFlow<List<UiMessage>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<UiMessage>> get() = _messages.asStateFlow()

    fun showMessage(message: UiMessage) {
        _messages.update { currentMessages -> currentMessages + message }
    }

    fun setMessageShown(messageId: String) {
        _messages.update { currentMessages -> currentMessages.filterNot { it.id == messageId } }
    }

    fun showMessage(message: String) {
        showMessage(UiMessage(message = message))
    }
}

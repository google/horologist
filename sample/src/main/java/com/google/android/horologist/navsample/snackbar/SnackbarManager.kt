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

package com.google.android.horologist.navsample.snackbar

import androidx.compose.runtime.mutableStateListOf
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi

/**
 * Coordination point for message producers and the single message consumer.
 */
@ExperimentalHorologistComposeLayoutApi
public class SnackbarManager {
    internal val messages = mutableStateListOf<UiMessage>()

    public fun showMessage(message: UiMessage) {
        messages.add(message)
    }

    internal fun setMessageShown(messageId: String) {
        messages.removeIf { it.id == messageId }
    }

    public fun showMessage(message: String) {
        showMessage(UiMessage(message = message))
    }
}

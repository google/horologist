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

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.snackbar.SnackbarDuration
import com.google.android.horologist.compose.snackbar.SnackbarHostState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * A ViewModel the maintainer the SnackbarHostState, and a reference to the Manager
 * for both processes snackbars sequentially and also showing a message.
 */
@ExperimentalHorologistComposeLayoutApi
public open class SnackbarViewModel(
    private val snackbarManager: SnackbarManager
) : ViewModel() {
    public val snackbarHostState: SnackbarHostState = SnackbarHostState()

    init {
        viewModelScope.launch {
            val messages = snapshotFlow { snackbarManager.messages.firstOrNull() }.filterNotNull()
            messages.collect { message ->
                snackbarHostState.showSnackbar(
                    message = message.message,
                    duration = SnackbarDuration.Short
                )
                snackbarManager.setMessageShown(message.id)
            }
        }
    }

    public fun showMessage(message: UiMessage) {
        snackbarManager.showMessage(message)
    }

    public fun showMessage(message: String) {
        snackbarManager.showMessage(message)
    }

    public object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            check(modelClass == SnackbarViewModel::class.java)

            val snackbarManager = SnackbarManager()
            return SnackbarViewModel(snackbarManager) as T
        }
    }
}

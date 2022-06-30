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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.snackbar.SnackbarDuration
import com.google.android.horologist.compose.snackbar.SnackbarHostState
import kotlinx.coroutines.launch

/**
 * A ViewModel the maintainer the SnackbarHostState, and a reference to the Manager
 * for both processes snackbars sequentially and also showing a message.
 */
@OptIn(ExperimentalHorologistComposeLayoutApi::class)
open class SnackbarViewModel(
    val snackbarManager: SnackbarManager,
) : ViewModel() {
    val snackbarHostState = SnackbarHostState()

    init {
        viewModelScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                currentMessages.firstOrNull()?.let {
                    snackbarHostState.showSnackbar(
                        message = it.message,
                        duration = SnackbarDuration.Short
                    )
                    snackbarManager.setMessageShown(it.id)
                }
            }
        }
    }

    companion object {
        val SnackbarManagerKey = object : CreationExtras.Key<SnackbarManager> {}

        val Factory = viewModelFactory {
            initializer {
                SnackbarViewModel(
                    snackbarManager = this[SnackbarManagerKey]!!,
                )
            }
        }
    }
}

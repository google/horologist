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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Scaffold
import com.google.android.horologist.compose.navscaffold.ExperimentalComposeLayoutApi
import com.google.android.horologist.compose.snackbar.material.SnackbarDuration
import com.google.android.horologist.compose.snackbar.material.SnackbarHostState
import kotlinx.coroutines.launch

/**
 * A ViewModel that backs the WearNavScaffold to allow each composable to interact and effect
 * the [Scaffold] positionIndicator, vignette and timeText.
 *
 * A ViewModel is used to allow the same current instance to be shared between the WearNavScaffold
 * and the composable screen via [NavHostController.currentBackStackEntry].
 */
@ExperimentalComposeLayoutApi
public open class SnackbarViewModel(
    private val snackbarManager: SnackbarManager
) : ViewModel() {
    public val snackbarHostState = SnackbarHostState()

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

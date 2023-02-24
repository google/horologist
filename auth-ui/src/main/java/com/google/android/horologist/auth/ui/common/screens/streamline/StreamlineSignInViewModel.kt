/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.auth.ui.common.screens.streamline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.data.common.repository.StreamlineAccountRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.ext.compareAndSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A view model for the default implementation of a streamline sign-in screen.
 *
 * It checks if there is a user already signed in, and emits the appropriate
 * [states][StreamlineSignInScreenState] through the [uiState] property.
 */
@ExperimentalHorologistAuthUiApi
public class StreamlineSignInViewModel<DomainModel, UiModel>(
    private val streamlineAccountRepository: StreamlineAccountRepository<DomainModel>,
    private val uiModelMapper: (DomainModel) -> UiModel
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<StreamlineSignInScreenState<UiModel>>(StreamlineSignInScreenState.Idle)
    public val uiState: StateFlow<StreamlineSignInScreenState<UiModel>> = _uiState

    /**
     * Indicate that the screen has observed the [idle][StreamlineSignInScreenState.Idle]
     * state and that the view model can start its work.
     */
    public fun onIdleStateObserved() {
        _uiState.compareAndSet(
            expect = StreamlineSignInScreenState.Idle,
            update = StreamlineSignInScreenState.Loading
        ) {
            viewModelScope.launch {
                val accounts = streamlineAccountRepository.getAvailable()

                when {
                    accounts.isEmpty() -> {
                        _uiState.value = StreamlineSignInScreenState.NoAccountsAvailable
                    }

                    accounts.size == 1 -> {
                        _uiState.value =
                            StreamlineSignInScreenState.SignedIn(
                                uiModelMapper(accounts.first())
                            )
                    }

                    else -> {
                        _uiState.value =
                            StreamlineSignInScreenState.MultipleAccountsAvailable(
                                accounts.map(uiModelMapper)
                            )
                    }
                }
            }
        }
    }

    public fun onAccountSelected(account: UiModel) {
        _uiState.value = StreamlineSignInScreenState.SignedIn(account)
    }

    public companion object {

        /**
         * Factory function for creating an instance of this view model using a single model for
         * domain and UI.
         */
        public fun <T> createForSingleModel(
            streamlineAccountRepository: StreamlineAccountRepository<T>
        ): StreamlineSignInViewModel<T, T> =
            StreamlineSignInViewModel(
                streamlineAccountRepository = streamlineAccountRepository,
                uiModelMapper = { model -> model }
            )
    }
}

@ExperimentalHorologistAuthUiApi
public sealed class StreamlineSignInScreenState<out T> {

    public object Idle : StreamlineSignInScreenState<Nothing>()

    public object Loading : StreamlineSignInScreenState<Nothing>()

    public data class SignedIn<T>(val account: T) : StreamlineSignInScreenState<T>()

    public data class MultipleAccountsAvailable<T>(val accounts: List<T>) :
        StreamlineSignInScreenState<T>()

    public object NoAccountsAvailable : StreamlineSignInScreenState<Nothing>()
}

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

package com.google.android.horologist.auth.ui.common.screens.streamline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.ext.compareAndSet
import com.google.android.horologist.auth.ui.mapper.AccountUiModelMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A view model for a streamline sign-in screen.
 *
 * It checks if there is a user already signed in, and emits the appropriate
 * [states][StreamlineSignInScreenState] through the [uiState] property.
 */
@ExperimentalHorologistAuthUiApi
public open class StreamlineSignInViewModel(
    private val authUserRepository: AuthUserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<StreamlineSignInScreenState>(StreamlineSignInScreenState.Idle)
    public val uiState: StateFlow<StreamlineSignInScreenState> = _uiState

    /**
     * Indicate that the screen has observed the [idle][StreamlineSignInScreenState.Idle] state and
     * that the view model can start its work.
     */
    public fun onIdleStateObserved() {
        _uiState.compareAndSet(
            expect = StreamlineSignInScreenState.Idle,
            update = StreamlineSignInScreenState.Loading
        ) {
            viewModelScope.launch {
                val authUsers = authUserRepository.getAvailable()

                _uiState.value = when {
                    authUsers.isEmpty() -> {
                        StreamlineSignInScreenState.NoAccountsAvailable
                    }

                    authUsers.size == 1 -> {
                        StreamlineSignInScreenState.SingleAccountAvailable(
                            AccountUiModelMapper.map(authUsers.first())
                        )
                    }

                    else -> {
                        StreamlineSignInScreenState.MultipleAccountsAvailable(
                            authUsers.map(AccountUiModelMapper::map)
                        )
                    }
                }
            }
        }
    }
}

/**
 * The states for a streamline sign-in screen.
 */
@ExperimentalHorologistAuthUiApi
public sealed class StreamlineSignInScreenState {

    public object Idle : StreamlineSignInScreenState()

    public object Loading : StreamlineSignInScreenState()

    public data class SingleAccountAvailable(
        val account: AccountUiModel
    ) : StreamlineSignInScreenState()

    public data class MultipleAccountsAvailable(
        val accounts: List<AccountUiModel>
    ) : StreamlineSignInScreenState()

    public object NoAccountsAvailable : StreamlineSignInScreenState()
}

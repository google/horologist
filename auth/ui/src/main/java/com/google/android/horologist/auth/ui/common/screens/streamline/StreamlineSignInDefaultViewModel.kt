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
import com.google.android.horologist.auth.composables.model.AccountUiModel
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.ui.ext.compareAndSet
import com.google.android.horologist.auth.ui.mapper.AccountUiModelMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A view model for the default implementation of a streamline sign-in screen.
 *
 * It checks if there is a user already signed in, and emits the appropriate
 * [states][StreamlineSignInDefaultScreenState] through the [uiState] property.
 *
 * @sample com.google.android.horologist.auth.sample.screens.common.streamline.StreamlineSignInSampleScreen
 */
public class StreamlineSignInDefaultViewModel(
    private val authUserRepository: AuthUserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<StreamlineSignInDefaultScreenState>(StreamlineSignInDefaultScreenState.Idle)
    public val uiState: StateFlow<StreamlineSignInDefaultScreenState> = _uiState

    /**
     * Indicate that the screen has observed the [idle][StreamlineSignInDefaultScreenState.Idle]
     * state and that the view model can start its work.
     */
    public fun onIdleStateObserved() {
        _uiState.compareAndSet(
            expect = StreamlineSignInDefaultScreenState.Idle,
            update = StreamlineSignInDefaultScreenState.Loading
        ) {
            viewModelScope.launch {
                val authUsers = authUserRepository.getAvailable()

                when {
                    authUsers.isEmpty() -> {
                        _uiState.value = StreamlineSignInDefaultScreenState.NoAccountsAvailable
                    }

                    authUsers.size == 1 -> {
                        _uiState.value = StreamlineSignInDefaultScreenState.SignedIn(
                            AccountUiModelMapper.map(authUsers.first())
                        )
                    }

                    else -> {
                        _uiState.value =
                            StreamlineSignInDefaultScreenState.MultipleAccountsAvailable(
                                authUsers.map(AccountUiModelMapper::map)
                            )
                    }
                }
            }
        }
    }

    public fun onAccountSelected(account: AccountUiModel) {
        _uiState.value = StreamlineSignInDefaultScreenState.SignedIn(account)
    }
}

public sealed class StreamlineSignInDefaultScreenState {

    public object Idle : StreamlineSignInDefaultScreenState()

    public object Loading : StreamlineSignInDefaultScreenState()

    public data class SignedIn(val account: AccountUiModel) : StreamlineSignInDefaultScreenState()

    public data class MultipleAccountsAvailable(val accounts: List<AccountUiModel>) :
        StreamlineSignInDefaultScreenState()

    public object NoAccountsAvailable : StreamlineSignInDefaultScreenState()
}

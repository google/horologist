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

package com.google.android.horologist.auth.ui.pkce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.phone.interactions.authentication.CodeVerifier
import com.google.android.horologist.auth.data.pkce.AuthPKCEConfigRepository
import com.google.android.horologist.auth.data.pkce.AuthPKCEOAuthCodeRepository
import com.google.android.horologist.auth.data.pkce.AuthPKCETokenPayloadListener
import com.google.android.horologist.auth.data.pkce.AuthPKCETokenPayloadListenerNoOpImpl
import com.google.android.horologist.auth.data.pkce.AuthPKCETokenRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ExperimentalHorologistAuthUiApi
public open class AuthPKCEViewModel<AuthPKCEConfig, OAuthCodePayload, TokenPayload>(
    private val authPKCEConfigRepository: AuthPKCEConfigRepository<AuthPKCEConfig>,
    private val authPKCEOAuthCodeRepository: AuthPKCEOAuthCodeRepository<AuthPKCEConfig, OAuthCodePayload>,
    private val authPKCETokenRepository: AuthPKCETokenRepository<AuthPKCEConfig, OAuthCodePayload, TokenPayload>,
    private val authPKCETokenPayloadListener: AuthPKCETokenPayloadListener<TokenPayload> = AuthPKCETokenPayloadListenerNoOpImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthPKCEScreenState>(AuthPKCEScreenState.Idle)
    public val uiState: StateFlow<AuthPKCEScreenState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = AuthPKCEScreenState.Idle
    )

    public fun startAuthFlow() {
        viewModelScope.launch {
            _uiState.value = AuthPKCEScreenState.Loading

            val config = authPKCEConfigRepository.fetch()

            val codeVerifier = CodeVerifier()

            // Step 1: Retrieve the OAuth code
            _uiState.value = AuthPKCEScreenState.CheckPhone
            val oAuthCodePayload = authPKCEOAuthCodeRepository.fetch(
                config = config,
                codeVerifier = codeVerifier
            ).getOrElse {
                _uiState.value = AuthPKCEScreenState.Failed
                return@launch
            }

            // Step 2: Retrieve the access token
            _uiState.value = AuthPKCEScreenState.Loading
            val tokenPayload = authPKCETokenRepository.fetch(
                config = config,
                codeVerifier = codeVerifier.value,
                oAuthCodePayload = oAuthCodePayload
            )
                .getOrElse {
                    _uiState.value = AuthPKCEScreenState.Failed
                    return@launch
                }
            authPKCETokenPayloadListener.onPayloadReceived(tokenPayload)

            _uiState.value = AuthPKCEScreenState.Success
        }
    }
}

@ExperimentalHorologistAuthUiApi
public sealed class AuthPKCEScreenState {
    public object Idle : AuthPKCEScreenState()
    public object Loading : AuthPKCEScreenState()
    public object CheckPhone : AuthPKCEScreenState()
    public object Success : AuthPKCEScreenState()
    public object Failed : AuthPKCEScreenState()
}

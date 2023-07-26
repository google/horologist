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

package com.google.android.horologist.auth.ui.oauth.pkce.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.phone.interactions.authentication.CodeVerifier
import com.google.android.horologist.auth.data.oauth.pkce.PKCEConfigRepository
import com.google.android.horologist.auth.data.oauth.pkce.PKCEOAuthCodeRepository
import com.google.android.horologist.auth.data.oauth.pkce.PKCETokenPayloadListener
import com.google.android.horologist.auth.data.oauth.pkce.PKCETokenPayloadListenerNoOpImpl
import com.google.android.horologist.auth.data.oauth.pkce.PKCETokenRepository
import com.google.android.horologist.auth.ui.ext.compareAndSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

public open class PKCESignInViewModel<PKCEConfig, OAuthCodePayload, TokenPayload>(
    private val pkceConfigRepository: PKCEConfigRepository<PKCEConfig>,
    private val pkceOAuthCodeRepository: PKCEOAuthCodeRepository<PKCEConfig, OAuthCodePayload>,
    private val pkceTokenRepository: PKCETokenRepository<PKCEConfig, OAuthCodePayload, TokenPayload>,
    private val pkceTokenPayloadListener: PKCETokenPayloadListener<TokenPayload> = PKCETokenPayloadListenerNoOpImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<PKCEScreenState>(PKCEScreenState.Idle)
    public val uiState: StateFlow<PKCEScreenState> = _uiState

    public fun startAuthFlow() {
        _uiState.compareAndSet(
            expect = PKCEScreenState.Idle,
            update = PKCEScreenState.Loading
        ) {
            viewModelScope.launch {
                val config = pkceConfigRepository.fetch()

                val codeVerifier = CodeVerifier()

                // Step 1: Retrieve the OAuth code
                _uiState.value = PKCEScreenState.CheckPhone
                val oAuthCodePayload = pkceOAuthCodeRepository.fetch(
                    config = config,
                    codeVerifier = codeVerifier
                ).getOrElse {
                    _uiState.value = PKCEScreenState.Failed
                    return@launch
                }

                // Step 2: Retrieve the access token
                _uiState.value = PKCEScreenState.Loading
                val tokenPayload = pkceTokenRepository.fetch(
                    config = config,
                    codeVerifier = codeVerifier.value,
                    oAuthCodePayload = oAuthCodePayload
                )
                    .getOrElse {
                        _uiState.value = PKCEScreenState.Failed
                        return@launch
                    }
                pkceTokenPayloadListener.onPayloadReceived(tokenPayload)

                _uiState.value = PKCEScreenState.Success
            }
        }
    }
}

public sealed class PKCEScreenState {
    public object Idle : PKCEScreenState()
    public object Loading : PKCEScreenState()
    public object CheckPhone : PKCEScreenState()
    public object Success : PKCEScreenState()
    public object Failed : PKCEScreenState()
}

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

package com.google.android.horologist.auth.ui.oauth.devicegrant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.data.oauth.devicegrant.AuthDeviceGrantConfigRepository
import com.google.android.horologist.auth.data.oauth.devicegrant.AuthDeviceGrantTokenPayloadListener
import com.google.android.horologist.auth.data.oauth.devicegrant.AuthDeviceGrantTokenPayloadListenerNoOpImpl
import com.google.android.horologist.auth.data.oauth.devicegrant.AuthDeviceGrantTokenRepository
import com.google.android.horologist.auth.data.oauth.devicegrant.AuthDeviceGrantVerificationInfoRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ExperimentalHorologistAuthUiApi
public open class AuthDeviceGrantViewModel<AuthDeviceGrantConfig, VerificationInfoPayload, TokenPayload>(
    private val authDeviceGrantConfigRepository: AuthDeviceGrantConfigRepository<AuthDeviceGrantConfig>,
    private val authDeviceGrantVerificationInfoRepository: AuthDeviceGrantVerificationInfoRepository<AuthDeviceGrantConfig, VerificationInfoPayload>,
    private val authDeviceGrantTokenRepository: AuthDeviceGrantTokenRepository<AuthDeviceGrantConfig, VerificationInfoPayload, TokenPayload>,
    private val checkPhonePayloadMapper: (AuthDeviceGrantConfig, VerificationInfoPayload) -> String,
    private val authDeviceGrantTokenPayloadListener: AuthDeviceGrantTokenPayloadListener<TokenPayload> = AuthDeviceGrantTokenPayloadListenerNoOpImpl()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AuthDeviceGrantScreenState>(AuthDeviceGrantScreenState.Idle)
    public val uiState: StateFlow<AuthDeviceGrantScreenState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = AuthDeviceGrantScreenState.Idle
    )

    public fun startAuthFlow() {
        viewModelScope.launch {
            _uiState.value = AuthDeviceGrantScreenState.Loading

            val config = authDeviceGrantConfigRepository.fetch()

            // Step 1: Retrieve the verification URI
            val verificationInfoPayload =
                authDeviceGrantVerificationInfoRepository.fetch(config).getOrElse {
                    _uiState.value = AuthDeviceGrantScreenState.Failed
                    return@launch
                }

            // Step 2: Show the pairing code & open the verification URI on the paired device
            // Step 3: Poll the Auth server for the token
            _uiState.value = AuthDeviceGrantScreenState.CheckPhone(
                checkPhonePayloadMapper(config, verificationInfoPayload)
            )
            val tokenPayload = authDeviceGrantTokenRepository.fetch(config, verificationInfoPayload)
                .getOrElse {
                    _uiState.value = AuthDeviceGrantScreenState.Failed
                    return@launch
                }
            authDeviceGrantTokenPayloadListener.onPayloadReceived(tokenPayload)

            _uiState.value = AuthDeviceGrantScreenState.Success
        }
    }
}

@ExperimentalHorologistAuthUiApi
public sealed class AuthDeviceGrantScreenState {
    public object Idle : AuthDeviceGrantScreenState()
    public object Loading : AuthDeviceGrantScreenState()
    public data class CheckPhone(val code: String) : AuthDeviceGrantScreenState()
    public object Success : AuthDeviceGrantScreenState()
    public object Failed : AuthDeviceGrantScreenState()
}

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

package com.google.android.horologist.auth.ui.oauth.devicegrant.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.data.oauth.devicegrant.DeviceGrantConfigRepository
import com.google.android.horologist.auth.data.oauth.devicegrant.DeviceGrantTokenPayloadListener
import com.google.android.horologist.auth.data.oauth.devicegrant.DeviceGrantTokenPayloadListenerNoOpImpl
import com.google.android.horologist.auth.data.oauth.devicegrant.DeviceGrantTokenRepository
import com.google.android.horologist.auth.data.oauth.devicegrant.DeviceGrantVerificationInfoRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.ext.compareAndSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ExperimentalHorologistAuthUiApi
public open class DeviceGrantViewModel<DeviceGrantConfig, VerificationInfoPayload, TokenPayload>(
    private val deviceGrantConfigRepository: DeviceGrantConfigRepository<DeviceGrantConfig>,
    private val deviceGrantVerificationInfoRepository: DeviceGrantVerificationInfoRepository<DeviceGrantConfig, VerificationInfoPayload>,
    private val deviceGrantTokenRepository: DeviceGrantTokenRepository<DeviceGrantConfig, VerificationInfoPayload, TokenPayload>,
    private val checkPhonePayloadMapper: (DeviceGrantConfig, VerificationInfoPayload) -> String,
    private val deviceGrantTokenPayloadListener: DeviceGrantTokenPayloadListener<TokenPayload> = DeviceGrantTokenPayloadListenerNoOpImpl()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<DeviceGrantScreenState>(DeviceGrantScreenState.Idle)
    public val uiState: StateFlow<DeviceGrantScreenState> = _uiState

    public fun startAuthFlow() {
        _uiState.compareAndSet(
            expect = DeviceGrantScreenState.Idle,
            update = DeviceGrantScreenState.Loading
        ) {
            viewModelScope.launch {
                val config = deviceGrantConfigRepository.fetch()

                // Step 1: Retrieve the verification URI
                val verificationInfoPayload =
                    deviceGrantVerificationInfoRepository.fetch(config).getOrElse {
                        _uiState.value = DeviceGrantScreenState.Failed
                        return@launch
                    }

                // Step 2: Show the pairing code & open the verification URI on the paired device
                // Step 3: Poll the Auth server for the token
                _uiState.value = DeviceGrantScreenState.CheckPhone(
                    checkPhonePayloadMapper(config, verificationInfoPayload)
                )
                val tokenPayload =
                    deviceGrantTokenRepository.fetch(config, verificationInfoPayload)
                        .getOrElse {
                            _uiState.value = DeviceGrantScreenState.Failed
                            return@launch
                        }
                deviceGrantTokenPayloadListener.onPayloadReceived(tokenPayload)

                _uiState.value = DeviceGrantScreenState.Success
            }
        }
    }
}

@ExperimentalHorologistAuthUiApi
public sealed class DeviceGrantScreenState {
    public object Idle : DeviceGrantScreenState()
    public object Loading : DeviceGrantScreenState()
    public data class CheckPhone(val code: String) : DeviceGrantScreenState()
    public object Success : DeviceGrantScreenState()
    public object Failed : DeviceGrantScreenState()
}

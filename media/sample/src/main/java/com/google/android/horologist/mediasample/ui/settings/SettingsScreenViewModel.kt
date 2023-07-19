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

package com.google.android.horologist.mediasample.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.mediasample.BuildConfig
import com.google.android.horologist.mediasample.data.auth.GoogleSignInAuthUserRepository
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.proto.copy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authUserRepository: GoogleSignInAuthUserRepository,
) : ViewModel() {
    val screenState = combine(
        settingsRepository.settingsFlow,
        authUserRepository.authState,
    ) { settings, authState ->
        SettingsScreenState(
            authUser = authState,
            guestMode = settings.guestMode,
            writable = true,
            showDeveloperOptions = BuildConfig.DEBUG,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SettingsScreenState(
            authUser = null,
            guestMode = false,
            writable = false,
            showDeveloperOptions = BuildConfig.DEBUG,
        ),
    )

    fun setGuestMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.edit {
                it.copy { guestMode = enabled }
            }
            if (enabled) {
                authUserRepository.signOut()
            }
        }
    }
}

data class SettingsScreenState(
    val authUser: AuthUser?,
    val guestMode: Boolean,
    val writable: Boolean,
    val showDeveloperOptions: Boolean,
)

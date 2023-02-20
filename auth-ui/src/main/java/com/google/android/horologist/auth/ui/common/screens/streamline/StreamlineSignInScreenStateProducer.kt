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

import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.ui.ExperimentalHorologistAuthUiApi
import com.google.android.horologist.auth.ui.mapper.AccountUiModelMapper

@OptIn(ExperimentalHorologistAuthUiApi::class)
internal object StreamlineSignInScreenStateProducer {

    suspend fun onIdleStateObserved(
        authUserRepository: AuthUserRepository
    ): StreamlineSignInScreenState {
        val authUsers = authUserRepository.getAvailable()

        return when {
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

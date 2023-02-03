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

package com.google.android.horologist.mediasample.domain

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import com.google.android.horologist.auth.data.googlesignin.GoogleSignInEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


class StatefulAuthUserRepository @Inject constructor(
    private val authUserRepository: AuthUserRepository
): AuthUserRepository, GoogleSignInEventListener {
    private val _authUser = MutableStateFlow<AuthUser?>(null)
    val authUser: StateFlow<AuthUser?> = _authUser

    override suspend fun getAuthenticated(): AuthUser? {
        return authUser.value
    }

    override suspend fun onSignedIn(account: GoogleSignInAccount) {
        TODO("Not yet implemented")
    }


}
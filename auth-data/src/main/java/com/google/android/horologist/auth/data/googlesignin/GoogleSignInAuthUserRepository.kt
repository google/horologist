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

package com.google.android.horologist.auth.data.googlesignin

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi
import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An implementation of [AuthUserRepository] for the Google Sign-In authentication method.
 */
@ExperimentalHorologistAuthDataApi
public class GoogleSignInAuthUserRepository(
    private val applicationContext: Context
) : AuthUserRepository, GoogleSignInEventListener {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)

    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun getAuthenticated(): AuthUser? {
        val user = _authState.value.toAuthUser()

        if (user != null) {
            return user
        }

        val account = getAuthenticatedGoogleSignInAccount()
        val newAuthState = AuthState.of(account)
        _authState.value = newAuthState

        return newAuthState.toAuthUser()
    }

    override suspend fun onSignedIn(account: GoogleSignInAccount) {
        _authState.value = AuthState.LoggedIn(account)
    }

    fun getAuthenticatedGoogleSignInAccount(): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(applicationContext)

    sealed interface AuthState {
        fun toAuthUser(): AuthUser? = null

        object Uninitialized: AuthState
        object NotLoggedIn: AuthState
        data class LoggedIn(val authUser: GoogleSignInAccount): AuthState {
            override fun toAuthUser(): AuthUser = AuthUserMapper.map(authUser)!!
        }

        companion object {
            fun of(account: GoogleSignInAccount?): AuthState = if (account != null) {
                LoggedIn(account)
            } else {
                NotLoggedIn
            }
        }
    }
}

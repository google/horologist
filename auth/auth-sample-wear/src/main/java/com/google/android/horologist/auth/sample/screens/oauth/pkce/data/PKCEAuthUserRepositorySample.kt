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

package com.google.android.horologist.auth.sample.screens.oauth.pkce.data

import com.google.android.horologist.auth.data.common.model.AuthUser
import com.google.android.horologist.auth.data.common.repository.AuthUserRepository

/**
 * Sample in-memory implementation of [AuthUserRepository].
 */
object PKCEAuthUserRepositorySample : AuthUserRepository {

    private var authUser: AuthUser? = null

    override suspend fun getAuthenticated(): AuthUser? = authUser

    fun setAuthenticated(authUser: AuthUser) {
        this.authUser = authUser
    }

    fun clearAuthenticated() {
        authUser = null
    }
}

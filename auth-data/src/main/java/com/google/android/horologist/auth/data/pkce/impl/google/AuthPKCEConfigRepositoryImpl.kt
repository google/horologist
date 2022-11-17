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

package com.google.android.horologist.auth.data.pkce.impl.google

import android.net.Uri
import com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi
import com.google.android.horologist.auth.data.pkce.AuthPKCEConfigRepository
import com.google.android.horologist.auth.data.pkce.impl.AuthPKCEDefaultConfig

@ExperimentalHorologistAuthDataApi
public class AuthPKCEConfigRepositoryImpl(
    private val clientId: String,
    private val clientSecret: String,
    private val encodedPath: String = ENCODED_PATH,
    private val queryParameters: Map<String, String> = mapOf(QUERY_PARAM_SCOPE_KEY to QUERY_PARAM_SCOPE_VALUE)
) : AuthPKCEConfigRepository<AuthPKCEDefaultConfig> {

    override suspend fun fetch(): AuthPKCEDefaultConfig {
        val uri = Uri.Builder()
            .encodedPath(encodedPath)
            .also { builder ->
                queryParameters.forEach { (key, value) ->
                    builder.appendQueryParameter(key, value)
                }
            }
            .build()

        return AuthPKCEDefaultConfig(
            clientId = clientId,
            clientSecret = clientSecret,
            authProviderUrl = uri
        )
    }

    public companion object {
        public const val ENCODED_PATH: String = "https://accounts.google.com/o/oauth2/v2/auth"
        public const val QUERY_PARAM_SCOPE_KEY: String = "scope"
        public const val QUERY_PARAM_SCOPE_VALUE: String =
            "https://www.googleapis.com/auth/userinfo.profile"
    }
}

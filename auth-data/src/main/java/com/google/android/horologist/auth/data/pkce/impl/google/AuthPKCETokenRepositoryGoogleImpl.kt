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

import android.util.Log
import com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi
import com.google.android.horologist.auth.data.pkce.AuthPKCETokenRepository
import com.google.android.horologist.auth.data.pkce.impl.AuthPKCEDefaultConfig
import com.google.android.horologist.auth.data.pkce.impl.google.api.GoogleOAuthService
import com.google.android.horologist.auth.data.pkce.impl.google.api.GoogleOAuthService.Companion.REQUEST_GRANT_TYPE_PARAM_VALUE
import com.google.android.horologist.auth.data.pkce.impl.google.api.TokenResponse
import kotlinx.coroutines.CancellationException

@ExperimentalHorologistAuthDataApi
public class AuthPKCETokenRepositoryGoogleImpl(
    private val googleOAuthService: GoogleOAuthService
) : AuthPKCETokenRepository<AuthPKCEDefaultConfig, AuthPKCEOAuthCodeGooglePayload, TokenResponse> {

    override suspend fun fetch(
        config: AuthPKCEDefaultConfig,
        codeVerifier: String,
        oAuthCodePayload: AuthPKCEOAuthCodeGooglePayload
    ): Result<TokenResponse> {
        Log.d(TAG, "Requesting token...")

        return try {
            val response = googleOAuthService.token(
                clientId = config.clientId,
                clientSecret = config.clientSecret,
                code = oAuthCodePayload.code,
                codeVerifier = codeVerifier,
                grantType = REQUEST_GRANT_TYPE_PARAM_VALUE,
                redirectUri = oAuthCodePayload.redirectUrl
            )

            Result.success(response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private companion object {
        private val TAG = AuthPKCETokenRepositoryGoogleImpl::class.java.simpleName
    }
}

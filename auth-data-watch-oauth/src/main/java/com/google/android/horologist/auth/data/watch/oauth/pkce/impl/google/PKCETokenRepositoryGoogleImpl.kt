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

package com.google.android.horologist.auth.data.watch.oauth.pkce.impl.google

import android.util.Log
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.data.oauth.pkce.PKCETokenRepository
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService.Companion.GRANT_TYPE_PARAM_AUTH_CODE_GRANT_VALUE
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.TokenResponse
import com.google.android.horologist.auth.data.watch.oauth.common.logging.TAG
import com.google.android.horologist.auth.data.watch.oauth.pkce.impl.PKCEDefaultConfig
import kotlinx.coroutines.CancellationException

@ExperimentalHorologistApi
public class PKCETokenRepositoryGoogleImpl(
    private val googleOAuthService: GoogleOAuthService
) : PKCETokenRepository<PKCEDefaultConfig, PKCEOAuthCodeGooglePayload, TokenResponse> {

    override suspend fun fetch(
        config: PKCEDefaultConfig,
        codeVerifier: String,
        oAuthCodePayload: PKCEOAuthCodeGooglePayload
    ): Result<TokenResponse> {
        Log.d(TAG, "Requesting token...")

        return try {
            val response = googleOAuthService.token(
                clientId = config.clientId,
                clientSecret = config.clientSecret,
                code = oAuthCodePayload.code,
                codeVerifier = codeVerifier,
                grantType = GRANT_TYPE_PARAM_AUTH_CODE_GRANT_VALUE,
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
}

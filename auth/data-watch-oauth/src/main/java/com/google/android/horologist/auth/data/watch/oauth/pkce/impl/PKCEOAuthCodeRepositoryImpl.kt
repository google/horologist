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

package com.google.android.horologist.auth.data.watch.oauth.pkce.impl

import android.app.Application
import android.util.Log
import androidx.wear.phone.interactions.authentication.CodeChallenge
import androidx.wear.phone.interactions.authentication.CodeVerifier
import androidx.wear.phone.interactions.authentication.OAuthRequest
import androidx.wear.phone.interactions.authentication.OAuthResponse
import androidx.wear.phone.interactions.authentication.RemoteAuthClient
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.data.oauth.pkce.PKCEOAuthCodeRepository
import com.google.android.horologist.auth.data.watch.oauth.common.logging.TAG
import com.google.android.horologist.auth.data.watch.oauth.pkce.impl.google.PKCEOAuthCodeGooglePayload
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

public class PKCEOAuthCodeRepositoryImpl(
    private val application: Application
) : PKCEOAuthCodeRepository<PKCEDefaultConfig, PKCEOAuthCodeGooglePayload> {

    /**
     * Start the authentication flow and do an authenticated request. This method implements
     * the steps described at
     * https://d.google.com/identity/protocols/oauth2/native-app#obtainingaccesstokens
     *
     * The [androidx.wear.phone.interactions.authentication] package helps with this implementation.
     * It can generate a code verifier and challenge, and helps to move the consent step to
     * the phone. After the user consents on their phone, the wearable app is notified and can
     * continue the authorization process.
     */
    override suspend fun fetch(
        config: PKCEDefaultConfig,
        codeVerifier: CodeVerifier
    ): Result<PKCEOAuthCodeGooglePayload> {
        val oauthRequest = OAuthRequest.Builder(application)
            .setAuthProviderUrl(config.authProviderUrl)
            .setCodeChallenge(CodeChallenge(codeVerifier))
            .setClientId(config.clientId)
            .also { builder -> config.redirectUrl?.let(builder::setRedirectUrl) }
            .build()

        Log.d(TAG, "Authorization requested. Request URL: ${oauthRequest.requestUrl}")

        // Wrap the callback-based request inside a coroutine wrapper
        return suspendCancellableCoroutine { continuation ->
            RemoteAuthClient.create(application).sendAuthorizationRequest(
                request = oauthRequest,
                executor = { command -> command?.run() },
                clientCallback = RemoteAuthClientCallback(oauthRequest, continuation)
            )
        }
    }

    private class RemoteAuthClientCallback(
        private val oauthRequest: OAuthRequest,
        private val continuation: Continuation<Result<PKCEOAuthCodeGooglePayload>>
    ) : RemoteAuthClient.Callback() {
        override fun onAuthorizationError(request: OAuthRequest, errorCode: Int) {
            Log.w(TAG, "Authorization failed with errorCode $errorCode")
            continuation.resume(Result.failure(IOException("Authorization failed [$errorCode]")))
        }

        override fun onAuthorizationResponse(
            request: OAuthRequest,
            response: OAuthResponse
        ) {
            val responseUrl = response.responseUrl
            Log.d(TAG, "Authorization success. ResponseUrl: $responseUrl")
            val code = responseUrl?.getQueryParameter("code")
            if (code.isNullOrBlank()) {
                Log.w(
                    TAG,
                    "Google OAuth 2.0 API token exchange failed. No code query parameter in response URL."
                )
                continuation.resume(Result.failure(IOException("Authorization failed")))
            } else {
                continuation.resume(
                    Result.success(
                        PKCEOAuthCodeGooglePayload(code, oauthRequest.redirectUrl)
                    )
                )
            }
        }
    }
}

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

package com.google.android.horologist.auth.data.oauth.devicegrant.impl.google

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi
import com.google.android.horologist.auth.data.oauth.common.impl.google.api.DeviceCodeResponse
import com.google.android.horologist.auth.data.oauth.common.impl.google.api.GoogleOAuthService
import com.google.android.horologist.auth.data.oauth.common.impl.google.api.GoogleOAuthService.Companion.GRANT_TYPE_PARAM_AUTH_DEVICE_GRANT_VALUE
import com.google.android.horologist.auth.data.oauth.devicegrant.AuthDeviceGrantTokenRepository
import com.google.android.horologist.auth.data.oauth.devicegrant.impl.AuthDeviceGrantDefaultConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@ExperimentalHorologistAuthDataApi
public class AuthDeviceGrantTokenRepositoryGoogleImpl(
    private val application: Application,
    private val googleOAuthService: GoogleOAuthService
) : AuthDeviceGrantTokenRepository<AuthDeviceGrantDefaultConfig, DeviceCodeResponse, String> {

    override suspend fun fetch(
        config: AuthDeviceGrantDefaultConfig,
        verificationInfoPayload: DeviceCodeResponse
    ): Result<String> {
        RemoteActivityHelper(application).startRemoteActivity(
            Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = Uri.parse(verificationInfoPayload.verificationUri)
            },
            null
        )

        return Result.success(
            retrieveToken(
                config,
                verificationInfoPayload.deviceCode,
                verificationInfoPayload.interval
            )
        )
    }

    /**
     * Poll the Auth server for the token. This will only return when the user has finished their
     * authorization flow on the paired device.
     *
     * For this sample the various exceptions aren't handled.
     */
    private tailrec suspend fun retrieveToken(
        config: AuthDeviceGrantDefaultConfig,
        deviceCode: String,
        interval: Int
    ): String {
        Log.d(TAG, "Polling for token...")
        return fetchToken(config, deviceCode).getOrElse {
            Log.d(TAG, "No token yet. Waiting...")
            delay(interval * 1000L)
            return retrieveToken(config, deviceCode, interval)
        }
    }

    private suspend fun fetchToken(
        config: AuthDeviceGrantDefaultConfig,
        deviceCode: String
    ): Result<String> {
        return try {
            val tokenResponse = googleOAuthService.token(
                clientId = config.clientId,
                clientSecret = config.clientSecret,
                grantType = GRANT_TYPE_PARAM_AUTH_DEVICE_GRANT_VALUE,
                deviceCode = deviceCode
            )

            Result.success(tokenResponse.accessToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private companion object {
        private val TAG = AuthDeviceGrantTokenRepositoryGoogleImpl::class.java.simpleName
    }
}

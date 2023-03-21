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

package com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.google

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.horologist.auth.data.oauth.devicegrant.DeviceGrantTokenRepository
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.DeviceCodeResponse
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService.Companion.GRANT_TYPE_PARAM_AUTH_DEVICE_GRANT_VALUE
import com.google.android.horologist.auth.data.watch.oauth.common.logging.TAG
import com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.DeviceGrantDefaultConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await

@ExperimentalHorologistApi
public class DeviceGrantTokenRepositoryGoogleImpl(
    private val application: Application,
    private val googleOAuthService: GoogleOAuthService
) : DeviceGrantTokenRepository<DeviceGrantDefaultConfig, DeviceCodeResponse, String> {

    override suspend fun fetch(
        config: DeviceGrantDefaultConfig,
        verificationInfoPayload: DeviceCodeResponse
    ): Result<String> {
        try {
            RemoteActivityHelper(application).startRemoteActivity(
                targetIntent = Intent(Intent.ACTION_VIEW).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                    data = Uri.parse(verificationInfoPayload.verificationUri)
                },
                targetNodeId = null
            ).await()
        } catch (e: RemoteActivityHelper.RemoteIntentException) {
            Log.e(TAG, "Error starting remote activity", e)
            // It should return `Result.failure(e)` here, however, as described in b/263238683,
            // the emulator seems to be receiving a `RemoteIntentException` even though the browser
            // opened the url successfully on the phone. It's allowing the code to proceed for now
            // until figured out how to reliably handle errors from `startRemoteActivity`.
        } catch (e: Exception) {
            Log.e(TAG, "Error starting remote activity", e)
            return Result.failure(e)
        }

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
        config: DeviceGrantDefaultConfig,
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
        config: DeviceGrantDefaultConfig,
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
            Log.e(TAG, "Error fetching token", e)
            Result.failure(e)
        }
    }
}

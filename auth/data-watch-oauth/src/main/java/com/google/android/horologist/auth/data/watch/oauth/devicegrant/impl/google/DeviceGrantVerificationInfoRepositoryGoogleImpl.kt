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

import android.util.Log
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.data.oauth.devicegrant.DeviceGrantVerificationInfoRepository
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.DeviceCodeResponse
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService.Companion.USER_INFO_PROFILE_SCOPE_VALUE
import com.google.android.horologist.auth.data.watch.oauth.common.logging.TAG
import com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.DeviceGrantDefaultConfig
import kotlinx.coroutines.CancellationException

@ExperimentalHorologistApi
public class DeviceGrantVerificationInfoRepositoryGoogleImpl(
    private val googleOAuthService: GoogleOAuthService
) : DeviceGrantVerificationInfoRepository<DeviceGrantDefaultConfig, DeviceCodeResponse> {

    override suspend fun fetch(config: DeviceGrantDefaultConfig): Result<DeviceCodeResponse> {
        return try {
            Log.d(TAG, "Retrieving verification info...")
            val deviceCodeResponse =
                googleOAuthService.deviceCode(config.clientId, USER_INFO_PROFILE_SCOPE_VALUE)

            Result.success(deviceCodeResponse)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

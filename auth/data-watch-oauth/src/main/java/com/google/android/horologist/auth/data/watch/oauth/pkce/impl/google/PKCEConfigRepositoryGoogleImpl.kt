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

import android.net.Uri
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.data.oauth.pkce.PKCEConfigRepository
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService.Companion.SCOPE_KEY
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService.Companion.USER_AUTH_ENDPOINT
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthService.Companion.USER_INFO_PROFILE_SCOPE_VALUE
import com.google.android.horologist.auth.data.watch.oauth.pkce.impl.PKCEDefaultConfig

public class PKCEConfigRepositoryGoogleImpl(
    private val clientId: String,
    private val clientSecret: String,
    private val encodedPath: String = USER_AUTH_ENDPOINT,
    private val queryParameters: Map<String, String> = mapOf(SCOPE_KEY to USER_INFO_PROFILE_SCOPE_VALUE)
) : PKCEConfigRepository<PKCEDefaultConfig> {

    override suspend fun fetch(): PKCEDefaultConfig {
        val uri = Uri.Builder()
            .encodedPath(encodedPath)
            .also { builder ->
                for ((key, value) in queryParameters) {
                    builder.appendQueryParameter(key, value)
                }
            }
            .build()

        return PKCEDefaultConfig(
            clientId = clientId,
            clientSecret = clientSecret,
            authProviderUrl = uri
        )
    }
}

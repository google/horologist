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

package com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

public interface GoogleOAuthService {

    // https://developers.google.com/identity/protocols/oauth2/native-app#exchange-authorization-code
    @FormUrlEncoded
    @POST("token")
    public suspend fun token(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String? = null,
        @Field("code_verifier") codeVerifier: String? = null,
        @Field("grant_type") grantType: String? = null,
        @Field("redirect_uri") redirectUri: String? = null,
        @Field("device_code") deviceCode: String? = null
    ): TokenResponse

    @FormUrlEncoded
    @POST("device/code")
    public suspend fun deviceCode(
        @Field("client_id") clientId: String,
        @Field("scope") scope: String
    ): DeviceCodeResponse

    public companion object {
        public const val GOOGLE_OAUTH_SERVER: String = "https://oauth2.googleapis.com/"

        // https://developers.google.com/identity/protocols/oauth2/native-app#step-2:-send-a-request-to-googles-oauth-2.0-server
        public const val USER_AUTH_ENDPOINT: String = "https://accounts.google.com/o/oauth2/v2/auth"

        public const val SCOPE_KEY: String = "scope"
        public const val USER_INFO_PROFILE_SCOPE_VALUE: String =
            "https://www.googleapis.com/auth/userinfo.profile"

        public const val GRANT_TYPE_PARAM_AUTH_CODE_GRANT_VALUE: String = "authorization_code"
        public const val GRANT_TYPE_PARAM_AUTH_DEVICE_GRANT_VALUE: String = "urn:ietf:params:oauth:grant-type:device_code"
    }
}

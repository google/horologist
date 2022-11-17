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

package com.google.android.horologist.auth.data.pkce.impl.google.api

import com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@ExperimentalHorologistAuthDataApi
public interface GoogleOAuthService {

    // See https://developers.google.com/identity/protocols/oauth2/native-app#exchange-authorization-code
    @FormUrlEncoded
    @POST("token")
    public suspend fun token(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("grant_type") grantType: String,
        @Field("redirect_uri") redirectUri: String
    ): TokenResponse

    public companion object {
        internal const val GOOGLE_OAUTH_SERVER = "https://oauth2.googleapis.com/"

        internal const val REQUEST_GRANT_TYPE_PARAM_VALUE = "authorization_code"
    }
}

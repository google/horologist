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

package com.google.android.horologist.auth.data.phone.tokenshare

import com.google.android.horologist.auth.data.phone.ExperimentalHorologistAuthDataPhoneApi

/**
 * Repository of access token.
 *
 * Tokens should be stored in the Android Wear network with [SERVICE_PATH_PREFIX] as path, and
 * [KEY_TOKEN] as key.
 */
@ExperimentalHorologistAuthDataPhoneApi
public interface TokenRepository {

    public suspend fun updateToken(token: String)

    public companion object {
        public const val SERVICE_PATH_PREFIX: String = "/horologist_auth"
        public const val KEY_TOKEN: String = "token"
    }
}

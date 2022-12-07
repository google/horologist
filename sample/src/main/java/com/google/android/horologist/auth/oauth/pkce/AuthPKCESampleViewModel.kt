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

package com.google.android.horologist.auth.oauth.pkce

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.auth.data.oauth.common.impl.google.api.GoogleOAuthServiceFactory
import com.google.android.horologist.auth.data.oauth.pkce.impl.AuthPKCEOAuthCodeRepositoryImpl
import com.google.android.horologist.auth.data.oauth.pkce.impl.google.AuthPKCEConfigRepositoryGoogleImpl
import com.google.android.horologist.auth.data.oauth.pkce.impl.google.AuthPKCETokenRepositoryGoogleImpl
import com.google.android.horologist.auth.ui.oauth.pkce.AuthPKCEViewModel
import com.google.android.horologist.sample.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object AuthPKCESampleViewModel {

    val Factory: ViewModelProvider.Factory = viewModelFactory {

        initializer {
            val application = this[APPLICATION_KEY]!!

            AuthPKCEViewModel(
                authPKCEConfigRepository = AuthPKCEConfigRepositoryGoogleImpl(
                    clientId = BuildConfig.OAUTH_PKCE_CLIENT_ID,
                    clientSecret = BuildConfig.OAUTH_PKCE_CLIENT_SECRET
                ),
                authPKCEOAuthCodeRepository = AuthPKCEOAuthCodeRepositoryImpl(application),
                authPKCETokenRepository = AuthPKCETokenRepositoryGoogleImpl(
                    GoogleOAuthServiceFactory(
                        okHttpClient = OkHttpClient.Builder()
                            .also { builder ->
                                builder.addInterceptor(
                                    HttpLoggingInterceptor().also { interceptor ->
                                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                                    }
                                )
                            }.build(),
                        moshi = Moshi.Builder().build()
                    ).get()
                )
            )
        }
    }
}

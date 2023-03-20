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

package com.google.android.horologist.auth.sample.screens.oauth.devicegrant.signin

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.horologist.auth.data.watch.oauth.common.impl.google.api.GoogleOAuthServiceFactory
import com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.DeviceGrantConfigRepositoryDefaultImpl
import com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.google.DeviceGrantTokenRepositoryGoogleImpl
import com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.google.DeviceGrantVerificationInfoRepositoryGoogleImpl
import com.google.android.horologist.auth.sample.BuildConfig
import com.google.android.horologist.auth.sample.SampleApplication
import com.google.android.horologist.auth.ui.oauth.devicegrant.signin.DeviceGrantViewModel

val DeviceGrantSampleViewModelFactory: ViewModelProvider.Factory = viewModelFactory {

    initializer {
        val application = this[APPLICATION_KEY]!! as SampleApplication

        val googleOAuthService = GoogleOAuthServiceFactory(
            okHttpClient = application.okHttpClient,
            moshi = application.moshi
        ).get()

        DeviceGrantViewModel(
            deviceGrantConfigRepository = DeviceGrantConfigRepositoryDefaultImpl(
                clientId = BuildConfig.OAUTH_DEVICE_GRANT_CLIENT_ID,
                clientSecret = BuildConfig.OAUTH_DEVICE_GRANT_CLIENT_SECRET
            ),
            deviceGrantVerificationInfoRepository = DeviceGrantVerificationInfoRepositoryGoogleImpl(
                googleOAuthService = googleOAuthService
            ),
            deviceGrantTokenRepository = DeviceGrantTokenRepositoryGoogleImpl(
                application = application,
                googleOAuthService = googleOAuthService
            ),
            checkPhonePayloadMapper = { _, deviceResponse -> deviceResponse.userCode },
            deviceGrantTokenPayloadListener = DeviceGrantTokenPayloadListenerSample
        )
    }
}

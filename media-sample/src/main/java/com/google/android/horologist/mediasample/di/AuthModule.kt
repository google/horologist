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

package com.google.android.horologist.mediasample.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.horologist.mediasample.BuildConfig
import com.google.android.horologist.mediasample.data.api.UampService
import com.google.android.horologist.mediasample.data.api.WearArtworkUampService
import com.google.android.horologist.mediasample.ui.AppConfig
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.InMemoryDataRequestRepository
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.highbandwidth.HighBandwidthNetworkMediator
import com.google.android.horologist.networks.highbandwidth.StandardHighBandwidthNetworkMediator
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import com.google.android.horologist.networks.okhttp.NetworkAwareCallFactory
import com.google.android.horologist.networks.okhttp.NetworkSelectingCallFactory
import com.google.android.horologist.networks.okhttp.impl.NetworkLoggingEventListenerFactory
import com.google.android.horologist.networks.request.NetworkRequester
import com.google.android.horologist.networks.request.NetworkRequesterImpl
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import com.google.android.horologist.networks.status.NetworkRepository
import com.google.android.horologist.networks.status.NetworkRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun googleSignIn(
        @ApplicationContext application: Context,
    ): GoogleSignInClient = GoogleSignIn.getClient(
        application,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
    )
}

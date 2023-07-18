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

package com.google.android.horologist.auth.sample

import android.app.Application
import android.os.StrictMode
import com.google.android.horologist.auth.sample.di.SampleAppDI
import com.google.android.horologist.auth.sample.shared.grpc.CounterServiceGrpcKt
import com.google.android.horologist.auth.sample.shared.grpc.GrpcDemoProto
import com.google.android.horologist.data.WearDataLayerRegistry
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient

class SampleApplication : Application() {
    lateinit var counterFlow: Flow<GrpcDemoProto.CounterValue>
    lateinit var counterService: CounterServiceGrpcKt.CounterServiceCoroutineStub
    lateinit var registry: WearDataLayerRegistry
    lateinit var servicesCoroutineScope: CoroutineScope
    lateinit var okHttpClient: OkHttpClient
    lateinit var moshi: Moshi

    override fun onCreate() {
        super.onCreate()

        setStrictMode()

        SampleAppDI.inject(this)
    }

    private fun setStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyDeath()
                .build()
        )
    }
}

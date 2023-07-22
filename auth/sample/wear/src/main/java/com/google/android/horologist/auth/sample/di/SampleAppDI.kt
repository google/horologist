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

package com.google.android.horologist.auth.sample.di

import android.util.Log
import com.google.android.horologist.auth.sample.SampleApplication
import com.google.android.horologist.auth.sample.shared.datalayer.CounterValueSerializer
import com.google.android.horologist.auth.sample.shared.grpc.CounterServiceGrpcKt
import com.google.android.horologist.data.ProtoDataStoreHelper.protoFlow
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.grpc.GrpcExtensions.grpcClient
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Simple DI implementation.
 */
object SampleAppDI {

    fun inject(sampleApplication: SampleApplication) {
        sampleApplication.registry = registry(sampleApplication, servicesCoroutineScope())
        sampleApplication.servicesCoroutineScope = servicesCoroutineScope()
        sampleApplication.okHttpClient = okHttpClient()
        sampleApplication.moshi = moshi()
        sampleApplication.counterFlow = sampleApplication.registry.protoFlow(TargetNodeId.PairedPhone)
        sampleApplication.counterService = sampleApplication.registry.grpcClient(
            nodeId = TargetNodeId.PairedPhone,
            coroutineScope = sampleApplication.servicesCoroutineScope
        ) {
            CounterServiceGrpcKt.CounterServiceCoroutineStub(it)
        }
    }

    private fun registry(
        sampleApplication: SampleApplication,
        coroutineScope: CoroutineScope
    ): WearDataLayerRegistry = WearDataLayerRegistry.fromContext(
        application = sampleApplication,
        coroutineScope = coroutineScope
    ).apply {
        registerSerializer(CounterValueSerializer)
    }

    private fun servicesCoroutineScope(): CoroutineScope {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.e(
                "SampleApplication",
                "Uncaught exception thrown by a service: ${throwable.message}",
                throwable
            )
        }
        return CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)
    }

    private fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().also { interceptor ->
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        ).build()

    private fun moshi(): Moshi = Moshi.Builder().build()
}

/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.ai.core

import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleService
import io.grpc.BindableService
import io.grpc.CompressorRegistry
import io.grpc.DecompressorRegistry
import io.grpc.Server
import io.grpc.binder.AndroidComponentAddress
import io.grpc.binder.BinderServerBuilder
import io.grpc.binder.IBinderReceiver
import io.grpc.binder.SecurityPolicy
import io.grpc.binder.ServerSecurityPolicy
import io.grpc.binder.UntrustedSecurityPolicies

abstract class BindableAiGrpcService : LifecycleService() {
    private lateinit var server: Server
    private val binderReceiver = IBinderReceiver()

    open val securityPolicy: SecurityPolicy = UntrustedSecurityPolicies.untrustedPublic()

    abstract val bindableService: BindableService

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        val serverSecurityPolicy =
            ServerSecurityPolicy.newBuilder()
                .servicePolicy(InferenceServiceGrpc.SERVICE_NAME, securityPolicy)
                .build()
        server =
            BinderServerBuilder.forAddress(AndroidComponentAddress.forContext(this), binderReceiver)
                .securityPolicy(serverSecurityPolicy)
                .addService(bindableService)
                .decompressorRegistry(DecompressorRegistry.emptyInstance())
                .compressorRegistry(CompressorRegistry.newEmptyInstance())
                .build()

        server.start()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binderReceiver.get()!!
    }

    override fun onDestroy() {
        server.shutdownNow()
        super.onDestroy()
    }
}

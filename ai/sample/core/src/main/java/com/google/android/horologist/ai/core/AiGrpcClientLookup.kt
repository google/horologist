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

import android.content.Context
import android.content.Intent
import io.grpc.binder.AndroidComponentAddress
import io.grpc.binder.BinderChannelBuilder
import io.grpc.binder.UntrustedSecurityPolicies

object AiGrpcClientLookup {
    fun lookupInferenceService(
        context: Context,
        packageName: String,
    ): InferenceServiceGrpcKt.InferenceServiceCoroutineStub {
        val channel = BinderChannelBuilder.forAddress(
            AndroidComponentAddress.forBindIntent(
                Intent().apply {
                    setAction("InferenceService")
                    setPackage(packageName)
                },
            ),
            context,
        )
            .securityPolicy(UntrustedSecurityPolicies.untrustedPublic())
            .build()

        return InferenceServiceGrpcKt.InferenceServiceCoroutineStub(channel)
    }
}

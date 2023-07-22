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

@file:OptIn(ExperimentalHorologistApi::class)

package com.google.android.horologist.datalayer.grpc.server

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.WearDataService
import io.grpc.BindableService
import kotlinx.coroutines.tasks.asTask

public abstract class BaseGrpcDataService<T : BindableService> : WearDataService(), LifecycleOwner {
    private lateinit var rpcServer: BaseMessageClientServer

    private val dispatcher = ServiceLifecycleDispatcher(this)

    public abstract fun buildService(): T

    override fun onCreate() {
        super.onCreate()

        rpcServer = MessageClientServer(
            buildService(),
            lifecycleScope
        )
    }

    override fun onRequest(node: String, path: String, data: ByteArray): Task<ByteArray>? {
        return rpcServer.handleIncomingMessage(data).asTask()
    }

    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle
}

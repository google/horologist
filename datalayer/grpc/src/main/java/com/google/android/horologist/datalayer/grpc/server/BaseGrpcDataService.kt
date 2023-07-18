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

abstract class BaseGrpcDataService<T: BindableService> : WearDataService(), LifecycleOwner {
    private lateinit var rpcServer: BaseMessageClientServer

    private val dispatcher = ServiceLifecycleDispatcher(this)

    abstract fun buildService(): T

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
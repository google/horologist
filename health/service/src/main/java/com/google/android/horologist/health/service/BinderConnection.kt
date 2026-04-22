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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.health.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.safeCast

public class BinderConnection<T : IBinder>(
    private val context: Context,
    private val type: KClass<out T>,
) : ServiceConnection {
    private val mutableBinder = MutableStateFlow<T?>(null)
    private val binder = mutableBinder.asStateFlow()

    public fun unbind() {
        context.unbindService(this)
    }

    public suspend fun <R> runWhenConnected(command: suspend (T) -> R): R =
        command(binder.filterNotNull().first())

    public fun <N, V : Flow<N>> flowWhenConnected(property: KProperty1<T, V>): Flow<N> =
        binder.flatMapLatest { it?.let { property.get(it) } ?: emptyFlow() }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        type.safeCast(service)?.also {
            mutableBinder.value = it
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mutableBinder.value = null
    }

    public companion object {
        // For Hilt
//        inline fun <reified T : IBinder, reified S : Service> ViewModelLifecycle.bindService(context: Context): BinderConnection<T> {
//            val connection = BinderConnection(context, T::class)
//            bindService(context, S::class, connection)
//            addOnClearedListener {
//                connection.unbind()
//            }
//            return connection
//        }

        public inline fun <reified T : IBinder, reified S : Service> Lifecycle.bindService(
            context: Context,
        ): BinderConnection<T> {
            val connection = BinderConnection(context, T::class)
            addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    bindService(context, S::class, connection)
                }

                override fun onStop(owner: LifecycleOwner) {
                    connection.unbind()
                }
            })
            return connection
        }

        public inline fun <reified T : IBinder, reified S : Service> CoroutineScope.bindService(
            context: Context,
        ): BinderConnection<T> {
            val connection = BinderConnection(context, T::class)
            launch {
                try {
                    bindService(context, S::class, connection)
                    awaitCancellation()
                } finally {
                    connection.unbind()
                }
            }
            return connection
        }

        public fun <S : Service> bindService(
            context: Context,
            service: KClass<S>,
            connection: BinderConnection<*>,
        ) {
            Intent(context, service.java).also { intent ->
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }
}

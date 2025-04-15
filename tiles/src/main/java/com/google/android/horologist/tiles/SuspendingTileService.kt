/*
 * Copyright 2017 The Android Open Source Project
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

package com.google.android.horologist.tiles

import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.concurrent.futures.SuspendToFutureAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.lifecycleScope
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope

/**
 * Base class for a Kotlin and Coroutines friendly TileService.
 * Also acts like a LifecycleService, allowing lifecycleScope,
 * and general lifecycle operations.
 */
@ExperimentalHorologistApi
public abstract class SuspendingTileService : TileService(), LifecycleOwner {
    // Code from LifecycleService

    @Suppress("LeakingThis")
    private val mDispatcher = ServiceLifecycleDispatcher(this)

    /**
     * Override if a custom serviceScope, otherwise lifecycleScope will be used.
     */
    public open val serviceScope: CoroutineScope? = null

    final override fun onTileRequest(
        requestParams: TileRequest,
    ): ListenableFuture<Tile> = launch {
        tileRequest(requestParams)
    }

    private fun <T> launch(
        block: suspend CoroutineScope.() -> T,
    ): ListenableFuture<T> {
        return SuspendToFutureAdapter.launchFuture(
            (serviceScope ?: lifecycleScope).coroutineContext,
        ) {
            block()
        }
    }

    /**
     * See [onTileRequest] for most details.
     *
     * This runs a suspending function inside the lifecycleScope
     * of the service on the Main thread.
     */
    public abstract suspend fun tileRequest(requestParams: TileRequest): Tile

    final override fun onTileResourcesRequest(
        requestParams: ResourcesRequest,
    ): ListenableFuture<Resources> = launch {
        resourcesRequest(requestParams)
    }

    /**
     * See [onResourcesRequest] for most details.
     *
     * This runs a suspending function inside the lifecycleScope
     * of the service on the Main thread.
     */
    public abstract suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources

    @CallSuper
    override fun onCreate() {
        mDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    @CallSuper
    override fun onBind(intent: Intent): IBinder? {
        mDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    @Deprecated("Use onStartCommand")
    final override fun onStart(intent: Intent?, startId: Int) {
        mDispatcher.onServicePreSuperOnStart()
        @Suppress("DEPRECATION")
        super.onStart(intent, startId)
    }

    // this method is added only to annotate it with @CallSuper.
    // In usual service super.onStartCommand is no-op, but in LifecycleService
    // it results in mDispatcher.onServicePreSuperOnStart() call, because
    // super.onStartCommand calls onStart().
    final override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    @CallSuper
    override fun onDestroy() {
        mDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override val lifecycle: Lifecycle
        get() = mDispatcher.lifecycle
}

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

package com.google.android.horologist.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.data.store.flow.dataItemFlow
import com.google.android.horologist.data.store.impl.WearLocalDataStore
import com.google.android.horologist.data.store.prefs.PreferencesSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KClass

// Workaround https://issuetracker.google.com/issues/239451111
@SuppressLint("VisibleForTests")
@ExperimentalHorologistDataLayerApi
/**
 * Implementation of Androidx Datastore for Proto and Preferences on top of the
 * Wearable DataClient.
 *
 * See https://developer.android.com/topic/libraries/architecture/datastore for DataStore features.
 */
public class WearDataLayerRegistry(
    public val dataClient: DataClient,
    public val nodeClient: NodeClient
) {
    /**
     * Returns a local Proto DataStore for the given Proto structure.
     *
     * @param path the path inside the data client namespace, of the form "/xyz/123"
     * @param coroutineScope the coroutine scope used for the internal SharedFlow.
     * @param serializer the data store Serializer
     * @param started the SharingStarted mode for the internal SharedFlow.
     */
    fun <T> protoDataStore(
        path: String,
        coroutineScope: CoroutineScope,
        serializer: Serializer<T>,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000)
    ): DataStore<T> {
        return WearLocalDataStore(
            this,
            started = started,
            coroutineScope = coroutineScope,
            serializer = serializer,
            path = path
        )
    }

    internal fun <T> protoFlow(
        targetNodeId: TargetNodeId,
        path: String,
        serializer: Serializer<T>
    ): Flow<T> = flow {
        val nodeId = targetNodeId.evaluate(this@WearDataLayerRegistry)

        if (nodeId != null) {
            emitAll(dataClient.dataItemFlow(nodeId, path, serializer))
        }
    }

    /**
     * Returns a local Preferences DataStore.
     *
     * @param path the path inside the data client namespace, of the form "/xyz/123"
     * @param coroutineScope the coroutine scope used for the internal SharedFlow.
     * @param started the SharingStarted mode for the internal SharedFlow.
     */
    fun preferencesDataStore(
        path: String,
        coroutineScope: CoroutineScope,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000)
    ): DataStore<Preferences> {
        return protoDataStore(path, coroutineScope, PreferencesSerializer, started)
    }

    fun preferencesFlow(
        targetNodeId: TargetNodeId,
        path: String
    ): Flow<Preferences> = protoFlow(targetNodeId, path, PreferencesSerializer)

    companion object {
        /**
         * Create an instance looking up Wearable DataClient and NodeClient using the given context.
         */
        fun fromContext(application: Context): WearDataLayerRegistry = WearDataLayerRegistry(
            dataClient = Wearable.getDataClient(application),
            nodeClient = Wearable.getNodeClient(application)
        )

        public fun buildUri(nodeId: String, path: String): Uri = Uri.Builder()
            .scheme(PutDataRequest.WEAR_URI_SCHEME)
            .authority(nodeId)
            .path(path)
            .build()

        public fun dataStorePath(t: KClass<*>, persisted: Boolean = false): String {
            // Use predictable paths for persisted data that should survive backups
            val prefix = if (persisted) "proto-persisted" else "proto"
            return "/$prefix/${t.simpleName!!}"
        }

        fun preferencesPath(name: String, persisted: Boolean = false): String {
            // Use predictable paths for persisted data that should survive backups
            val prefix = if (persisted) "proto-persisted" else "proto"
            return "/$prefix/prefs/$name"
        }
    }
}

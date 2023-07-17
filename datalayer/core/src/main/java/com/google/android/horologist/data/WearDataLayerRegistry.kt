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
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.store.ProtoDataListener
import com.google.android.horologist.data.store.impl.ProtoDataListenerRegistration
import com.google.android.horologist.data.store.impl.WearLocalDataStore
import com.google.android.horologist.data.store.impl.dataItemFlow
import com.google.android.horologist.data.store.prefs.PreferencesSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

// Workaround https://issuetracker.google.com/issues/239451111
@SuppressLint("VisibleForTests")
@ExperimentalHorologistApi
/**
 * Implementation of Androidx Datastore for Proto and Preferences on top of the
 * Wearable DataClient.
 *
 * See https://developer.android.com/topic/libraries/architecture/datastore for DataStore features.
 */
public class WearDataLayerRegistry(
    public val dataClient: DataClient,
    public val nodeClient: NodeClient,
    public val messageClient: MessageClient,
    public val capabilityClient: CapabilityClient,
    private val coroutineScope: CoroutineScope
) {
    public val serializers: SerializerRegistry = SerializerRegistry()
    private val protoDataListeners: MutableList<ProtoDataListenerRegistration<*>> = mutableListOf()

    init {
        serializers.registerSerializer(PreferencesSerializer)
    }

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

    fun <T> protoFlow(
        targetNodeId: TargetNodeId,
        serializer: Serializer<T>,
        path: String
    ): Flow<T> = flow {
        val nodeId = targetNodeId.evaluate(this@WearDataLayerRegistry)

        if (nodeId != null) {
            emitAll(dataClient.dataItemFlow(nodeId, path, serializer))
        }
    }

    inline fun <reified T : Any> registerSerializer(serializer: Serializer<T>) {
        serializers.registerSerializer(serializer)
    }

    inline fun <reified T : Any> registerProtoDataListener(
        path: String,
        listener: ProtoDataListener<T>
    ) {
        val serializer = serializers.serializerForType<T>()
        registerProtoDataListener(path, listener, serializer)
    }

    fun <T : Any> registerProtoDataListener(
        path: String,
        listener: ProtoDataListener<T>,
        serializer: Serializer<T>
    ) {
        val element = ProtoDataListenerRegistration(path, serializer, listener)
        protoDataListeners.add(element)
    }

    fun onDataChanged(dataEvents: List<DataEvent>) {
        runBlocking {
            for (dataEvent in dataEvents) {
                val listeners = protoDataListeners.filter { it.path == dataEvent.dataItem.uri.path }

                if (listeners.isNotEmpty()) {
                    fireListeners(dataEvent, listeners)
                }
            }
        }
    }

    private suspend fun fireListeners(
        dataEvent: DataEvent,
        listeners: List<ProtoDataListenerRegistration<*>>
    ) {
        val nodeId = dataEvent.dataItem.uri.host!!
        val path = dataEvent.dataItem.uri.path!!
        if (dataEvent.type == DataEvent.TYPE_CHANGED) {
            val data = dataEvent.dataItem.data!!
            for (listener in listeners) {
                listener.dataAdded(nodeId, path, data)
            }
        } else {
            for (listener in listeners) {
                listener.dataDeleted(nodeId, path)
            }
        }
    }

    companion object {
        /**
         * Create an instance looking up Wearable DataClient and NodeClient using the given context.
         */
        fun fromContext(
            application: Context,
            coroutineScope: CoroutineScope
        ): WearDataLayerRegistry = WearDataLayerRegistry(
            dataClient = Wearable.getDataClient(application),
            nodeClient = Wearable.getNodeClient(application),
            messageClient = Wearable.getMessageClient(application),
            capabilityClient = Wearable.getCapabilityClient(application),
            coroutineScope = coroutineScope
        )

        public fun buildUri(nodeId: String, path: String): Uri = Uri.Builder()
            .scheme(PutDataRequest.WEAR_URI_SCHEME)
            .authority(nodeId)
            .path(path)
            .build()

        public fun dataStorePath(t: KClass<*>, persisted: Boolean = false): String {
            // Use predictable paths for persisted data that should survive backups
            val prefix = if (persisted) "proto/persisted" else "proto"
            return "/$prefix/${t.simpleName!!}"
        }

        fun preferencesPath(name: String, persisted: Boolean = false): String {
            // Use predictable paths for persisted data that should survive backups
            val prefix = if (persisted) "proto/persisted" else "proto"
            return "/$prefix/prefs/$name"
        }
    }
}

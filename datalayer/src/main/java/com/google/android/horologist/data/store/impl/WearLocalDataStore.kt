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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.data.store.impl

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.WearDataLayerRegistry.Companion.buildUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

// Workaround https://issuetracker.google.com/issues/239451111
@SuppressLint("VisibleForTests")
@ExperimentalHorologistApi
public class WearLocalDataStore<T>(
    private val wearDataLayerRegistry: WearDataLayerRegistry,
    started: SharingStarted = SharingStarted.Eagerly,
    coroutineScope: CoroutineScope,
    private val serializer: Serializer<T>,
    private val path: String
) : DataStore<T> {
    private val nodeIdFlow = nodeIdFlow().shareIn(coroutineScope, started = started, replay = 1)

    private val mutex = Mutex()

    val dataClient: DataClient
        get() = wearDataLayerRegistry.dataClient

    private fun nodeIdFlow() = flow {
        val nodeId = TargetNodeId.ThisNodeId.evaluate(wearDataLayerRegistry)
        emit(
            NodeIdAndPath(
                nodeId = nodeId,
                fullPath = buildUri(nodeId, path)
            )
        )
    }

    private val sharedFlow: SharedFlow<T> = nodeIdFlow.flatMapLatest { (nodeId, _) ->
        dataClient.dataItemFlow(
            nodeId,
            path,
            serializer
        )
    }.shareIn(coroutineScope, started = SharingStarted.Eagerly, replay = 1)

    override val data: Flow<T> = sharedFlow

    private suspend fun writeBytes(t: T): ByteArray {
        return ByteArrayOutputStream().apply {
            serializer.writeTo(t, this)
        }.toByteArray()
    }

    override suspend fun updateData(transform: suspend (t: T) -> T): T = mutex.withLock {
        val nodeId = nodeIdFlow.first()

        val oldT = readExistingValue(nodeId)
        val newT = transform(oldT)

        if (newT == null) {
            dataClient.deleteDataItems(nodeId.fullPath)
                .await()
        } else if (newT != oldT) {
            val request = PutDataRequest.create(path).apply {
                data = writeBytes(newT)
            }

            dataClient.putDataItem(request).await()
        }

        return newT
    }

    private suspend fun readExistingValue(nodeId: NodeIdAndPath): T {
        try {
            val data = dataClient.getDataItem(buildUri(nodeId.nodeId, path)).await().data

            if (data != null) {
                return serializer.readFrom(ByteArrayInputStream(data))
            }
        } catch (e: Exception) {
            // TODO introduce CorruptionHandler
            Log.w("WearLocalDataStore", "Corrupted data for DataStore")
        }

        return serializer.defaultValue
    }
}

data class NodeIdAndPath(
    val nodeId: String,
    val fullPath: Uri
)

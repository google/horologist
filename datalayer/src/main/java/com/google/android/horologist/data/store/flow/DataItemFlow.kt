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

package com.google.android.horologist.data.store.flow

import android.annotation.SuppressLint
import android.net.Uri
import androidx.datastore.core.Serializer
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.horologist.data.ExperimentalHorologistDataLayerApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream

// Workaround https://issuetracker.google.com/issues/239451111
@SuppressLint("VisibleForTests")
@ExperimentalHorologistDataLayerApi
public fun <T> DataClient.dataItemFlow(
    nodeId: String,
    path: String,
    serializer: Serializer<T>
) = callbackFlow {
    val listener: (DataEventBuffer) -> Unit = {
        val dataItem = it[it.count - 1].dataItem
        val data = dataItem.data
        this.trySend(data)
    }

    val uri = Uri.Builder()
        .scheme(PutDataRequest.WEAR_URI_SCHEME)
        .path(path)
        .authority(nodeId)
        .build()

    val item = this@dataItemFlow.getDataItem(uri).await()
    trySend(item.data)

    addListener(listener, uri, DataClient.FILTER_LITERAL)

    awaitClose {
        removeListener(listener)
    }
}.map {
    serializer.readFrom(ByteArrayInputStream(it))
}

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

package com.google.android.horologist.data.store.impl

import android.annotation.SuppressLint
import android.net.Uri
import androidx.datastore.core.Serializer
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataClient.OnDataChangedListener
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream

// Workaround https://issuetracker.google.com/issues/239451111
@SuppressLint("VisibleForTests")
public fun <T> DataClient.dataItemFlow(
    nodeId: String,
    path: String,
    serializer: Serializer<T>,
    defaultValue: () -> T = { serializer.defaultValue }
): Flow<T> = callbackFlow {
    val listener = OnDataChangedListener {
        val dataItem = it[it.getCount() - 1].dataItem
        val data = dataItem.data
        trySend(data)
    }

    val uri = Uri.Builder()
        .scheme(PutDataRequest.WEAR_URI_SCHEME)
        .path(path)
        .authority(nodeId)
        .build()

    addListener(
        listener,
        uri,
        DataClient.FILTER_LITERAL
    ).await() // Ensure we are subscribed to updates first,

    val item: DataItem? = this@dataItemFlow.getDataItem(uri).await() // then get the current value.

    trySend(item?.data)

    awaitClose {
        removeListener(listener)
    }
}.map {
    if (it != null) {
        serializer.parse(it)
    } else {
        defaultValue()
    }
}

private suspend fun <T> Serializer<T>.parse(
    data: ByteArray
) = readFrom(ByteArrayInputStream(data))

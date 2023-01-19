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

package com.google.android.horologist.components

import android.app.Application
import com.google.android.horologist.data.ProtoDataStoreHelper.registerProtoDataListener
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.proto.SampleProto
import com.google.android.horologist.data.store.ProtoDataListener
import com.google.android.horologist.datalayer.SampleDataSerializer
import com.google.android.horologist.networks.InMemoryStatusLogger
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.status.NetworkRepository
import com.google.android.horologist.sample.di.SampleAppDI
import kotlinx.coroutines.CoroutineScope
import okhttp3.Call
import okhttp3.OkHttpClient

class SampleApplication : Application() {
    lateinit var okHttpClient: OkHttpClient
    lateinit var coroutineScope: CoroutineScope
    lateinit var networkLogger: InMemoryStatusLogger
    lateinit var networkRepository: NetworkRepository
    lateinit var dataRequestRepository: DataRequestRepository
    lateinit var networkAwareCallFactory: Call.Factory

    lateinit var registry: WearDataLayerRegistry

    override fun onCreate() {
        super.onCreate()

        SampleAppDI.inject(this)

        registry = WearDataLayerRegistry.fromContext(this, coroutineScope).apply {
            registerSerializer(SampleDataSerializer)

            registerProtoDataListener(object : ProtoDataListener<SampleProto.Data> {
                override fun dataAdded(nodeId: String, path: String, value: SampleProto.Data) {
                    println("Data Added: $nodeId $path $value")
                }

                override fun dataDeleted(nodeId: String, path: String) {
                    println("Data Deleted: $nodeId $path")
                }
            })
        }
    }
}

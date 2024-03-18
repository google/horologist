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

package com.google.android.horologist.datalayer.sample.di

import android.content.Context
import com.google.android.horologist.data.ProtoDataStoreHelper.protoFlow
import com.google.android.horologist.data.ProtoDataStoreHelper.registerProtoDataListener
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.proto.SampleProto
import com.google.android.horologist.data.store.ProtoDataListener
import com.google.android.horologist.datalayer.grpc.GrpcExtensions.grpcClient
import com.google.android.horologist.datalayer.sample.TileSync
import com.google.android.horologist.datalayer.sample.screens.nodes.SampleDataSerializer
import com.google.android.horologist.datalayer.sample.shared.CounterValueSerializer
import com.google.android.horologist.datalayer.sample.shared.grpc.CounterServiceGrpcKt
import com.google.android.horologist.datalayer.sample.shared.grpc.GrpcDemoProto
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow

@Module
@InstallIn(ActivityRetainedComponent::class)
object DatalayerModule {

    @ActivityRetainedScoped
    @Provides
    fun coroutineScope(
        activityRetainedLifecycle: ActivityRetainedLifecycle,
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default).also {
            activityRetainedLifecycle.addOnClearedListener {
                it.cancel()
            }
        }
    }

    @ActivityRetainedScoped
    @Provides
    fun wearDataLayerRegistry(
        @ApplicationContext applicationContext: Context,
        coroutineScope: CoroutineScope,
    ): WearDataLayerRegistry = WearDataLayerRegistry.fromContext(
        application = applicationContext,
        coroutineScope = coroutineScope,
    ).apply {
        registerSerializer(CounterValueSerializer)

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

    @ActivityRetainedScoped
    @Provides
    fun wearDataLayerAppHelper(
        @ApplicationContext applicationContext: Context,
        wearDataLayerRegistry: WearDataLayerRegistry,
        coroutineScope: CoroutineScope,
    ) = WearDataLayerAppHelper(
        context = applicationContext,
        registry = wearDataLayerRegistry,
        scope = coroutineScope,
    )

    @ActivityRetainedScoped
    @Provides
    fun counterFlow(wearDataLayerRegistry: WearDataLayerRegistry): Flow<GrpcDemoProto.CounterValue> =
        wearDataLayerRegistry.protoFlow(TargetNodeId.PairedPhone)

    @ActivityRetainedScoped
    @Provides
    fun counterService(
        wearDataLayerRegistry: WearDataLayerRegistry,
        coroutineScope: CoroutineScope,
    ): CounterServiceGrpcKt.CounterServiceCoroutineStub =
        wearDataLayerRegistry.grpcClient(
            nodeId = TargetNodeId.PairedPhone,
            coroutineScope = coroutineScope,
        ) {
            CounterServiceGrpcKt.CounterServiceCoroutineStub(it)
        }

    @ActivityRetainedScoped
    @Provides
    fun tileSync(
        wearDataLayerRegistry: WearDataLayerRegistry,
        wearDataLayerAppHelper: WearDataLayerAppHelper,
    ): TileSync = TileSync(wearDataLayerRegistry, wearDataLayerAppHelper)
}

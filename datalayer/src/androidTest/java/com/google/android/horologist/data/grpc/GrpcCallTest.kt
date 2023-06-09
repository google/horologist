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

@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.google.android.horologist.data.grpc

import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.data.CommandServiceGrpcKt
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.protobuf.Empty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class GrpcCallTest {

    @get:Rule
    public val testName: TestName = TestName()

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    val testScope = TestScope()

    val scope = CoroutineScope(testScope.coroutineContext + Job())

    private lateinit var registry: WearDataLayerRegistry

    private lateinit var path: String

    private lateinit var client: CommandServiceGrpcKt.CommandServiceCoroutineStub

    @Before
    fun setUp() {
        registry = WearDataLayerRegistry.fromContext(context, scope)

        path = "/GrpcCallTest/" + testName.methodName

        client = registry.grpcClient<CommandServiceGrpcKt.CommandServiceCoroutineStub>(
            TargetNodeId.ThisNodeId,
            scope,
            path
        )

        val serverImpl = CommandServiceImpl()
        registry.registerGrpcServer(serverImpl, path)
    }

    @Test
    fun testCalls() = testScope.runTest {
        val response = client.sendCommand(Empty.getDefaultInstance())
        println(response)
    }
}

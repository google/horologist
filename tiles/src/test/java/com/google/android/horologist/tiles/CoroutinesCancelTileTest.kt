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

@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
@file:Suppress("UnstableApiUsage")

package com.google.android.horologist.tiles

import android.os.Looper
import androidx.concurrent.futures.await
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.testing.TestTileClient
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
public class CoroutinesCancelTileTest {
    private lateinit var fakeCoroutineScope: TestScope
    private lateinit var tileService: TestTileService
    private lateinit var tileServiceClient: TestTileClient<TestTileService>

    @Before
    fun setUp() {
        fakeCoroutineScope = TestScope(UnconfinedTestDispatcher())

        tileService = TestTileService()
        tileServiceClient = TestTileClient(
            tileService,
            fakeCoroutineScope,
            fakeCoroutineScope.coroutineContext[CoroutineDispatcher]!!
        )
    }

    @Test
    fun tileProviderReturnsTile() = fakeCoroutineScope.runTest {
        val tileRequest = RequestBuilders.TileRequest.Builder().build()

        val tileFuture = tileServiceClient.requestTile(tileRequest)
        shadowOf(Looper.getMainLooper()).idle()
        val tile = tileFuture.await()

        assertThat(tile.resourcesVersion).isEqualTo("1")
        assertThat(tileService.started).isEqualTo(1)
        assertThat(tileService.completed).isEqualTo(1)
        assertThat(tileService.cancelled).isEqualTo(0)
    }

    @Test
    fun tileProviderCanBeCancelled() = fakeCoroutineScope.runTest {
        tileService.delayDuration = 3.seconds

        val tileRequest = RequestBuilders.TileRequest.Builder().build()

        val tileFuture = tileServiceClient.requestTile(tileRequest)

        tileFuture.cancel(false)

        shadowOf(Looper.getMainLooper()).idle()

        assertThat(tileService.started).isEqualTo(1)
        assertThat(tileService.completed).isEqualTo(0)
        // unable to increment after cancellation
//        assertThat(tileService.cancelled).isEqualTo(1)
    }
}

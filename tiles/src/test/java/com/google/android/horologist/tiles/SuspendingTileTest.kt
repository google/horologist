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

package com.google.android.horologist.tiles

import android.os.Looper
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.testing.TestTileClient
import com.google.common.truth.Truth.assertThat
import com.google.common.util.concurrent.MoreExecutors
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
public class SuspendingTileTest {
    private val fakeTileService = TestTileService()

    private lateinit var clientUnderTest: TestTileClient<TestTileService>

    @Before
    public fun setUp() {
        val executor = MoreExecutors.directExecutor()
        clientUnderTest = TestTileClient(fakeTileService, executor)
    }

    @Test
    public fun canCallOnTileRequest() {
        val future = clientUnderTest.requestTile(RequestBuilders.TileRequest.Builder().build())

        shadowOf(Looper.getMainLooper()).idle()

        assertThat(future.isDone).isTrue()
        assertThat(future.get().resourcesVersion).isEqualTo(TestTileService.FAKE_VERSION)
    }

    @Test
    public fun canCallOnResourcesRequest() {
        val future = clientUnderTest.requestTileResourcesAsync(
            RequestBuilders.ResourcesRequest.Builder().build()
        )

        shadowOf(Looper.getMainLooper()).idle()

        assertThat(future.isDone).isTrue()
        assertThat(future.get().version).isEqualTo(TestTileService.FAKE_VERSION)
    }
}

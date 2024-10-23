/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.datalayer.watch

import android.app.Application
import android.os.Looper
import androidx.concurrent.futures.ResolvableFuture
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.testing.TestTileClient
import com.google.android.horologist.data.SurfacesInfo
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.apphelper.SurfacesInfoSerializer
import com.google.common.truth.Truth.assertThat
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.util.concurrent.InlineExecutorService
import org.robolectric.annotation.internal.DoNotInstrument
import org.robolectric.internal.bytecode.InstrumentationConfiguration

@RunWith(TilesTestingTestRunner::class)
@DoNotInstrument
class WearDataLayerAppHelperRoboTest {

    private val fakeTileService = FakeTileService()
    private lateinit var clientUnderTest: TestTileClient<FakeTileService>
    private lateinit var executor: InlineExecutorService

    @Before
    fun setUp() {
        executor = InlineExecutorService()
        clientUnderTest = TestTileClient(fakeTileService, executor)
    }

    @Ignore("This won't work until https://issuetracker.google.com/issues/374901735 is fixed")
    @Test
    fun testTilesWithUpdate() = runTest {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val registry = WearDataLayerRegistry.fromContext(context, this)

        clientUnderTest.requestTile(RequestBuilders.TileRequest.Builder().build())

        val testDataStore: DataStore<SurfacesInfo> =
            DataStoreFactory.create(
                scope = this,
                produceFile = { context.dataStoreFile("testTiles") },
                serializer = SurfacesInfoSerializer,
            )

        val helper = WearDataLayerAppHelper(
            context = context,
            registry = registry,
            appStoreUri = null,
            scope = this,
            surfacesInfoDataStoreFn = { testDataStore },
        )
        val infoInitial = testDataStore.data.first()
        assertThat(infoInitial.tilesList).isEmpty()

        clientUnderTest.sendOnTileAddedEvent()
        helper.updateInstalledTiles()

        val infoUpdated = testDataStore.data.first()
        assertThat(infoUpdated.tilesList).hasSize(1)
        assertThat(infoUpdated.tilesList.first().name).isEqualTo(
            "com.google.android.horologist.datalayer.watch.FakeTileService",
        )

        clientUnderTest.sendOnTileRemovedEvent()

        helper.updateInstalledTiles()
        shadowOf(Looper.getMainLooper()).idle()

        val infoReverted = testDataStore.data.first()
        assertThat(infoReverted.tilesList).isEmpty()
        coroutineContext.cancelChildren()
    }
}

// This class is taken from
// https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/tiles/tiles-testing/src/test/java/androidx/wear/tiles/testing/TestTileClientTest.kt
private class FakeTileService : TileService() {

    private var onTileAddFired = false
    private var onTileRemoveFired = false
    private var onTileEnterFired = false
    private var onTileLeaveFired = false
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val f = ResolvableFuture.create<TileBuilders.Tile>()

        f.set(TileBuilders.Tile.Builder().setResourcesVersion(RESOURCES_VERSION).build())

        return f
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> {
        val f = ResolvableFuture.create<ResourceBuilders.Resources>()

        f.set(ResourceBuilders.Resources.Builder().setVersion(RESOURCES_VERSION).build())

        return f
    }

    override fun onTileAddEvent(requestParams: EventBuilders.TileAddEvent) {
        onTileAddFired = true
    }

    override fun onTileRemoveEvent(requestParams: EventBuilders.TileRemoveEvent) {
        onTileRemoveFired = true
    }

    override fun onTileEnterEvent(requestParams: EventBuilders.TileEnterEvent) {
        onTileEnterFired = true
    }

    override fun onTileLeaveEvent(requestParams: EventBuilders.TileLeaveEvent) {
        onTileLeaveFired = true
    }

    companion object {
        private const val RESOURCES_VERSION = "10"
    }
}

internal class TilesTestingTestRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) {
    override fun createClassLoaderConfig(method: FrameworkMethod): InstrumentationConfiguration =
        InstrumentationConfiguration.Builder(super.createClassLoaderConfig(method))
            .doNotInstrumentPackage("androidx.wear.tiles.connection")
            .doNotInstrumentPackage("androidx.wear.tiles.testing")
            .build()
}

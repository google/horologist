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
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.wear.watchface.complications.data.ComplicationType
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.data.SurfacesInfo
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.WearableApiAvailability
import com.google.android.horologist.data.apphelper.SurfacesInfoSerializer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows

@RunWith(AndroidJUnit4::class)
class WearDataLayerAppHelperTest {

    @Test
    fun testAvailable() = runTest {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val capabilityClient = Wearable.getCapabilityClient(context)

        val checkApiAvailability = async { WearableApiAvailability.isAvailable(capabilityClient) }

        while (!checkApiAvailability.isCompleted) {
            delay(1000)
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }

        assertThat(checkApiAvailability.await()).isFalse()

        coroutineContext.cancelChildren()
    }

    @Test
    fun testCreate() = runTest {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val registry = WearDataLayerRegistry.fromContext(context, this)
        val helper = WearDataLayerAppHelper(context, registry, this)

        val checkApiAvailability = async { helper.isAvailable() }

        while (!checkApiAvailability.isCompleted) {
            delay(1000)
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }

        assertThat(checkApiAvailability.await()).isFalse()

        coroutineContext.cancelChildren()
    }

    @Test
    fun testComplications() = runTest {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val registry = WearDataLayerRegistry.fromContext(context, this)

        val testDataStore: DataStore<SurfacesInfo> =
            DataStoreFactory.create(
                scope = this,
                produceFile = { context.dataStoreFile("testComplications") },
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
        assertThat(infoInitial.complicationsList).isEmpty()

        helper.markComplicationAsActivated("my.SampleComplicationService", 1, ComplicationType.SHORT_TEXT)

        val infoUpdated = testDataStore.data.first()
        assertThat(infoUpdated.complicationsList).hasSize(1)
        assertThat(infoUpdated.complicationsList.first().name).isEqualTo("my.SampleComplicationService")

        helper.markComplicationAsDeactivated(1)

        val infoReverted = testDataStore.data.first()
        assertThat(infoReverted.complicationsList).isEmpty()

        coroutineContext.cancelChildren()
    }
}

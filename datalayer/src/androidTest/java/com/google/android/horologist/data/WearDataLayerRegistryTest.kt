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

package com.google.android.horologist.data

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WearDataLayerRegistryTest {

    @Test
    fun testPreferencesDataStore() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val scope = CoroutineScope(this.coroutineContext + Job())

        val registry = WearDataLayerRegistry.fromContext(context)

        val path = WearDataLayerRegistry.preferencesPath("settings")
        val preferencesDataStore = registry.preferencesDataStore(
            path,
            scope
        )

        preferencesDataStore.edit {
            it[aStringKey] = "a"
        }

        val preferences = preferencesDataStore.data.filter {
            // We may possibly receive the existing item as a replay, so skip this.
            it[aStringKey] == "a"
        }.first()

        val preferences2 = registry.preferencesFlow(TargetNodeId.ThisNodeId, path).first()

        assertThat(preferences).isEqualTo(preferences2)
        assertThat(preferences[aStringKey]).isEqualTo("a")

        scope.cancel()
    }

    companion object {
        val aStringKey = stringPreferencesKey("aString")
    }
}

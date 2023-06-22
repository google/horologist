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

package com.google.android.horologist.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.data.store.prefs.PreferencesSerializer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

@Ignore("https://github.com/google/horologist/issues/1191")
class WearLocalDataStoreTest {

    @get:Rule
    public val testName: TestName = TestName()

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    val testScope = TestScope()

    val scope = CoroutineScope(testScope.coroutineContext + Job())

    private lateinit var preferencesDataStore: DataStore<Preferences>

    private lateinit var registry: WearDataLayerRegistry

    private lateinit var path: String

    @Before
    fun setUp() {
        registry = WearDataLayerRegistry.fromContext(context, scope)

        path =
            WearDataLayerRegistry.dataStorePath(Preferences::class) + "-" + testName.methodName

        preferencesDataStore = registry.protoDataStore(
            path,
            scope,
            registry.serializers.serializerForType<Preferences>()
        )
    }

    @Test
    fun testPreferencesDataStore() = testScope.runTest {
        preferencesDataStore.edit {
            it.clear()
        }

        val aStringKey = stringPreferencesKey("aString")

        preferencesDataStore.edit {
            it[aStringKey] = "a"
        }

        val preferences = preferencesDataStore.data.filter {
            // We may possibly receive the existing item as a replay, so skip this.
            it[aStringKey] == "a"
        }.first()

        val preferences2 =
            registry.protoFlow(TargetNodeId.ThisNodeId, PreferencesSerializer, path).first()

        assertThat(preferences).isEqualTo(preferences2)
        assertThat(preferences[aStringKey]).isEqualTo("a")

        scope.cancel()
    }

    @Test
    fun testConcurrentWrites() = testScope.runTest {
        preferencesDataStore.edit {
            it.clear()
        }

        val map: Map<String, Int> = (0 until 10).associateBy { it.toString() }

        coroutineScope {
            map.forEach { (key, value) ->
                launch {
                    preferencesDataStore.edit {
                        it[intPreferencesKey(key)] = value
                    }
                }
            }
        }

        val preferences =
            registry.protoFlow(TargetNodeId.ThisNodeId, PreferencesSerializer, path).first()

        assertThat(preferences.asMap().mapKeys { it.key.name }).isEqualTo(map)

        scope.cancel()
    }
}

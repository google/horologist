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

package com.google.android.horologist.data

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WearDataLayerRegistryTest {

    @Test
    fun testPreferencesDataStore() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val registry = WearDataLayerRegistry.fromContext(context)

        val preferencesDataStore = registry.preferencesDataStore(
            WearDataLayerRegistry.preferencesPath("settings"),
            this
        )

        preferencesDataStore.updateData {
            it?.toMutablePreferences()?.apply {
                this[aStringKey] = "a"
            }
        }

        val preferences = preferencesDataStore.data.first()

        val preferences2 = registry.
    }

    companion object {
        val aStringKey = stringPreferencesKey("aString")
    }
}

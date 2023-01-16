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

package com.google.android.horologist.test.toolbox.testdoubles

import androidx.datastore.core.DataStore
import com.google.android.horologist.mediasample.domain.proto.SettingsProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDataStore<T> : DataStore<T> {
    override val data: Flow<T>
        get() = flow {
            SettingsProto.Settings.getDefaultInstance()
        }

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        TODO("Not yet implemented")
    }
}

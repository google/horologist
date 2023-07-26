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

package com.google.android.horologist.auth.data.phone.tokenshare.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.android.horologist.auth.data.phone.tokenshare.TokenBundleRepository
import com.google.android.horologist.auth.data.phone.tokenshare.impl.TokenBundleRepositoryImpl.Companion.DEFAULT_TOKEN_BUNDLE_KEY
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.data.WearableApiAvailability
import kotlinx.coroutines.CoroutineScope

/**
 * Default implementation for [TokenBundleRepository].
 *
 * If multiple [token bundles][T] are required to be shared, specify a [key] in
 * order to identify each one, otherwise the same [default][DEFAULT_TOKEN_BUNDLE_KEY] key
 * will be used and only a single token bundle will be persisted.
 *
 * @sample com.google.android.horologist.auth.sample.MainActivity
 */
public class TokenBundleRepositoryImpl<T>(
    private val registry: WearDataLayerRegistry,
    private val key: String = DEFAULT_TOKEN_BUNDLE_KEY,
    private val coroutineScope: CoroutineScope,
    private val serializer: Serializer<T>
) : TokenBundleRepository<T> {

    override suspend fun update(tokenBundle: T) {
        getDataStore()?.updateData { tokenBundle }
    }

    override suspend fun isAvailable(): Boolean =
        WearableApiAvailability.isAvailable(registry.dataClient)

    private suspend fun getDataStore(): DataStore<T>? =
        if (isAvailable()) {
            registry.protoDataStore(
                path = buildPath(key),
                coroutineScope = coroutineScope,
                serializer = serializer
            )
        } else {
            null
        }

    private fun buildPath(key: String) =
        if (key.startsWith("/")) {
            key
        } else {
            "/$key"
        }

    public companion object {
        private const val DEFAULT_TOKEN_BUNDLE_KEY = "/horologist_token_bundle"
    }
}

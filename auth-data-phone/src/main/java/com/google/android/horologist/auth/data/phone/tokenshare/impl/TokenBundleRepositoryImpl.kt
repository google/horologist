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
import com.google.android.horologist.auth.data.phone.ExperimentalHorologistAuthDataPhoneApi
import com.google.android.horologist.auth.data.phone.tokenshare.TokenBundleRepository
import com.google.android.horologist.data.WearDataLayerRegistry
import kotlinx.coroutines.CoroutineScope

/**
 * Default implementation for [TokenBundleRepository].
 */
@ExperimentalHorologistAuthDataPhoneApi
public class TokenBundleRepositoryImpl<T>(
    private val dataStore: DataStore<T>
) : TokenBundleRepository<T> {

    override suspend fun update(tokenBundle: T) {
        dataStore.updateData { tokenBundle }
    }

    public companion object {

        private const val DEFAULT_TOKEN_BUNDLE_KEY = "/horologist_token_bundle"

        /**
         * Factory method for [TokenBundleRepositoryImpl].
         *
         * If multiple [token bundles][T] are required to be shared, specify a [key] in
         * order to identify each one, otherwise the same [default][DEFAULT_TOKEN_BUNDLE_KEY] key
         * will be used and only a single token bundle will be persisted.
         */
        public fun <T> create(
            registry: WearDataLayerRegistry,
            key: String = DEFAULT_TOKEN_BUNDLE_KEY,
            coroutineScope: CoroutineScope,
            serializer: Serializer<T>
        ): TokenBundleRepositoryImpl<T> = TokenBundleRepositoryImpl(
            registry.protoDataStore(
                path = buildPath(key),
                coroutineScope = coroutineScope,
                serializer = serializer
            )
        )

        private fun buildPath(key: String) =
            if (key.startsWith("/")) {
                key
            } else {
                "/$key"
            }
    }
}

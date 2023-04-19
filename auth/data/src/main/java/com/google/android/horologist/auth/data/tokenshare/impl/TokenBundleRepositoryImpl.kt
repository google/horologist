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

package com.google.android.horologist.auth.data.tokenshare.impl

import androidx.datastore.core.Serializer
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.auth.data.tokenshare.TokenBundleRepository
import com.google.android.horologist.data.TargetNodeId
import com.google.android.horologist.data.WearDataLayerRegistry
import kotlinx.coroutines.flow.Flow

/**
 * Default implementation of [TokenBundleRepository].
 *
 * @sample com.google.android.horologist.auth.sample.screens.tokenshare.customkey.TokenShareCustomKeyViewModel
 * @sample com.google.android.horologist.auth.sample.screens.tokenshare.defaultkey.TokenShareDefaultKeyViewModel
 */
@ExperimentalHorologistApi
public class TokenBundleRepositoryImpl<T>(
    private val registry: WearDataLayerRegistry,
    private val serializer: Serializer<T>,
    private val path: String
) : TokenBundleRepository<T> {

    override val flow: Flow<T>
        get() = registry.protoFlow(
            targetNodeId = TargetNodeId.PairedPhone,
            serializer = serializer,
            path = path
        )

    public companion object {

        private const val DEFAULT_TOKEN_BUNDLE_KEY = "/horologist_token_bundle"

        /**
         * Factory method for [TokenBundleRepositoryImpl].
         *
         * If multiple [token bundles][T] are available, specify the [key] of the specific
         * token bundle wished to be retrieved. Otherwise the token bundle stored with the
         * [default][DEFAULT_TOKEN_BUNDLE_KEY] key will be used.
         */
        public fun <T> create(
            registry: WearDataLayerRegistry,
            serializer: Serializer<T>,
            key: String = DEFAULT_TOKEN_BUNDLE_KEY
        ): TokenBundleRepositoryImpl<T> = TokenBundleRepositoryImpl(
            registry = registry,
            serializer = serializer,
            path = buildPath(key)
        )

        private fun buildPath(key: String) =
            if (key.startsWith("/")) {
                key
            } else {
                "/$key"
            }
    }
}

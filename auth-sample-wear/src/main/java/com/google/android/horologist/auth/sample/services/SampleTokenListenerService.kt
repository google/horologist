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

package com.google.android.horologist.auth.sample.services

import android.util.Log
import com.google.android.horologist.auth.data.tokenshare.TokenListenerService
import com.google.android.horologist.auth.sample.SampleApplication
import kotlinx.coroutines.CoroutineScope

class SampleTokenListenerService : TokenListenerService() {

    override fun getCoroutineScope(): CoroutineScope =
        (application as SampleApplication).servicesCoroutineScope

    override suspend fun onTokenReceived(token: String) {
        // This class does not do anything and is only here for sample purposes.
        Log.d(TAG, "Token received: $token")
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}

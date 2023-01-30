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

package com.google.android.horologist.auth.data.tokenshare

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import com.google.android.horologist.auth.data.ExperimentalHorologistAuthDataApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Base service class for applications wishing to receive auth token via data layer events.
 *
 * You should consider using [TokenListener] instead of this service so the app doesn't get started
 * every time there is a token update.
 *
 * Must include the appropriate registration in the manifest. Such as
 *
 * ```
 * <service
 *   android:name=".SampleTokenListenerService"
 *   android:exported="true">
 *   <intent-filter>
 *     <action android:name="com.google.android.gms.wearable.DATA_CHANGED" />
 *     <data android:scheme="wear" android:host="*" android:pathPrefix="/horologist_auth" />
 *   </intent-filter>
 * </service>
 * ```
 */
@ExperimentalHorologistAuthDataApi
public abstract class TokenListenerService : WearableListenerService() {

    /**
     * Should return the [CoroutineScope] on which the execution of this service's functions should
     * run.
     */
    public abstract fun getCoroutineScope(): CoroutineScope

    /**
     * Called when a token is received.
     */
    public abstract suspend fun onTokenReceived(token: String): Unit

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        OnDataChangedHandler.handle(dataEventBuffer) { token ->
            getCoroutineScope().launch {
                onTokenReceived(token)
            }
        }
    }
}

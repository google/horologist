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

package com.google.android.horologist.networks.logging

import android.util.Log
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.RequestType

@ExperimentalHorologistNetworksApi
public interface NetworkStatusLogger {
    public fun logNetworkEvent(event: String, error: Boolean = false)
    public fun logJobEvent(event: String, error: Boolean = false)
    public fun debugNetworkEvent(event: String)

    public fun logNetworkResponse(
        requestType: RequestType,
        networkInfo: NetworkInfo,
        bytesTransferred: Long
    )

    @ExperimentalHorologistNetworksApi
    public object Logging : NetworkStatusLogger {
        override fun logNetworkEvent(event: String, error: Boolean) {
            Log.println(if (error) Log.WARN else Log.INFO, "networks", event)
        }

        override fun logJobEvent(event: String, error: Boolean) {
            Log.println(if (error) Log.WARN else Log.INFO, "networks", event)
        }

        override fun debugNetworkEvent(event: String) {
            Log.d("networks", event)
        }

        override fun logNetworkResponse(
            requestType: RequestType,
            networkInfo: NetworkInfo,
            bytesTransferred: Long
        ) {
            Log.d("networks", "response $requestType ${networkInfo.type} ${bytesTransferred}B")
        }
    }

    @ExperimentalHorologistNetworksApi
    public class InMemory : NetworkStatusLogger {
        public val events: MutableList<String> = mutableListOf<String>()

        override fun logNetworkEvent(event: String, error: Boolean) {
            events.add(event)
        }

        override fun logJobEvent(event: String, error: Boolean) {
            events.add(event)
        }

        override fun debugNetworkEvent(event: String) {
            events.add(event)
        }

        override fun logNetworkResponse(
            requestType: RequestType,
            networkInfo: NetworkInfo,
            bytesTransferred: Long
        ) {
            val event = "response $requestType ${networkInfo.type} ${bytesTransferred}B"
            events.add(event)
        }
    }
}

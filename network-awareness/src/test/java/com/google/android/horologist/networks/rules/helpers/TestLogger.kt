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

package com.google.android.horologist.networks.rules.helpers

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.logging.NetworkStatusLogger

@OptIn(ExperimentalHorologistApi::class)
public class TestLogger : NetworkStatusLogger {
    public val events: MutableList<String> = mutableListOf()

    override fun logNetworkEvent(event: String, error: Boolean) {
        println(event)
        events.add(event)
    }

    override fun logJobEvent(event: String, error: Boolean) {
        println(event)
        events.add(event)
    }

    override fun debugNetworkEvent(event: String) {
        println(event)
        events.add(event)
    }

    override fun logNetworkResponse(
        requestType: RequestType,
        networkInfo: NetworkInfo,
        bytesTransferred: Long
    ) {
        val event = "response $requestType ${networkInfo.type} ${bytesTransferred}B"
        println(event)
        events.add(event)
    }
}

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

package com.google.android.horologist.networks

import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

public class InMemoryStatusLogger : NetworkStatusLogger {
    public val events: MutableStateFlow<List<Event>> = MutableStateFlow(listOf())

    override fun logNetworkEvent(event: String, error: Boolean) {
        add(Event.Message(event))
    }

    private fun add(message: Event) {
        events.update {
            it + message
        }
    }

    override fun logJobEvent(event: String, error: Boolean) {
        add(Event.Message(event))
    }

    override fun debugNetworkEvent(event: String) {
        println(event)
    }

    override fun logNetworkResponse(
        requestType: RequestType,
        networkInfo: NetworkInfo,
        bytesTransferred: Long
    ) {
        val event = "response $requestType ${networkInfo.type} ${bytesTransferred}B"
        add(Event.NetworkResponse(event, requestType, networkInfo, bytesTransferred))
    }

    sealed interface Event {
        val message: String

        data class NetworkResponse(
            override val message: String,
            val requestType: RequestType,
            val networkInfo: NetworkInfo,
            val bytesTransferred: Long
        ) : Event

        data class Message(override val message: String) : Event
    }
}

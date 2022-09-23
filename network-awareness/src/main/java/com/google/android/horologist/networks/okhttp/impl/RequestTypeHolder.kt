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

package com.google.android.horologist.networks.okhttp.impl

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.highbandwidth.HighBandwidthConnectionLease
import com.google.android.horologist.networks.okhttp.requestTypeOrNull
import okhttp3.Request

/**
 * Payload logic to carry additional request/call details on the
 * Request object, for use by this libraries interceptors and event
 * listeners.
 */
@ExperimentalHorologistNetworksApi
public data class RequestTypeHolder(
    public var requestType: RequestType = RequestType.UnknownRequest,
    public var networkInfo: NetworkInfo? = null,
    public var highBandwidthConnectionLease: HighBandwidthConnectionLease? = null
) {
    override fun toString(): String {
        return "$requestType/$networkInfo"
    }

    public companion object {
        @ExperimentalHorologistNetworksApi
        public fun Request.withDefaultRequestType(defaultRequestType: RequestType): Request =
            if (requestTypeOrNull == null) {
                newBuilder()
                    .requestType(defaultRequestType)
                    .build()
            } else {
                this
            }

        public fun Request.Builder.requestType(value: RequestType): Request.Builder {
            return this.tag(RequestTypeHolder::class.java, RequestTypeHolder(requestType = value))
        }
    }
}

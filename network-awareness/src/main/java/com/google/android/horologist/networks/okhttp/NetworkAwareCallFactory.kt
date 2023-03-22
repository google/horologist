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

package com.google.android.horologist.networks.okhttp

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.okhttp.impl.RequestTypeHolder.Companion.withDefaultRequestType
import okhttp3.Call
import okhttp3.Request

/**
 * [Call.Factory] wrapper that sets of known [Request.requestType] that a shared
 * [NetworkSelectingCallFactory] can make network decisions and bring up high bandwidth networks
 * based on the request type.
 */
@ExperimentalHorologistApi
public class NetworkAwareCallFactory(
    private val delegate: Call.Factory,
    private val defaultRequestType: RequestType
) : Call.Factory {
    override fun newCall(request: Request): Call {
        val finalRequest = request.withDefaultRequestType(defaultRequestType)
        return delegate.newCall(finalRequest)
    }
}

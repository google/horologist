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
import com.google.android.horologist.networks.okhttp.impl.RequestTypeHolder.Companion.requestType
import com.google.android.horologist.networks.okhttp.requestType
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.Timeout

/**
 * A standard call Wrapper, that exists solely to ensure that the
 * [NetworkSelectingCallFactory] is used for [clone].
 */
@ExperimentalHorologistNetworksApi
internal class StandardCall(
    private val callFactory: Call.Factory,
    private val delegate: Call
) : Call {
    override fun cancel(): Unit = delegate.cancel()

    override fun clone(): Call {
        val request = request()
        // Remove network and lease from new request
        val cleanRequest = request.newBuilder().requestType(request.requestType).build()
        return callFactory.newCall(cleanRequest)
    }

    override fun enqueue(responseCallback: Callback) {
        return delegate.enqueue(responseCallback)
    }

    override fun execute(): Response {
        return delegate.execute()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled()

    override fun isExecuted(): Boolean = delegate.isExecuted()

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}

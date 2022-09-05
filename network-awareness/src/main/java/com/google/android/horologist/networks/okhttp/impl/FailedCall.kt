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

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import okio.Timeout

/**
 * A call that must fail because no suitable network is available.
 */
internal class FailedCall(
    private val callFactory: Call.Factory,
    private val request: Request,
    private val message: String
) : Call {
    private var cancelled = false
    private var executed = false

    override fun cancel() {
        cancelled = true
    }

    override fun clone(): Call = callFactory.newCall(request)

    override fun enqueue(responseCallback: Callback) {
        responseCallback.onFailure(this, IOException(message))
    }

    override fun execute(): Response {
        throw IOException(message)
    }

    override fun isCanceled(): Boolean = cancelled

    override fun isExecuted(): Boolean = executed

    override fun request(): Request = request

    override fun timeout(): Timeout = Timeout.NONE
}

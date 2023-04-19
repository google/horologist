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

import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.okhttp.networkInfo
import com.google.android.horologist.networks.okhttp.requestType
import com.google.android.horologist.networks.rules.ForbiddenRequest
import com.google.android.horologist.networks.rules.NetworkingRulesEngine
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that short circuits and requests on unsuitable
 * networks, such that accidental downloads over expensive networks are
 * not possible.
 *
 * No specific rules are implemented, instead deferring to
 * [com.google.android.horologist.networks.rules.NetworkingRules]
 */
@ExperimentalHorologistApi
public class RequestVerifyingInterceptor(
    private val networkingRulesEngine: NetworkingRulesEngine
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestType = request.requestType
        val networkType = request.networkInfo

        val check = networkingRulesEngine.checkValidRequest(requestType, networkType)

        if (check.isForbidden) {
            throw ForbiddenRequest("Request $requestType is forbidden on $networkType")
        }

        return chain.proceed(request)
    }
}

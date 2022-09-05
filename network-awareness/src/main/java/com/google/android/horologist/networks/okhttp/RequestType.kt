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

@file:OptIn(ExperimentalHorologistNetworksApi::class)

package com.google.android.horologist.networks.okhttp

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.RequestType
import com.google.android.horologist.networks.highbandwidth.HighBandwithConnectionLease
import com.google.android.horologist.networks.okhttp.impl.RequestTypeHolder
import okhttp3.Request

public val Request.requestType: RequestType
    get() = requestTypeOrNull ?: RequestType.UnknownRequest

public val Request.requestTypeOrNull: RequestType?
    get() = this.tag(RequestTypeHolder::class.java)?.requestType

public var Request.networkInfo: NetworkInfo?
    get() {
        return this.tag(RequestTypeHolder::class.java)!!.networkInfo
    }
    set(value) {
        this.tag(RequestTypeHolder::class.java)!!.networkInfo = value
    }

public var Request.highBandwithConnectionLease: HighBandwithConnectionLease?
    get() {
        return this.tag(RequestTypeHolder::class.java)!!.highBandwithConnectionLease
    }
    set(value) {
        this.tag(RequestTypeHolder::class.java)!!.highBandwithConnectionLease = value
    }

/*
 * Copyright 2022 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.networks.okhttp

import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.RequestType
import okhttp3.Request

public data class RequestTypeHolder(
    public var requestType: RequestType = RequestType.UnknownRequest,
    public var networkType: NetworkType? = null
) {
    override fun toString(): String {
        return "$requestType/$networkType"
    }

    public companion object {
        public val Request.requestTypeOrNull: RequestType?
            get() = this.tag(RequestTypeHolder::class.java)?.requestType

        public val Request.requestType: RequestType
            get() = requestTypeOrNull ?: RequestType.UnknownRequest

        public var Request.networkType: NetworkType?
            get() {
                return this.tag(RequestTypeHolder::class.java)!!.networkType
            }
            set(value) {
                this.tag(RequestTypeHolder::class.java)!!.networkType = value
            }

        public fun Request.Builder.requestType(value: RequestType): Request.Builder {
            return this.tag(RequestTypeHolder::class.java, RequestTypeHolder(requestType = value))
        }
    }
}

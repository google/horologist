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

package com.google.android.horologist.networks.data

import android.net.LinkProperties
import android.net.NetworkCapabilities
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import java.net.Inet6Address
import java.net.InetAddress
import java.net.Socket

@ExperimentalHorologistNetworksApi
public data class NetworkStatus(
    public val id: String,
    public val status: Status,
    public val type: NetworkType,
    public val addresses: List<InetAddress>,
    public val capabilities: NetworkCapabilities?,
    public val linkProperties: LinkProperties?,
    public val bindSocket: (Socket) -> Unit
) {
    public val firstAddress: InetAddress?
        get() = addresses.minByOrNull { it is Inet6Address }

    override fun toString(): String {
        return "NetworkStatus(id=$id, status=$status, type=$type, addresses=$addresses)"
    }
}

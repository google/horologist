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

import android.net.Network
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.NetworkStatus
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

/**
 * SocketFactory that only creates connections on a specific Network Address.
 * Uses [Network.bindSocket] to control the network on outgoing socket connections.
 */
@ExperimentalHorologistNetworksApi
internal class NetworkSpecificSocketFactory(
    private val networkStatus: NetworkStatus,
    private val socketFactory: SocketFactory = getDefault()
) : SocketFactory() {
    private fun Socket.bindToPreferredNetwork(): Socket {
        networkStatus.bindSocket(this)
        return this
    }

    override fun createSocket(): Socket {
        return socketFactory.createSocket().bindToPreferredNetwork()
    }

    override fun createSocket(host: String, port: Int): Socket {
        return socketFactory.createSocket(host, port).bindToPreferredNetwork()
    }

    override fun createSocket(host: InetAddress, port: Int): Socket {
        return socketFactory.createSocket(host, port).bindToPreferredNetwork()
    }

    // Unsupported operations

    override fun createSocket(
        host: String,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ): Socket {
        return socketFactory.createSocket(host, port, localHost, localPort)
    }

    override fun createSocket(
        address: InetAddress,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket {
        return socketFactory.createSocket(address, port, localAddress, localPort)
    }
}

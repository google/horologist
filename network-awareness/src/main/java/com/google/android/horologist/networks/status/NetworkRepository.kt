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

package com.google.android.horologist.networks.status

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.activity.ComponentActivity
import com.google.android.horologist.networks.data.NetworkStatus
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.data.Status
import com.google.android.horologist.networks.logging.NetworkStatusLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@SuppressLint("MissingPermission")
public class NetworkRepository(
    private val connectivityManager: ConnectivityManager,
    private val coroutineScope: CoroutineScope,
    private val logger: NetworkStatusLogger,
) {
    private val networks = ConcurrentHashMap<String, Network>()
    private val networkBuilders = ConcurrentHashMap<String, NetworkStatusBuilder>()
    private val linkAddresses = ConcurrentHashMap<InetAddress, String>()

    private var priorityNetwork: Network? = null
    private var initialised = false

    private val _networkStatus: MutableStateFlow<Networks>
    public val networkStatus: StateFlow<Networks>
        get() = _networkStatus

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            networks[network.toString()] = network
            getOrBuild(network, Status.Available)

            postUpdate()
        }

        override fun onLost(network: Network) {
            val networkString = network.toString()
            getOrBuild(network, Status.Lost)

            coroutineScope.launch {
                delay(5000)
                if (networkBuilders[networkString]?.status == Status.Lost) {
                    networks.remove(networkString)
                }
            }

            postUpdate()
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            getOrBuild(network, Status.Losing(Instant.now().plusMillis(maxMsToLive.toLong())))

            postUpdate()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            getOrBuild(network).networkCapabilities = networkCapabilities
            postUpdate()
        }

        override fun onLinkPropertiesChanged(
            network: Network,
            networkLinkProperties: LinkProperties
        ) {
            getOrBuild(network).apply {
                linkProperties = networkLinkProperties
                networkLinkProperties.linkAddresses.forEach {
                    linkAddresses[it.address] = network.toString()
                }
            }
            postUpdate()
        }
    }

    // TODO consider deferring the init work here.
    init {
        // TODO check this ordering is ok? read before subscribe.
        @Suppress("DEPRECATION")
        connectivityManager.allNetworks.forEach { network ->
            getOrBuild(network).apply {
                connectivityManager.getNetworkCapabilities(network)?.let {
                    this.networkCapabilities = it
                }
                connectivityManager.getLinkProperties(network)?.let {
                    this.linkProperties = it
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        _networkStatus = MutableStateFlow(buildStatus())
        initialised = true
    }

    private fun getOrBuild(network: Network, status: Status? = null): NetworkStatusBuilder {
        return networkBuilders.getOrPut(
            network.toString()
        ) {
            NetworkStatusBuilder(network = network, id = network.toString())
        }.apply {
            if (status != null) {
                this.status = status
            }
        }
    }

    private fun postUpdate() {
        if (initialised) {
            _networkStatus.value = buildStatus()
        }
    }

    public fun close() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun buildStatus(): Networks {
        val allNetworks = networkBuilders.filter { (id, builder) ->
            builder.status != Status.Lost
        }.values.map { it.buildNetworkStatus() }

        val priorityNetwork = priorityNetwork
        if (priorityNetwork != null) {
            val priorityNetworkStatus = allNetworks.find { it.id == priorityNetwork.id }
            if (priorityNetworkStatus != null) {
                return Networks(
                    activeNetwork = priorityNetworkStatus,
                    networks = allNetworks
                )
            }
        }

        val connectedNetwork = connectivityManager.activeNetwork
        val activeNetwork = allNetworks.find { it.id == connectedNetwork?.id }

        return Networks(
            activeNetwork = activeNetwork,
            networks = allNetworks
        )
    }

    internal fun networkByAddress(localAddress: InetAddress): NetworkStatus? {
        val network = linkAddresses[localAddress]?.let {
            networkBuilders[it]?.buildNetworkStatus()
        }

        // TODO assume null is bluetooth if available
        @Suppress("FoldInitializerAndIfToElvis")
        if (network == null) {
            return networkBuilders.values.firstOrNull { it.type is NetworkType.Bluetooth }
                ?.buildNetworkStatus()
        }

        return network
    }

    private val highBandwidthListener = object : HighBandwidthRequester.HighbandwidthListener {
        override fun onHighbandwidthAvailable(priorityNetwork: Network) {
            this@NetworkRepository.priorityNetwork = priorityNetwork
            postUpdate()
        }

        override fun onHighbandwidthUnAvailable() {
            this@NetworkRepository.priorityNetwork = null
            postUpdate()
        }
    }

    public companion object {
        public fun fromContext(
            application: Context,
            coroutineScope: CoroutineScope,
            logger: NetworkStatusLogger
        ): NetworkRepository {
            val connectivityManager =
                application.getSystemService(ComponentActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
            return NetworkRepository(
                connectivityManager,
                coroutineScope,
                logger,
            )
        }
    }
}

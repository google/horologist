/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.networks.request

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Handler
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.id
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetwork
import org.robolectric.shadows.ShadowNetworkCapabilities
import org.robolectric.shadows.ShadowNetworkInfo

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class NetworkRequesterTest {
    val connectivityManager = ApplicationProvider.getApplicationContext<Context>()
        .getSystemService(ConnectivityManager::class.java)
    val networkRequester = NetworkRequesterImpl(connectivityManager)
    val shadowConnectivityManager = shadowOf(connectivityManager)

    val request = HighBandwidthRequest(HighBandwidthRequest.Type.CellOnly)

    @Test
    fun returnsResult() {
        val lease = networkRequester.requestHighBandwidthNetwork(request)

        val callback = shadowConnectivityManager.networkCallbacks.first()

        val cellNetwork: Network = ShadowNetwork.newInstance(123)

        @Suppress("DEPRECATION")
        val cellNetworkInfo: NetworkInfo =
            ShadowNetworkInfo.newInstance(
                NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_VPN,
                0,
                true,
                NetworkInfo.State.CONNECTED,
            )
        val cellCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(cellCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        shadowConnectivityManager.addNetwork(cellNetwork, cellNetworkInfo)
        shadowConnectivityManager.setNetworkCapabilities(cellNetwork, cellCapabilities)

        callback.onAvailable(cellNetwork)

        assertThat(lease.grantedNetwork.value).isEqualTo(
            NetworkReference(
                id = cellNetwork.id,
                type = NetworkType.Cell,
            ),
        )
    }

    @Test
    fun returnsUnavailable() {
        val lease = networkRequester.requestHighBandwidthNetwork(request)

        val callback = shadowConnectivityManager.networkCallbacks.first()

        callback.onUnavailable()

        assertThat(lease.grantedNetwork.value).isNull()
    }

    @Test
    @Config(shadows = [FailingConnectivityManager::class])
    fun catchesTooManyRequestsException() {
        val lease = networkRequester.requestHighBandwidthNetwork(request)

        assertThat(lease.grantedNetwork.value).isNull()
    }
}

@Implements(ConnectivityManager::class)
public class FailingConnectivityManager : ShadowConnectivityManager() {
    override fun registerNetworkCallback(
        request: NetworkRequest,
        networkCallback: ConnectivityManager.NetworkCallback,
        handler: Handler,
    ) {
        throw RuntimeException("TooManyRequestsException")
    }
}

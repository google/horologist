/*
 * Copyright 2023 The Android Open Source Project
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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.networks.rules

import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.NetworkType.Wifi
import com.google.android.horologist.networks.highbandwidth.StandardHighBandwidthNetworkMediator
import com.google.android.horologist.networks.request.HighBandwidthRequest
import com.google.android.horologist.networks.rules.helpers.TestLogger
import com.google.android.horologist.networks.testdoubles.FakeNetworkRepository
import com.google.android.horologist.networks.testdoubles.FakeNetworkRequester
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class StandardHighBandwidthNetworkMediatorTest {
    private val testScope = TestScope()
    private val networkRepository = FakeNetworkRepository()
    private val logger = TestLogger()
    private val networkRequester = FakeNetworkRequester(networkRepository)
    private val delayToRelease = 3.seconds
    private val highBandwidthRequester = StandardHighBandwidthNetworkMediator(
        logger,
        networkRequester,
        testScope,
        delayToRelease,
    )
    val wifiRequest = HighBandwidthRequest(HighBandwidthRequest.Type.WifiOnly)

    @Test
    fun requestAndRelease() = testScope.runTest {
        val lease = highBandwidthRequester.requestHighBandwidthNetwork(wifiRequest)

        advanceUntilIdle()

        assertThat(highBandwidthRequester.pinned.first()).isEqualTo(setOf(Wifi))

        lease.close()
        advanceUntilIdle()

        assertThat(highBandwidthRequester.pinned.first()).isEqualTo(setOf<NetworkType>())
    }

    @Test
    fun closeTwiceIsSafe() = testScope.runTest {
        val lease = highBandwidthRequester.requestHighBandwidthNetwork(wifiRequest)

        lease.awaitGranted(2.seconds)

        lease.close()
        lease.close()
    }

    @Test
    fun requestBeforeRelease() = testScope.runTest {
        val lease1 = highBandwidthRequester.requestHighBandwidthNetwork(wifiRequest)

        advanceUntilIdle()

        assertThat(highBandwidthRequester.pinned.first()).isEqualTo(setOf(Wifi))

        lease1.close()

        advanceTimeBy((delayToRelease / 2.0).inWholeMilliseconds)

        assertThat(highBandwidthRequester.pinned.first()).isEqualTo(setOf(Wifi))

        val lease2 = highBandwidthRequester.requestHighBandwidthNetwork(wifiRequest)

        assertThat(highBandwidthRequester.pinned.first()).isEqualTo(setOf(Wifi))

        advanceUntilIdle()

        lease2.close()
        advanceUntilIdle()

        assertThat(highBandwidthRequester.pinned.first()).isEqualTo(setOf<NetworkType>())
    }
}

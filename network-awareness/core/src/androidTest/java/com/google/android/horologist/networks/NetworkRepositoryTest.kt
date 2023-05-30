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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.google.android.horologist.networks

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.horologist.networks.data.id
import com.google.android.horologist.networks.status.NetworkRepositoryImpl
import com.google.common.truth.Truth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("https://github.com/google/horologist/issues/1191")
@MediumTest
class NetworkRepositoryTest {
    private lateinit var networkRepository: NetworkRepositoryImpl
    private lateinit var context: Context
    val scope = TestScope()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))

        context = InstrumentationRegistry.getInstrumentation().context
        networkRepository =
            NetworkRepositoryImpl.fromContext(context, scope)
    }

    @After
    fun tearDown() {
        networkRepository.close()

        Dispatchers.resetMain()
    }

    @Suppress("DEPRECATION")
    @Test
    public fun testNetworks() = scope.runTest {
        val networks = networkRepository.networkStatus.value

        Truth.assertThat(networks.networks).isNotEmpty()

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkIds = connectivityManager.allNetworks.map { it.toString() }
        val activeNetworkId = connectivityManager.activeNetwork?.id

        val map: List<String> = networks.networks.map { it.id }
        Truth.assertThat(map).containsExactlyElementsIn(networkIds)
        Truth.assertThat(networks.activeNetwork?.id).isEqualTo(activeNetworkId)
    }
}

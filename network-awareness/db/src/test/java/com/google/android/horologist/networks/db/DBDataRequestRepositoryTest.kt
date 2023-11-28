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

package com.google.android.horologist.networks.db

import com.google.android.horologist.networks.data.DataRequest
import com.google.android.horologist.networks.data.NetworkInfo
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.RequestType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
class DBDataRequestRepositoryTest {
    @Test
    fun testInsertAndRead(): Unit = runBlocking {
        val db = NetworkUsageDatabase.getDatabase(RuntimeEnvironment.getApplication())
        val dao = db.networkUsageDao()
        val repository = DBDataRequestRepository(dao, this)

        assertThat(repository.currentPeriodUsage().first().dataByType).containsExactlyEntriesIn(
            mapOf(
                NetworkType.Wifi to 0L,
                NetworkType.BT to 0L,
                NetworkType.Cell to 0L,
                NetworkType.Unknown to 0L,
            ),
        )

        val wifiInfo = NetworkInfo.Wifi("wlan1", "Pretty Fly For a Wifi")
        val btInfo = NetworkInfo.Bluetooth("bt1")
        repository.storeRequest(
            DataRequest(
                requestType = RequestType.ImageRequest,
                wifiInfo,
                1L,
            ),
        )
        repository.storeRequest(
            DataRequest(
                requestType = RequestType.LogsRequest,
                wifiInfo,
                100L,
            ),
        )
        repository.storeRequest(
            DataRequest(
                requestType = RequestType.ApiRequest,
                btInfo,
                10L,
            ),
        )

        delay(1000)

        assertThat(repository.currentPeriodUsage().first().dataByType).containsExactlyEntriesIn(
            mapOf(
                NetworkType.Wifi to 101L,
                NetworkType.BT to 10L,
                NetworkType.Cell to 0L,
                NetworkType.Unknown to 0L,
            ),
        )
    }
}

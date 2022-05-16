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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

public interface DataRequestRepository {
    public fun storeRequest(dataRequest: DataRequest)
    public fun currentPeriodUsage(): Flow<DataUsageReport>

    public object InMemoryDataRequestRepository : DataRequestRepository {
        private val from = Instant.now()
        private var ble = 0L
        private var wifi = 0L
        private var cell = 0L
        private var unknown = 0L

        public val _currentPeriodUsage: MutableStateFlow<DataUsageReport> =
            MutableStateFlow(DataUsageReport(dataByType = mapOf(), from = from, to = from))

        override fun currentPeriodUsage(): Flow<DataUsageReport> = _currentPeriodUsage

        override fun storeRequest(dataRequest: DataRequest) {
            when (dataRequest.networkType) {
                is NetworkType.Cellular -> cell += dataRequest.dataBytes
                is NetworkType.Bluetooth -> ble += dataRequest.dataBytes
                is NetworkType.Wifi -> wifi += dataRequest.dataBytes
                is NetworkType.Unknown -> unknown += dataRequest.dataBytes
            }

            _currentPeriodUsage.value =
                DataUsageReport(
                    dataByType = mapOf(
                        NetworkType.cell to cell,
                        NetworkType.ble to ble,
                        NetworkType.wifi to wifi,
                        NetworkType.unknown to unknown,
                    ),
                    from = from, to = Instant.now()
                )
        }
    }
}

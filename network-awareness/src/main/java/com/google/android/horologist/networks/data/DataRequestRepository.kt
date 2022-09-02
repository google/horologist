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

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

@ExperimentalHorologistNetworksApi
public interface DataRequestRepository {
    public fun storeRequest(dataRequest: DataRequest)
    public fun currentPeriodUsage(): Flow<DataUsageReport>

    @ExperimentalHorologistNetworksApi
    public class InMemoryDataRequestRepository : DataRequestRepository {
        private val from = Instant.now()
        private var ble = 0L
        private var wifi = 0L
        private var cell = 0L
        private var unknown = 0L

        private val _currentPeriodUsage: MutableStateFlow<DataUsageReport> =
            MutableStateFlow(DataUsageReport(dataByType = mapOf(), from = from, to = from))

        override fun currentPeriodUsage(): Flow<DataUsageReport> = _currentPeriodUsage

        override fun storeRequest(dataRequest: DataRequest) {
            when (dataRequest.networkInfo) {
                is NetworkInfo.Cellular -> cell += dataRequest.dataBytes
                is NetworkInfo.Bluetooth -> ble += dataRequest.dataBytes
                is NetworkInfo.Wifi -> wifi += dataRequest.dataBytes
                is NetworkInfo.Unknown -> unknown += dataRequest.dataBytes
            }

            _currentPeriodUsage.value =
                DataUsageReport(
                    dataByType = mapOf(
                        NetworkType.Cell to cell,
                        NetworkType.BT to ble,
                        NetworkType.Wifi to wifi,
                        NetworkType.Unknown to unknown
                    ),
                    from = from,
                    to = Instant.now()
                )
        }
    }
}

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

package com.google.android.horologist.networks.db

import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.DataRequest
import com.google.android.horologist.networks.data.DataRequestRepository
import com.google.android.horologist.networks.data.DataUsageReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@ExperimentalHorologistNetworksApi
public class DBDataRequestRepository(
    public val networkUsageDao: NetworkUsageDao,
    public val coroutineScope: CoroutineScope
) : DataRequestRepository {
    public val today: LocalDate = LocalDate.now()

    // TODO update on day roll
    public val day: Int = today.let { it.year * 1000 + it.dayOfYear }

    public val from: Instant = today.atStartOfDay().toInstant(ZoneOffset.UTC)
    public val to: Instant = today.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)

    override fun storeRequest(dataRequest: DataRequest) {
        val bytes = dataRequest.dataBytes
        coroutineScope.launch {
            var rows = networkUsageDao.updateBytes(day, bytes)

            if (rows == 0) {
                rows =
                    networkUsageDao.insert(DataUsage(dataRequest.networkType.typeName, bytes, day))
                        .toInt()

                if (rows == -1) {
                    networkUsageDao.updateBytes(day, bytes)
                }
            }
        }
    }

    override fun currentPeriodUsage(): Flow<DataUsageReport> {
        return networkUsageDao.getRecords(day).map { list ->
            var ble = 0L
            var cell = 0L
            var wifi = 0L
            var unknown = 0L

            list.forEach {
                when (it.networkType) {
                    "ble" -> ble += it.bytesTotal
                    "cell" -> cell += it.bytesTotal
                    "wifi" -> wifi += it.bytesTotal
                    "unknown" -> unknown += it.bytesTotal
                }
            }

            DataUsageReport(
                dataByType = mapOf(
                    "ble" to ble,
                    "cell" to cell,
                    "wifi" to wifi,
                    "unknown" to unknown
                ),
                from = from,
                to = to
            )
        }
    }
}

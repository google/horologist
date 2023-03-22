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

package com.google.android.horologist.networks.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.networks.data.DataUsageReport
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks

@ExperimentalHorologistApi
@Composable
public fun DataUsageTimeText(
    showData: Boolean,
    networkStatus: Networks,
    networkUsage: DataUsageReport?,
    modifier: Modifier = Modifier,
    pinnedNetworks: Set<NetworkType> = setOf()
) {
    val style = CurvedTextStyle(MaterialTheme.typography.caption1)
    val context = LocalContext.current

    if (showData) {
        TimeText(
            modifier = modifier,
            startCurvedContent = {
                curveDataUsage(
                    networkStatus = networkStatus,
                    networkUsage = networkUsage,
                    style = style,
                    context = context,
                    pinnedNetworks = pinnedNetworks
                )
            },
            startLinearContent = {
                LinearDataUsage(
                    networkStatus = networkStatus,
                    networkUsage = networkUsage,
                    style = MaterialTheme.typography.caption1,
                    context = context
                )
            }
        )
    } else {
        TimeText(modifier = modifier)
    }
}

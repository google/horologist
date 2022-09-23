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

package com.google.android.horologist.mediasample.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.foundation.CurvedScope
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.curvedText
import com.google.android.horologist.compose.layout.StateUtils.rememberStateWithLifecycle
import com.google.android.horologist.media3.offload.AudioOffloadStatus
import com.google.android.horologist.networks.ExperimentalHorologistNetworksApi
import com.google.android.horologist.networks.data.DataUsageReport
import com.google.android.horologist.networks.data.NetworkType
import com.google.android.horologist.networks.data.Networks
import com.google.android.horologist.networks.ui.curveDataUsage

@Composable
public fun MediaInfoTimeText(
    mediaInfoTimeTextViewModel: MediaInfoTimeTextViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by rememberStateWithLifecycle(mediaInfoTimeTextViewModel.uiState)

    if (uiState.enabled) {
        MediaInfoTimeText(
            modifier = modifier,
            networkStatus = uiState.networks,
            networkUsage = uiState.dataUsageReport,
            offloadStatus = uiState.audioOffloadStatus,
            pinnedNetworks = uiState.pinnedNetworks
        )
    } else {
        TimeText(modifier = modifier)
    }
}

@Composable
public fun MediaInfoTimeText(
    networkStatus: Networks,
    networkUsage: DataUsageReport?,
    offloadStatus: AudioOffloadStatus?,
    pinnedNetworks: Set<NetworkType>,
    modifier: Modifier = Modifier
) {
    val style = CurvedTextStyle(MaterialTheme.typography.caption3)
    val context = LocalContext.current

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
        endCurvedContent = {
            offloadDataStatus(
                offloadStatus = offloadStatus,
                style = style
            )
        }
    )
}

@ExperimentalHorologistNetworksApi
public fun CurvedScope.offloadDataStatus(
    offloadStatus: AudioOffloadStatus?,
    style: CurvedTextStyle
) {
    if (offloadStatus != null) {
        curvedText(
            text = offloadStatus.trackOffloadDescription(),
            style = style
        )
    }
}

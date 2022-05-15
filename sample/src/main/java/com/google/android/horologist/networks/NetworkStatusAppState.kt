package com.google.android.horologist.networks

import com.google.android.horologist.networks.data.DataUsageReport
import com.google.android.horologist.networks.data.Networks

data class NetworkStatusAppState(
    val networks: Networks,
    val dataUsage: DataUsageReport? = null
)

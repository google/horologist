package com.google.android.horologist.networks.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import com.google.android.horologist.networks.data.DataUsageReport
import com.google.android.horologist.networks.data.Networks

@Composable
public fun DataUsageTimeText(
    showData: Boolean,
    networkStatus: Networks,
    networkUsage: DataUsageReport?,
    modifier: Modifier = Modifier,
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
                    context = context
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

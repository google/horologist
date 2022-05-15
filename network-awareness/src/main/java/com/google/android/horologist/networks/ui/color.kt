package com.google.android.horologist.networks.ui

import androidx.compose.ui.graphics.Color
import com.google.android.horologist.networks.data.Status

internal val Status.color: Color
    get() = when (this) {
        is Status.Available -> Color.Green
        is Status.Losing -> Color.Yellow
        is Status.Lost -> Color.Red
        is Status.Unknown -> Color.Gray
    }

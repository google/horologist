package com.google.android.horologist.media.ui.material3.colorscheme

import androidx.compose.ui.graphics.Color

internal fun Color.toDisabledColor(disabledAlpha: Float = DisabledContentAlpha) =
    this.copy(alpha = this.alpha * disabledAlpha)

internal const val DisabledContentAlpha = 0.38f
internal const val DisabledContainerAlpha = 0.16f
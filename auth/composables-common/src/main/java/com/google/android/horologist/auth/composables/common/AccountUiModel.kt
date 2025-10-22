package com.google.android.horologist.auth.composables.common

import com.google.android.horologist.images.base.paintable.Paintable

/**
 * A UI model to represent an account.
 */
public data class AccountUiModel(
    val email: String,
    val name: String? = null,
    val avatar: Paintable? = null,
)

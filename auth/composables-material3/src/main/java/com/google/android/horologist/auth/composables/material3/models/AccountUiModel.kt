package com.google.android.horologist.auth.composables.material3.models

import com.google.android.horologist.images.base.paintable.Paintable

/**
 * A UI model to represent an account.
 */
public data class AccountUiModel(
    val email: String,
    val name: String,
    val avatar: Paintable? = null,
)
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

package com.google.android.horologist.base.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * An alternative function to [Title] that allows a string resource id to be passed as text.
 */
@Suppress("DEPRECATION")
@Deprecated(
    "Replaced by Title in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "Title(textId, modifier)",
        "com.google.android.horologist.compose.material.Title"
    )
)
@ExperimentalHorologistApi
@Composable
public fun Title(
    @StringRes textId: Int,
    modifier: Modifier = Modifier
) {
    Title(
        text = stringResource(id = textId),
        modifier = modifier
    )
}

/**
 * This composable fulfils the redlines of the following components:
 * - Primary title;
 */
@Deprecated(
    "Replaced by Title in Horologist Material Compose library",
    replaceWith = ReplaceWith(
        "Title(text, modifier)",
        "com.google.android.horologist.compose.material.Title"
    )
)
@ExperimentalHorologistApi
@Composable
public fun Title(
    text: String,
    modifier: Modifier = Modifier
) {
    com.google.android.horologist.compose.material.Title(
        text = text,
        modifier = modifier
    )
}

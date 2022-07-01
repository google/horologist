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

package com.google.android.horologist.mediasample.ui.player

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.google.android.horologist.audio.ui.semantics.CustomSemanticsProperties.iconImageVector
import com.google.android.horologist.mediasample.R

/**
 * Button to toggle a track as a favorite.
 *
 * TODO Currently not persisted anywhere.
 */
@Composable
public fun FavoriteButton(
    modifier: Modifier = Modifier,
) {
    var faved by remember { mutableStateOf(false) }
    Button(
        modifier = modifier
            .size(ButtonDefaults.SmallButtonSize),
        onClick = { faved = !faved },
        colors = ButtonDefaults.iconButtonColors(),
    ) {
        val imageVector = if (faved) Icons.Default.Favorite else Icons.Default.FavoriteBorder

        Icon(
            imageVector = imageVector,
            contentDescription = stringResource(R.string.horologist_favorite_content_description),
            modifier = Modifier.semantics { iconImageVector = imageVector }
        )
    }
}

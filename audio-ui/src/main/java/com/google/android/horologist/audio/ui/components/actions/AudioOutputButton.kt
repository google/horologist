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

package com.google.android.horologist.audio.ui.components.actions

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.google.android.horologist.audio.ui.ExperimentalHorologistAudioUiApi
import com.google.android.horologist.audio.ui.R

/**
 * A button to launch the system bluetooth settings to connect to a headset.
 */
@ExperimentalHorologistAudioUiApi
@Composable
public fun AudioOutputButton(
    onOutputClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onOutputClick,
        modifier = modifier.size(ButtonDefaults.SmallButtonSize),
        enabled = enabled,
        colors = ButtonDefaults.iconButtonColors(),
    ) {
        Icon(
            imageVector = Icons.Default.Radio,
            contentDescription = stringResource(R.string.horologist_audio_output_content_description)
        )
    }
}

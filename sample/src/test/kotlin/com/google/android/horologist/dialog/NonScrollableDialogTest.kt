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

package com.google.android.horologist.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import com.google.android.horologist.audio.ui.R.string.horologist_volume_screen_change_audio_output
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import org.junit.Test
import org.robolectric.annotation.Config

class NonScrollableDialogTest(device: WearDevice) : WearDeviceScreenshotTest(device = device) {
    public override val tolerance: Float = 0.01f

    override fun testName(suffix: String): String =
        "src/test/screenshots/${this.javaClass.simpleName}_${testInfo.methodName}_${device.id}$suffix.png"

    @Test
    fun turnOnBluetoothScrollable() = runTest {
        AlertContent(
            title = "Turn on Bluetooth?",
            onOk = {},
            onCancel = {}
        )
    }

    @Test
    fun turnOnBluetooth() = runTest {
        NonScrollableAlertContent(
            title = "Turn on Bluetooth?",
            onOk = {},
            onCancel = {},
        )
    }

    @Test
    fun changeAudioOutputEn() = runTest {
        NonScrollableAlertContent(
            title = stringResource(horologist_volume_screen_change_audio_output),
            onOk = {},
            onCancel = {},
        )
    }

    @Test
    @Config(qualifiers = "+ka")
    fun changeAudioOutputKa() = runTest {
        NonScrollableAlertContent(
            title = stringResource(horologist_volume_screen_change_audio_output),
            onOk = {},
            onCancel = {},
        )
    }

    @Test
    @Config(qualifiers = "+ta")
    fun changeAudioOutputTa() = runTest {
        NonScrollableAlertContent(
            title = stringResource(horologist_volume_screen_change_audio_output),
            onOk = {},
            onCancel = {},
        )
    }

    @Test
    @Config(qualifiers = "+ru")
    fun changeAudioOutputRu() = runTest {
        NonScrollableAlertContent(
            title = stringResource(horologist_volume_screen_change_audio_output),
            onOk = {},
            onCancel = {},
        )
    }
}

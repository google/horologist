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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Icon
import com.google.android.horologist.auth.composables.R.string.horologist_auth_error_message
import com.google.android.horologist.compose.material.ConfirmationContent
import com.google.android.horologist.compose.material.util.DECORATIVE_ELEMENT_CONTENT_DESCRIPTION
import com.google.android.horologist.screenshots.rng.WearDevice
import com.google.android.horologist.screenshots.rng.WearDeviceScreenshotTest
import org.junit.Test
import org.robolectric.annotation.Config

class NonScrollableConfirmationTest(device: WearDevice) : WearDeviceScreenshotTest(device = device) {
    public override val tolerance: Float = 0.01f

    override fun testName(suffix: String): String =
        "src/test/screenshots/${this.javaClass.simpleName}_${testInfo.methodName.substringBefore('[')}_${device.id}$suffix.png"

    @Test
    fun confirmationContentEn() = runTest {
        ConfirmationContent(
            title = stringResource(horologist_auth_error_message),
        )
    }

    @Test
    @Config(qualifiers = "+ka")
    fun confirmationContentKa() = runTest {
        ConfirmationContent(
            title = stringResource(horologist_auth_error_message),
        )
    }

    @Test
    @Config(qualifiers = "+ta")
    fun confirmationContentTa() = runTest {
        ConfirmationContent(
            title = stringResource(horologist_auth_error_message),
        )
    }

    @Test
    @Config(qualifiers = "+ru")
    fun confirmationContentRu() = runTest {
        ConfirmationContent(
            title = stringResource(horologist_auth_error_message),
        )
    }

    @Test
    fun confirmationContentWithIcon() = runTest {
        ConfirmationContent(
            title = stringResource(horologist_auth_error_message),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = DECORATIVE_ELEMENT_CONTENT_DESCRIPTION,
                )
            },
        )
    }
}

/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.audit

import android.os.Looper
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.screenshots.rng.WearDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.robolectric.Shadows.shadowOf

public class MainMenuAuditScreenshotTest(device: WearDevice) :
    AuditScreenshotTest(device) {

    override val audit: AuditNavigation
        get() = AuditNavigation.MainMenu

    @Test
    fun mainMenu() {
        lateinit var columnState: ScalingLazyColumnState

        runTest(captureScreenshot = false) {
            columnState = rememberResponsiveColumnState(
                contentPadding = padding(
                    first = ItemType.Text,
                    last = ItemType.Chip
                )
            )

            AuditMenuScreen(
                columnState = columnState
            ) {  }
        }

        captureScreenshot(suffix = "_top")

        runBlocking {
            columnState.state.scrollToItem(99, 0)
        }

        shadowOf(Looper.getMainLooper()).idle()

        captureScreenshot(suffix = "_bottom")
    }
}

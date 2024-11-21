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

@file:Suppress("ObjectLiteralToLambda")

package com.google.android.horologist.media.ui.screens.browse

import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults.scalingParams
import androidx.wear.compose.foundation.lazy.ScalingParams
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult
import com.google.android.apps.common.testing.accessibility.framework.checks.DuplicateSpeakableTextCheck
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.screenshots.rng.WearLegacyA11yTest
import org.hamcrest.Matcher
import org.junit.Test
import org.robolectric.annotation.Config

@Config(
    sdk = [35],
    qualifiers = "w227dp-h330dp-small-notlong-notround-watch-xhdpi-keyshidden-nonav",
)
class PlaylistDownloadBrowseScreenA11yTallScreenshotTest : WearLegacyA11yTest() {
    // TODO fix this warning
    override fun accessibilitySuppressions(): Matcher<in AccessibilityViewCheckResult> =
        AccessibilityCheckResultUtils.matchesCheck(DuplicateSpeakableTextCheck::class.java)

    @Test
    fun browseScreen() {
        val screenState = BrowseScreenState.Loaded(downloadList)

        runScreenTest {
            val columnState = rememberResponsiveColumnState().copy(
                scalingParams = scalingParams(
                    edgeScale = 1f,
                    edgeAlpha = 1f,
                ),
            )

            ScreenScaffold(scrollState = columnState) {
                SectionedList(
                    columnState = columnState,
                    sections = BrowseScreenScope().apply {
                        PlaylistDownloadBrowseScreenContent(
                            browseScreenState = screenState,
                            onDownloadItemClick = { },
                            onDownloadItemInProgressClick = { },
                            onPlaylistsClick = { },
                            onSettingsClick = { },
                            onDownloadItemInProgressClickActionLabel = "cancel",
                        )
                    }.sections,
                )
            }
        }
    }
}

public fun ScalingLazyColumnState.copy(scalingParams: ScalingParams): ScalingLazyColumnState =
    ScalingLazyColumnState(
        initialScrollPosition = initialScrollPosition,
        timeTextHomeOffset = timeTextHomeOffset,
        autoCentering = autoCentering,
        anchorType = anchorType,
        contentPadding = contentPadding,
        rotaryMode = rotaryMode,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        userScrollEnabled = userScrollEnabled,
        scalingParams = scalingParams,
    )

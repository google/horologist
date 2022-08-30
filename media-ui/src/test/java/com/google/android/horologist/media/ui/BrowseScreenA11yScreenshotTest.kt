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

@file:OptIn(ExperimentalHorologistMediaUiApi::class)

package com.google.android.horologist.media.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.ScalingLazyListState
import app.cash.paparazzi.Paparazzi
import com.google.android.horologist.media.ui.a11y.ComposeA11yExtension
import com.google.android.horologist.media.ui.screens.browse.BrowseScreen
import com.google.android.horologist.media.ui.screens.browse.BrowseScreenState
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.paparazzi.GALAXY_WATCH4_CLASSIC_LARGE
import com.google.android.horologist.paparazzi.WearSnapshotHandler
import com.google.android.horologist.paparazzi.a11y.A11ySnapshotHandler
import com.google.android.horologist.paparazzi.determineHandler
import org.junit.Rule
import org.junit.Test
import kotlin.reflect.full.declaredMemberProperties

class BrowseScreenA11yScreenshotTest {
    private val maxPercentDifference = 0.1

    val composeA11yExtension = ComposeA11yExtension()

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = GALAXY_WATCH4_CLASSIC_LARGE,
        theme = "android:ThemeOverlay.Material.Dark",
        maxPercentDifference = maxPercentDifference,
        renderExtensions = setOf(composeA11yExtension),
        snapshotHandler = WearSnapshotHandler(
            A11ySnapshotHandler(
                delegate = determineHandler(
                    maxPercentDifference = maxPercentDifference
                ),
                accessibilityStateFn = { composeA11yExtension.accessibilityState }
            )
        )
    )

    private val downloadList = buildList {
        add(
            PlaylistDownloadUiModel.InProgress(
                PlaylistUiModel(
                    id = "id",
                    title = "Rock Classics",
                    artworkUri = "https://www.example.com/album1.png"
                ),
                percentage = 15
            )
        )

        add(
            PlaylistDownloadUiModel.Completed(
                PlaylistUiModel(
                    id = "id",
                    title = "Pop Punk",
                    artworkUri = "https://www.example.com/album2.png"
                )
            )
        )
    }

    @Test
    fun browseScreen() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(0, 0)

        val screenState = BrowseScreenState.Loaded(downloadList)

        paparazzi.snapshot {
            BrowseScreen(
                browseScreenState = screenState,
                onDownloadItemClick = { },
                onPlaylistsClick = { },
                onSettingsClick = { },
                focusRequester = FocusRequester(),
                scalingLazyListState = scrollState
            )
        }
    }

    @Test
    fun secondPage() {
        val scrollState = ScalingLazyListState()
        scrollState.forceState(4, 0)

        val screenState = BrowseScreenState.Loaded(downloadList)

        paparazzi.snapshot {
            BrowseScreen(
                browseScreenState = screenState,
                onDownloadItemClick = { },
                onPlaylistsClick = { },
                onSettingsClick = { },
                focusRequester = FocusRequester(),
                scalingLazyListState = scrollState
            )
        }
    }
}

private fun ScalingLazyListState.forceState(topIndex: Int, topScrollOffset: Int) {
    this.lazyListState.scrollPositionScrollOffset.value = topScrollOffset
    this.lazyListState.scrollPositionIndex = topIndex
    this.initialized.value = true
}

val ScalingLazyListState.initialized: MutableState<Boolean>
    get() {
        return ScalingLazyListState::class.declaredMemberProperties.first { it.name == "initialized" }
            .get(this) as MutableState<Boolean>
    }

val ScalingLazyListState.lazyListState: LazyListState
    get() {
        return ScalingLazyListState::class.declaredMemberProperties.first { it.name == "lazyListState" }
            .get(this) as LazyListState
    }

val LazyListState.scrollPosition: Any
    get() {
        return LazyListState::class.java.declaredFields.first { it.name == "scrollPosition" }
            .apply {
                isAccessible = true
            }
            .get(this) as Any
    }

var LazyListState.scrollPositionIndex: Int
    get() {
        val positionType = Class.forName("androidx.compose.foundation.lazy.LazyListScrollPosition")
        val dataIndexType = Class.forName("androidx.compose.foundation.lazy.DataIndex")
        val dataIndex = positionType.declaredFields
            .first { it.name == "index\$delegate" }.apply {
                isAccessible = true
            }
            .get(scrollPosition) as MutableState<Any>
        return dataIndexType.declaredFields
            .first { it.name == "value" }.apply {
                isAccessible = true
            }
            .get(dataIndex.value) as Int
    }
    set(value) {
        val positionType = Class.forName("androidx.compose.foundation.lazy.LazyListScrollPosition")
        val dataIndexType = Class.forName("androidx.compose.foundation.lazy.DataIndex")
        val dataIndex = positionType.declaredFields
            .first { it.name == "index\$delegate" }.apply {
                isAccessible = true
            }
            .get(scrollPosition) as MutableState<Any>
        dataIndexType.declaredFields
            .first { it.name == "value" }.apply {
                isAccessible = true
            }
            .set(dataIndex.value, value)
    }

val LazyListState.scrollPositionScrollOffset: MutableState<Int>
    get() {
        val type = Class.forName("androidx.compose.foundation.lazy.LazyListScrollPosition")
        return type.declaredFields
            .first { it.name == "scrollOffset\$delegate" }.apply {
                isAccessible = true
            }
            .get(scrollPosition) as MutableState<Int>
    }


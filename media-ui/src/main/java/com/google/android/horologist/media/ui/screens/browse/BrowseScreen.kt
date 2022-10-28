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

@file:OptIn(ExperimentalHorologistComposablesApi::class)

package com.google.android.horologist.media.ui.screens.browse

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.ScalingParams
import androidx.wear.compose.material.Text
import com.google.android.horologist.base.ui.components.StandardChip
import com.google.android.horologist.base.ui.components.StandardChipType
import com.google.android.horologist.base.ui.components.Title
import com.google.android.horologist.composables.ExperimentalHorologistComposablesApi
import com.google.android.horologist.composables.Section
import com.google.android.horologist.composables.SectionContentScope
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.media.ui.ExperimentalHorologistMediaUiApi
import com.google.android.horologist.media.ui.R
import com.google.android.horologist.media.ui.state.model.PlaylistDownloadUiModel

/**
 * A screen to browse contents of a media app.
 *
 * This is an opinionated implementation that allows a lot of customisation. If further
 * customisation is required, then [SectionedList] should be used directly.
 *
 * This screen provide an implementation of:
 * - a section to display user's list of [downloads][BrowseScreenScope.downloadsSection];
 * - a section to display user's list of [playlists][BrowseScreenScope.playlistsSection];
 * - a [button][BrowseScreenScope.button] to be used to navigate to another screen;
 */
@ExperimentalHorologistMediaUiApi
@Composable
public fun BrowseScreen(
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState,
    modifier: Modifier = Modifier,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    autoCentering: AutoCenteringParams? = AutoCenteringParams(),
    content: BrowseScreenScope.() -> Unit
) {
    SectionedList(
        focusRequester = focusRequester,
        scalingLazyListState = scalingLazyListState,
        modifier = modifier,
        scalingParams = scalingParams,
        autoCentering = autoCentering,
        sections = BrowseScreenScope().apply(content).sections
    )
}

/**
 * Receiver scope which is used by content parameter in [BrowseScreen].
 */
@ExperimentalHorologistMediaUiApi
@BrowseScreenScopeMarker
public class BrowseScreenScope {

    internal val sections: MutableList<Section<*>> = mutableListOf()

    @BrowseScreenScopeMarker
    public fun <T> section(
        state: Section.State<T>,
        @StringRes titleId: Int,
        @StringRes emptyMessageId: Int,
        @StringRes failedMessageId: Int? = null,
        displayFooterOnlyOnLoadedState: Boolean = true,
        content: BrowseScreenSectionScope<T>.() -> Unit
    ) {
        val scope = BrowseScreenSectionScope<T>().apply(content)
        val firstSectionAdded = sections.isEmpty()
        sections.add(
            Section(
                state = state,
                headerContent = {
                    Title(
                        textId = titleId,
                        modifier = if (firstSectionAdded) {
                            Modifier.padding(bottom = 8.dp)
                        } else {
                            Modifier.padding(top = 8.dp, bottom = 8.dp)
                        }
                    )
                },
                loadingContent = scope.loadingContent,
                loadedContent = scope.loadedContent,
                failedContent = {
                    failedMessageId?.let {
                        Text(
                            text = stringResource(id = failedMessageId),
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2
                        )
                    }
                },
                emptyContent = {
                    Text(
                        text = stringResource(id = emptyMessageId),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                },
                footerContent = scope.footerContent,
                displayFooterOnlyOnLoadedState = displayFooterOnlyOnLoadedState
            )
        )
    }

    @BrowseScreenScopeMarker
    public fun <T> downloadsSection(
        state: Section.State<T>,
        displayFooterOnlyOnLoadedState: Boolean = true,
        content: BrowseScreenSectionScope<T>.() -> Unit
    ) {
        section(
            state = state,
            titleId = R.string.horologist_browse_downloads_title,
            emptyMessageId = R.string.horologist_browse_downloads_empty,
            failedMessageId = null,
            displayFooterOnlyOnLoadedState = displayFooterOnlyOnLoadedState,
            content = content
        )
    }

    @BrowseScreenScopeMarker
    public fun playlistsSection(buttons: List<BrowseScreenPlaylistsSectionButton>) {
        val firstSectionAdded = sections.isEmpty()
        sections.add(
            Section(
                state = Section.State.Loaded(buttons),
                headerContent = {
                    Title(
                        textId = R.string.horologist_browse_library_playlists,
                        modifier = if (firstSectionAdded) {
                            Modifier.padding(bottom = 8.dp)
                        } else {
                            Modifier.padding(top = 8.dp, bottom = 8.dp)
                        }
                    )
                },
                loadedContent = { item: BrowseScreenPlaylistsSectionButton ->
                    StandardChip(
                        labelId = item.textId,
                        onClick = item.onClick,
                        icon = item.icon,
                        chipType = StandardChipType.Secondary
                    )
                }
            )
        )
    }

    @BrowseScreenScopeMarker
    public fun button(button: BrowseScreenPlaylistsSectionButton) {
        sections.add(
            Section(
                state = Section.State.Loaded(listOf(button)),
                loadedContent = { item: BrowseScreenPlaylistsSectionButton ->
                    StandardChip(
                        labelId = item.textId,
                        onClick = item.onClick,
                        icon = item.icon,
                        chipType = StandardChipType.Secondary
                    )
                }
            )
        )
    }
}

/**
 * DSL marker used to distinguish between scopes of [BrowseScreen].
 */
@DslMarker
internal annotation class BrowseScreenScopeMarker

/**
 * Receiver scope which is used by content parameter in [BrowseScreenScope].
 */
@ExperimentalHorologistMediaUiApi
@BrowseScreenScopeMarker
public class BrowseScreenSectionScope<T> {

    internal var loadingContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    internal var loadedContent: @Composable SectionContentScope.(T) -> Unit = { }
        private set

    internal var footerContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    @BrowseScreenScopeMarker
    public fun loading(content: @Composable SectionContentScope.() -> Unit) {
        loadingContent = content
    }

    @BrowseScreenScopeMarker
    public fun loaded(content: @Composable SectionContentScope.(T) -> Unit) {
        loadedContent = content
    }

    @BrowseScreenScopeMarker
    public fun footer(content: @Composable SectionContentScope.() -> Unit) {
        footerContent = content
    }
}

/**
 * Represents the state of [BrowseScreen].
 */
@ExperimentalHorologistMediaUiApi
public sealed class BrowseScreenState {

    public object Loading : BrowseScreenState()

    public data class Loaded(
        val downloadList: List<PlaylistDownloadUiModel>
    ) : BrowseScreenState()

    public object Failed : BrowseScreenState()
}

@ExperimentalHorologistMediaUiApi
public data class BrowseScreenPlaylistsSectionButton(
    @StringRes val textId: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)

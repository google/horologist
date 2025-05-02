/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.screens.browse

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.media.ui.material3.composables.Section
import com.google.android.horologist.media.ui.material3.composables.Section.Companion.ALL_STATES
import com.google.android.horologist.media.ui.material3.composables.Section.Companion.LOADED_STATE_ONLY
import com.google.android.horologist.media.ui.material3.composables.SectionContentScope
import com.google.android.horologist.media.ui.material3.composables.SectionedList
import com.google.android.horologist.media.ui.model.R
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
@ExperimentalHorologistApi
@Composable
public fun BrowseScreen(
    modifier: Modifier = Modifier,
    content: BrowseScreenScope.() -> Unit,
) {
    val scrollState = rememberScalingLazyListState()
    ScreenScaffold(scrollState = scrollState) {
        SectionedList(
            scrollState = scrollState,
            modifier = modifier,
            sections = BrowseScreenScope().apply(content).sections,
        )
    }
}

/**
 * Receiver scope which is used by content parameter in [BrowseScreen].
 */
@ExperimentalHorologistApi
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
        content: BrowseScreenSectionScope<T>.() -> Unit,
    ) {
        val scope = BrowseScreenSectionScope<T>().apply(content)
        sections.add(
            Section(
                state = state,
                headerContent = {
                    ListHeader { Text(text = stringResource(id = titleId)) }
                },
                loadingContent = scope.loadingContent,
                loadedContent = scope.loadedContent,
                failedContent = {
                    failedMessageId?.let {
                        Text(
                            text = stringResource(id = failedMessageId),
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                },
                emptyContent = {
                    Text(
                        text = stringResource(id = emptyMessageId),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                footerContent = scope.footerContent,
                footerVisibleStates = if (displayFooterOnlyOnLoadedState) {
                    LOADED_STATE_ONLY
                } else {
                    ALL_STATES
                },
            ),
        )
    }

    @BrowseScreenScopeMarker
    public fun <T> downloadsSection(
        state: Section.State<T>,
        displayFooterOnlyOnLoadedState: Boolean = true,
        content: BrowseScreenSectionScope<T>.() -> Unit,
    ) {
        section(
            state = state,
            titleId = R.string.horologist_browse_downloads_title,
            emptyMessageId = R.string.horologist_browse_downloads_empty,
            failedMessageId = null,
            displayFooterOnlyOnLoadedState = displayFooterOnlyOnLoadedState,
            content = content,
        )
    }

    @BrowseScreenScopeMarker
    public fun playlistsSection(buttons: List<BrowseScreenPlaylistsSectionButton>) {
        val firstSectionAdded = sections.isEmpty()
        sections.add(
            Section(
                state = Section.State.Loaded(buttons),
                headerContent = {
                    ListHeader(
                        modifier = if (firstSectionAdded) {
                            Modifier.padding(bottom = 8.dp)
                        } else {
                            Modifier.padding(top = 8.dp, bottom = 8.dp)
                        },
                    ) { Text(stringResource(R.string.horologist_browse_library_playlists)) }
                },
                loadedContent = { item: BrowseScreenPlaylistsSectionButton ->
                    FilledTonalButton(
                        onClick = item.onClick,
                        label = { Text(stringResource(item.textId)) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                            )
                        },
                    )
                },
            ),
        )
    }

    @BrowseScreenScopeMarker
    public fun button(button: BrowseScreenPlaylistsSectionButton) {
        sections.add(
            Section(
                state = Section.State.Loaded(listOf(button)),
                loadedContent = { item: BrowseScreenPlaylistsSectionButton ->
                    FilledTonalButton(
                        onClick = item.onClick,
                        label = { Text(stringResource(item.textId)) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                            )
                        },
                    )
                },
            ),
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
@ExperimentalHorologistApi
@BrowseScreenScopeMarker
public class BrowseScreenSectionScope<T> {

    internal var loadingContent: @Composable SectionContentScope.() -> Unit = { }
        private set

    internal var loadedContent: @Composable SectionContentScope.(T) -> Unit = { }
        private set

    internal var footerContent: (@Composable SectionContentScope.() -> Unit)? = null
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
@ExperimentalHorologistApi
public sealed class BrowseScreenState {

    public object Loading : BrowseScreenState()

    public data class Loaded(
        val downloadList: List<PlaylistDownloadUiModel>,
    ) : BrowseScreenState()

    public object Failed : BrowseScreenState()
}

@ExperimentalHorologistApi
public data class BrowseScreenPlaylistsSectionButton(
    @StringRes val textId: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

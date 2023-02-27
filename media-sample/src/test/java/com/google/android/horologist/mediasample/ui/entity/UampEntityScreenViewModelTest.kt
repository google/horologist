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

package com.google.android.horologist.mediasample.ui.entity

import androidx.datastore.core.DataStore
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.android.horologist.media.model.Media
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.ui.screens.entity.PlaylistDownloadScreenState
import com.google.android.horologist.media.ui.screens.entity.createPlaylistDownloadScreenStateLoaded
import com.google.android.horologist.media.ui.state.model.DownloadMediaUiModel
import com.google.android.horologist.media.ui.state.model.PlaylistUiModel
import com.google.android.horologist.mediasample.domain.SettingsRepository
import com.google.android.horologist.mediasample.domain.proto.SettingsProto.Settings
import com.google.android.horologist.test.toolbox.MainDispatcherRule
import com.google.android.horologist.test.toolbox.testdoubles.FakeDataStore
import com.google.android.horologist.test.toolbox.testdoubles.FakeMediaDownloadRepository
import com.google.android.horologist.test.toolbox.testdoubles.FakePlayerRepository
import com.google.android.horologist.test.toolbox.testdoubles.FakePlaylistDownloadDataSource
import com.google.android.horologist.test.toolbox.testdoubles.FakePlaylistDownloadRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UampEntityScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var sut: UampEntityScreenViewModel
    private val dataStore: DataStore<Settings> = FakeDataStore()

    private val playlistId = "playlistId"
    private val playlistName = "playlistId"
    private val playlistToTest = Playlist(
        id = playlistId,
        name = playlistName,
        artworkUri = null,
        mediaList = listOf(
            Media(
                id = "media1",
                uri = "",
                title = "media_name1",
                artist = "",
                artworkUri = null,
                extras = emptyMap()
            ),
            Media(
                id = "media2",
                uri = "",
                title = "media_name2",
                artist = "",
                artworkUri = null,
                extras = emptyMap()
            )
        )
    )

    private val playlistUiModel = PlaylistUiModel(
        id = playlistId,
        title = playlistName
    )

    private val savedStateHandle = SavedStateHandle()
    private val fakePlaylistDownloadDataSource = FakePlaylistDownloadDataSource(playlist = playlistToTest)
    private val fakePlaylistDownloadRepository =
        FakePlaylistDownloadRepository(fakePlaylistDownloadDataSource)
    private val fakeMediaDownloadRepository = FakeMediaDownloadRepository(fakePlaylistDownloadDataSource)
    private val fakePlayerRepository = FakePlayerRepository()
    private val fakeSettingsRepository = SettingsRepository(dataStore)

    @Before
    fun setup() {
        savedStateHandle["id"] = playlistId
        sut = UampEntityScreenViewModel(
            savedStateHandle,
            fakePlaylistDownloadRepository,
            fakeMediaDownloadRepository,
            fakePlayerRepository,
            fakeSettingsRepository
        )
    }

    @Test
    fun loadWithoutPlaylist_returnsFailedUiState() = runTest {
        val fakePlaylistDownloadDataSource2 = FakePlaylistDownloadDataSource(playlist = null)
        val sut2 = UampEntityScreenViewModel(
            savedStateHandle,
            FakePlaylistDownloadRepository(fakePlaylistDownloadDataSource2),
            FakeMediaDownloadRepository(fakePlaylistDownloadDataSource2),
            fakePlayerRepository,
            fakeSettingsRepository
        )

        sut2.uiState.test {
            assertThat(awaitItem()).isInstanceOf(PlaylistDownloadScreenState.Failed::class.java)
        }
    }

    @Test
    fun validInitLoad_returnsFullyNotDownloadedUiState() = runTest {
        val expectedUiState: PlaylistDownloadScreenState<PlaylistUiModel, DownloadMediaUiModel> =
            createPlaylistDownloadScreenStateLoaded(
                playlistUiModel,
                mediaListToFullyNotDownloadedMediaUiModelList(playlistToTest.mediaList)
            )

        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(expectedUiState)
        }
    }

    @Test
    fun validInitLoad_DownloadFullList_returnsFullyDownloadedUiState() = runTest {
        val expectedIdleUiState =
            createPlaylistDownloadScreenStateLoaded(
                playlistUiModel,
                mediaListToFullyNotDownloadedMediaUiModelList(playlistToTest.mediaList)
            )
        val expectedDownloadedUiState =
            createPlaylistDownloadScreenStateLoaded(
                playlistUiModel,
                mediaListToFullyDownloadedMediaUiModelList(playlistToTest.mediaList)
            )

        sut.uiState.test {
            assertThat(awaitItem()).isEqualTo(expectedIdleUiState)
            sut.download()
            assertThat(awaitItem()).isEqualTo(expectedDownloadedUiState)
        }
    }

    @Test
    fun validInitLoadFullyDownloaded_removeFullList_returnsFullyNotDownloadedUiState() = runTest {
        val expectedUiState =
            createPlaylistDownloadScreenStateLoaded(
                playlistUiModel,
                mediaListToFullyNotDownloadedMediaUiModelList(playlistToTest.mediaList)
            )

        sut.uiState.test {
            sut.download()
            skipItems(2) // Fully Not Downloaded -> Fully Downloaded

            sut.remove()
            assertThat(awaitItem()).isEqualTo(expectedUiState)
        }
    }

    @Test
    fun validInitLoadFullyDownloaded_removeSingleItem_returnsFullyDownloadedExceptRemovedItemUiState() = runTest {
        val expectedPartialDownloadedUiState =
            createPlaylistDownloadScreenStateLoaded(
                playlistUiModel,
                mediaListToFullyDownloadedMediaUiModelListExceptMediaId(playlistToTest.mediaList, "media1")
            )

        // Setup initial load to idle then fully downloaded
        sut.uiState.test {
            sut.download()
            skipItems(2) // Fully Not Downloaded -> Fully Downloaded
            sut.removeMediaItem("media1")
            assertThat(awaitItem()).isEqualTo(expectedPartialDownloadedUiState)
        }
    }

    private fun mediaListToFullyNotDownloadedMediaUiModelList(mediaList: List<Media>) =
        mediaList.map { media ->
            DownloadMediaUiModel.NotDownloaded(
                id = media.id,
                title = media.title,
                artist = media.artist,
                artworkUri = null
            )
        }

    private fun mediaListToFullyDownloadedMediaUiModelList(mediaList: List<Media>) =
        mediaList.map { media ->
            DownloadMediaUiModel.Downloaded(
                id = media.id,
                title = media.title,
                artist = media.artist,
                artworkUri = null
            )
        }

    private fun mediaListToFullyDownloadedMediaUiModelListExceptMediaId(
        mediaList: List<Media>,
        mediaId: String
    ) =
        mediaList.map { media ->
            if (media.id == mediaId) {
                DownloadMediaUiModel.NotDownloaded(
                    id = media.id,
                    title = media.title,
                    artist = media.artist,
                    artworkUri = null
                )
            } else {
                DownloadMediaUiModel.Downloaded(
                    id = media.id,
                    title = media.title,
                    artist = media.artist,
                    artworkUri = null
                )
            }
        }
}

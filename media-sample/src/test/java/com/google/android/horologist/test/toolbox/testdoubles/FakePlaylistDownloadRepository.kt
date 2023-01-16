/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.test.toolbox.testdoubles

import com.google.android.horologist.media.ExperimentalHorologistMediaApi
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.model.PlaylistDownload
import com.google.android.horologist.media.repository.PlaylistDownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalHorologistMediaApi::class)
class FakePlaylistDownloadRepository(
    private val fakeDownloadDataSource: FakeDownloadDataSource
) : PlaylistDownloadRepository {

    private val playlistDownloadFlow =
        MutableStateFlow(fakeDownloadDataSource.getCurrentPlaylistDownload())

    override fun get(playlistId: String): Flow<PlaylistDownload?> {
        return playlistDownloadFlow
    }

    override fun download(playlist: Playlist) {
        fakeDownloadDataSource.setAllMediaDownloadsToCompleted()
        runBlocking {
            playlistDownloadFlow.emit(
                fakeDownloadDataSource.getCurrentPlaylistDownload()
            )
        }
    }

    override fun remove(playlist: Playlist) {
        fakeDownloadDataSource.setAllMediaDownloadsToIdle()
        runBlocking {
            playlistDownloadFlow.emit(
                fakeDownloadDataSource.getCurrentPlaylistDownload()
            )
        }
    }
}

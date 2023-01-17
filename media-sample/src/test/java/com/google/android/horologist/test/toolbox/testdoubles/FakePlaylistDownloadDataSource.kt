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

import com.google.android.horologist.media.model.MediaDownload
import com.google.android.horologist.media.model.Playlist
import com.google.android.horologist.media.model.PlaylistDownload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePlaylistDownloadDataSource(private val playlist: Playlist? = null) {

    private val _playlistDownloadFlow: MutableStateFlow<PlaylistDownload?> =
        MutableStateFlow(
            playlist?.let {
                PlaylistDownload(
                    playlist = it,
                    mediaList = playlist.mediaList?.map { media ->
                        MediaDownload(
                            media = media,
                            status = MediaDownload.Status.Idle,
                            size = MediaDownload.Size.Unknown
                        )
                    } ?: emptyList()
                )
            }
        )
    val playlistDownloadFlow: StateFlow<PlaylistDownload?> get() =
        _playlistDownloadFlow

    fun setAllMediaDownloadsToIdle() {
        playlist?.let {
            _playlistDownloadFlow.value =

                PlaylistDownload(
                    playlist = it,
                    mediaList = playlist.mediaList?.map { media ->
                        MediaDownload(
                            media = media,
                            status = MediaDownload.Status.Idle,
                            size = MediaDownload.Size.Unknown
                        )
                    } ?: emptyList()
                )
        }
    }

    fun setAllMediaDownloadsToCompleted() {
        playlist?.let {
            _playlistDownloadFlow.value =

                PlaylistDownload(
                    playlist = it,
                    mediaList = playlist.mediaList?.map { media ->
                        MediaDownload(
                            media = media,
                            status = MediaDownload.Status.Completed,
                            size = MediaDownload.Size.Unknown
                        )
                    } ?: emptyList()
                )
        }
    }

    fun cancelMediaDownloadDownloadProgress(mediaId: String) {
        _playlistDownloadFlow.value?.let { playlistDownload ->

            playlist?.let { playlist ->

                _playlistDownloadFlow.value =

                    PlaylistDownload(
                        playlist = playlist,
                        mediaList = playlistDownload.mediaList.map { mediaDownload ->
                            if (mediaDownload.media.id == mediaId) {
                                MediaDownload(
                                    media = mediaDownload.media,
                                    status = MediaDownload.Status.Idle,
                                    size = MediaDownload.Size.Unknown
                                )
                            } else {
                                mediaDownload
                            }
                        }
                    )
            }
        }
    }
}

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

@file:OptIn(ExperimentalHorologistMediaDataApi::class)

package com.google.android.horologist.media.data.mapper

import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi
import com.google.android.horologist.media.data.database.model.MediaDownloadEntity
import com.google.android.horologist.media.data.database.model.MediaDownloadEntityStatus
import com.google.android.horologist.media.model.MediaDownload
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MediaDownloadStatusMapperTest(
    private val mediaDownloadEntity: MediaDownloadEntity,
    private val expectedStatus: MediaDownload.Status
) {

    @Test
    fun mapsCorrectly() {
        // when
        val result = MediaDownloadStatusMapper.map(mediaDownloadEntity)

        // then
        assertThat(result).isEqualTo(expectedStatus)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun params() = listOf(
            arrayOf(
                MediaDownloadEntity(
                    mediaId = "mediaId",
                    status = MediaDownloadEntityStatus.NotDownloaded,
                    progress = 0F,
                    size = 0L
                ),
                MediaDownload.Status.Idle
            ),
            arrayOf(
                MediaDownloadEntity(
                    mediaId = "mediaId",
                    status = MediaDownloadEntityStatus.Downloading,
                    progress = 75F,
                    size = 12345L
                ),
                MediaDownload.Status.InProgress(75F)
            ),
            arrayOf(
                MediaDownloadEntity(
                    mediaId = "mediaId",
                    status = MediaDownloadEntityStatus.Downloaded,
                    progress = 100F,
                    size = 12345L
                ),
                MediaDownload.Status.Completed
            ),
            arrayOf(
                MediaDownloadEntity(
                    mediaId = "mediaId",
                    status = MediaDownloadEntityStatus.Failed,
                    progress = 0F,
                    size = 0L
                ),
                MediaDownload.Status.Idle
            )
        )
    }
}

/*
 * Copyright 2022-2026 The Android Open Source Project
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

package com.google.android.horologist.media.database.mapper

import androidx.media3.exoplayer.offline.Download
import com.google.android.horologist.media.database.model.MediaDownloadEntityStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MediaDownloadEntityStatusMapperTest(
    @Download.State private val state: Int,
    private val expectedStatus: MediaDownloadEntityStatus,
) {

    @Test
    fun mapsCorrectly() {
        // when
        val result = MediaDownloadEntityStatusMapper.map(state)

        // then
        assertThat(result).isEqualTo(expectedStatus)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0} -> {1}")
        fun params() = listOf(
            arrayOf<Any>(
                Download.STATE_QUEUED,
                MediaDownloadEntityStatus.Downloading,
            ),
            arrayOf<Any>(
                Download.STATE_STOPPED,
                MediaDownloadEntityStatus.NotDownloaded,
            ),
            arrayOf<Any>(
                Download.STATE_DOWNLOADING,
                MediaDownloadEntityStatus.Downloading,
            ),
            arrayOf<Any>(
                Download.STATE_COMPLETED,
                MediaDownloadEntityStatus.Downloaded,
            ),
            arrayOf<Any>(
                Download.STATE_FAILED,
                MediaDownloadEntityStatus.Failed,
            ),
            arrayOf<Any>(
                Download.STATE_REMOVING,
                MediaDownloadEntityStatus.NotDownloaded,
            ),
            arrayOf<Any>(
                Download.STATE_RESTARTING,
                MediaDownloadEntityStatus.Downloading,
            ),
        )
    }
}

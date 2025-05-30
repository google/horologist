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

package com.google.android.horologist.media.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A table to store [media][MediaEntity] download information.
 */
@ExperimentalHorologistApi
@Entity
public data class MediaDownloadEntity(
    @PrimaryKey val mediaId: String,
    val status: MediaDownloadEntityStatus,
    val progress: Float,
    val size: Long,
)

/**
 * Represents the download status of [MediaDownloadEntity].
 */
public enum class MediaDownloadEntityStatus {
    NotDownloaded, Downloading, Downloaded, Failed
}

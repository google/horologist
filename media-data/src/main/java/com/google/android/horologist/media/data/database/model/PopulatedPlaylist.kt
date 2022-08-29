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

package com.google.android.horologist.media.data.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.google.android.horologist.media.data.ExperimentalHorologistMediaDataApi

/**
 * [PlaylistEntity] populated with a list of [MediaEntity].
 */
@ExperimentalHorologistMediaDataApi
public data class PopulatedPlaylist(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "mediaId",
        associateBy = Junction(
            PlaylistMediaEntity::class,
            parentColumn = "playlistId",
            entityColumn = "mediaId"
        )
    )
    val mediaList: List<MediaEntity>
)

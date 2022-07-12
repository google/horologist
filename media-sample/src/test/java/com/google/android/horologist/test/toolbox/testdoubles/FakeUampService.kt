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

package com.google.android.horologist.test.toolbox.testdoubles

import com.google.android.horologist.mediasample.data.api.UampService
import com.google.android.horologist.mediasample.data.api.model.CatalogApiModel
import com.google.android.horologist.mediasample.data.api.model.MusicApiModel

class FakeUampService : UampService {

    override suspend fun catalog(): CatalogApiModel = CatalogApiModel(
        music = listOf(
            MusicApiModel(
                album = "album1",
                artist = "artist1",
                duration = 1,
                genre = "genre1",
                id = "id1",
                image = "artworkUri1",
                site = "site1",
                source = "source1",
                title = "title1",
                totalTrackCount = 1,
                trackNumber = 1,
            )
        )
    )
}

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

package com.google.android.horologist.mediasample.data.api

import com.google.android.horologist.mediasample.data.api.model.CatalogApiModel

/**
 * Decorating [UampService] to use wear appropriate image sizes.
 */
class WearArtworkUampService(
    private val uampService: UampService
) : UampService {
    private val githubPrefix =
        "https://media.githubusercontent.com/media/google/horologist/main/media-sample/backend/images/drawable-mdpi"

    override suspend fun catalog(): CatalogApiModel {
        val originalCatalog = uampService.catalog()
        return originalCatalog.copy(
            music = originalCatalog.music.map {
                it.copy(image = wearSizedArtwork(it.image))
            }
        )
    }

    private fun wearSizedArtwork(image: String): String {
        return when (image) {
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg" -> "$githubPrefix/kyoto.jpg"
            "https://storage.googleapis.com/uamp/Kai_Engel_-_Irsens_Tale/art.jpg" -> "$githubPrefix/kai.jpg"
            "https://storage.googleapis.com/automotive-media/album_art.jpg" -> "$githubPrefix/album_art.jpg"
            "https://storage.googleapis.com/automotive-media/album_art_2.jpg" -> "$githubPrefix/album_art_2.jpg"
            "https://storage.googleapis.com/automotive-media/album_art_3.jpg" -> "$githubPrefix/album_art_3.jpg"
            "https://storage.googleapis.com/uamp/Spatial Audio/Marching band.jpg" -> "$githubPrefix/marching_band.jpg"
            "https://storage.googleapis.com/uamp/Spatial Audio/Chickens.jpg" -> "$githubPrefix/chickens.jpg"
            "https://storage.googleapis.com/uamp/Spatial Audio/Rural market.jpg" -> "$githubPrefix/rural_market.jpg"
            else -> image
        }
    }
}

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

package com.google.android.horologist.ai.sample.wear.gemini.service

import com.google.genai.types.MediaModality

data class GeminiModel(
    val name: String,
    val displayName: String,
    val inputs: List<MediaModality>,
    val outputs: List<MediaModality>,
) {
    val isImagesOnly: Boolean
        get() = outputs.singleOrNull() == Image

    companion object {
        val Audio = MediaModality(MediaModality.Known.AUDIO)
        val Text = MediaModality(MediaModality.Known.TEXT)
        val Document = MediaModality(MediaModality.Known.DOCUMENT)
        val Video = MediaModality(MediaModality.Known.VIDEO)
        val Image = MediaModality(MediaModality.Known.IMAGE)

        val Gemini2dot5Pro = GeminiModel(
            "gemini-2.5-pro",
            "Gemini 2.5 Pro",
            listOf(Audio, Image, Video, Text, Document),
            listOf(
                Text,
            ),
        )
        val Gemini2dot5Flash = GeminiModel(
            "gemini-2.5-flash",
            "Gemini 2.5 Flash",
            listOf(Audio, Image, Video, Text),
            listOf(
                Text,
            ),
        )
        val Imagen4 = GeminiModel(
            "imagen-4.0-ultra-generate-preview-06-06",
            "Imagen 4",
            listOf(Text),
            listOf(Image),
        )
        val Gemini2dot0FlashLive =
            GeminiModel(
                "gemini-2.0-flash-live-001",
                "Gemini 2.0 Flash Live",
                listOf(Audio, Video, Text),
                listOf(Text, Audio),
            )
        val Gemini2dot5FlashLiveFlashLive =
            GeminiModel(
                "gemini-live-2.5-flash-preview",
                "Gemini 2.5 Flash Live",
                listOf(Audio, Video, Text),
                listOf(Text, Audio),
            )
        val Veo2 = GeminiModel("veo-2.0-generate-001", "Veo 2", listOf(Text, Image), listOf(Video))

        val All = listOf(
            Gemini2dot5Flash,
            Gemini2dot5Pro,
            Imagen4,
            Gemini2dot0FlashLive,
            Gemini2dot5FlashLiveFlashLive,
            Veo2,
        )
    }
}

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

package com.google.android.horologist.mediasample.playback

import com.google.android.horologist.media.model.Media

object TestMedia {
    val songMp3 = Media(
        "1",
        "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3",
        "Intro - The Way Of Waking Up (feat. Alan Watts)",
        "The Kyoto Connection",
        "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"
    )

    // https://docs.espressif.com/projects/esp-adf/en/latest/design-guide/audio-samples.html
    val songAac = Media(
        "2",
        "https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.aac",
        "Espressif",
        "Sample",
        null
    )

    // https://docs.espressif.com/projects/esp-adf/en/latest/design-guide/audio-samples.html
    val songMp3_24bit = Media(
        "3",
        "https://dl.espressif.com/dl/audio/ff-16b-2c-8000hz.mp3",
        "Espressif",
        "Sample",
        null
    )

    // https://docs.espressif.com/projects/esp-adf/en/latest/design-guide/audio-samples.html
    val songOgg = Media(
        "4",
        "https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.ogg",
        "Espressif",
        "Sample",
        null
    )

    // https://podnews.net/article/audio-quality-comparisons
    val songMp3_192 = Media(
        "4",
        "https://podnews.net/audio/s-192-44100.mp3",
        "Espressif",
        "Sample",
        null
    )
}

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

package com.google.android.horologist.media.benchmark

import com.google.android.horologist.media.benchmark.MediaItems.buildMediaItem

object TestMedia {
    val Intro = buildMediaItem(
        "1",
        "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3",
        "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg",
        "Intro - The Way Of Waking Up (feat. Alan Watts)",
        "The Kyoto Connection"
    )

    val MediaSampleApp = MediaApp(
        "com.google.android.horologist.mediasample",
        "com.google.android.horologist.mediasample.data.service.playback.PlaybackService",
        listOf(Intro, Intro, Intro)
    )
}

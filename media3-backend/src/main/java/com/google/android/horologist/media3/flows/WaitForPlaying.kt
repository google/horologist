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

@file:OptIn(ExperimentalHorologistMedia3BackendApi::class)

package com.google.android.horologist.media3.flows

import androidx.media3.common.Player
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

/**
 * Wait for the Player to reach isPlaying state.
 */
public suspend fun Player.waitForPlaying() {
    isPlayingFlow().filter { it }.first()
}

/**
 * Wait for the Player to leave isPlaying state.
 */
public suspend fun Player.waitForNotPlaying() {
    isPlayingFlow().filter { !it }.first()
}

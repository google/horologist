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

package com.google.android.horologist.media3.logging

import androidx.annotation.StringRes
import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi

/**
 * Simple API to allow switching logic for logging and showing user messages.
 */
@ExperimentalHorologistMedia3BackendApi
public interface ErrorReporter {
    public fun showMessage(@StringRes message: Int)

    public fun logMessage(
        message: String,
        category: Category = Category.Unknown,
        level: Level = Level.Info
    )

    public enum class Category {
        Playback, Downloads, Network, App, DB, Jobs, Unknown
    }

    public enum class Level {
        Error, Info, Debug
    }
}

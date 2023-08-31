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

package com.google.android.horologist.mediasample.data.log

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.google.android.horologist.media3.logging.ErrorReporter

public class Logging(
    private val res: Resources,
) : ErrorReporter {
    override fun showMessage(
        @StringRes message: Int,
    ) {
        val messageString = res.getString(message)
        Log.i("ErrorReporter", messageString)
    }

    private val ErrorReporter.Level.loggingLevel: Int
        get() = when (this) {
            ErrorReporter.Level.Error -> Log.ERROR
            ErrorReporter.Level.Info -> Log.INFO
            ErrorReporter.Level.Debug -> Log.DEBUG
        }

    override fun logMessage(message: String, category: ErrorReporter.Category, level: ErrorReporter.Level) {
        Log.println(level.loggingLevel, category.name, message)
    }
}

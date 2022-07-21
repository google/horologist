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

package com.google.android.horologist.test.toolbox.testdoubles

import com.google.android.horologist.media3.ExperimentalHorologistMedia3BackendApi
import com.google.android.horologist.media3.logging.ErrorReporter
import java.util.Collections

/**
 * Test appropriate implementation of ErrorReporter.
 */
@ExperimentalHorologistMedia3BackendApi
public class InMemoryErrorReporter : ErrorReporter {
    public val logs = Collections.synchronizedList(mutableListOf<String>())
    public val messages = Collections.synchronizedList(mutableListOf<Int>())

    override fun showMessage(message: Int) {
        messages.add(message)
    }

    override fun logMessage(
        message: String,
        category: ErrorReporter.Category,
        level: ErrorReporter.Level
    ) {
        logs.add(message)
    }
}

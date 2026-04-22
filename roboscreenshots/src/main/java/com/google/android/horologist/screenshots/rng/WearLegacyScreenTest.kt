/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.android.horologist.screenshots.rng

public abstract class WearLegacyScreenTest : WearScreenshotTest() {

    override fun testName(suffix: String): String =
        "src/test/snapshots/images/" +
            "${this.javaClass.`package`?.name}_${this.javaClass.simpleName}_" +
            "${testInfo.methodName}$suffix.png"
}

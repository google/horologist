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

package com.google.android.horologist.sample.benchmark

import android.content.Intent
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfile {

    @get:Rule
    public val baselineRule: BaselineProfileRule = BaselineProfileRule()

    @Test
    public fun profile() {
        baselineRule.collectBaselineProfile(
            packageName = "com.google.android.horologist.sample",
            profileBlock = {
                startActivityAndWait(
                    Intent("com.google.android.horologist.sample.MAIN")
                )
            }
        )
    }
}

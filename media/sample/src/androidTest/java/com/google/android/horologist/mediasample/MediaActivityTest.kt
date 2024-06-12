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

package com.google.android.horologist.mediasample

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.filters.LargeTest
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToLibrary
import com.google.android.horologist.media.ui.navigation.NavigationScreen
import com.google.android.horologist.mediasample.ui.app.MediaActivity
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@LargeTest
class MediaActivityTest {
    @get:Rule
    var rule = createAndroidComposeRule<MediaActivity>()

    @Ignore("https://github.com/google/horologist/issues/282")
    @Test
    fun testEvent() {
        val scenario = rule.activityRule.scenario

        rule.waitForIdle()

        toListAndBack()

        scenario.moveToState(Lifecycle.State.STARTED)

        scenario.moveToState(Lifecycle.State.RESUMED)

        toListAndBack()
    }

    private fun toListAndBack() {
        rule.runOnUiThread {
            rule.activity.navController.navigate(NavigationScreen.Volume)
        }
        rule.waitForIdle()

        rule.runOnUiThread {
            rule.activity.navController.navigateToLibrary()
        }
        rule.waitForIdle()
    }
}

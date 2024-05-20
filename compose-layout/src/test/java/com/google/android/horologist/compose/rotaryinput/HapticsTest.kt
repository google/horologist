/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("DEPRECATION")

package com.google.android.horologist.compose.rotaryinput

import android.R
import android.app.Activity
import android.provider.Settings
import android.view.View
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBuild

@RunWith(RobolectricTestRunner::class)
class HapticsTest {
    @Test
    @Config(sdk = [33])
    fun testPixelWatch1Wear4() {
        ShadowBuild.setManufacturer("Google")
        ShadowBuild.setModel("Google Pixel Watch")

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("Wear4AtLeastRotaryHapticFeedback")
    }

    @Test
    @Config(sdk = [30])
    fun testPixelWatch1Wear35() {
        ShadowBuild.setManufacturer("Google")
        ShadowBuild.setModel("Google Pixel Watch")
        Settings.Global.putString(
            RuntimeEnvironment.getApplication().contentResolver,
            "wear_platform_mr_number",
            "5",
        )

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("Wear3point5RotaryHapticFeedback")
    }

    @Test
    @Config(sdk = [33])
    fun testGenericWear4() {
        ShadowBuild.setManufacturer("XXX")
        ShadowBuild.setModel("YYY")

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("Wear4AtLeastRotaryHapticFeedback")
    }

    @Test
    @Config(sdk = [30])
    fun testGenericWear35() {
        ShadowBuild.setManufacturer("XXX")
        ShadowBuild.setModel("YYY")
        Settings.Global.putString(
            RuntimeEnvironment.getApplication().contentResolver,
            "wear_platform_mr_number",
            "5",
        )

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("Wear3point5RotaryHapticFeedback")
    }

    @Test
    @Config(sdk = [30])
    fun testGenericWear3() {
        ShadowBuild.setManufacturer("XXX")
        ShadowBuild.setModel("YYY")

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("DefaultRotaryHapticFeedback")
    }

    @Test
    @Config(sdk = [28])
    fun testGenericWear2() {
        ShadowBuild.setManufacturer("XXX")
        ShadowBuild.setModel("YYY")

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("DefaultRotaryHapticFeedback")
    }

    @Test
    @Config(sdk = [33])
    fun testGalaxyWatchClassic() {
        ShadowBuild.setManufacturer("Samsung")
        // Galaxy Watch4 Classic
        ShadowBuild.setModel("SM-R890")

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("GalaxyWatchHapticFeedback")
    }

    @Test
    @Config(sdk = [33])
    fun testGalaxyWatch() {
        ShadowBuild.setManufacturer("Samsung")
        // Galaxy Watch 5 Pro
        ShadowBuild.setModel("SM-R925")

        val hapticFeedback = getHapticFeedback()

        assertThat(hapticFeedback.javaClass.simpleName).isEqualTo("GalaxyWatchHapticFeedback")
    }

    private fun getHapticFeedback(): RotaryHapticFeedback {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val view = activity.findViewById<View>(R.id.content)

        return findDeviceSpecificHapticFeedback(view)
    }
}

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

package com.google.android.horologist.paparazzi

import app.cash.paparazzi.DeviceConfig
import com.android.resources.Density
import com.android.resources.Keyboard
import com.android.resources.KeyboardState
import com.android.resources.Navigation
import com.android.resources.ScreenOrientation
import com.android.resources.ScreenRatio
import com.android.resources.ScreenSize
import com.android.resources.TouchScreen

// https://www.techidence.com/galaxy-watch4-features-reviews-and-price/
public val GALAXY_WATCH4_CLASSIC_LARGE: DeviceConfig = DeviceConfig(
    screenHeight = 450,
    screenWidth = 450,
    xdpi = 321,
    ydpi = 321,
    orientation = ScreenOrientation.PORTRAIT,
    density = Density.DPI_340,
    ratio = ScreenRatio.NOTLONG,
    size = ScreenSize.SMALL,
    keyboard = Keyboard.NOKEY,
    touchScreen = TouchScreen.FINGER,
    keyboardState = KeyboardState.HIDDEN,
    softButtons = true,
    navigation = Navigation.NONAV,
    released = "October 15, 2020"
)

// https://android.googlesource.com/platform/tools/base/+/mirror-goog-studio-master-dev/sdklib/src/main/java/com/android/sdklib/devices/wear.xml
@JvmField
public val WEAR_OS_SMALL_ROUND: DeviceConfig = DeviceConfig(
    screenHeight = 320,
    screenWidth = 320,
    xdpi = 240,
    ydpi = 240,
    orientation = ScreenOrientation.PORTRAIT,
    density = Density.HIGH,
    ratio = ScreenRatio.LONG,
    size = ScreenSize.SMALL,
    keyboard = Keyboard.NOKEY,
    touchScreen = TouchScreen.FINGER,
    keyboardState = KeyboardState.HIDDEN,
    softButtons = true,
    navigation = Navigation.NONAV,
    released = "June 7, 2014"
)

// https://android.googlesource.com/platform/tools/base/+/mirror-goog-studio-master-dev/sdklib/src/main/java/com/android/sdklib/devices/wear.xml
@JvmField
public val WEAR_OS_SQUARE: DeviceConfig = DeviceConfig(
    screenHeight = 280,
    screenWidth = 280,
    xdpi = 240,
    ydpi = 240,
    orientation = ScreenOrientation.PORTRAIT,
    density = Density.HIGH,
    ratio = ScreenRatio.LONG,
    size = ScreenSize.SMALL,
    keyboard = Keyboard.NOKEY,
    touchScreen = TouchScreen.FINGER,
    keyboardState = KeyboardState.HIDDEN,
    softButtons = true,
    navigation = Navigation.NONAV,
    released = "June 7, 2014"
)

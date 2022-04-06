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

package com.google.android.horologist.audioui
import android.content.Context
import android.os.Build
import android.os.VibrationEffect.Composition
import android.os.Vibrator
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.android.horologist.audioui.devices.Haptics
import com.google.android.horologist.audioui.devices.LegacyHaptics
import com.google.android.horologist.audioui.devices.PremiumHaptics
import com.google.android.horologist.audioui.devices.PrimitiveHaptics
import com.google.android.horologist.audioui.devices.RotaryInput

public val LocalHaptics: ProvidableCompositionLocal<Haptics> =
    staticCompositionLocalOf { Haptics.None }
public val LocalRotaryInput: ProvidableCompositionLocal<RotaryInput?> =
    staticCompositionLocalOf { null }

public fun haptics(context: Context): Haptics {
    if (Build.PRODUCT.startsWith("sdk_gwear_")) {
        return EmulatedHaptics
    }

    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager

    val touchExplorationEnabled = accessibilityManager.isTouchExplorationEnabled

    if (touchExplorationEnabled) {
        return Haptics.None
    }

    @Suppress("DEPRECATION")
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        && vibrator.areAllPrimitivesSupported(Composition.PRIMITIVE_CLICK)) {
        PremiumHaptics(vibrator)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        PrimitiveHaptics(vibrator)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LegacyHaptics(vibrator)
    } else {
        Haptics.None
    }
}
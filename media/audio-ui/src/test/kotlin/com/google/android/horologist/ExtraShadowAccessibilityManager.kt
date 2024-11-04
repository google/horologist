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

package com.google.android.horologist

import android.view.accessibility.AccessibilityManager
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowAccessibilityManager
import org.robolectric.versioning.AndroidVersions.U

@Implements(AccessibilityManager::class)
class ExtraShadowAccessibilityManager: ShadowAccessibilityManager() {

    /**
     * This shadow method is required because {@link
     * android.view.accessibility.DirectAccessibilityConnection} calls it to determine if any
     * transformations have occurred on this window.
     */
    @Implementation(minSdk = U.SDK_INT)
    fun getWindowTransformationSpec(windowId: Int):  Any {
        val instance = Class.forName("android.view.accessibility.IAccessibilityManager\$WindowTransformationSpec").newInstance()

        return instance
    }
}
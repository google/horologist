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

@file:OptIn(ExperimentalTestApi::class)

package com.google.android.horologist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowBuild

@Config(
    sdk = [35],
    qualifiers = RobolectricDeviceQualifiers.WearOSLargeRound,
    shadows = [ExtraShadowAccessibilityManager::class, NonShadowShadowAccessibilityNodeInfo::class],
)
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BoxA11yTest {
    @Test
    fun badBox() = runComposeUiTest {
        setContent {
            Box(
                Modifier.size(48.dp).semantics {
                    // The SemanticsModifier will make this node importantForAccessibility
                    // Having no content description is now a violation
                    contentDescription = ""
                }
            )
        }

        // TODO change this check in ATF
        ShadowBuild.setFingerprint("test_fingerprint")

        enableAccessibilityChecks()

        onRoot().tryPerformAccessibilityChecks()
    }

    @Test
    fun goodBox() = runComposeUiTest {
        setContent {
            Box(Modifier.size(20.dp))
        }

        // TODO change this check in ATF
        ShadowBuild.setFingerprint("test_fingerprint")

        enableAccessibilityChecks()

        onRoot().tryPerformAccessibilityChecks()
    }
}

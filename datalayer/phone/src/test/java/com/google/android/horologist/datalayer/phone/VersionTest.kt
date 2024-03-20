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

package com.google.android.horologist.datalayer.phone

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VersionTest {
    @Test
    fun testValid() {
        assertThat(Version.parse("1.2")!!.inputVersion).containsExactly(1, 2).inOrder()
        assertThat(Version.parse("2.1.0.576785526")!!.inputVersion).containsExactly(
            2,
            1,
            0,
            576785526,
        ).inOrder()
    }

    @Test
    fun testNotValid() {
        assertThat(Version.parse("XYZ")).isNull()
        assertThat(Version.parse("2.1.0.X")).isNull()
    }

    @Test
    fun testComparison() {
        val known = Version.parse("2.1.0.576785526")
        val knownPlusOne = Version.parse("2.1.1")
        val old = Version.parse("1.1.0")

        assertThat(known).isGreaterThan(old)
        assertThat(known).isLessThan(knownPlusOne)
        assertThat(known).isEquivalentAccordingToCompareTo(known)
    }
}

/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlotMapTest {

  @Test
  fun primaryConstructor_initializesColorSlotsDirectly() {
    val expectedColor: RemoteColor = Color(0xFF112233).rc
    val slotMap = SlotMap(colorSlots = mapOf("slot1" to expectedColor))

    assertThat(slotMap.colorSlots).containsExactly("slot1", expectedColor)
  }

  @Test
  fun primaryConstructor_initializesMultipleColorSlots() {
    val red = Color.Red.rc
    val blue = Color.Blue.rc
    val slotMap = SlotMap(mapOf("slot_red" to red, "slot_blue" to blue))

    assertThat(slotMap.colorSlots).containsExactly("slot_red", red, "slot_blue", blue)
  }

  @Test
  fun defaultConstructor_hasEmptyColorSlots() {
    val slotMap = SlotMap()

    assertThat(slotMap.colorSlots).isEmpty()
  }

  @Test
  fun emptyCompanion_hasEmptyColorSlots() {
    assertThat(SlotMap.Empty.colorSlots).isEmpty()
  }

  @Test
  fun equalsAndHashCode_basedOnColorSlots() {
    val color = Color.Red.rc
    val map1 = SlotMap(mapOf("color" to color))
    val map2 = SlotMap(mapOf("color" to color))
    val map3 = SlotMap(mapOf("different" to color))

    assertThat(map1).isEqualTo(map2)
    assertThat(map1.hashCode()).isEqualTo(map2.hashCode())
    assertThat(map1).isNotEqualTo(map3)
  }
}

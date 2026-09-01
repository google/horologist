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
import com.google.android.horologist.remotecompose.lottie.format.properties.StaticColorProperty

/**
 * A mapping of slot IDs to values.
 *
 * Slots can be used to share values between properties, or to override values at runtime. For
 * example, a fill color can reference a slot ID, which can be resolved to a color provided by the
 * application to enable dynamic theming.
 */
class SlotMap(colors: Map<String, Int>) {
  private val colorSlots: Map<String, StaticColorProperty> =
    colors.mapValues { (slotId, colorInt) ->
      StaticColorProperty(slotId = slotId, colorInt = colorInt)
    }

  fun getColor(slotId: String): RemoteColor? {
    val prop = colorSlots[slotId] ?: return null
    return prop.value
  }

  companion object {
    val Empty: SlotMap = SlotMap(emptyMap())
  }
}

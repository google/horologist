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

package com.google.android.horologist.remotecompose.lottie.format.properties

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * Normalizes legacy Bodymovin/After Effects keyframe arrays during deserialization.
 *
 * In Lottie exports prior to Bodymovin 5.5, terminal keyframes (or hold keyframes) frequently omit
 * their start value field (`"s"`), relying on the preceding keyframe's end value (`"e"`) or start
 * value (`"s"`). This transforming serializer backfills missing `"s"` fields before delegating to
 * the strongly typed keyframe element serializer.
 */
internal open class KeyframeListSerializer<T : Any>(elementSerializer: KSerializer<T>) :
  JsonTransformingSerializer<List<T>>(ListSerializer(elementSerializer)) {

  override fun transformDeserialize(element: JsonElement): JsonElement {
    val array = element as? JsonArray ?: return element
    var changed = false
    val result = ArrayList<JsonElement>(array.size)
    for (i in 0 until array.size) {
      val item = array[i]
      if (item is JsonObject && !item.containsKey("s") && item.containsKey("t") && i > 0) {
        val prev = result[i - 1] as? JsonObject
        val inheritedValue = prev?.get("e") ?: prev?.get("s")
        if (inheritedValue != null) {
          val patched = LinkedHashMap<String, JsonElement>(item.size + 1)
          patched.putAll(item)
          patched["s"] = inheritedValue
          result.add(JsonObject(patched))
          changed = true
          continue
        }
      }
      result.add(item)
    }
    return if (changed) JsonArray(result) else element
  }
}

internal object ScalarKeyframeListSerializer :
  KeyframeListSerializer<ScalarPropertyKeyframe>(ScalarPropertyKeyframe.serializer())

internal object PositionKeyframeListSerializer :
  KeyframeListSerializer<PositionPropertyKeyframe>(PositionPropertyKeyframe.serializer())

internal object VectorKeyframeListSerializer :
  KeyframeListSerializer<VectorPropertyKeyframe>(VectorPropertyKeyframe.serializer())

internal object ColorKeyframeListSerializer :
  KeyframeListSerializer<ColorPropertyKeyframe>(ColorPropertyKeyframe.serializer())

internal object BezierKeyframeListSerializer :
  KeyframeListSerializer<BezierKeyframe>(BezierKeyframe.serializer())

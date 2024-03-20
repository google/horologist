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

import kotlin.math.max

public data class Version internal constructor(val inputVersion: List<Int>) :
    Comparable<Version> {

        public override fun compareTo(other: Version): Int =
            (inputVersion to other.inputVersion).let { (thisParts, thatParts) ->
                val length = max(thisParts.size, thatParts.size)
                for (i in 0 until length) {
                    val thisPart = if (i < thisParts.size) thisParts[i] else 0
                    val thatPart = if (i < thatParts.size) thatParts[i] else 0
                    if (thisPart < thatPart) return -1
                    if (thisPart > thatPart) return 1
                }
                0
            }

        public companion object {
            public fun parse(version: String): Version? {
                if (!version.matches("[0-9]+(\\.[0-9]+)*".toRegex())) {
                    return null
                }

                return Version(version.split(".").toList().map { it.toInt() })
            }
        }
    }

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

public data class Version(val inputVersion: List<Int>) :
    Comparable<Version> {

        public override fun compareTo(other: Version): Int {
            inputVersion.zip(other.inputVersion).forEach { (t, o) ->
                val comparison = t.compareTo(o)
                if (comparison != 0) {
                    return comparison
                }
            }
            return inputVersion.size.compareTo(other.inputVersion.size)
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

/*
 * Copyright 2022 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.networks.data

import java.time.Instant

sealed class Status(val order: Int) {
    object Available : Status(order = 1) {
        override fun toString() = "Available"
    }
    class Losing(val instant: Instant) : Status(order = 2) {
        override fun toString() = "Losing"
    }
    object Lost : Status(order = 3) {
        override fun toString() = "Lost"
    }
    object Unknown : Status(order = 4) {
        override fun toString() = "Unknown"
    }
}

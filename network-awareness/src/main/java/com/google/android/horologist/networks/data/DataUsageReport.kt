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
import java.time.temporal.ChronoUnit

public data class DataUsageReport(
    public val dataByType: Map<String, Long>,
    public val from: Instant,
    public val to: Instant,
) {
    public companion object {
        public val Empty = DataUsageReport(
            dataByType = mapOf(),
            from = Instant.now().minus(1, ChronoUnit.DAYS),
            to = Instant.now()
        )
    }
}
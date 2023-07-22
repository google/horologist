/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.health.composables
public class ActiveDurationDefaults {
    public companion object {
        public const val HH_MM_SS: String = "%1\$02d:%2\$02d:%3\$02d"
        public const val H_MM_SS: String = "%1\$01d:%2\$02d:%3\$02d"
        public const val MM_SS: String = "%2\$02d:%3\$02d"
        public const val M_SS: String = "%2\$01d:%3\$02d"
        public const val HH_MM: String = "%1\$02d:%2\$02d"
        public const val H_MM: String = "%1\$01d:%2\$02d"
    }
}

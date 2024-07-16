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

package com.google.android.horologist.audit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Icon
import com.google.android.horologist.compose.material.Confirmation

@Composable
fun ConfirmationsAudit(route: AuditNavigation.Confirmations.Audit) {
    when (route.config) {
        AuditNavigation.Confirmations.Config.IconAnd1Line -> {
            Confirmation(
                showDialog = true,
                title = "Title",
                icon = { Icon(Icons.Default.Cyclone, contentDescription = "") },
                onTimeout = {},
            )
        }

        AuditNavigation.Confirmations.Config.IconAnd3Line -> {
            Confirmation(
                showDialog = true,
                title = "Title\nThis is a second and third line.",
                icon = { Icon(Icons.Default.Cyclone, contentDescription = "") },
                onTimeout = {},
            )
        }

        AuditNavigation.Confirmations.Config.TwoBottomRound -> {
        }
    }
}

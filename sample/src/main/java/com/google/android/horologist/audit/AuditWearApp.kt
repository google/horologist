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

import androidx.compose.runtime.Composable
import androidx.navigation.toRoute
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable

@Composable
fun AuditWearApp() {
    val navController = rememberSwipeDismissableNavController()

    AppScaffold {
        SwipeDismissableNavHost(navController = navController, startDestination = AuditNavigation.MainMenu) {
            composable<AuditNavigation.MainMenu> {
                AuditMenuScreen(onClick = { navController.navigate(it) })
            }
            composable<AuditNavigation.Lists.Audit> {
                val route = it.toRoute<AuditNavigation.Lists.Audit>()
                ListsAudit(route)
            }
            composable<AuditNavigation.Dialogs.Audit> {
                val route = it.toRoute<AuditNavigation.Dialogs.Audit>()
                DialogsAudit(route)
            }
            composable<AuditNavigation.Confirmations.Audit> {
                val route = it.toRoute<AuditNavigation.Confirmations.Audit>()
                ConfirmationsAudit(route)
            }
            composable<AuditNavigation.Pickers.Audit> {
                val route = it.toRoute<AuditNavigation.Pickers.Audit>()
                PickersAudit(route)
            }
            composable<AuditNavigation.Stepper.Audit> {
                val route = it.toRoute<AuditNavigation.Stepper.Audit>()
                StepperAudit(route)
            }
            composable<AuditNavigation.ProgressIndicator.Audit> {
                val route = it.toRoute<AuditNavigation.ProgressIndicator.Audit>()
                ProgressIndicatorAudit(route)
            }
            composable<AuditNavigation.PageIndicator.Audit> {
                val route = it.toRoute<AuditNavigation.PageIndicator.Audit>()
                PageIndicatorAudit(route)
            }
            composable<AuditNavigation.PositionIndicator.Audit> {
                val route = it.toRoute<AuditNavigation.PositionIndicator.Audit>()
                PositionIndicatorAudit(route)
            }
            composable<AuditNavigation.VolumeRsb.Audit> {
                val route = it.toRoute<AuditNavigation.VolumeRsb.Audit>()
                VolumeRsbAudit(route)
            }
            composable<AuditNavigation.CurvedTimeText.Audit> {
                val route = it.toRoute<AuditNavigation.CurvedTimeText.Audit>()
                CurvedTimeTextAudit(route)
            }
            composable<AuditNavigation.Cards.Audit> {
                val route = it.toRoute<AuditNavigation.Cards.Audit>()
                CardsAudit(route)
            }
        }
    }
}


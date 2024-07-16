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
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.audit.AuditNavigation.Cards
import com.google.android.horologist.audit.AuditNavigation.Confirmations
import com.google.android.horologist.audit.AuditNavigation.CurvedTimeText
import com.google.android.horologist.audit.AuditNavigation.Dialogs
import com.google.android.horologist.audit.AuditNavigation.Lists
import com.google.android.horologist.audit.AuditNavigation.PageIndicator
import com.google.android.horologist.audit.AuditNavigation.Pickers
import com.google.android.horologist.audit.AuditNavigation.PositionIndicator
import com.google.android.horologist.audit.AuditNavigation.ProgressIndicator
import com.google.android.horologist.audit.AuditNavigation.Stepper
import com.google.android.horologist.audit.AuditNavigation.VolumeRsb
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable
import kotlin.reflect.typeOf

@Composable
fun AuditWearApp(
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    AppScaffold {
        SwipeDismissableNavHost(navController = navController, startDestination = AuditNavigation.MainMenu) {
            composable<AuditNavigation.MainMenu> {
                AuditMenuScreen(onClick = { navController.navigate(it) })
            }
            composable<Lists.Audit>(
                typeMap = mapOf(typeOf<Lists.Config>() to enumType<Lists.Config>()),
            ) {
                val route = it.toRoute<Lists.Audit>()

                route.compose()
            }
            composable<Dialogs.Audit>(
                typeMap = mapOf(typeOf<Dialogs.Config>() to enumType<Dialogs.Config>()),
            ) {
                val route = it.toRoute<Dialogs.Audit>()
                DialogsAudit(route)
            }
            composable<Confirmations.Audit>(
                typeMap = mapOf(typeOf<Confirmations.Config>() to enumType<Confirmations.Config>()),
            ) {
                val route = it.toRoute<Confirmations.Audit>()
                ConfirmationsAudit(route)
            }
            composable<Pickers.Audit>(
                typeMap = mapOf(typeOf<Pickers.Config>() to enumType<Pickers.Config>()),
            ) {
                val route = it.toRoute<Pickers.Audit>()
                PickersAudit(route)
            }
            composable<Stepper.Audit>(
                typeMap = mapOf(typeOf<Stepper.Config>() to enumType<Stepper.Config>()),
            ) {
                val route = it.toRoute<Stepper.Audit>()
                StepperAudit(route)
            }
            composable<ProgressIndicator.Audit>(
                typeMap = mapOf(typeOf<ProgressIndicator.Config>() to enumType<ProgressIndicator.Config>()),
            ) {
                val route = it.toRoute<ProgressIndicator.Audit>()
                ProgressIndicatorAudit(route)
            }
            composable<PageIndicator.Audit>(
                typeMap = mapOf(typeOf<PageIndicator.Config>() to enumType<PageIndicator.Config>()),
            ) {
                val route = it.toRoute<PageIndicator.Audit>()
                PageIndicatorAudit(route)
            }
            composable<PositionIndicator.Audit>(
                typeMap = mapOf(typeOf<PositionIndicator.Config>() to enumType<PositionIndicator.Config>()),
            ) {
                val route = it.toRoute<PositionIndicator.Audit>()
                PositionIndicatorAudit(route)
            }
            composable<VolumeRsb.Audit>(
                typeMap = mapOf(typeOf<VolumeRsb.Config>() to enumType<VolumeRsb.Config>()),
            ) {
                val route = it.toRoute<VolumeRsb.Audit>()
                VolumeRsbAudit(route)
            }
            composable<CurvedTimeText.Audit>(
                typeMap = mapOf(typeOf<CurvedTimeText.Config>() to enumType<CurvedTimeText.Config>()),
            ) {
                val route = it.toRoute<CurvedTimeText.Audit>()
                CurvedTimeTextAudit(route)
            }
            composable<Cards.Audit>(
                typeMap = mapOf(typeOf<Cards.Config>() to enumType<Cards.Config>()),
            ) {
                val route = it.toRoute<Cards.Audit>()
                CardsAudit(route)
            }
        }
    }
}

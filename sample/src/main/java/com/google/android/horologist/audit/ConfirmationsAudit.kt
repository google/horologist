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

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.material.Confirmation
import com.google.android.horologist.sample.R

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun ConfirmationsAudit(route: AuditNavigation.Confirmations.Audit) {
    when (route.config) {
        AuditNavigation.Confirmations.Config.IconAnd1Line -> {
            Confirmation(
                showDialog = true,
                title = "Title",
                icon = {
                    AnimatedIcon(
                        AnimatedImageVector.animatedVectorResource(R.drawable.openonphone),
                    )
                },
                onTimeout = {},
            )
        }

        AuditNavigation.Confirmations.Config.IconAnd3Line -> {
            Confirmation(
                showDialog = true,
                title = "Title\nThis is a second and third line.",
                icon = {
                    AnimatedIcon(
                        AnimatedImageVector.animatedVectorResource(R.drawable.openonphone),
                    )
                },
                onTimeout = {},
            )
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun AnimatedIcon(animation: AnimatedImageVector) {
    // Initially, animation is static and shown at the start position (atEnd =
    // false).
    // Then, we use the EffectAPI to trigger a state change to atEnd = true,
    // which plays the animation from start to end.
    var atEnd by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        atEnd = true
        onDispose {}
    }
    Image(
        painter = rememberAnimatedVectorPainter(animation, atEnd),
        contentDescription = "Open on phone",
        modifier = Modifier.size(48.dp),
    )
}

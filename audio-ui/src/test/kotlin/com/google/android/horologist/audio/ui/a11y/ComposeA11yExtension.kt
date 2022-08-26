/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.android.horologist.audio.ui.a11y

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import app.cash.paparazzi.RenderExtension
import com.google.android.horologist.paparazzi.a11y.AccessibilityElement
import com.google.android.horologist.paparazzi.a11y.AccessibilityState

class ComposeA11yExtension : RenderExtension {
    public lateinit var accessibilityState: AccessibilityState

    private lateinit var rootForTest: RootForTest

    init {
        ViewRootForTest.onViewCreatedCallback = { viewRoot ->
            viewRoot.view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(p0: View) {
                    // Grab the AndroidComposeView using the same hook that createComposeRule uses
                    // Note: It isn't usefully populated at this point.
                    rootForTest = p0 as RootForTest
                }

                override fun onViewDetachedFromWindow(p0: View) {
                }
            })
        }
    }

    private fun processAccessibleChildren(p0: SemanticsNode, fn: (AccessibilityElement) -> Unit) {
        val contentDescription = p0.config.getOrNull(SemanticsProperties.ContentDescription)
        val stateDescription = p0.config.getOrNull(SemanticsProperties.StateDescription)
        val onClickLabel = p0.config.getOrNull(SemanticsActions.OnClick)?.label
        val role = p0.config.getOrNull(SemanticsProperties.Role)?.toString()
        val disabled = p0.config.getOrNull(SemanticsProperties.Disabled) != null

        if (contentDescription != null || stateDescription != null || onClickLabel != null || role != null) {
            val position = p0.boundsInRoot.toAndroidRect()
            val touchBounds = p0.touchBoundsInRoot.toAndroidRect()
            fn(
                AccessibilityElement(
                    position,
                    if (touchBounds != position) touchBounds else null,
                    contentDescription,
                    stateDescription,
                    onClickLabel,
                    role,
                    disabled
                )
            )
        }

        p0.children.forEach {
            processAccessibleChildren(it, fn)
        }
    }

    override fun renderView(contentView: View): View {
        val composeView = (contentView as ViewGroup).children.first() as ComposeView

        // Capture the accessibility elements during the drawing phase after
        // measurement and layout has occurred
        composeView.doOnPreDraw {
            val elements = buildList {
                processAccessibleChildren(rootForTest.semanticsOwner.rootSemanticsNode) {
                    add(it)
                }
            }
            accessibilityState = AccessibilityState(contentView.width, contentView.height, elements)
        }

        return contentView
    }
}

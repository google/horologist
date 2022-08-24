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

package com.google.android.horologist.audio.ui

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import app.cash.paparazzi.RenderExtension

class ComposeA11yExtension : RenderExtension {
    private var rootForTest: RootForTest? = null

    init {
        ViewRootForTest.onViewCreatedCallback = { viewRoot ->
            viewRoot.view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(p0: View?) {
                    Exception("Attach").printStackTrace()

                    if (p0 is RootForTest) {
                        rootForTest = p0
                    }
                }

                override fun onViewDetachedFromWindow(p0: View?) {
                    if (p0 is RootForTest) {
                        printChildren(p0.semanticsOwner.rootSemanticsNode)
                    }
                }
            })
        }
    }

    private fun printChildren(p0: SemanticsNode) {
        val contentDescription = p0.config.getOrNull(SemanticsProperties.ContentDescription)
        val stateDescription = p0.config.getOrNull(SemanticsProperties.StateDescription)
        val onClickLabel = p0.config.getOrNull(SemanticsActions.OnClick)?.label

        if (contentDescription != null || stateDescription != null || onClickLabel != null) {
            println("Position: ${p0.layoutInfo.coordinates.positionInRoot()} ${p0.layoutInfo.coordinates.size}")
            if (contentDescription != null) {
                println("Content Description $contentDescription")
            }
            if (stateDescription != null) {
                println("State Description $stateDescription")
            }
            if (onClickLabel != null) {
                println("On Click $onClickLabel")
            }
        }

        p0.children.forEach {
            printChildren(it)
        }
    }

    private fun buildAccessibilityView(contentView: View): View {
        val linearLayout = LinearLayout(contentView.context).apply {
            orientation = LinearLayout.VERTICAL
        }

        return linearLayout
    }

    override fun renderView(contentView: View): View {
        Exception("renderView").printStackTrace()

        rootForTest?.let {
            printChildren(it.semanticsOwner.rootSemanticsNode)
        }

        return LinearLayout(contentView.context).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            val contentLayoutParams = contentView.layoutParams ?: generateLayoutParams(null)
            addView(
                contentView,
                LinearLayout.LayoutParams(
                    contentLayoutParams.width,
                    contentLayoutParams.height,
                    1f
                )
            )
            addView(
                buildAccessibilityView(contentView),
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1f
                )
            )
        }
    }
}
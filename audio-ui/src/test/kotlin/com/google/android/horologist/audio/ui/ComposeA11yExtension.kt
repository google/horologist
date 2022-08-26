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

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.layout.positionInRoot
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

class ComposeA11yExtension : RenderExtension {
    private lateinit var rootForTest: RootForTest

    init {
        ViewRootForTest.onViewCreatedCallback = { viewRoot ->
            viewRoot.view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(p0: View?) {
                    rootForTest = p0 as RootForTest
                }

                override fun onViewDetachedFromWindow(p0: View?) {
                }
            })
        }
    }

    data class AccessibilityElement(
        val layoutInfo: LayoutInfo,
        val contentDescription: List<String>?,
        val stateDescription: String?,
        val onClickLabel: String?,
    )

    private fun printChildren(p0: SemanticsNode) {
        processAccessibleChildren(p0) {
            println("Position: ${it.layoutInfo.coordinates.positionInRoot()} ${it.layoutInfo.coordinates.size}")
            if (it.contentDescription != null) {
                println("Content Description ${it.contentDescription}")
            }
            if (it.stateDescription != null) {
                println("State Description ${it.stateDescription}")
            }
            if (it.onClickLabel != null) {
                println("On Click ${it.onClickLabel}")
            }
        }
    }


    private fun processAccessibleChildren(p0: SemanticsNode, fn: (AccessibilityElement) -> Unit) {
        val contentDescription = p0.config.getOrNull(SemanticsProperties.ContentDescription)
        val stateDescription = p0.config.getOrNull(SemanticsProperties.StateDescription)
        val onClickLabel = p0.config.getOrNull(SemanticsActions.OnClick)?.label

        if (contentDescription != null || stateDescription != null || onClickLabel != null) {
            fn(AccessibilityElement(p0.layoutInfo, contentDescription, stateDescription, onClickLabel))
        }

        p0.children.forEach {
            processAccessibleChildren(it, fn)
        }
    }

    private fun buildAccessibilityView(contentView: View): View {
        val linearLayout = LinearLayout(contentView.context).apply {
            orientation = LinearLayout.VERTICAL
        }

        return linearLayout
    }

    override fun renderView(contentView: View): View {
        val composeView = (contentView as ViewGroup).children.first() as ComposeView

        return LinearLayout(contentView.context).apply {
            val linearLayout = this

            orientation = LinearLayout.HORIZONTAL
            weightSum = 1f
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
//            val accessibilityView = buildAccessibilityView(contentView)
//            addView(
//                accessibilityView,
//                LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    1f
//                )
//            )

            composeView.doOnPreDraw {
                printChildren(rootForTest.semanticsOwner.rootSemanticsNode)
                processAccessibleChildren(rootForTest.semanticsOwner.rootSemanticsNode) {
                    linearLayout.addView(View(context).apply {
                        val innerMargin = dip(4)

                        layoutParams = ViewGroup.LayoutParams(
                            dip(DEFAULT_RECT_SIZE),
                            dip(DEFAULT_RECT_SIZE)
                        )
                        background = GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            intArrayOf(android.graphics.Color.CYAN, android.graphics.Color.CYAN)
                        ).apply {
                            cornerRadius = dip(DEFAULT_RECT_SIZE / 4f)
                        }
                        setPaddingRelative(innerMargin, innerMargin, innerMargin, innerMargin)
                    })
                }
            }
        }
    }
}

private fun View.dip(value: Float): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    )

private fun View.dip(value: Int): Int = dip(value.toFloat()).toInt()

const val DEFAULT_TEXT_SIZE: Float = 10f
const val DEFAULT_RECT_SIZE: Int = 16
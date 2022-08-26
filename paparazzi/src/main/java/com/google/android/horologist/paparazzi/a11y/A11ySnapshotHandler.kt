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

package com.google.android.horologist.paparazzi.a11y

import app.cash.paparazzi.Snapshot
import app.cash.paparazzi.SnapshotHandler
import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Composite
import java.awt.Graphics2D
import java.awt.image.BufferedImage

public class A11ySnapshotHandler(
    private val delegate: SnapshotHandler,
    private val accessibilityStateFn: () -> AccessibilityState
) : SnapshotHandler {
    private val colors =
        listOf(Color.BLUE, Color.CYAN, Color.GREEN, Color.GRAY, Color.PINK, Color.MAGENTA, Color.YELLOW, Color.ORANGE)

    override fun close() {
        delegate.close()
    }

    private fun colorFor(i: Int): Int = colors[i % colors.size].rgb

    override fun newFrameHandler(
        snapshot: Snapshot,
        frameCount: Int,
        fps: Int
    ): SnapshotHandler.FrameHandler {
        val delegateFrameHandler = delegate.newFrameHandler(snapshot, frameCount, fps)
        return object : SnapshotHandler.FrameHandler {
            override fun close() {
                delegateFrameHandler.close()
            }

            override fun handle(image: BufferedImage) {
                val modifiedImage = BufferedImage(image.width * 2, image.height, image.type)

                val accessibilityState = accessibilityStateFn()

                // TODO handle rectangles
                val scale = 1000f / accessibilityState.height

                val accessibilityItems = accessibilityState.elements.mapIndexed { i, it ->
                    AccessibilityItem(
                        i,
                        it.contentDescription?.firstOrNull() ?: it.stateDescription
                            ?: it.onClickLabel ?: "N/A",
                        colorFor(i),
                        it.scaleBy(scale)
                    )
                }

                modifiedImage.createGraphics().apply {
                    drawScreenshotImage(image = image, alpha = 0.5f)

                    drawBoxes(accessibilityItems)

                    drawLegend(accessibilityItems)

                    dispose()
                }

                delegateFrameHandler.handle(modifiedImage)
            }
        }
    }

    private fun Graphics2D.drawScreenshotImage(image: BufferedImage, alpha: Float = 1f) {
        withComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)) {
            drawImage(image, 0, 0, image.width, image.height, null)
        }
    }

    private fun Graphics2D.withComposite(newComposite: Composite, fn: () -> Unit = {}) {
        val current = composite
        composite = newComposite

        fn()

        composite = current
    }

    private fun Graphics2D.drawBoxes(accessibilityItems: List<AccessibilityItem>) {
        accessibilityItems.forEach {
            val color = Color(it.color)
            paint = color
            stroke = BasicStroke(3f)
            drawRect(
                it.element.displayBounds.left,
                it.element.displayBounds.top,
                it.element.displayBounds.width(),
                it.element.displayBounds.height()
            )
            paint = Color(color.red, color.green, color.blue, 255 / 3)
            fillRect(
                it.element.displayBounds.left,
                it.element.displayBounds.top,
                it.element.displayBounds.width(),
                it.element.displayBounds.height()
            )
            if (it.element.touchBounds != null) {
                drawRect(
                    it.element.touchBounds.left,
                    it.element.touchBounds.top,
                    it.element.touchBounds.width(),
                    it.element.touchBounds.height()
                )
            }
        }
    }

    private fun Graphics2D.drawLegend(accessibilityItems: List<AccessibilityItem>) {
        paint = Color.WHITE
        fillRect(1000, 0, 1000, 1000)

        font = font.deriveFont(20f)

        var index = 1
        accessibilityItems.forEach { it ->
            paint = Color(it.color)
            if (it.element.role != null || it.element.disabled) {
                val role = if (it.element.role != null) "Role " + it.element.role + " " else ""
                val disabled = if (it.element.disabled) "Disabled" else ""
                drawString(role + disabled, 1050f, 28f * index++)
            }
            if (it.element.contentDescription != null) {
                drawString(
                    "Content Description " + it.element.contentDescription.joinToString(", "),
                    1050f,
                    28f * index++
                )
            }
            if (it.element.stateDescription != null) {
                drawString("State Description " + it.element.stateDescription, 1050f, 28f * index++)
            }
            if (it.element.onClickLabel != null) {
                drawString("On Click " + it.element.onClickLabel, 1050f, 28f * index++)
            }
            index++
        }
    }
}

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

package com.google.android.horologist.paparazzi

import app.cash.paparazzi.Snapshot
import app.cash.paparazzi.SnapshotHandler
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage

@ExperimentalHorologistPaparazziApi
public class WearSnapshotHandler(private val delegate: SnapshotHandler) : SnapshotHandler {
    override fun close() {
        delegate.close()
    }

    private fun frameImage(image: BufferedImage): BufferedImage {
        val newImage = BufferedImage(image.width, image.height, image.type)
        newImage.createGraphics().apply {
            clip = Ellipse2D.Float(0f, 0f, image.height.toFloat(), image.width.toFloat())
            drawImage(image, 0, 0, image.width, image.height, null)
            dispose()
        }
        return newImage
    }

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
                val isSquare = snapshot.testName.methodName.contains("square", ignoreCase = true)
                val processedImage = if (isSquare) {
                    image
                } else {
                    frameImage(image)
                }

                delegateFrameHandler.handle(processedImage)
            }
        }
    }
}

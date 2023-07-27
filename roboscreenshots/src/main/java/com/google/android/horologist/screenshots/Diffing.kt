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

package com.google.android.horologist.screenshots

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.google.android.horologist.screenshots.ScreenshotTestRule.RecordMode
import com.quickbird.snapshot.Diffing
import com.quickbird.snapshot.FileSnapshotting
import com.quickbird.snapshot.FileStoring
import com.quickbird.snapshot.Snapshotting
import com.quickbird.snapshot.asserted
import com.quickbird.snapshot.bitmap
import okio.Buffer
import okio.ByteString
import java.io.File
import kotlin.math.ceil

internal val Diffing.Companion.highlightWithRed
    get() = Diffing<Color> { first, second ->
        if (first == second) {
            first.copy(alpha = first.alpha / 3f)
        } else {
            Color.Red
        }
    }

internal val Bitmap.size: Size
    get() = Size(width, height)

internal fun Bitmap.eachPixel(fn: (Int, Int) -> Unit) {
    val size = this.size
    for (x in 0 until size.width) {
        for (y in 0 until size.height) {
            fn(x, y)
        }
    }
}

internal fun Diffing.Companion.bitmapWithTolerance(tolerance: Float, colorDiffing: Diffing<Color>) =
    Diffing<Bitmap> { originalBitmap, newBitmap ->
        val originalBytes = originalBitmap.asByteString()
        val newBytes = newBitmap.asByteString()
        if (originalBitmap.size != newBitmap.size) {
            newBitmap
        } else if (originalBytes == newBytes) {
            null
        } else {
            var differentCount = 0
            val diffBitmap = originalBitmap.copy(originalBitmap.config, true).apply {
                eachPixel { x, y ->
                    val originalColor = Color(originalBitmap.getPixel(x, y))
                    val newColor = Color(newBitmap.getPixel(x, y))

                    if (originalColor != newColor) {
                        differentCount += 1
                    }

                    val diffColor = colorDiffing(originalColor, newColor) ?: originalColor
                    setPixel(x, y, diffColor.toArgb())
                }
            }
            val pixelCount = originalBitmap.width * originalBitmap.height
            val toleratedDiffs = ceil(tolerance * pixelCount).toInt()
            if (differentCount <= toleratedDiffs) {
                null
            } else {
                val diffPercent = differentCount.toDouble() / pixelCount
                println("$diffPercent")
                diffBitmap
            }
        }
    }

internal fun Bitmap.asByteString(): ByteString = Buffer().apply {
    compress(Bitmap.CompressFormat.PNG, 0, outputStream())
}.readByteString()

internal fun Snapshotting<SemanticsNodeInteraction, Bitmap>.fileSnapshottingX() = FileSnapshotting(
    fileStoring = FileStoring.bitmap,
    snapshotting = this
)

@SuppressLint("NewApi")
internal suspend fun FileSnapshotting<SemanticsNodeInteraction, Bitmap>.snapshot(
    value: SemanticsNodeInteraction,
    testClass: Class<*>,
    testName: String,
    record: RecordMode
) {
    paparazziCompatibleSnapshot(
        value = value,
        record = record,
        testName = testName,
        testClass = testClass
    )
}

@SuppressLint("NewApi")
internal suspend fun FileSnapshotting<SemanticsNodeInteraction, Bitmap>.paparazziCompatibleSnapshot(
    value: SemanticsNodeInteraction,
    record: RecordMode = RecordMode.Test,
    testClass: Class<*>,
    testName: String
) {
    val referenceDirectory = File("src/test/snapshots/images").apply {
        mkdirs()
    }
    val diffDirectory = File("out")

    val packageName = testClass.packageName
    val className = testClass.simpleName

    val filePrefix = packageName + "_" + className + "_" + testName
    val referenceFile = File(referenceDirectory, "$filePrefix.png")
    val diffFileName = File(diffDirectory, "${filePrefix}_delta.png")

    val snapshot = snapshotting.snapshot(value)
    val fileStoring = fileStoring.asserted

    if (record == RecordMode.Record) {
        fileStoring.store(snapshot, referenceFile)
        diffFileName.deleteRecursively()
        println("Stored snapshot to: ${referenceFile.absolutePath}")
    } else {
        val reference = fileStoring.load(referenceFile)
        val diff = snapshotting.diffing(reference, snapshot)
        if (diff == null) {
            diffFileName.deleteRecursively()
        } else {
            diffDirectory.mkdirs()

            fileStoring.store(diff, diffFileName)

            if (record == RecordMode.Test) {
                throw AssertionError(
                    "Snapshot is different from the reference!\nDiff stored to: ${diffFileName.absolutePath}"
                )
            } else if (record == RecordMode.Repair) {
                fileStoring.store(snapshot, referenceFile)
            }
        }
    }
}

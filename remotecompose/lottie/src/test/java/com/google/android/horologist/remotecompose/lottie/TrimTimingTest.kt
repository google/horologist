/*
 * Copyright 2026 The Android Open Source Project
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

package com.google.android.horologist.remotecompose.lottie

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathIterator
import android.graphics.PathMeasure
import androidx.compose.remote.creation.compose.state.RemoteFloat
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.format.layer.ShapeLayer
import com.google.android.horologist.remotecompose.lottie.renderer.RemoteLottiePath
import com.google.android.horologist.remotecompose.lottie.renderer.gatherShapesForTest
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import kotlin.math.abs
import kotlin.math.floor
import org.junit.Test

/** Native path measurement at subframes, independently of production trim/geometry helpers. */
class TrimTimingTest : MotionPixelHarness() {
  private val outline
    get() =
      """{"ty":"st","c":${fixed("[1,0,0,1]")},"o":${fixed("100")},"w":${fixed("2")},"lc":1,"lj":1,"ml":4}"""

  private fun fast(a: String, b: String) = animated(a, b).replace("\"t\":10", "\"t\":1")

  private fun trim(end: String, offset: String = fixed("0"), start: String = fixed("0")) =
    """{"ty":"tm","s":$start,"e":$end,"o":$offset,"m":1}"""

  private fun geometry(f: Float) =
    """{"c":false,"v":[[8,44],[${32-8*f},${18+26*f}],[56,44]],"i":[[0,0],[-10,0],[-4,-18]],"o":[[${16-12*f},${-38+30*f}],[12,2],[0,0]]}"""

  private fun shape(moving: Boolean = false) =
    """{"ty":"sh","ks":${if(moving) fast("[${geometry(0f)}]", "[${geometry(1f)}]") else fixed(geometry(0f))}}"""

  private fun curve(f: Float = 0f) =
    Path().apply {
      moveTo(8f, 44f)
      cubicTo(24 - 12 * f, 6 + 30 * f, 22 - 8 * f, 18 + 26 * f, 32 - 8 * f, 18 + 26 * f)
      cubicTo(44 - 8 * f, 20 + 26 * f, 52f, 26f, 56f, 44f)
    }

  private fun compoundLines(moving: Boolean = false): String {
    fun geometry(x: Int, y: Int) =
      """{"c":false,"v":[[8,$y],[$x,$y]],"i":[[0,0],[0,0]],"o":[[0,0],[0,0]]}"""
    val second =
      if (moving) fast("[${geometry(40,44)}]", "[${geometry(56,44)}]") else fixed(geometry(40, 44))
    return """{"ty":"sh","ks":${fixed(geometry(24,20))}},{"ty":"sh","ks":$second},{"ty":"mm","mm":1}"""
  }

  // Analytic line lengths, independent of production cubic measurement and trim helpers.
  private fun cutCompoundLines(growth: Float, end: Float, offset: Float = 0f): Path {
    val lengths = listOf(16f, 32f + 16f * growth)
    val total = lengths.sum()
    val from = offset - floor(offset)
    val windows = mutableListOf(from * total to minOf(from + end, 1f) * total)
    if (from + end > 1f) windows.add(0f to (from + end - 1f) * total)
    val pieces = mutableListOf<Triple<Float, Float, Float>>()
    var prefix = 0f
    for ((index, length) in lengths.withIndex()) {
      for ((start, stop) in windows) {
        val a = (start - prefix).coerceIn(0f, length)
        val b = (stop - prefix).coerceIn(0f, length)
        if (b > a) {
          pieces.add(Triple(8f + a, 8f + b, if (index == 0) 20f else 44f))
        }
      }
      prefix += length
    }
    return Path().apply {
      for ((a, b, y) in pieces) {
        moveTo(a, y)
        lineTo(b, y)
      }
    }
  }

  @Test
  fun compoundConstantTrimUsesCombinedContourLength() {
    verify("compound-static", "${compoundLines()},${trim(fixed("50"))}") {
      cutCompoundLines(0f, .5f)
    }
  }

  private fun separateLines(moving: Boolean = false) =
    compoundLines(moving).removeSuffix(",{\"ty\":\"mm\",\"mm\":1}")

  private fun individualTrim(end: String, offset: String = fixed("0")) =
    trim(end, offset).replace("\"m\":1", "\"m\":2")

  @Test
  fun individualModeSharesLengthsAcrossAuthoredPaths() {
    verify("individual-static", "${separateLines()},${individualTrim(fixed("50"))}") {
      cutCompoundLines(0f, .5f)
    }
  }

  @Test
  fun individualModeMeasuresAnimatedUnequalPaths() {
    verify("individual-live", "${separateLines(true)},${individualTrim(fast("[20]", "[100]"))}") {
      cutCompoundLines(it, .2f + .8f * it)
    }
  }

  @Test
  fun individualModeWrapsAcrossPaths() {
    verify(
      "individual-wrapped",
      "${separateLines(true)},${individualTrim(fixed("60"), fast("[-90]", "[180]"))}",
    ) {
      cutCompoundLines(it, .6f, -.25f + .75f * it)
    }
  }

  @Test
  fun individualModeDoesNotRenormalizeEachPaintView() {
    val painted = separateLines(true).replace("},{\"ty\":\"sh\"", "},$outline,{\"ty\":\"sh\"")
    verify(
      "individual-paints",
      "$painted,$outline,${individualTrim(fixed("50"))}",
      appendOutline = false,
    ) {
      cutCompoundLines(it, .5f)
    }
  }

  @Test
  fun individualModeSharesLengthsAcrossOwnedGroupStrokes() {
    val grouped =
      separateLines(true)
        .replace("},{\"ty\":\"sh\"", "},$outline]},{\"ty\":\"gr\",\"it\":[{\"ty\":\"sh\"")
    verify(
      "individual-groups",
      "{\"ty\":\"gr\",\"it\":[$grouped,$outline]},${individualTrim(fixed("50"))}",
      appendOutline = false,
    ) {
      cutCompoundLines(it, .5f)
    }
  }

  @Test
  fun simultaneousModeStillTrimsEachAuthoredPathSeparately() {
    verify("individual-mode-control", "${separateLines()},${trim(fixed("50"))}") {
      Path().apply {
        moveTo(8f, 20f)
        lineTo(16f, 20f)
        moveTo(8f, 44f)
        lineTo(24f, 44f)
      }
    }
  }

  @Test
  fun individualModeCountsCoincidentAuthoredPathsSeparately() {
    val same = separateLines().replace("[8,44],[40,44]", "[8,20],[24,20]")
    verify("individual-coincident", "$same,${individualTrim(fixed("50"))}") {
      Path().apply {
        moveTo(8f, 20f)
        lineTo(24f, 20f)
      }
    }
  }

  @Test
  fun individualModeInsideGroupFeedsInheritedStroke() {
    verify(
      "individual-child",
      "{\"ty\":\"gr\",\"it\":[${separateLines(true)},${individualTrim(fixed("50"))}]}",
    ) {
      cutCompoundLines(it, .5f)
    }
  }

  @Test
  fun individualModeRetainsEachViewAcrossEarlierModifiers() {
    // First cut is independently applied to both paths. The second combines those cut lengths.
    val shapes =
      "${separateLines(true)},${trim(fixed("80"))},$outline,${individualTrim(fixed("50"))}"
    verify("individual-earlier-trim", shapes, appendOutline = false) {
      val secondLength = (32f + 16f * it) * .8f
      val firstLength = 16f * .8f
      val selected = (firstLength + secondLength) / 2f
      Path().apply {
        moveTo(8f, 20f)
        lineTo(8f + minOf(selected, firstLength), 20f)
        if (selected > firstLength) {
          moveTo(8f, 44f)
          lineTo(8f + selected - firstLength, 44f)
        }
      }
    }
  }

  @Test
  fun individualModeRetainsMergedOperandIdentity() {
    verify(
      "individual-merged",
      "${compoundLines(true)},$outline,${individualTrim(fixed("50"))}",
      appendOutline = false,
    ) {
      cutCompoundLines(it, .5f)
    }
  }

  @Test
  fun individualModeDistinguishesPaintedRepeaterCopies() {
    val line =
      """{"ty":"sh","ks":${fixed("""{"c":false,"v":[[8,20],[24,20]],"i":[[0,0],[0,0]],"o":[[0,0],[0,0]]}""")}}"""
    val repeater =
      """{"ty":"rp","m":2,"c":${fixed("2")},"o":${fixed("0")},"tr":{"p":${fixed("[0,16]")},"s":${fixed("[100,100]")},"r":${fixed("0")},"a":${fixed("[0,0]")},"so":${fixed("100")},"eo":${fixed("100")}}}"""
    verify(
      "individual-repeater",
      "$line,$outline,$repeater,${individualTrim(fixed("50"))}",
      appendOutline = false,
    ) {
      Path().apply {
        moveTo(8f, 36f)
        lineTo(24f, 36f)
      }
    }
  }

  @Test
  fun individualModeMeasuresLiveCurvesNotEndpointChords() {
    fun geometry(x: Int, y: Int, height: Int) =
      """{"c":false,"v":[[8,$y],[$x,$y]],"i":[[0,0],[0,${-height}]],"o":[[0,${-height}],[0,0]]}"""
    val first = """{"ty":"sh","ks":${fixed(geometry(24,20,8))}}"""
    val second = """{"ty":"sh","ks":${fast("[${geometry(40,44,8)}]", "[${geometry(40,44,16)}]")}}"""
    verify("individual-curves", "$first,$second,${individualTrim(fast("[20]", "[100]"))}") {
      val source =
        Path().apply {
          moveTo(8f, 20f)
          cubicTo(8f, 12f, 24f, 12f, 24f, 20f)
          moveTo(8f, 44f)
          cubicTo(8f, 36f - 8f * it, 40f, 36f - 8f * it, 40f, 44f)
        }
      preciseCut(source, .2f + .8f * it)
    }
  }

  @Test
  fun compoundLiveTrimUsesChangingContourLengths() {
    verify("compound-live", "${compoundLines(true)},${trim(fast("[20]", "[100]"))}") {
      cutCompoundLines(it, .2f + .8f * it)
    }
  }

  @Test
  fun compoundWrappedTrimKeepsContourBoundaries() {
    verify(
      "compound-wrapped",
      "${compoundLines(true)},${trim(fixed("60"), offset = fast("[-90]", "[180]"))}",
    ) {
      cutCompoundLines(it, .6f, -.25f + .75f * it)
    }
  }

  @Test
  fun compoundTrimThenPuckerThenTrimMeasuresOnlyVisiblePieces() {
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify(
      "compound-retrim",
      "${compoundLines(true)},${trim(fast("[20]", "[100]"))},$pucker,${trim(fixed("50"))}",
    ) {
      val lengths = listOf(16f, 32f + 16f * it)
      var remaining = lengths.sum() * (.2f + .8f * it)
      val deformed = Path()
      for ((index, length) in lengths.withIndex()) {
        val y = if (index == 0) 20f else 44f
        val source =
          Path().apply {
            moveTo(8f, y)
            cubicTo(8f, y, 8f + length, y, 8f + length, y)
          }
        val fraction = (remaining / length).coerceIn(0f, 1f)
        if (fraction > 0f) deformed.addPath(puckerNativeCut(source, fraction))
        remaining -= length
      }
      preciseCut(deformed, .5f)
    }
  }

  @Test
  fun compoundTrimPreservesEachFullyCoveredClosedContour() {
    fun square(x: Int, size: Int) =
      """{"ty":"sh","ks":${fixed("""{"c":true,"v":[[$x,20],[${x+size},20],[${x+size},${20+size}],[$x,${20+size}]],"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}""")}}"""
    verify(
      "compound-closed",
      "${square(8,8)},${square(32,16)},{\"ty\":\"mm\",\"mm\":1},${trim(fast("[20]", "[100]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) { frame ->
      var remaining = 96f * (.2f + .8f * frame)
      Path().apply {
        for ((x, size) in listOf(8f to 8f, 32f to 16f)) {
          val points = listOf(x to 20f, x + size to 20f, x + size to 20f + size, x to 20f + size)
          val source =
            Path().apply {
              moveTo(x, 20f)
              for (i in points.indices) {
                val a = points[i]
                val b = points[(i + 1) % points.size]
                cubicTo(a.first, a.second, b.first, b.second, b.first, b.second)
              }
              close()
            }
          val fraction = (remaining / (4f * size)).coerceIn(0f, 1f)
          if (fraction > 0f) addPath(puckerNativeCut(source, fraction))
          remaining -= 4f * size
        }
      }
    }
  }

  @Test
  fun compoundEmptyContoursDoNotLeaveRoundCapsOrDuplicateAlpha() {
    verify(
      "compound-empty-alpha",
      "${compoundLines(true)},${trim(fast("[0]", "[100]"))},${outline.replace("\"lc\":1", "\"lc\":2").replace("\"o\":${fixed("100")}", "\"o\":${fixed("50")}")}",
      appendOutline = false,
      expectedStrokeCap = Paint.Cap.ROUND,
      expectedAlpha = 128,
    ) {
      cutCompoundLines(it, it)
    }
  }

  private fun cut(path: Path, end: Float, offset: Float = 0f, start: Float = 0f): Path {
    val measure = PathMeasure(path, false)
    val span = abs(end - start).coerceAtMost(1f)
    if (span >= 1) return Path(path)
    val from = minOf(start, end) + offset
    val normalized = from - floor(from)
    return Path().apply {
      measure.getSegment(
        normalized * measure.length,
        minOf(1f, normalized + span) * measure.length,
        this,
        true,
      )
      if (normalized + span > 1)
        measure.getSegment(0f, (normalized + span - 1) * measure.length, this, true)
    }
  }

  private val frames =
    listOf(
      0f,
      0.125f,
      0.25f,
      0.375f,
      0.49f,
      0.5f,
      0.51f,
      0.59f,
      0.61f,
      0.75f,
      0.875f,
      0.99f,
      1f,
      0.5f,
      0.125f,
      0.125f,
    )

  private fun verify(
    name: String,
    shapes: String,
    appendOutline: Boolean = true,
    expectedStrokeCap: Paint.Cap = Paint.Cap.BUTT,
    expectedAlpha: Int = 255,
    expectedStyle: Paint.Style = Paint.Style.STROKE,
    nativeFillEdgeVariance: Boolean = false,
    expectedPath: (Float) -> Path,
  ) {
    val progress = show(animation(if (appendOutline) "$shapes,$outline" else shapes))
    val folder = File("build/outputs/lottie-motion/trimtiming-$name").apply { mkdirs() }
    val errors = mutableListOf<String>()
    for ((index, frame) in frames.withIndex()) {
      advance(progress, frame)
      val actual = capture("motion")
      val expected = Bitmap.createBitmap(actual.width, actual.height, Bitmap.Config.ARGB_8888)
      try {
        Canvas(expected).apply {
          drawColor(Color.BLACK)
          scale(actual.width / 64f, actual.height / 64f)
          drawPath(
            expectedPath(frame),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
              color = Color.argb(expectedAlpha, 255, 0, 0)
              style = expectedStyle
              strokeWidth = 2f
              strokeCap = expectedStrokeCap
              strokeJoin = Paint.Join.MITER
            },
          )
        }
        var sum = 0.0
        var peak = 0.0
        var edgeSum = 0.0
        var edgeCount = 0
        var offEdgeDifferences = 0
        for (y in 0 until actual.height) for (x in 0 until actual.width) {
          val error =
            abs(Color.red(actual.getPixel(x, y)) - Color.red(expected.getPixel(x, y))) / 255.0
          sum += error
          peak = maxOf(peak, error)
          if (nativeFillEdgeVariance) {
            var lo = 255
            var hi = 0
            for (dy in -1..1) for (dx in -1..1) {
              val red =
                Color.red(
                  expected.getPixel(
                    (x + dx).coerceIn(0, actual.width - 1),
                    (y + dy).coerceIn(0, actual.height - 1),
                  )
                )
              lo = minOf(lo, red)
              hi = maxOf(hi, red)
            }
            if (lo != hi) {
              edgeSum += error
              edgeCount++
            } else if (error != 0.0) offEdgeDifferences++
          }
        }
        val mean = sum / (actual.width * actual.height)
        if (nativeFillEdgeVariance) {
          // Zero-area contours can switch native convex-fill AA paths. The separate
          // padded-native control retains the strict whole-image gate below.
          val edgeMean = edgeSum / maxOf(edgeCount, 1)
          if (edgeMean > .1 || offEdgeDifferences != 0 || peak > .25)
            errors.add("$frame: edgeMean=$edgeMean offEdge=$offEdgeDifferences peak=$peak")
        } else if (mean > 0.0001 || peak > 0.25) errors.add("$frame: mean=$mean peak=$peak")
        for ((kind, bitmap) in listOf("rc" to actual, "reference" to expected)) {
          File(folder, "frame$index-$kind.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
          }
        }
      } finally {
        actual.recycle()
        expected.recycle()
      }
    }
    assertWithMessage(errors.joinToString("\n")).that(errors).isEmpty()
  }

  @Test
  fun movingCurveWithFixedTrimUsesCurrentLength() =
    verify("morph", "${shape(true)},${trim(fixed("60"))}") { cut(curve(it), 0.6f) }

  @Test
  fun fastTrimDoesNotInterpolatePrecutCurves() =
    verify("fast", "${shape()},${trim(fast("[20]","[85]"))}") { cut(curve(), 0.2f + 0.65f * it) }

  @Test
  fun movingCurveAndTrimStayLiveTogether() =
    verify("both", "${shape(true)},${trim(fast("[20]","[85]"))}") {
      cut(curve(it), 0.2f + 0.65f * it)
    }

  @Test
  fun wrappedOffsetStaysLiveBetweenFrames() =
    verify("wrapped", "${shape()},${trim(fixed("55"),fast("[0]","[270]"))}") {
      cut(curve(), 0.55f, 0.75f * it)
    }

  @Test
  fun nonintegerHoldIsNotSmoothed() {
    val held = """{"a":1,"k":[{"t":0,"s":[20],"h":1},{"t":0.6,"s":[85]}]}"""
    verify("hold", "${shape()},${trim(held)}") { cut(curve(), if (it < 0.6f) 0.2f else 0.85f) }
  }

  @Test
  fun consecutiveLiveTrimsUseVisibleLength() =
    verify("compound", "${shape(true)},${trim(fast("[60]","[90]"))},${trim(fixed("50"))}") {
      cut(curve(it), (0.6f + 0.3f * it) * 0.5f)
    }

  @Test
  fun staticRectangleUsesLiveTrimValues() {
    verify(
      "rectangle",
      "${rectangle(fixed("[32,32]"),fixed("[40,32]"))},${trim(fast("[20]","[85]"))}",
    ) {
      val path =
        Path().apply {
          moveTo(52f, 16f)
          lineTo(52f, 48f)
          lineTo(12f, 48f)
          lineTo(12f, 16f)
          close()
        }
      cut(path, 0.2f + 0.65f * it)
    }
  }

  @Test
  fun easedTrimUsesTheEvaluatedFraction() {
    val end =
      animated(
          "[20]",
          "[85]",
          """"o":{"x":0.3333333333,"y":0},"i":{"x":0.6666666667,"y":0.3333333333}""",
        )
        .replace("\"t\":10", "\"t\":1")
    verify("eased", "${shape()},${trim(end)}") { cut(curve(), 0.2f + 0.65f * it * it) }
  }

  @Test
  fun fullTrimRetainsClosedStrokeJoins() {
    verify("full", "${rectangle(fixed("[32,32]"),fixed("[40,32]"))},${trim(fixed("100"))}") {
      Path().apply {
        moveTo(52f, 16f)
        lineTo(52f, 48f)
        lineTo(12f, 48f)
        lineTo(12f, 16f)
        close()
      }
    }
  }

  @Test
  fun trimAfterDeformationMeasuresTheDeformedCurve() {
    val pucker = """{"ty":"pb","a":${fast("[20]","[40]")}}"""
    verify("pucker", "${shape()},$pucker,${trim(fast("[20]","[85]"))}") { frame ->
      cut(puckeredCurve(0.2f + 0.2f * frame), 0.2f + 0.65f * frame)
    }
  }

  @Test
  fun constantTrimAfterLiveDeformationMeasuresTheDeformedCurve() {
    val pucker = """{"ty":"pb","a":${fast("[20]","[40]")}}"""
    verify("pucker-fixed-trim", "${shape()},$pucker,${trim(fixed("60"))}") {
      cut(puckeredCurve(0.2f + 0.2f * it), 0.6f)
    }
  }

  @Test
  fun constantTrimAfterConstantDeformationMeasuresTheDeformedCurve() {
    val pucker = """{"ty":"pb","a":${fixed("40")}}"""
    verify("pucker-static-trim", "${shape()},$pucker,${trim(fixed("60"))}") {
      cut(puckeredCurve(0.4f), 0.6f)
    }
  }

  @Test
  fun parentTrimRunsAfterChildDeformation() {
    val pucker = """{"ty":"pb","a":${fast("[20]","[40]")}}"""
    verify("child-pucker-trim", """{"ty":"gr","it":[${shape()},$pucker]},${trim(fixed("60"))}""") {
      cut(puckeredCurve(0.2f + 0.2f * it), 0.6f)
    }
  }

  @Test
  fun trimAfterPaintRunsAfterEarlierDeformation() {
    val pucker = """{"ty":"pb","a":${fast("[20]","[40]")}}"""
    verify(
      "paint-pucker-trim",
      """{"ty":"gr","it":[${shape()},$pucker,$outline,${trim(fixed("60"))}]}""",
      appendOutline = false,
    ) {
      cut(puckeredCurve(0.2f + 0.2f * it), 0.6f)
    }
  }

  @Test
  fun parentTrimReachesTheChildsOwnedPaintAfterDeformation() {
    val pucker = """{"ty":"pb","a":${fast("[20]","[40]")}}"""
    verify(
      "owned-pucker-trim",
      """{"ty":"gr","it":[${shape()},$pucker,$outline]},${trim(fixed("60"))}""",
      appendOutline = false,
    ) {
      cut(puckeredCurve(0.2f + 0.2f * it), 0.6f)
    }
  }

  private fun puckeredCurve(amount: Float): Path {
    fun vertex(x: Float, y: Float) =
      floatArrayOf(x + (32 - x) * amount, y + (106f / 3 - y) * amount)
    fun control(x: Float, y: Float) =
      floatArrayOf(x + (x - 32) * amount, y + (y - 106f / 3) * amount)
    val a = vertex(8f, 44f)
    val b = vertex(32f, 18f)
    val c = vertex(56f, 44f)
    val ab = control(24f, 6f)
    val ba = control(22f, 18f)
    val bc = control(44f, 20f)
    val cb = control(52f, 26f)
    return Path().apply {
      moveTo(a[0], a[1])
      cubicTo(ab[0], ab[1], ba[0], ba[1], b[0], b[1])
      cubicTo(bc[0], bc[1], cb[0], cb[1], c[0], c[1])
    }
  }

  private fun cutThenPucker(name: String, trims: String, nested: Boolean = false) {
    val path =
      """{"ty":"sh","ks":${fixed("""{"c":false,"v":[[8,48],[8,16],[56,16]],"i":[[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0]]}""")}}"""
    val pucker = """{"ty":"pb","a":${fast("[20]","[40]")}}"""
    val source = if (nested) """{"ty":"gr","it":[$path,$trims]}""" else "$path,$trims"
    verify(name, "$source,$pucker") { frame ->
      // Forty percent of the 80-unit polyline is exactly its first 32-unit edge.
      // Pucker uses the two cut vertices' centroid, not the original three vertices.
      val d = 16f * (0.2f + 0.2f * frame)
      Path().apply {
        moveTo(8f, 48f - d)
        cubicTo(8f, 48f + d, 8f, 16f - d, 8f, 16f + d)
      }
    }
  }

  @Test
  fun constantCutExposesItsVerticesToALaterLiveModifier() =
    cutThenPucker("cut-pucker", trim(fixed("40")))

  @Test
  fun compoundConstantCutsExposeTheirFinalVertices() =
    cutThenPucker("compound-cut-pucker", "${trim(fixed("80"))},${trim(fixed("50"))}")

  @Test
  fun childCutExposesItsVerticesToAParentModifier() =
    cutThenPucker("child-cut-pucker", trim(fixed("40")), nested = true)

  @Test
  fun constantLocalCutPrecedesNonuniformInheritedTransform() {
    val path =
      """{"ty":"sh","ks":${fixed("""{"c":false,"v":[[8,48],[8,16],[56,16]],"i":[[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0]]}""")}}"""
    val transform =
      """{"ty":"tr","a":${fixed("[0,0]")},"p":${fixed("[0,0]")},"s":${fixed("[50,100]")},"r":${fixed("0")},"o":${fixed("100")}}"""
    verify("local-cut-transform", """{"ty":"gr","it":[$path,${trim(fixed("40"))},$transform]}""") {
      Path().apply {
        moveTo(4f, 48f)
        lineTo(4f, 16f)
      }
    }
  }

  @Test
  fun liveCutExposesItsChangingVerticesToPucker() {
    val path =
      """{"ty":"sh","ks":${fixed("""{"c":false,"v":[[8,48],[8,16],[56,16]],"i":[[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0]]}""")}}"""
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify("live-cut-pucker", "$path,${trim(fast("[20]", "[80]"))},$pucker") { frame ->
      val distance = 80f * (0.2f + 0.6f * frame)
      val points =
        if (distance <= 32f) listOf(8f to 48f, 8f to 48f - distance)
        else listOf(8f to 48f, 8f to 16f, 8f + distance - 32f to 16f)
      // These authored lines are zero-handle cubics. Invert their analytic distance
      // (3t²-2t³), retaining the cut cubic's control rather than reparameterizing it.
      fun parameter(fraction: Float): Float {
        var low = 0.0
        var high = 1.0
        repeat(40) {
          val t = (low + high) / 2
          if (3 * t * t - 2 * t * t * t < fraction) low = t else high = t
        }
        return ((low + high) / 2).toFloat()
      }
      val cx = points.map { it.first }.average().toFloat()
      val cy = points.map { it.second }.average().toFloat()
      fun moved(p: Pair<Float, Float>) =
        p.first + (cx - p.first) * 0.25f to p.second + (cy - p.second) * 0.25f
      fun control(p: Pair<Float, Float>) =
        p.first + (p.first - cx) * 0.25f to p.second + (p.second - cy) * 0.25f
      Path().apply {
        val first = moved(points.first())
        moveTo(first.first, first.second)
        for (j in 0 until points.lastIndex) {
          val a = control(points[j])
          val originalEnd = if (j == 0) 8f to 16f else 56f to 16f
          val fraction = if (j == 0) minOf(distance / 32f, 1f) else (distance - 32f) / 48f
          val t = parameter(fraction)
          val start = points[j]
          val c2 =
            start.first + (originalEnd.first - start.first) * t * t to
              start.second + (originalEnd.second - start.second) * t * t
          val b = control(c2)
          val end = moved(points[j + 1])
          cubicTo(a.first, a.second, b.first, b.second, end.first, end.second)
        }
      }
    }
  }

  // Independent adaptive integration and de Casteljau oracle. PathMeasure's coarse
  // distance inversion is sufficient for native draw comparisons, but not for testing
  // the control points passed into a subsequent nonlinear modifier.
  private fun preciseCut(path: Path, fraction: Float, startFraction: Float = 0f): Path {
    val iterator = path.pathIterator
    val curves = mutableListOf<DoubleArray>()
    val contourStarts = mutableSetOf<Int>()
    while (iterator.hasNext()) {
      val points = FloatArray(8)
      when (val verb = iterator.next(points, 0)) {
        PathIterator.VERB_MOVE -> contourStarts.add(curves.size)
        PathIterator.VERB_CLOSE,
        PathIterator.VERB_DONE -> Unit
        PathIterator.VERB_CUBIC -> curves.add(DoubleArray(8) { points[it].toDouble() })
        else -> error("Expected cubic source, got $verb")
      }
    }
    fun length(c: DoubleArray, end: Double): Double {
      fun speed(t: Double): Double {
        val u = 1 - t
        fun axis(j: Int) =
          3 * (c[j + 2] - c[j]) * u * u +
            6 * (c[j + 4] - c[j + 2]) * u * t +
            3 * (c[j + 6] - c[j + 4]) * t * t
        return kotlin.math.hypot(axis(0), axis(1))
      }
      fun integrate(
        a: Double,
        b: Double,
        fa: Double,
        fm: Double,
        fb: Double,
        whole: Double,
        tolerance: Double,
        depth: Int,
      ): Double {
        val m = (a + b) / 2
        val leftMid = speed((a + m) / 2)
        val rightMid = speed((m + b) / 2)
        val left = (m - a) * (fa + 4 * leftMid + fm) / 6
        val right = (b - m) * (fm + 4 * rightMid + fb) / 6
        val delta = left + right - whole
        if (depth == 0 || kotlin.math.abs(delta) <= 15 * tolerance) return left + right + delta / 15
        return integrate(a, m, fa, leftMid, fm, left, tolerance / 2, depth - 1) +
          integrate(m, b, fm, rightMid, fb, right, tolerance / 2, depth - 1)
      }
      val a = speed(0.0)
      val m = speed(end / 2)
      val b = speed(end)
      return integrate(0.0, end, a, m, b, end * (a + 4 * m + b) / 6, 1e-9, 20)
    }
    val lengths = curves.map { length(it, 1.0) }
    val startDistance = lengths.sum() * minOf(fraction, startFraction).coerceIn(0f, 1f)
    val endDistance = lengths.sum() * maxOf(fraction, startFraction).coerceIn(0f, 1f)
    fun split(c: DoubleArray, t: Double): Pair<DoubleArray, DoubleArray> {
      val first = DoubleArray(6) { c[it] + (c[it + 2] - c[it]) * t }
      val second = DoubleArray(4) { first[it] + (first[it + 2] - first[it]) * t }
      val end = DoubleArray(2) { second[it] + (second[it + 2] - second[it]) * t }
      return doubleArrayOf(c[0], c[1], first[0], first[1], second[0], second[1], end[0], end[1]) to
        doubleArrayOf(end[0], end[1], second[2], second[3], first[4], first[5], c[6], c[7])
    }
    return Path().apply {
      var prefix = 0.0
      var firstPiece = true
      for ((j, c) in curves.withIndex()) {
        if (j in contourStarts) firstPiece = true
        val a = (startDistance - prefix).coerceIn(0.0, lengths[j])
        val b = (endDistance - prefix).coerceIn(0.0, lengths[j])
        prefix += lengths[j]
        if (b <= a) continue
        fun parameter(distance: Double): Double {
          if (distance <= 0) return 0.0
          if (distance >= lengths[j]) return 1.0
          var low = 0.0
          var high = 1.0
          repeat(40) {
            val t = (low + high) / 2
            if (length(c, t) < distance) low = t else high = t
          }
          return (low + high) / 2
        }
        val t0 = parameter(a)
        val t1 = parameter(b)
        val remaining = if (t0 == 0.0) c else split(c, t0).second
        val result = if (t1 == 1.0) remaining else split(remaining, (t1 - t0) / (1 - t0)).first
        if (firstPiece) {
          moveTo(result[0].toFloat(), result[1].toFloat())
          firstPiece = false
        }
        cubicTo(
          result[2].toFloat(),
          result[3].toFloat(),
          result[4].toFloat(),
          result[5].toFloat(),
          result[6].toFloat(),
          result[7].toFloat(),
        )
      }
    }
  }

  @Test
  fun preciseReferenceRetainsAnalyticHalfCurveControls() {
    val source =
      Path().apply {
        moveTo(8f, 32f)
        cubicTo(8f, 0f, 56f, 0f, 56f, 32f)
      }
    val iterator = preciseCut(source, 0.5f).pathIterator
    val points = FloatArray(8)
    assertThat(iterator.next(points, 0)).isEqualTo(PathIterator.VERB_MOVE)
    assertThat(iterator.next(points, 0)).isEqualTo(PathIterator.VERB_CUBIC)
    val expected = floatArrayOf(8f, 32f, 8f, 16f, 20f, 8f, 32f, 8f)
    for (j in points.indices) assertThat(points[j]).isWithin(0.00001f).of(expected[j])
  }

  private fun puckerNativeCut(
    path: Path,
    fraction: Float,
    amount: Float = 0.25f,
    startFraction: Float = 0f,
  ): Path {
    val clipped = preciseCut(path, fraction, startFraction)
    val iterator = clipped.pathIterator
    val cubics = mutableListOf<FloatArray>()
    var first = floatArrayOf()
    while (iterator.hasNext()) {
      val points = FloatArray(8)
      when (val verb = iterator.next(points, 0)) {
        PathIterator.VERB_MOVE -> first = points.copyOf(2)
        PathIterator.VERB_CUBIC -> cubics.add(points)
        PathIterator.VERB_DONE -> Unit
        else -> error("Expected native cubic cut, got verb $verb")
      }
    }
    if (cubics.isEmpty()) return Path()
    check(first.size == 2)
    val closed = fraction - startFraction >= 1f && PathMeasure(path, false).isClosed
    val ends = if (closed) cubics.dropLast(1) else cubics
    val cx = (first[0] + ends.sumOf { it[6].toDouble() }) / (ends.size + 1)
    val cy = (first[1] + ends.sumOf { it[7].toDouble() }) / (ends.size + 1)
    fun vertex(x: Float, y: Float) =
      (x + (cx - x) * amount).toFloat() to (y + (cy - y) * amount).toFloat()
    fun control(x: Float, y: Float) =
      (x + (x - cx) * amount).toFloat() to (y + (y - cy) * amount).toFloat()
    return Path().apply {
      val start = vertex(first[0], first[1])
      moveTo(start.first, start.second)
      for (cubic in cubics) {
        val a = control(cubic[2], cubic[3])
        val b = control(cubic[4], cubic[5])
        val end = vertex(cubic[6], cubic[7])
        cubicTo(a.first, a.second, b.first, b.second, end.first, end.second)
      }
      if (closed) close()
    }
  }

  private fun closedGeometry(f: Float) =
    """{"c":true,"v":[[${8+4*f},${40-6*f}],[${48-6*f},${24+8*f}]],"i":[[-8,18],[-8,-24]],"o":[[0,-32],[8,24]]}"""

  private fun closedShape(moving: Boolean = false) =
    """{"ty":"sh","ks":${if (moving) fast("[${closedGeometry(0f)}]", "[${closedGeometry(1f)}]") else fixed(closedGeometry(0f))}}"""

  private fun closedCurve(f: Float = 0f) =
    Path().apply {
      val x = 8 + 4 * f
      val y = 40 - 6 * f
      val u = 48 - 6 * f
      val v = 24 + 8 * f
      moveTo(x, y)
      cubicTo(x, y - 32, u - 8, v - 24, u, v)
      cubicTo(u + 8, v + 24, x - 8, y + 18, x, y)
      close()
    }

  @Test
  fun closedMovingCutPrecedesPucker() =
    verify(
      "closed-moving-cut",
      "${closedShape(true)},${trim(fixed("65"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      puckerNativeCut(closedCurve(it), .65f)
    }

  @Test
  fun closedLiveCutCanRestoreClosure() =
    verify(
      "closed-live-full",
      "${closedShape()},${trim(fast("[20]","[100]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      puckerNativeCut(closedCurve(), .2f + .8f * it)
    }

  @Test
  fun closedWrappedCutPrecedesPucker() =
    verify(
      "closed-wrapped",
      "${closedShape(true)},${trim(fixed("45"),fast("[0]","[324]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      val from = .9f * it
      puckerNativeCut(closedCurve(it), minOf(from + .45f, 1f), startFraction = from).apply {
        if (from + .45f > 1f) addPath(puckerNativeCut(closedCurve(it), from + .45f - 1f))
      }
    }

  @Test
  fun closedEmptyToFullCutHasCorrectRoundCaps() =
    verify(
      "closed-empty-full",
      "${closedShape(true)},${trim(fast("[0]","[100]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}},${outline.replace("\"lc\":1","\"lc\":2")}",
      appendOutline = false,
      expectedStrokeCap = Paint.Cap.ROUND,
    ) {
      puckerNativeCut(closedCurve(it), it)
    }

  @Test
  fun closedHeldFullExtentPreservesSeam() =
    verify(
      "closed-held-full",
      "${closedShape(true)},${trim("""{"a":1,"k":[{"t":0,"s":[100],"h":1},{"t":0.6,"s":[65]}]}""")},{\"ty\":\"pb\",\"a\":${fixed("25")}},${outline.replace("\"lc\":1","\"lc\":2")}",
      appendOutline = false,
      expectedStrokeCap = Paint.Cap.ROUND,
    ) {
      puckerNativeCut(closedCurve(it), if (it < .6f) 1f else .65f)
    }

  @Test
  fun closedLocalCutPrecedesInheritedScale() {
    val transform =
      """{"ty":"tr","a":${fixed("[0,0]")},"p":${fixed("[0,0]")},"s":${fixed("[50,100]")},"r":${fixed("0")},"o":${fixed("100")}}"""
    verify(
      "closed-transform",
      """{"ty":"gr","it":[${closedShape(true)},${trim(fixed("65"))},$transform]}""",
    ) {
      preciseCut(closedCurve(it), .65f).apply { transform(Matrix().apply { setScale(.5f, 1f) }) }
    }
  }

  @Test
  fun closedLiveCutFillsOnlyActiveContours() =
    verify(
      "closed-fill",
      "${closedShape(true)},${trim(fast("[20]","[100]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}},{\"ty\":\"fl\",\"c\":${fixed("[1,0,0,1]")},\"o\":${fixed("100")},\"r\":1}",
      appendOutline = false,
      expectedStyle = Paint.Style.FILL,
      nativeFillEdgeVariance = true,
    ) {
      puckerNativeCut(closedCurve(it), .2f + .8f * it)
    }

  @Test
  fun closedConstantCutRetainsAccurateControls() =
    verify(
      "closed-constant-cut",
      "${closedShape()},${trim(fixed("65"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      puckerNativeCut(closedCurve(), .65f)
    }

  @Test
  fun closedFillPaddingControl() =
    verify(
      "closed-fill-padding-control",
      "${closedShape(true)},${trim(fast("[20]","[100]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}},{\"ty\":\"fl\",\"c\":${fixed("[1,0,0,1]")},\"o\":${fixed("100")},\"r\":1}",
      appendOutline = false,
      expectedStyle = Paint.Style.FILL,
    ) {
      puckerNativeCut(closedCurve(it), .2f + .8f * it).apply {
        moveTo(0f, 0f)
        cubicTo(0f, 0f, 0f, 0f, 0f, 0f)
        cubicTo(0f, 0f, 0f, 0f, 0f, 0f)
        close()
      }
    }

  @Test
  fun closedPolygonCutRestoresItsClosingJoin() {
    val vertices = listOf(12f to 12f, 52f to 12f, 52f to 48f, 12f to 48f)
    val path =
      Path().apply {
        moveTo(12f, 12f)
        vertices.indices.forEach { i ->
          val a = vertices[i]
          val b = vertices[(i + 1) % vertices.size]
          cubicTo(a.first, a.second, b.first, b.second, b.first, b.second)
        }
        close()
      }
    val shape =
      """{"ty":"sh","ks":${fixed("""{"c":true,"v":[[12,12],[52,12],[52,48],[12,48]],"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}""")}}"""
    verify(
      "closed-polygon-full",
      "$shape,${trim(fast("[20]","[100]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      puckerNativeCut(path, .2f + .8f * it)
    }
  }

  private val closedLoopShape
    get() =
      """{"ty":"sh","ks":${fixed("""{"c":true,"v":[[32,44]],"i":[[-26,-40]],"o":[[26,-40]]}""")}}"""

  private fun closedLoop() =
    Path().apply {
      moveTo(32f, 44f)
      cubicTo(58f, 4f, 6f, 4f, 32f, 44f)
      close()
    }

  @Test
  fun closedSingleVertexLoopCanBeTrimmed() =
    verify(
      "closed-loop-constant",
      "$closedLoopShape,${trim(fixed("65"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      puckerNativeCut(closedLoop(), .65f)
    }

  @Test
  fun closedSingleVertexLoopCanRestoreFullExtent() =
    verify(
      "closed-loop-full",
      "$closedLoopShape,${trim(fast("[20]","[100]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      puckerNativeCut(closedLoop(), .2f + .8f * it)
    }

  @Test
  fun liveCurvedCutExposesItsControlsToPucker() {
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify("curve-cut-pucker", "${shape()},${trim(fast("[20]", "[85]"))},$pucker") {
      puckerNativeCut(curve(), 0.2f + 0.65f * it)
    }
  }

  private fun wrappedPucker(
    frame: Float,
    moving: Boolean = false,
    offset: Float = .9f * frame,
  ): Path {
    val from = offset - floor(offset)
    return puckerNativeCut(
        curve(if (moving) frame else 0f),
        minOf(from + .45f, 1f),
        startFraction = from,
      )
      .apply {
        if (from + .45f > 1f)
          addPath(puckerNativeCut(curve(if (moving) frame else 0f), from + .45f - 1f))
      }
  }

  @Test
  fun wrappedCutPrecedesPucker() =
    verify(
      "wrapped-cut-pucker",
      "${shape()},${trim(fixed("45"),fast("[0]","[324]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      wrappedPucker(it)
    }

  @Test
  fun wrappedMovingCutPrecedesPucker() =
    verify(
      "wrapped-morph-pucker",
      "${shape(true)},${trim(fixed("45"),fast("[0]","[324]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      wrappedPucker(it, true)
    }

  @Test
  fun emptyWrappedIntervalHasNoRoundCap() =
    verify(
      "wrapped-round-caps",
      "${shape()},${trim(fixed("45"),fast("[0]","[324]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}},${outline.replace("\"lc\":1","\"lc\":2")}",
      appendOutline = false,
      expectedStrokeCap = Paint.Cap.ROUND,
    ) {
      wrappedPucker(it)
    }

  @Test
  fun negativeWrappedCutPrecedesPucker() =
    verify(
      "wrapped-negative",
      "${shape(true)},${trim(fixed("45"),fast("[0]","[-324]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      wrappedPucker(it, true, -.9f * it)
    }

  @Test
  fun heldWrappedCutPrecedesPucker() =
    verify(
      "wrapped-held",
      "${shape(true)},${trim(fixed("45"),"""{"a":1,"k":[{"t":0,"s":[0],"h":1},{"t":0.6,"s":[270]}]}""")},{\"ty\":\"pb\",\"a\":${fixed("25")}}",
    ) {
      wrappedPucker(it, true, if (it < .6f) 0f else .75f)
    }

  @Test
  fun translucentWrappedStrokeKeepsPaintOpacity() =
    verify(
      "wrapped-alpha",
      "${shape(true)},${trim(fixed("45"),fast("[0]","[324]"))},{\"ty\":\"pb\",\"a\":${fixed("25")}},${outline.replace("\"o\":${fixed("100")}", "\"o\":${fixed("50")}")}",
      appendOutline = false,
      expectedAlpha = 128,
    ) {
      wrappedPucker(it, true)
    }

  @Test
  fun movingCurveIsCutBeforePucker() {
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify("morph-cut-pucker", "${shape(true)},${trim(fixed("60"))},$pucker") {
      puckerNativeCut(curve(it), 0.6f)
    }
  }

  @Test
  fun movingCurveAndCutStayLiveBeforePucker() {
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify("both-cut-pucker", "${shape(true)},${trim(fast("[20]", "[85]"))},$pucker") {
      puckerNativeCut(curve(it), 0.2f + 0.65f * it)
    }
  }

  @Test
  fun movingStartRemovesLeadingLogicalVertices() {
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify(
      "sliding-cut-pucker",
      "${shape()},${trim(fixed("85"), start=fast("[10]","[75]"))},$pucker",
    ) {
      puckerNativeCut(curve(), 0.85f, startFraction = 0.1f + 0.65f * it)
    }
  }

  @Test
  fun heldCutKeepsItsDiscontinuityBeforePucker() {
    val held = """{"a":1,"k":[{"t":0,"s":[20],"h":1},{"t":0.6,"s":[85]}]}"""
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify("hold-cut-pucker", "${shape()},${trim(held)},$pucker") {
      puckerNativeCut(curve(), if (it < 0.6f) 0.2f else 0.85f)
    }
  }

  @Test
  fun liveCutCanBecomeEmptyAndFullBeforePucker() {
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    verify("empty-full-cut", "${shape()},${trim(fast("[0]","[100]"))},$pucker") {
      puckerNativeCut(curve(), it)
    }
  }

  @Test
  fun emptyLiveCutDoesNotLeaveRoundCaps() {
    val pucker = """{"ty":"pb","a":${fixed("25")}}"""
    val progress =
      show(
        animation(
          "${shape()},${trim(fast("[0]","[100]"))},$pucker,${outline.replace("\"lc\":1","\"lc\":2")}"
        )
      )
    for (frame in listOf(0f, 1f, 0f)) {
      advance(progress, frame)
      val bitmap = capture("motion")
      try {
        var visible = 0
        for (y in 0 until bitmap.height) for (x in 0 until bitmap.width) if (
          Color.red(bitmap.getPixel(x, y)) > 0
        )
          visible++
        if (frame == 0f) assertThat(visible).isEqualTo(0) else assertThat(visible).isGreaterThan(20)
      } finally {
        bitmap.recycle()
      }
    }
  }

  @Test
  fun liveLocalCutPrecedesNonuniformInheritedTransform() {
    val transform =
      """{"ty":"tr","a":${fixed("[0,0]")},"p":${fixed("[0,0]")},"s":${fixed("[50,100]")},"r":${fixed("0")},"o":${fixed("100")}}"""
    verify(
      "live-cut-transform",
      """{"ty":"gr","it":[${shape(true)},${trim(fixed("60"))},$transform]}""",
    ) {
      preciseCut(curve(it), 0.6f).apply { transform(Matrix().apply { setScale(0.5f, 1f) }) }
    }
  }

  @Test
  fun recordingSizeDoesNotDependOnAnimationDuration() {
    val input =
      "${shape(true)},${trim(fast("[20]","[85]"))},$outline".replace("\"t\":1", "\"t\":10000")
    val layer = Animation.decodeFromString(animation(input)).layers.single() as ShapeLayer
    val result =
      gatherShapesForTest(
          layer.shapes,
          LottieSettings(RemoteFloat(androidx.compose.remote.creation.Rc.Time.ANIMATION_TIME)),
        )
        .single()
        .shapes
        .single() as RemoteLottiePath
    assertThat(result.path).hasSize(1)
    assertThat(result.path.single().vertices).hasSize(3)
    assertThat(result.trim).isNotNull()
  }
}

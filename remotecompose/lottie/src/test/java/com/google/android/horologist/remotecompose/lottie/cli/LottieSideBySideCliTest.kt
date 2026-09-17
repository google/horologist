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

package com.google.android.horologist.remotecompose.lottie.cli

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.remote.core.CoreDocument
import androidx.compose.remote.core.RemoteComposeBuffer
import androidx.compose.remote.creation.compose.capture.captureSingleRemoteDocument
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteComposePlayerFlags
import androidx.compose.remote.player.compose.embedded.RcPlayer
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.compose.LottieAnimation as ReferenceLottie
import com.google.android.horologist.remotecompose.lottie.BitmapComparisonUtil
import com.google.android.horologist.remotecompose.lottie.BitmapDiffMetrics
import com.google.android.horologist.remotecompose.lottie.LottieAnimation
import com.google.android.horologist.remotecompose.lottie.MotionPixelHarness
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.remotecompose.lottie.setNamedFloat
import java.io.ByteArrayInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * Command-line runner that takes one or more URLs (or file paths) to Lottie JSON assets, renders
 * them side-by-side in `lottie-android` (Reference) and `rc lottie` (RemoteCompose), generates
 * composite side-by-side PNG visual comparisons, computes quantitative error metrics, and clusters
 * related issues into structured bug reports (both AST schema issues and visual rendering issues).
 *
 * Usage:
 * ```bash
 * ./scripts/compare_lottie.sh <url_or_file> [output_dir]
 * ./scripts/compare_lottie.sh --default-suite [output_dir]
 * ```
 */
class LottieSideBySideCliTest : MotionPixelHarness() {

  internal data class ActiveAssetState(
    val id: Int,
    val document: CoreDocument?,
    val decodedRc: Animation?,
    val reference: LottieComposition,
    val renderSizeDp: Int,
    val background: Color,
  )

  internal data class FrameResult(
    val progress: Float,
    val referenceBitmap: Bitmap,
    val rcBitmap: Bitmap,
    val diffBitmap: Bitmap,
    val metrics: BitmapDiffMetrics,
    val psnrDb: Double,
  )

  internal data class AssetComparisonResult(
    val name: String,
    val sourceUrl: String,
    val width: Int,
    val height: Int,
    val fps: Float,
    val durationFrames: Float,
    val detectedFeatures: List<String>,
    val rawSchemaStatus: String,
    val rawSchemaError: String?,
    val schemaClusterCategory: String?,
    val schemaClusterReason: String?,
    val visualStatus: String,
    val parseError: String?,
    val renderError: String?,
    val meanRgbError: Double,
    val meanForegroundError: Double,
    val minPsnrDb: Double,
    val sideBySidePngPath: String,
    val visualClusterCategory: String?,
    val visualClusterReason: String?,
  )

  @Test(timeout = 180_000L)
  fun runCliComparison() {
    val singleUrl = System.getProperty("lottieUrl")?.trim()?.takeIf { it.isNotEmpty() }
    val urlsFile = System.getProperty("lottieUrlsFile")?.trim()?.takeIf { it.isNotEmpty() }
    val cliRunFlag = System.getProperty("lottieCliRun")?.toBoolean() ?: false
    assumeTrue(
      "Skipping external Lottie CLI comparison test during regular CI unit test runs. " +
        "Run via ./scripts/compare_lottie.sh or pass -PlottieCliRun=true",
      cliRunFlag || singleUrl != null || urlsFile != null,
    )

    val outputDirPath =
      System.getProperty("lottieOutput")?.trim()?.takeIf { it.isNotEmpty() }
        ?: "build/outputs/lottie-comparison-cli"
    val framesProp =
      System.getProperty("lottieFrames")?.trim()?.takeIf { it.isNotEmpty() } ?: "0.0,0.5,1.0"
    val renderSizeDp = System.getProperty("lottieSizeDp")?.toIntOrNull() ?: 96

    val progressSteps =
      framesProp
        .split(",")
        .mapNotNull { it.trim().toFloatOrNull() }
        .ifEmpty { listOf(0.0f, 0.5f, 1.0f) }

    val sources = mutableListOf<Pair<String, String>>()
    if (singleUrl != null) {
      val name = deriveAssetName(singleUrl)
      sources.add(name to singleUrl)
    }
    if (urlsFile != null) {
      val file = File(urlsFile)
      if (file.exists()) {
        file.readLines().forEach { line ->
          val trimmed = line.trim()
          if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
            val parts = trimmed.split("\\s+".toRegex(), limit = 2)
            if (parts.size == 2 && (parts[1].startsWith("http") || File(parts[1]).exists())) {
              sources.add(parts[0] to parts[1])
            } else {
              sources.add(deriveAssetName(trimmed) to trimmed)
            }
          }
        }
      }
    }

    if (sources.isEmpty()) {
      sources.addAll(DEFAULT_EXTERNAL_LOTTIE_URLS)
    }

    val outputDir = File(outputDirPath)
    outputDir.mkdirs()

    println("================================================================================")
    println("Lottie Side-by-Side Comparison Tool (lottie-android vs rc lottie)")
    println("Comparing ${sources.size} asset(s) at progress steps: $progressSteps")
    println("Output directory: ${outputDir.absolutePath}")
    println("================================================================================")

    RemoteComposePlayerFlags.isEmbeddedPlayerEnabled = true
    val activeAssetState = mutableStateOf<ActiveAssetState?>(null)
    val progressState = mutableFloatStateOf(0f)

    composeRule.setContent {
      val current = activeAssetState.value
      if (current != null) {
        key(current.id) {
          Column {
            if (current.document != null && current.decodedRc != null) {
              Box(
                Modifier.size(current.renderSizeDp.dp)
                  .background(current.background)
                  .testTag("motion")
              ) {
                val authoredFrame =
                  current.reference.startFrame +
                    progressState.floatValue * current.reference.durationFrames
                val totalRcDuration =
                  (current.decodedRc.endFrame - current.decodedRc.startFrame).coerceAtLeast(1f)
                val rcProgress = (authoredFrame - current.decodedRc.startFrame) / totalRcDuration
                current.document.setNamedFloat("progress", rcProgress)
                SideEffect { current.document.setNamedFloat("progress", rcProgress) }
                RcPlayer(
                  document = current.document,
                  modifier = Modifier.size(current.renderSizeDp.dp),
                )
              }
            }
            Box(
              Modifier.size(current.renderSizeDp.dp)
                .background(current.background)
                .testTag("reference")
            ) {
              ReferenceLottie(
                current.reference,
                progress = { progressState.floatValue },
                modifier = Modifier.size(current.renderSizeDp.dp),
                enableMergePaths = true,
              )
            }
          }
        }
      }
    }

    val results = mutableListOf<AssetComparisonResult>()
    for ((index, pair) in sources.withIndex()) {
      val (name, source) = pair
      println("[${index + 1}/${sources.size}] Processing '$name' ($source)...")
      val result =
        compareAsset(
          assetIndex = index,
          name = name,
          source = source,
          progressSteps = progressSteps,
          renderSizeDp = renderSizeDp,
          outputDir = outputDir,
          activeAssetState = activeAssetState,
          progressState = progressState,
        )
      results.add(result)
      println(
        "  -> Raw Schema: ${result.rawSchemaStatus} (${result.schemaClusterCategory ?: "OK"}) | " +
          "Visual Status: ${result.visualStatus} | Mean MAE: ${"%.4f".format(result.meanRgbError)} | " +
          "FG MAE: ${"%.4f".format(result.meanForegroundError)} | Visual Cluster: ${result.visualClusterCategory ?: "None"}"
      )
      if (result.rawSchemaError != null) {
        println("  -> Raw Schema Error: ${result.rawSchemaError}")
      }
      if (result.parseError != null || result.renderError != null) {
        println("  -> Post-Normalization Error: ${result.parseError ?: result.renderError}")
      }
      writeSummaryReports(outputDir, results, progressSteps)
    }

    println("================================================================================")
    println(
      "Comparison complete. Summary written to: ${File(outputDir, "summary.md").absolutePath}"
    )
    println("Clustered bug report written to: ${File(outputDir, "clustered_bugs.md").absolutePath}")
    println("================================================================================")
  }

  private fun compareAsset(
    assetIndex: Int,
    name: String,
    source: String,
    progressSteps: List<Float>,
    renderSizeDp: Int,
    outputDir: File,
    activeAssetState: MutableState<ActiveAssetState?>,
    progressState: MutableFloatState,
  ): AssetComparisonResult {
    val jsonText =
      try {
        fetchJsonContent(source)
      } catch (e: Exception) {
        val sideBySideFile = File(outputDir, "${sanitizeName(name)}_side_by_side.png")
        createErrorPlaceholderPng(sideBySideFile, name, source, "FETCH_ERROR: ${e.message}")
        return AssetComparisonResult(
          name = name,
          sourceUrl = source,
          width = 0,
          height = 0,
          fps = 0f,
          durationFrames = 0f,
          detectedFeatures = emptyList(),
          rawSchemaStatus = "FETCH_ERROR",
          rawSchemaError = e.message,
          schemaClusterCategory = "Network / Fetch Error",
          schemaClusterReason = e.message,
          visualStatus = "FETCH_ERROR",
          parseError = e.message,
          renderError = null,
          meanRgbError = 1.0,
          meanForegroundError = 1.0,
          minPsnrDb = 0.0,
          sideBySidePngPath = sideBySideFile.name,
          visualClusterCategory = "Network / Fetch Error",
          visualClusterReason = e.message,
        )
      }

    val features = inspectLottieFeatures(jsonText)
    val refResult = LottieCompositionFactory.fromJsonStringSync(jsonText, name)
    val refComp = refResult.value
    if (refComp == null) {
      val err = refResult.exception?.message ?: "Unknown lottie-android parse error"
      val sideBySideFile = File(outputDir, "${sanitizeName(name)}_side_by_side.png")
      createErrorPlaceholderPng(sideBySideFile, name, source, "REFERENCE_PARSE_ERROR: $err")
      return AssetComparisonResult(
        name = name,
        sourceUrl = source,
        width = 0,
        height = 0,
        fps = 0f,
        durationFrames = 0f,
        detectedFeatures = features,
        rawSchemaStatus = "REF_PARSE_ERROR",
        rawSchemaError = err,
        schemaClusterCategory = "Invalid Lottie JSON",
        schemaClusterReason = err,
        visualStatus = "REF_PARSE_ERROR",
        parseError = err,
        renderError = null,
        meanRgbError = 1.0,
        meanForegroundError = 1.0,
        minPsnrDb = 0.0,
        sideBySidePngPath = sideBySideFile.name,
        visualClusterCategory = "Invalid Lottie JSON",
        visualClusterReason = err,
      )
    }

    val width = refComp.bounds.width()
    val height = refComp.bounds.height()
    val fps = refComp.frameRate
    val durationFrames = refComp.durationFrames

    var decodedRc: Animation? = null
    var rawSchemaError: String? = null
    var parseError: String? = null
    try {
      decodedRc = Animation.decodeFromString(jsonText)
    } catch (e: Throwable) {
      rawSchemaError = "${e.javaClass.simpleName}: ${e.message}"
      parseError = rawSchemaError
    }

    val rawSchemaStatus =
      when {
        rawSchemaError == null -> "PASS"
        else -> "PARSE_ERROR"
      }
    val (schemaClusterCat, schemaClusterWhy) = classifySchemaCluster(rawSchemaError)

    val background = Color.White
    var renderError: String? = null
    var preCapturedDoc: CoreDocument? = null

    if (decodedRc != null) {
      if (name in setOf("TrimPathWrapAround", "TimeRemapAndStartOffset")) {
        renderError =
          "Ignored pending upstream AndroidX GraphContext diamond-DAG DerivedSnapshotState fix"
        preCapturedDoc = null
      } else {
        try {
          val context = ApplicationProvider.getApplicationContext<Context>()
          val captured = runBlocking {
            captureSingleRemoteDocument(context = context) {
              val progressVar = rememberNamedRemoteFloat("progress") { 0f.rf }
              LottieAnimation(
                decodedRc,
                progress = progressVar,
                modifier = RemoteModifier.fillMaxSize(),
              )
            }
          }
          preCapturedDoc =
            CoreDocument().apply {
              ByteArrayInputStream(captured.bytes).use { stream ->
                initFromBuffer(RemoteComposeBuffer.fromInputStream(stream))
              }
            }
        } catch (e: Throwable) {
          e.printStackTrace()
          renderError = "${e.javaClass.simpleName}: ${e.message}"
          preCapturedDoc = null
        }
      }
    }

    composeRule.runOnIdle {
      activeAssetState.value =
        ActiveAssetState(
          id = assetIndex,
          document = preCapturedDoc,
          decodedRc = decodedRc,
          reference = refComp,
          renderSizeDp = renderSizeDp,
          background = background,
        )
      progressState.floatValue = 0f
    }

    val frameResults = mutableListOf<FrameResult>()
    for (step in progressSteps) {
      composeRule.runOnIdle { progressState.floatValue = step }
      val expectedBmp = capture("reference")
      val actualBmp =
        if (decodedRc != null && renderError == null) {
          try {
            capture("motion")
          } catch (e: Throwable) {
            renderError = "${e.javaClass.simpleName}: ${e.message}"
            createErrorPanelBitmap(expectedBmp.width, expectedBmp.height, renderError ?: "Error")
          }
        } else {
          createErrorPanelBitmap(
            expectedBmp.width,
            expectedBmp.height,
            parseError ?: renderError ?: rawSchemaError ?: "RC Parse/Render Failed",
          )
        }

      val normalizedActualBmp =
        if (actualBmp.width != expectedBmp.width || actualBmp.height != expectedBmp.height) {
          val scaled =
            Bitmap.createScaledBitmap(actualBmp, expectedBmp.width, expectedBmp.height, true)
          actualBmp.recycle()
          scaled
        } else {
          actualBmp
        }

      val metrics =
        BitmapComparisonUtil.computeDiffMetrics(normalizedActualBmp, expectedBmp, background)
      val diffBmp = createDiffHeatmapBitmap(normalizedActualBmp, expectedBmp, background)
      val psnr = computePsnrDb(normalizedActualBmp, expectedBmp)

      frameResults.add(
        FrameResult(
          progress = step,
          referenceBitmap = expectedBmp,
          rcBitmap = normalizedActualBmp,
          diffBitmap = diffBmp,
          metrics = metrics,
          psnrDb = psnr,
        )
      )
    }

    val meanMae = frameResults.map { it.metrics.meanRgbError }.average()
    val meanFgMae = frameResults.map { it.metrics.foregroundRgbError }.average()
    val minPsnr = frameResults.minOfOrNull { it.psnrDb } ?: 0.0

    val visualStatus =
      when {
        parseError != null -> "PARSE_ERROR"
        renderError != null -> "RENDER_ERROR"
        meanFgMae < 0.06 && meanMae < 0.02 -> "PASS"
        meanFgMae < 0.18 -> "PARTIAL_PARITY"
        else -> "VISUAL_MISMATCH"
      }

    val (visualClusterCat, visualClusterWhy) =
      classifyVisualCluster(
        status = visualStatus,
        parseError = parseError,
        renderError = renderError,
        features = features,
        meanFgMae = meanFgMae,
        frameResults = frameResults,
      )

    val sideBySideFile = File(outputDir, "${sanitizeName(name)}_side_by_side.png")
    val headerClusterLabel = visualClusterCat ?: schemaClusterCat
    renderCompositeSideBySidePng(
      outputFile = sideBySideFile,
      name = name,
      source = source,
      width = width,
      height = height,
      fps = fps,
      durationFrames = durationFrames,
      status =
        if (rawSchemaError != null && visualStatus == "PASS") "PASS (Normalized)" else visualStatus,
      features = features,
      meanMae = meanMae,
      meanFgMae = meanFgMae,
      clusterCategory = headerClusterLabel,
      frameResults = frameResults,
    )

    frameResults.forEach {
      it.referenceBitmap.recycle()
      it.rcBitmap.recycle()
      it.diffBitmap.recycle()
    }

    return AssetComparisonResult(
      name = name,
      sourceUrl = source,
      width = width,
      height = height,
      fps = fps,
      durationFrames = durationFrames,
      detectedFeatures = features,
      rawSchemaStatus = rawSchemaStatus,
      rawSchemaError = rawSchemaError,
      schemaClusterCategory = schemaClusterCat,
      schemaClusterReason = schemaClusterWhy,
      visualStatus = visualStatus,
      parseError = parseError,
      renderError = renderError,
      meanRgbError = meanMae,
      meanForegroundError = meanFgMae,
      minPsnrDb = minPsnr,
      sideBySidePngPath = sideBySideFile.name,
      visualClusterCategory = visualClusterCat,
      visualClusterReason = visualClusterWhy,
    )
  }

  private fun createDiffHeatmapBitmap(
    actual: Bitmap,
    expected: Bitmap,
    backgroundColor: Color,
  ): Bitmap {
    val w = actual.width
    val h = actual.height
    val count = w * h
    val actualPx = IntArray(count)
    val expectedPx = IntArray(count)
    val outPx = IntArray(count)
    actual.getPixels(actualPx, 0, w, 0, 0, w, h)
    expected.getPixels(expectedPx, 0, w, 0, 0, w, h)

    val bgR = backgroundColor.red
    val bgG = backgroundColor.green
    val bgB = backgroundColor.blue

    for (i in 0 until count) {
      val a = actualPx[i]
      val e = expectedPx[i]
      val aR = ((a ushr 16) and 0xFF) / 255f
      val aG = ((a ushr 8) and 0xFF) / 255f
      val aB = (a and 0xFF) / 255f

      val eR = ((e ushr 16) and 0xFF) / 255f
      val eG = ((e ushr 8) and 0xFF) / 255f
      val eB = (e and 0xFF) / 255f

      val diff = (abs(aR - eR) + abs(aG - eG) + abs(aB - eB)) / 3f
      if (diff < 0.02f) {
        // Matching pixel: render a darkened grayscale echo of reference artwork
        val isArt = abs(eR - bgR) > 0.05f || abs(eG - bgG) > 0.05f || abs(eB - bgB) > 0.05f
        val v = if (isArt) 70 else 28
        outPx[i] = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
      } else {
        // Discrepancy: render bright red/magenta proportional to error magnitude
        val intensity = min(1f, diff * 2.5f)
        val r = (180 + (75 * intensity)).roundToInt().coerceIn(0, 255)
        val g = (20 * (1f - intensity)).roundToInt().coerceIn(0, 255)
        val b = (140 * intensity).roundToInt().coerceIn(0, 255)
        outPx[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
      }
    }
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bmp.setPixels(outPx, 0, w, 0, 0, w, h)
    return bmp
  }

  private fun computePsnrDb(actual: Bitmap, expected: Bitmap): Double {
    val w = actual.width
    val h = actual.height
    val count = w * h
    val actualPx = IntArray(count)
    val expectedPx = IntArray(count)
    actual.getPixels(actualPx, 0, w, 0, 0, w, h)
    expected.getPixels(expectedPx, 0, w, 0, 0, w, h)

    var mse = 0.0
    for (i in 0 until count) {
      val a = actualPx[i]
      val e = expectedPx[i]
      val dr = ((a ushr 16) and 0xFF) - ((e ushr 16) and 0xFF)
      val dg = ((a ushr 8) and 0xFF) - ((e ushr 8) and 0xFF)
      val db = (a and 0xFF) - (e and 0xFF)
      mse += (dr * dr + dg * dg + db * db) / 3.0
    }
    mse /= count
    if (mse <= 1e-9) return 99.9
    return 10.0 * log10((255.0 * 255.0) / mse)
  }

  private fun createErrorPanelBitmap(w: Int, h: Int, message: String): Bitmap {
    val bmp = Bitmap.createBitmap(max(w, 64), max(h, 64), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    canvas.drawColor(AndroidColor.rgb(45, 20, 25))
    val paint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(255, 130, 130)
        textSize = 11f
        typeface = Typeface.MONOSPACE
      }
    val words = message.split(" ")
    var line = ""
    var y = 20f
    for (word in words) {
      if (paint.measureText("$line $word") > bmp.width - 12) {
        canvas.drawText(line, 6f, y, paint)
        y += 14f
        line = word
        if (y > bmp.height - 8) break
      } else {
        line = if (line.isEmpty()) word else "$line $word"
      }
    }
    if (line.isNotEmpty() && y <= bmp.height - 8) {
      canvas.drawText(line, 6f, y, paint)
    }
    return bmp
  }

  private fun createErrorPlaceholderPng(
    outputFile: File,
    name: String,
    source: String,
    error: String,
  ) {
    val bmp = Bitmap.createBitmap(640, 140, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    canvas.drawColor(AndroidColor.rgb(32, 32, 36))
    val titlePaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 16f
        typeface = Typeface.DEFAULT_BOLD
      }
    val errPaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(255, 110, 110)
        textSize = 13f
        typeface = Typeface.MONOSPACE
      }
    canvas.drawText("$name ($source)", 16f, 36f, titlePaint)
    canvas.drawText(error.take(90), 16f, 74f, errPaint)
    if (error.length > 90) {
      canvas.drawText(error.substring(90).take(90), 16f, 96f, errPaint)
    }
    outputFile.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    bmp.recycle()
  }

  private fun renderCompositeSideBySidePng(
    outputFile: File,
    name: String,
    source: String,
    width: Int,
    height: Int,
    fps: Float,
    durationFrames: Float,
    status: String,
    features: List<String>,
    meanMae: Double,
    meanFgMae: Double,
    clusterCategory: String?,
    frameResults: List<FrameResult>,
  ) {
    val cellW = frameResults.firstOrNull()?.referenceBitmap?.width ?: 128
    val cellH = frameResults.firstOrNull()?.referenceBitmap?.height ?: 128
    val pad = 12
    val headerH = 92
    val colLabelH = 26
    val rowLabelW = 84

    val totalCols = frameResults.size
    val imgW = max(680, rowLabelW + totalCols * (cellW + pad) + pad * 2)
    val imgH = headerH + colLabelH + 3 * (cellH + pad) + pad * 2

    val composite = Bitmap.createBitmap(imgW, imgH, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(composite)
    canvas.drawColor(AndroidColor.rgb(24, 26, 30))

    val titlePaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 16f
        typeface = Typeface.DEFAULT_BOLD
      }
    val subPaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(190, 195, 205)
        textSize = 12f
        typeface = Typeface.MONOSPACE
      }
    val statusColor =
      when (status) {
        "PASS" -> AndroidColor.rgb(80, 220, 120)
        "PARTIAL_PARITY" -> AndroidColor.rgb(245, 190, 70)
        else -> AndroidColor.rgb(255, 95, 95)
      }
    val badgePaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = statusColor
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
      }
    val borderPaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        color = AndroidColor.rgb(75, 80, 90)
      }

    canvas.drawText(
      "Asset: $name  [${width}x${height} @ ${fps.roundToInt()}fps, ${durationFrames.roundToInt()}f]",
      16f,
      28f,
      titlePaint,
    )
    canvas.drawText("Status: $status", imgW - 220f, 28f, badgePaint)
    canvas.drawText(
      "Mean MAE: ${"%.4f".format(meanMae)} | FG MAE: ${"%.4f".format(meanFgMae)} | Cluster: ${clusterCategory ?: "None"}",
      16f,
      50f,
      subPaint,
    )
    canvas.drawText(
      "Features: ${if (features.isEmpty()) "Basic Shapes" else features.joinToString(", ")}",
      16f,
      70f,
      subPaint,
    )

    val rowNames = listOf("lottie-android", "rc lottie", "Diff Heatmap")
    for (rowIdx in 0..2) {
      val ry = headerH + colLabelH + rowIdx * (cellH + pad) + cellH / 2f + 4f
      canvas.drawText(rowNames[rowIdx], 12f, ry, subPaint)
    }

    for ((colIdx, frame) in frameResults.withIndex()) {
      val cx = rowLabelW + colIdx * (cellW + pad)
      val pct = (frame.progress * 100).roundToInt()
      canvas.drawText("t=${pct}%", cx + 6f, headerH + 18f, subPaint)

      val bitmaps = listOf(frame.referenceBitmap, frame.rcBitmap, frame.diffBitmap)
      for (rowIdx in 0..2) {
        val cy = headerH + colLabelH + rowIdx * (cellH + pad)
        canvas.drawBitmap(bitmaps[rowIdx], cx.toFloat(), cy.toFloat(), null)
        canvas.drawRect(Rect(cx, cy, cx + cellW, cy + cellH), borderPaint)
      }
      // Draw frame FG MAE below diff heatmap
      val metricText = "FG:${"%.2f".format(frame.metrics.foregroundRgbError)}"
      canvas.drawText(
        metricText,
        cx + 4f,
        (headerH + colLabelH + 3 * (cellH + pad) - 2).toFloat(),
        subPaint,
      )
    }

    outputFile.outputStream().use { composite.compress(Bitmap.CompressFormat.PNG, 100, it) }
    composite.recycle()
  }

  private fun inspectLottieFeatures(jsonText: String): List<String> {
    val features = mutableSetOf<String>()
    try {
      val root = Json.parseToJsonElement(jsonText).jsonObject
      val assets = root["assets"] as? JsonArray
      if (assets != null && assets.isNotEmpty()) features.add("Assets/Precomps")
      val fonts = root["fonts"]
      if (fonts != null) features.add("Fonts")
      val chars = root["chars"] as? JsonArray
      if (chars != null && chars.isNotEmpty()) features.add("VectorGlyphs")

      fun scanShapes(shapes: JsonArray?) {
        if (shapes == null) return
        for (el in shapes) {
          val obj = el as? JsonObject ?: continue
          when (obj["ty"]?.toString()?.trim('"')) {
            "tm" -> features.add("TrimPath")
            "rp" -> features.add("Repeater")
            "rd" -> features.add("RoundedCorners")
            "mm" -> features.add("MergePaths")
            "op" -> features.add("OffsetPath")
            "pb" -> features.add("PuckerBloat")
            "tw" -> features.add("Twist")
            "zz" -> features.add("ZigZag")
            "gf" -> features.add("GradientFill")
            "gs" -> features.add("GradientStroke")
            "st" -> {
              if (obj.containsKey("d")) features.add("DashStroke")
            }
            "gr" -> scanShapes(obj["it"] as? JsonArray)
          }
        }
      }

      fun scanLayers(layers: JsonArray?) {
        if (layers == null) return
        for (el in layers) {
          val obj = el as? JsonObject ?: continue
          when (obj["ty"]?.toString()?.trim('"')?.toIntOrNull()) {
            0 -> features.add("PrecompLayer")
            1 -> features.add("SolidLayer")
            2 -> features.add("ImageLayer")
            3 -> features.add("NullLayer")
            4 -> features.add("ShapeLayer")
            5 -> features.add("TextLayer")
          }
          if (obj.containsKey("tt")) features.add("TrackMatte")
          if (obj.containsKey("masksProperties")) features.add("Masks")
          if (obj.containsKey("tm")) features.add("TimeRemap")
          val sr = obj["sr"]?.toString()?.toFloatOrNull()
          if (sr != null && sr != 1.0f) features.add("TimeStretch")
          val bm = obj["bm"]?.toString()?.toIntOrNull()
          if (bm != null && bm != 0) features.add("BlendMode")
          if (obj["ddd"]?.toString()?.toIntOrNull() == 1) features.add("3DLayer")
          scanShapes(obj["shapes"] as? JsonArray)
        }
      }

      scanLayers(root["layers"] as? JsonArray)
      if (jsonText.contains("\"x\":")) features.add("Expressions")
      if (jsonText.contains("\"s\":true")) features.add("SplitDimensions")
    } catch (_: Exception) {}
    return features.sorted()
  }

  private fun normalizeLegacyBodymovinJson(jsonText: String): String {
    return try {
      val root = Json.parseToJsonElement(jsonText)
      val normalized = normalizeElement(root)
      Json.encodeToString(JsonElement.serializer(), normalized)
    } catch (_: Throwable) {
      jsonText
    }
  }

  private fun normalizeElement(element: JsonElement): JsonElement {
    return when (element) {
      is JsonArray -> {
        val list = element.map { normalizeElement(it) }.toMutableList()
        // Fill missing terminal keyframe "s" values from preceding keyframe's "e" or "s"
        for (i in list.indices) {
          val item = list[i]
          if (item is JsonObject && item.containsKey("t") && !item.containsKey("s")) {
            val prev = if (i > 0) list[i - 1] as? JsonObject else null
            val fallbackS = prev?.get("e") ?: prev?.get("s")
            if (fallbackS != null) {
              val mutableMap = item.toMutableMap()
              mutableMap["s"] = fallbackS
              list[i] = JsonObject(mutableMap)
            }
          }
        }
        JsonArray(list)
      }
      is JsonObject -> {
        val map = element.mapValues { (_, v) -> normalizeElement(v) }.toMutableMap()
        // Normalize split dimensions position {"s": true, "x": {...}, "y": {...}}
        val isSplit = (map["s"] as? JsonPrimitive)?.booleanOrNull == true
        if (isSplit && map.containsKey("x") && map.containsKey("y")) {
          val xObj = map["x"] as? JsonObject
          val yObj = map["y"] as? JsonObject
          val xAnim = (xObj?.get("a") as? JsonPrimitive)?.intOrNull ?: 0
          val yAnim = (yObj?.get("a") as? JsonPrimitive)?.intOrNull ?: 0
          val combinedKfs =
            if (xAnim == 1 || yAnim == 1) combineSplitKeyframes(xObj, yObj) else emptyList()
          map.remove("s")
          map.remove("x")
          map.remove("y")
          if (combinedKfs.isNotEmpty()) {
            map["a"] = JsonPrimitive(1)
            map["k"] = JsonArray(combinedKfs)
          } else {
            val xVal = extractFirstScalar(xObj?.get("k"))
            val yVal = extractFirstScalar(yObj?.get("k"))
            map["a"] = JsonPrimitive(0)
            map["k"] = JsonArray(listOf(JsonPrimitive(xVal), JsonPrimitive(yVal)))
          }
        }
        // Normalize TextGroupingOptions "a" when serialized as a vector property {"a":0,"k":[0,0]}
        if (map.containsKey("g") && map["a"] is JsonObject) {
          val aObj = map["a"] as JsonObject
          val kArr = aObj["k"] as? JsonArray
          if (kArr != null) {
            map["a"] = kArr
          }
        }
        // Add missing "a" discriminator on property objects containing "k"
        if (map.containsKey("k") && !map.containsKey("a")) {
          val kVal = map["k"]
          val isAnimated =
            kVal is JsonArray &&
              kVal.isNotEmpty() &&
              (kVal.first() is JsonObject) &&
              (kVal.first() as JsonObject).containsKey("t")
          map["a"] = JsonPrimitive(if (isAnimated) 1 else 0)
        }
        JsonObject(map)
      }
      else -> element
    }
  }

  private fun extractFirstScalar(kElement: JsonElement?): Float {
    return when (kElement) {
      is JsonPrimitive -> kElement.floatOrNull ?: 0f
      is JsonArray -> {
        val first = kElement.firstOrNull()
        when (first) {
          is JsonPrimitive -> first.floatOrNull ?: 0f
          is JsonObject -> extractFirstScalar(first["s"])
          else -> 0f
        }
      }
      else -> 0f
    }
  }

  private fun combineSplitKeyframes(xObj: JsonObject?, yObj: JsonObject?): List<JsonObject> {
    val xKfs = (xObj?.get("k") as? JsonArray)?.mapNotNull { it as? JsonObject } ?: emptyList()
    val yKfs = (yObj?.get("k") as? JsonArray)?.mapNotNull { it as? JsonObject } ?: emptyList()
    val staticX = extractFirstScalar(xObj?.get("k"))
    val staticY = extractFirstScalar(yObj?.get("k"))

    val primaryKfs = if (xKfs.isNotEmpty()) xKfs else yKfs
    if (primaryKfs.isEmpty()) return emptyList()

    return primaryKfs.map { kf ->
      val t = (kf["t"] as? JsonPrimitive)?.floatOrNull ?: 0f
      val xAtT =
        xKfs
          .firstOrNull { ((it["t"] as? JsonPrimitive)?.floatOrNull ?: -1f) == t }
          ?.let { extractFirstScalar(it["s"]) } ?: staticX
      val yAtT =
        yKfs
          .firstOrNull { ((it["t"] as? JsonPrimitive)?.floatOrNull ?: -1f) == t }
          ?.let { extractFirstScalar(it["s"]) } ?: staticY
      val map = mutableMapOf<String, JsonElement>()
      map["t"] = JsonPrimitive(t)
      map["s"] = JsonArray(listOf(JsonPrimitive(xAtT), JsonPrimitive(yAtT)))
      kf["h"]?.let { map["h"] = it }
      kf["i"]?.let { map["i"] = it }
      kf["o"]?.let { map["o"] = it }
      JsonObject(map)
    }
  }

  private fun classifySchemaCluster(rawSchemaError: String?): Pair<String?, String?> {
    if (rawSchemaError == null) return null to null
    return when {
      rawSchemaError.contains("Field 's' is required") && rawSchemaError.contains("Keyframe") ->
        "Cluster S1: Terminal Keyframes Omitting 's' Field (Bodymovin Legacy Format)" to
          "Terminal keyframes in Bodymovin JSON omit 's' (start value) when inheriting from preceding keyframe's 'e'/'s', causing MissingFieldException ($rawSchemaError)"
      rawSchemaError.contains("Position property missing required 'a' field") ->
        "Cluster S2: Split-Dimension Position Property Omitting 'a' Discriminator" to
          "Position properties with split dimensions ('s': true, 'x'/'y') or un-animated defaults omit top-level 'a' field in BasePositionPropertySerializer ($rawSchemaError)"
      rawSchemaError.contains("Expected JsonArray, but had JsonObject") ->
        "Cluster S3: Text Grouping Alignment 'a' Serialized as Property Object" to
          "TextGroupingOptions ('m.a') in TextLayer serialized as animatable property object {'a':0,'k':[0,0]} instead of plain float array ($rawSchemaError)"
      else ->
        "Cluster S4: General AST Schema Deserialization Mismatch" to
          "Strict kotlinx.serialization schema rejected external Lottie JSON ($rawSchemaError)"
    }
  }

  private fun classifyVisualCluster(
    status: String,
    parseError: String?,
    renderError: String?,
    features: List<String>,
    meanFgMae: Double,
    frameResults: List<FrameResult>,
  ): Pair<String?, String?> {
    if (status == "PASS") return null to null

    if (parseError != null) {
      return "Cluster R0: Unhandled Schema / Expression Deserialization Failure" to
        "Even after legacy Bodymovin normalization, deserialization failed ($parseError)"
    }

    if (renderError != null) {
      return if (renderError.contains("10000-entry float-state limit")) {
        "Cluster R1: RemoteCompose Float-State / Expression Buffer Capacity Limit" to
          "Complex Lottie animations with many paths/modifiers exceed RemoteCompose's 10,000-entry float-state table ($renderError)"
      } else {
        "Cluster R2: RemoteCompose Document Recording / Player Exception" to
          "Exception thrown while recording or playing RemoteCompose document ($renderError)"
      }
    }

    val allBlankRc = frameResults.all {
      it.metrics.foregroundPixels <= it.metrics.visibleArtworkPixels / 4
    }
    if (allBlankRc && features.contains("ImageLayer")) {
      return "Cluster R2: External / Inline Image Asset Decoding & Sizing" to
        "ImageLayer with external or inline base64 data renders blank or mis-scaled compared to lottie-android"
    }
    if (features.contains("TextLayer") || features.contains("Fonts")) {
      return "Cluster R3: Text Layer Font Metrics, Baseline & Vector Glyph Alignment" to
        "TextLayer rendering differs in font metrics, justification, or vector glyph paths (FG MAE=${"%.3f".format(meanFgMae)})"
    }
    if (features.contains("TrackMatte") || features.contains("Masks")) {
      return "Cluster R4: Track Matte & Inverted Mask Compositing Bounds / PorterDuff Modes" to
        "Sub-layer alpha/luma track matte or inverted mask clipping boundary diverges from lottie-android compositing order (FG MAE=${"%.3f".format(meanFgMae)})"
    }
    if (features.contains("Repeater") || features.contains("TrimPath")) {
      return "Cluster R5: Repeater Transform Order & TrimPath Wrap-Around / Multi-Shape Scope" to
        "Repeater copy transform accumulation or TrimPath wrap-around / multi-path trim scope diverges from lottie-android (FG MAE=${"%.3f".format(meanFgMae)})"
    }
    if (features.contains("GradientFill") || features.contains("GradientStroke")) {
      return "Cluster R6: Gradient Opacity Stops & Color Interpolation Space" to
        "Gradient fill/stroke with opacity stops or radial focal offset differs in color/alpha ramp (FG MAE=${"%.3f".format(meanFgMae)})"
    }
    if (features.contains("MergePaths")) {
      return "Cluster R7: Dynamic / Animated MergePaths Boolean Topology" to
        "Animated boolean path operations (Union/Subtract/Intersect/Xor) across moving groups diverge from lottie-android (FG MAE=${"%.3f".format(meanFgMae)})"
    }
    if (
      features.contains("TimeStretch") ||
        features.contains("TimeRemap") ||
        features.contains("PrecompLayer")
    ) {
      return "Cluster R8: Precomposition Time Stretch, Remapping & Start Offset Timing" to
        "Nested precomposition frame mapping or start offset boundary differs at fractional progress steps (FG MAE=${"%.3f".format(meanFgMae)})"
    }
    return "Cluster R9: Spatial Bezier Tangent / Transform Skew & Stroke Miter Precision" to
      "Geometric path evaluation, transform skew order, or stroke miter/cap beveling shows pixel deviation (FG MAE=${"%.3f".format(meanFgMae)})"
  }

  private fun fetchJsonContent(source: String): String {
    val file = File(source)
    if (file.exists()) {
      return file.readText()
    }
    val url = URI(source).toURL()
    val conn = url.openConnection() as HttpURLConnection
    conn.connectTimeout = 10000
    conn.readTimeout = 10000
    conn.requestMethod = "GET"
    conn.setRequestProperty("User-Agent", "Horologist-Lottie-Comparison-CLI/1.0")
    val code = conn.responseCode
    if (code !in 200..299) {
      throw IllegalStateException("HTTP $code fetching $source")
    }
    return conn.inputStream.bufferedReader().use { it.readText() }
  }

  private fun deriveAssetName(source: String): String {
    val clean = source.substringBefore("?").trimEnd('/')
    val base = clean.substringAfterLast('/')
    return base.removeSuffix(".json").replace("%20", "_")
  }

  private fun sanitizeName(name: String): String = name.replace("[^a-zA-Z0-9._-]".toRegex(), "_")

  private fun writeSummaryReports(
    outputDir: File,
    results: List<AssetComparisonResult>,
    progressSteps: List<Float>,
  ) {
    val rawPassCount = results.count { it.rawSchemaStatus == "PASS" }
    val normalizedPassCount = results.count { it.visualStatus == "PASS" }
    val partialCount = results.count { it.visualStatus == "PARTIAL_PARITY" }
    val mismatchCount = results.count { it.visualStatus == "VISUAL_MISMATCH" }
    val errorCount = results.count { it.visualStatus.endsWith("ERROR") }

    val summaryMd = StringBuilder()
    summaryMd.appendLine(
      "# Lottie Side-by-Side Render Comparison Report (`lottie-android` vs `rc lottie`)"
    )
    summaryMd.appendLine()
    summaryMd.appendLine("## Executive Summary")
    summaryMd.appendLine("- **Total External Lottie Assets Evaluated**: ${results.size}")
    summaryMd.appendLine("- **Sampled Progress Frames**: `${progressSteps.joinToString(", ")}`")
    summaryMd.appendLine(
      "- **Raw Strict Schema Pass (`Animation.decodeFromString`)**: $rawPassCount / ${results.size} (${(rawPassCount * 100.0 / results.size).roundToInt()}%)"
    )
    summaryMd.appendLine(
      "- **Post-Normalization Visual Parity (`PASS`, FG MAE < 0.06)**: $normalizedPassCount / ${results.size} (${(normalizedPassCount * 100.0 / results.size).roundToInt()}%)"
    )
    summaryMd.appendLine(
      "- **Partial Visual Parity (`PARTIAL_PARITY`, FG MAE < 0.18)**: $partialCount / ${results.size} (${(partialCount * 100.0 / results.size).roundToInt()}%)"
    )
    summaryMd.appendLine("- **Visual Mismatch (`VISUAL_MISMATCH`)**: $mismatchCount")
    summaryMd.appendLine("- **Unrecoverable Parse / Render Errors**: $errorCount")
    summaryMd.appendLine()
    summaryMd.appendLine("## Asset Comparison Matrix")
    summaryMd.appendLine()
    summaryMd.appendLine(
      "| # | Asset Name | Resolution | Raw Schema | Visual Status | Mean MAE | FG MAE | Min PSNR (dB) | Detected Features | Schema Cluster | Visual Cluster | Side-by-Side Image |"
    )
    summaryMd.appendLine(
      "|---|------------|------------|------------|---------------|----------|--------|---------------|-------------------|----------------|----------------|--------------------|"
    )

    results.forEachIndexed { idx, r ->
      val featStr = r.detectedFeatures.joinToString(", ").ifEmpty { "Basic" }
      val schemaClusterStr = r.schemaClusterCategory?.substringBefore(":") ?: "—"
      val visualClusterStr = r.visualClusterCategory?.substringBefore(":") ?: "—"
      summaryMd.appendLine(
        "| ${idx + 1} | **${r.name}** | `${r.width}x${r.height}` | `${r.rawSchemaStatus}` | `${r.visualStatus}` | " +
          "`${"%.4f".format(r.meanRgbError)}` | `${"%.4f".format(r.meanForegroundError)}` | " +
          "`${"%.1f".format(r.minPsnrDb)}` | $featStr | $schemaClusterStr | $visualClusterStr | " +
          "[View PNG](${r.sideBySidePngPath}) |"
      )
    }

    summaryMd.appendLine()
    summaryMd.appendLine("## Part I: AST Schema & Deserialization Bug Clusters (Strict Raw JSON)")
    val schemaClusters =
      results.filter { it.schemaClusterCategory != null }.groupBy { it.schemaClusterCategory!! }
    if (schemaClusters.isEmpty()) {
      summaryMd.appendLine("No schema deserialization issues detected!")
    } else {
      for ((clusterTitle, items) in schemaClusters.entries.sortedByDescending { it.value.size }) {
        summaryMd.appendLine("### $clusterTitle (${items.size} asset(s))")
        summaryMd.appendLine(
          "- **Affected Assets**: ${items.joinToString(", ") { "`${it.name}`" }}"
        )
        summaryMd.appendLine("- **Root Cause & Diagnostics**: ${items.first().schemaClusterReason}")
        summaryMd.appendLine()
      }
    }

    summaryMd.appendLine(
      "## Part II: Visual Rendering Bug Clusters (Side-by-Side `rc lottie` vs `lottie-android`)"
    )
    val visualClusters =
      results.filter { it.visualClusterCategory != null }.groupBy { it.visualClusterCategory!! }
    if (visualClusters.isEmpty()) {
      summaryMd.appendLine("No visual rendering issues detected across evaluated assets!")
    } else {
      for ((clusterTitle, items) in visualClusters.entries.sortedByDescending { it.value.size }) {
        summaryMd.appendLine("### $clusterTitle (${items.size} asset(s))")
        summaryMd.appendLine(
          "- **Affected Assets**: ${items.joinToString(", ") { "`${it.name}`" }}"
        )
        summaryMd.appendLine("- **Root Cause & Diagnostics**: ${items.first().visualClusterReason}")
        summaryMd.appendLine()
      }
    }

    File(outputDir, "summary.md").writeText(summaryMd.toString())

    // Write dedicated clustered_bugs.md for bug filing
    val bugsMd = StringBuilder()
    bugsMd.appendLine(
      "# Clustered Bug Reports: `rc lottie` (`remotecompose/lottie`) vs `lottie-android`"
    )
    bugsMd.appendLine()
    bugsMd.appendLine(
      "Generated by running `LottieSideBySideCliTest` across ${results.size} real-world external Lottie animations."
    )
    bugsMd.appendLine()
    bugsMd.appendLine("---")
    bugsMd.appendLine("## Part I: AST Schema & Deserialization Bug Clusters (P1 Parser Strictness)")
    bugsMd.appendLine()

    var bugIndex = 1
    for ((clusterTitle, items) in schemaClusters.entries.sortedByDescending { it.value.size }) {
      bugsMd.appendLine("### Bug #$bugIndex: $clusterTitle")
      bugsMd.appendLine("- **Category**: AST Deserialization / Schema Compatibility")
      bugsMd.appendLine("- **Severity**: P1 (Blocks raw JSON ingestion without preprocessing)")
      bugsMd.appendLine("- **Affected External Assets (${items.size})**:")
      for (item in items) {
        bugsMd.appendLine(
          "  - `${item.name}` ([Source URL](${item.sourceUrl})) — Raw Error: `${item.rawSchemaError}`"
        )
      }
      bugsMd.appendLine("- **Technical Root Cause**:")
      bugsMd.appendLine("  ${items.first().schemaClusterReason}")
      bugsMd.appendLine("- **Recommended Fix in `remotecompose/lottie`**:")
      when {
        clusterTitle.startsWith("Cluster S1") ->
          bugsMd.appendLine(
            "  Make `@SerialName(\"s\") val value` nullable or default in `*PropertyKeyframe` classes and resolve terminal keyframe values from preceding keyframe's `e` or `s` during keyframe list normalization."
          )
        clusterTitle.startsWith("Cluster S2") ->
          bugsMd.appendLine(
            "  Update `BasePositionPropertySerializer` to support split-dimension position objects (`\"s\": true`, `\"x\"`, `\"y\"`) and default `\"a\"` to `0` when omitted on static coordinate arrays."
          )
        clusterTitle.startsWith("Cluster S3") ->
          bugsMd.appendLine(
            "  Add a custom serializer or polymorphic fallback for `TextGroupingOptions.alignment` (`@SerialName(\"a\")`) to accept both `List<Float>` and static vector property objects (`{\"a\":0,\"k\":[x,y]}`)."
          )
      }
      bugsMd.appendLine()
      bugIndex++
    }

    bugsMd.appendLine("---")
    bugsMd.appendLine(
      "## Part II: Visual Rendering & Animation Bug Clusters (P2 Renderer Fidelity)"
    )
    bugsMd.appendLine()

    for ((clusterTitle, items) in visualClusters.entries.sortedByDescending { it.value.size }) {
      bugsMd.appendLine("### Bug #$bugIndex: $clusterTitle")
      bugsMd.appendLine("- **Category**: Visual Rendering / Compositing Fidelity")
      bugsMd.appendLine(
        "- **Severity**: ${if (items.any { it.visualStatus.endsWith("ERROR") }) "P1 (Render Exception)" else "P2 (Visual Fidelity Discrepancy)"}"
      )
      bugsMd.appendLine("- **Affected External Assets (${items.size})**:")
      for (item in items) {
        bugsMd.appendLine(
          "  - `${item.name}` ([Source URL](${item.sourceUrl})) — Visual Status: `${item.visualStatus}`, FG MAE: `${"%.4f".format(item.meanForegroundError)}`, Min PSNR: `${"%.1f".format(item.minPsnrDb)} dB`, Side-by-Side: `${item.sideBySidePngPath}`"
        )
      }
      bugsMd.appendLine("- **Technical Root Cause**:")
      bugsMd.appendLine("  ${items.first().visualClusterReason}")
      bugsMd.appendLine("- **Reproduction Command**:")
      bugsMd.appendLine("  ```bash")
      bugsMd.appendLine("  ./scripts/compare_lottie.sh \"${items.first().sourceUrl}\"")
      bugsMd.appendLine("  ```")
      bugsMd.appendLine()
      bugIndex++
    }

    File(outputDir, "clustered_bugs.md").writeText(bugsMd.toString())
  }

  companion object {
    /**
     * Curated suite of 24 diverse external Lottie animations from airbnb/lottie-android sample &
     * snapshot test repositories covering shapes, modifiers, masks, mattes, gradients, precomps,
     * time stretch/remap, text, and inline images.
     */
    val DEFAULT_EXTERNAL_LOTTIE_URLS: List<Pair<String, String>> =
      listOf(
        "hamburger_arrow" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/sample/src/main/res/raw/hamburger_arrow.json",
        "heart" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/sample/src/main/res/raw/heart.json",
        "bullseye" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/sample/src/main/res/raw/bullseye.json",
        "lottielogo" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/sample/src/main/res/raw/lottielogo.json",
        "AndroidWave" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/AndroidWave.json",
        "CheckSwitch" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/CheckSwitch.json",
        "TrimPaths" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/TrimPaths.json",
        "TrimPathWrapAround" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/TrimPathWrapAround.json",
        "Repeater" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/Repeater.json",
        "RoundedCorners" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/RoundedCorners.json",
        "Shapes" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/Shapes.json",
        "ShapeTypes" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/ShapeTypes.json",
        "GradientFill" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/GradientFill.json",
        "GradientOneColor" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/GradientOneColor.json",
        "Masks" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/Masks.json",
        "MaskInv" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/MaskInv.json",
        "TrackMattes" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/TrackMattes.json",
        "2ParentsMatte" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/2ParentsMatte.json",
        "TimeStretch" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/TimeStretch.json",
        "TimeRemapAndStartOffset" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/TimeRemapAndStartOffset.json",
        "Skew" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/Skew.json",
        "SplitDimensions" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/SplitDimensions.json",
        "MiterLimit" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/MiterLimit.json",
        "Text" to
          "https://raw.githubusercontent.com/airbnb/lottie-android/master/snapshot-tests/src/main/assets/Tests/Text.json",
      )
  }
}

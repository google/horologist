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

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieCompositionFactory
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.horologist.remotecompose.lottie.format.Animation
import com.google.android.horologist.screenshots.rng.WearScreenshotTest
import com.google.common.truth.Truth.assertThat

public class LottieDiffTestScope
internal constructor(
  private val clock: SettableRemoteClock,
  private val captureCallback: (suffix: String, progress: Float) -> Unit,
) {
  public fun capture(
    suffix: String = "",
    progress: Float = 0f,
  ) {
    captureCallback(suffix, progress)
  }

  public fun captureFrame(
    frame: Float,
    totalFrames: Float = 60f,
    fps: Float = 60f,
    suffix: String = "_frame${frame.toInt()}",
  ) {
    clock.setFrame(frame, fps)
    val progress = (frame / totalFrames).coerceIn(0f, 1f)
    capture(suffix = suffix, progress = progress)
  }

  public fun captureProgress(
    progress: Float,
    suffix: String = "_progress${(progress * 100).toInt()}",
  ) {
    capture(suffix = suffix, progress = progress)
  }
}

public abstract class LottieDiffScreenshotTest : WearScreenshotTest() {

  public fun runLottieDiffTest(
    @RawRes animationResId: Int,
    progress: Float = 0f,
    suffix: String = "",
    expectedFailure: Boolean = false,
    block: (LottieDiffTestScope.() -> Unit)? = null,
  ) {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val animationResult = runCatching { Animation.load(animationResId, context) }

    if (expectedFailure) {
      assertThat(animationResult.isFailure).isTrue()
    } else {
      assertThat(animationResult.isSuccess).isTrue()
    }

    val progressState = mutableStateOf(progress)
    val clockNanoTimeState = mutableStateOf(0L)
    val clock = SettableRemoteClock()

    composeRule.setContent {
      val currentProgress by progressState

      Row(
        modifier = Modifier.background(Color(0xFF1E1E1E)).padding(8.dp).testTag("LottieDiff"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        LottieAndroidPreview(
          animationResId = animationResId,
          progress = currentProgress,
        )

        LottieRcPreview(
          animationResId = animationResId,
          progress = currentProgress,
          animationResult = animationResult,
        )
      }
    }

    if (block != null) {
      val scope =
        LottieDiffTestScope(clock) { stepSuffix, stepProgress ->
          progressState.value = stepProgress
          clockNanoTimeState.value = clock.currentNanoTime
          composeRule.waitForIdle()
          composeRule.onNodeWithTag("LottieDiff").captureRoboImage(screenshotFilePath(stepSuffix))
        }
      scope.block()
    } else {
      composeRule.onNodeWithTag("LottieDiff").captureRoboImage(screenshotFilePath(suffix))
    }
  }

  protected open fun screenshotFilePath(suffix: String): String =
    "src/test/screenshots/${this.javaClass.simpleName}_${testInfo.methodName}$suffix.png"

  public companion object {
    public fun sanitizeErrorMessage(throwable: Throwable?): String {
      if (throwable == null) return "Unknown error"
      val simpleName = throwable::class.java.simpleName
      val msg =
        throwable.message
          .orEmpty()
          .replace(Regex("@[0-9a-fA-F]+"), "@...")
          .replace(Regex("0x[0-9a-fA-F]+"), "0x...")
          .replace("com.google.android.horologist.remotecompose.lottie.format.", "")
          .replace("kotlinx.serialization.json.", "")
          .replace("kotlinx.serialization.", "")
          .lines()
          .firstOrNull { it.isNotBlank() }
          ?.trim()
          .orEmpty()

      return if (msg.isNotBlank()) {
        "$simpleName:\n$msg"
      } else {
        simpleName
      }
    }
  }
}

/**
 * Compose UI Composable for rendering a Lottie animation using the reference lottie-android
 * library.
 */
@Composable
public fun LottieAndroidPreview(
  @RawRes animationResId: Int,
  modifier: Modifier = Modifier,
  boxSize: Dp = 84.dp,
  progress: Float = 0f,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = modifier,
  ) {
    BasicText(
      text = "lottie-android",
      style = TextStyle(color = Color.LightGray, fontSize = 10.sp),
    )
    Box(
      modifier = Modifier.size(boxSize).background(Color(0xFF2D2D2D)).padding(4.dp),
      contentAlignment = Alignment.Center,
    ) {
      val context = LocalContext.current
      val composition =
        remember(animationResId) {
          LottieCompositionFactory.fromRawResSync(context, animationResId).value
        }

      com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}

/** Compose UI Composable for rendering a Lottie animation using Remote Compose. */
@Composable
public fun LottieRcPreview(
  @RawRes animationResId: Int,
  modifier: Modifier = Modifier,
  boxSize: Dp = 84.dp,
  progress: Float = 0f,
  animationResult: Result<Animation>? = null,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = modifier,
  ) {
    BasicText(
      text = "LottiePreview",
      style = TextStyle(color = Color.LightGray, fontSize = 10.sp),
    )
    Box(
      modifier = Modifier.size(boxSize).background(Color(0xFF2D2D2D)).padding(4.dp),
      contentAlignment = Alignment.Center,
    ) {
      val context = LocalContext.current
      val result =
        animationResult
          ?: remember(animationResId) { runCatching { Animation.load(animationResId, context) } }
      val animation = result.getOrNull()
      if (animation != null) {
        LottiePreview(
          animation = animation,
          progress = progress,
          modifier = Modifier.fillMaxSize(),
        )
      } else {
        val errorMessage = LottieDiffScreenshotTest.sanitizeErrorMessage(result.exceptionOrNull())
        BasicText(
          text = errorMessage,
          style =
            TextStyle(
              color = Color(0xFFFF6B6B),
              fontSize = 8.sp,
              textAlign = TextAlign.Center,
            ),
        )
      }
    }
  }
}

package com.google.android.horologist.lottie.util

import android.annotation.SuppressLint
import androidx.annotation.RawRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.remote.creation.compose.capture.rememberRemoteDocument
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.rememberNamedRemoteFloat
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import com.google.android.horologist.remotecompose.lottie.LottieAnimation
import com.google.android.horologist.remotecompose.lottie.SlotMap

@SuppressLint("RestrictedApi")
@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@UiComposable
@Composable
fun AnimatedLottiePlayer(
  @RawRes rawRes: Int,
  modifier: Modifier = Modifier,
  slotMap: SlotMap = SlotMap.Empty,
  durationMillis: Int = 2000,
  progressOverride: Float? = null,
) {
  // 1. Drive continuous progress with Compose InfiniteTransition
  val transition = rememberInfiniteTransition(label = "LottieProgress")
  val animatedProgress by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = durationMillis, easing = LinearEasing),
          repeatMode = RepeatMode.Restart,
        ),
      label = "Progress",
    )

  val currentProgress = progressOverride ?: animatedProgress

  // 2. Capture the document with a named "progress" variable
  val doc = rememberRemoteDocument {
    val progressVar = rememberNamedRemoteFloat("progress") { 0f.rf }
    LottieAnimation(
      rawRes = rawRes,
      slotMap = slotMap,
      progress = progressVar,
      modifier = RemoteModifier.fillMaxSize(),
    )
  }

  // 3. Update the named float on the player on each frame
  doc.value?.let { document ->
    RemoteDocumentPlayer(
      document = document,
      modifier = modifier,
      documentWidth = document.width,
      documentHeight = document.height,
      update = { player ->
        player.setUserLocalFloat("progress", currentProgress)
        player.invalidate()
      },
    )
  }
}

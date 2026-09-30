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

package com.google.android.horologist.lottie.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults
import com.google.android.horologist.lottie.LottieDemoItem
import com.google.android.horologist.lottie.PlaybackRegime
import com.google.android.horologist.lottie.util.AnimatedLottiePlayer
import kotlin.math.abs

/** Interactive detail view rendering full-size animation with playback controls. */
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun LottieDetailPlayer(
  item: LottieDemoItem,
  currentIndex: Int,
  totalCount: Int,
  onPrevious: () -> Unit,
  onNext: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var regime by remember { mutableStateOf(PlaybackRegime.TIME) }
  var isPlaying by remember(item.rawRes) { mutableStateOf(true) }
  var crownProgress by remember(item.rawRes) { mutableFloatStateOf(0f) }

  val isLowRes = RotaryDefaults.isLowResInput()

  val columnState =
    rememberResponsiveColumnState(
      contentPadding =
        padding(
          first = ScalingLazyColumnDefaults.ItemType.Text,
          last = ScalingLazyColumnDefaults.ItemType.Chip,
        )
    )

  ScreenScaffold(
    scrollState = columnState,
    modifier =
      modifier.fillMaxSize().onPreRotaryScrollEvent { event ->
        if (regime == PlaybackRegime.CROWN) {
          val pixels =
            if (event.verticalScrollPixels != 0f) {
              event.verticalScrollPixels
            } else {
              event.horizontalScrollPixels
            }
          val delta =
            if (isLowRes || abs(pixels) <= 2.0f) {
              pixels * 0.05f
            } else {
              pixels * 0.003f
            }
          crownProgress = (crownProgress + delta).coerceIn(0f, 1f)
          true
        } else {
          false
        }
      },
  ) {
    ScalingLazyColumn(columnState = columnState, modifier = Modifier.fillMaxSize()) {
      // Header: Counter and Category
      item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Text(
            text = "${currentIndex + 1} / $totalCount • ${item.category}",
            style = MaterialTheme.typography.caption2,
            color = Color(0xFFAAAAAA),
            textAlign = TextAlign.Center,
          )
        }
      }

      // Title
      item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Text(
            text = item.title,
            style = MaterialTheme.typography.title3,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      // Subtitle
      item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.caption1,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp),
          )
        }
      }

      // Regime selector row (Time vs Crown)
      item {
        Row(
          modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CompactChip(
            label = { Text("⏰ Time", fontSize = 11.sp) },
            onClick = { regime = PlaybackRegime.TIME },
            colors =
              if (regime == PlaybackRegime.TIME) {
                ChipDefaults.primaryChipColors()
              } else {
                ChipDefaults.secondaryChipColors()
              },
          )

          Spacer(modifier = Modifier.width(6.dp))

          CompactChip(
            label = { Text("⚙️ Crown", fontSize = 11.sp) },
            onClick = { regime = PlaybackRegime.CROWN },
            colors =
              if (regime == PlaybackRegime.CROWN) {
                ChipDefaults.primaryChipColors()
              } else {
                ChipDefaults.secondaryChipColors()
              },
          )
        }
      }

      // Large Live Animation Viewport (Keyed by rawRes and regime for instant reload)
      item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Box(
            modifier =
              Modifier.size(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(item.color ?: Color(0xFF121212)),
            contentAlignment = Alignment.Center,
          ) {
            key(item.rawRes, regime) {
              when (regime) {
                PlaybackRegime.TIME -> {
                  AnimatedLottiePlayer(
                    rawRes = item.rawRes,
                    progressOverride = if (isPlaying) null else 0f,
                    modifier = Modifier.size(118.dp),
                  )
                }
                PlaybackRegime.CROWN -> {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                  ) {
                    AnimatedLottiePlayer(
                      rawRes = item.rawRes,
                      progressOverride = crownProgress,
                      modifier = Modifier.size(100.dp),
                    )
                    Text(
                      text = "${(crownProgress * 100).toInt()}%",
                      fontSize = 10.sp,
                      color = Color(0xFFAAAAAA),
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Playback Controls Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Previous button
          CompactButton(onClick = onPrevious, colors = ButtonDefaults.secondaryButtonColors()) {
            Text(text = "◀", fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.width(8.dp))

          when (regime) {
            PlaybackRegime.TIME -> {
              // Play / Pause toggle button
              Button(
                onClick = { isPlaying = !isPlaying },
                colors =
                  if (isPlaying) {
                    ButtonDefaults.primaryButtonColors()
                  } else {
                    ButtonDefaults.secondaryButtonColors()
                  },
                modifier = Modifier.size(44.dp),
              ) {
                Text(text = if (isPlaying) "⏸" else "▶", fontSize = 16.sp)
              }
            }
            PlaybackRegime.CROWN -> {
              // Step buttons for touch / non-rotary fallback
              CompactButton(
                onClick = { crownProgress = (crownProgress - 0.1f).coerceIn(0f, 1f) },
                colors = ButtonDefaults.secondaryButtonColors(),
              ) {
                Text(text = "-10%", fontSize = 9.sp)
              }
              Spacer(modifier = Modifier.width(4.dp))
              CompactButton(
                onClick = { crownProgress = (crownProgress + 0.1f).coerceIn(0f, 1f) },
                colors = ButtonDefaults.secondaryButtonColors(),
              ) {
                Text(text = "+10%", fontSize = 9.sp)
              }
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Next button
          CompactButton(onClick = onNext, colors = ButtonDefaults.secondaryButtonColors()) {
            Text(text = "▶", fontSize = 12.sp)
          }
        }
      }

      // Return to List Button
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Chip(label = "Back to Gallery", onClick = onClose, modifier = Modifier.fillMaxWidth())
      }
    }
  }
}

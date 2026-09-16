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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.lottie.LottieDemoItem
import com.google.android.horologist.lottie.util.AnimatedLottiePlayer
import kotlinx.coroutines.delay

/** Auto-cycling kiosk demo mode player presenting all catalog animations sequentially. */
@Composable
fun LottieDemoModePlayer(
  catalog: List<LottieDemoItem>,
  initialIndex: Int,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var currentIndex by remember { mutableStateOf(initialIndex.coerceIn(0, catalog.lastIndex)) }
  val currentItem = catalog[currentIndex]

  // Auto-advance timer: 3 seconds per animation
  LaunchedEffect(currentIndex) {
    delay(3000L)
    currentIndex = (currentIndex + 1) % catalog.size
  }

  Box(
    modifier = modifier.fillMaxSize().background(Color.Black).clickable { onClose() },
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Text(
        text = "DEMO • ${currentIndex + 1} / ${catalog.size}",
        style = MaterialTheme.typography.caption2,
        color = Color(0xFFFFCC00),
        textAlign = TextAlign.Center,
      )

      Text(
        text = currentItem.title,
        style = MaterialTheme.typography.title3,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )

      Spacer(modifier = Modifier.height(4.dp))

      Box(
        modifier =
          Modifier.size(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(currentItem.color ?: Color(0xFF121212)),
        contentAlignment = Alignment.Center,
      ) {
        key(currentItem.rawRes) {
          AnimatedLottiePlayer(rawRes = currentItem.rawRes, modifier = Modifier.size(110.dp))
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Tap anywhere to exit",
        style = MaterialTheme.typography.caption2,
        color = Color(0xFF666666),
        textAlign = TextAlign.Center,
      )
    }
  }
}

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.SecondaryTitle
import com.google.android.horologist.lottie.LottieDemoItem
import com.google.android.horologist.lottie.util.AnimatedLottiePlayer

/** Scrollable gallery displaying all animations grouped with live preview cards. */
@Composable
fun LottieGalleryList(
  catalog: List<LottieDemoItem>,
  onSelect: (Int) -> Unit,
  onStartDemo: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val columnState =
    rememberResponsiveColumnState(
      contentPadding =
        padding(
          first = ScalingLazyColumnDefaults.ItemType.Text,
          last = ScalingLazyColumnDefaults.ItemType.Chip,
        )
    )

  val groupedByCategory = remember(catalog) { catalog.groupBy { it.category } }

  ScreenScaffold(scrollState = columnState) {
    ScalingLazyColumn(columnState = columnState, modifier = modifier.fillMaxSize()) {
      item {
        ListHeader {
          Text(
            text = "Lottie Gallery (${catalog.size})",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.title3,
          )
        }
      }

      // Top-level kiosk demo mode button
      item {
        Chip(
          label = "Start Demo Mode",
          secondaryLabel = "Auto-cycle all ${catalog.size} animations",
          icon = {
            Box(
              modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF332200)),
              contentAlignment = Alignment.Center,
            ) {
              Text("▶", fontSize = 16.sp, color = Color(0xFFFFCC00))
            }
          },
          onClick = onStartDemo,
          modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        )
      }

      groupedByCategory.forEach { (category, items) ->
        item { SecondaryTitle(category) }
        items.forEach { item ->
          val itemIndex = catalog.indexOf(item)
          item { LottieCard(item = item, onClick = { onSelect(itemIndex) }) }
        }
      }
    }
  }
}

/** Card item rendering a live thumbnail preview and descriptive labels. */
@Composable
fun LottieCard(item: LottieDemoItem, onClick: () -> Unit) {
  Chip(
    label = item.title,
    secondaryLabel = item.subtitle,
    icon = {
      Box(
        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center,
      ) {
        AnimatedLottiePlayer(rawRes = item.rawRes, modifier = Modifier.size(32.dp))
      }
    },
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
  )
}

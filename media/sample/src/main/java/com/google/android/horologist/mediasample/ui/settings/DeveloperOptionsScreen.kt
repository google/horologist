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

package com.google.android.horologist.mediasample.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.google.android.horologist.media.ui.material3.navigation.CustomRoute
import com.google.android.horologist.mediasample.R
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.AudioDebug
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.NewHotness
import com.google.android.horologist.mediasample.ui.navigation.UampNavigationScreen.Samples

@Composable
fun DeveloperOptionsScreen(
  developerOptionsScreenViewModel: DeveloperOptionsScreenViewModel,
  backStack: NavBackStack<CustomRoute>,
  modifier: Modifier = Modifier,
) {
  val uiState by developerOptionsScreenViewModel.uiState.collectAsStateWithLifecycle()

  val transformationSpec = rememberTransformationSpec()
  val columnState = rememberTransformingLazyColumnState()

  ScreenScaffold(
    scrollState = columnState,
    modifier = modifier,
  ) { contentPadding ->
    TransformingLazyColumn(
      state = columnState,
      contentPadding = contentPadding,
    ) {
      item {
        ListHeader(
          modifier =
            Modifier.fillMaxWidth()
              .transformedHeight(this, transformationSpec)
              .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
          transformation = SurfaceTransformation(transformationSpec),
        ) {
          Text(text = stringResource(id = R.string.sample_developer_options))
        }
      }
      developerActionSetting(
        transformationSpec = transformationSpec,
        text = "New Hotness Player",
      ) {
        backStack.add(CustomRoute(NewHotness.navRoute))
      }
      developerCheckedSetting(
        transformationSpec = transformationSpec,
        value = uiState.networkRequest != null,
        textId = R.string.request_network,
        enabled = uiState.writable,
      ) {
        developerOptionsScreenViewModel.toggleNetworkRequest()
      }
      developerActionSetting(
        transformationSpec = transformationSpec,
        textId = R.string.sample_audio_debug,
      ) {
        backStack.add(CustomRoute(AudioDebug.navRoute))
      }
      developerActionSetting(
        transformationSpec = transformationSpec,
        textId = R.string.sample_samples,
      ) {
        backStack.add(CustomRoute(Samples.navRoute))
      }
      developerCheckedSetting(
        transformationSpec = transformationSpec,
        value = uiState.showTimeTextInfo,
        textId = R.string.show_time_text_info,
        enabled = uiState.writable,
      ) {
        developerOptionsScreenViewModel.setShowTimeTextInfo(it)
      }
      developerCheckedSetting(
        transformationSpec = transformationSpec,
        value = uiState.debugOffload,
        textId = R.string.debug_offload,
        enabled = uiState.writable,
      ) {
        developerOptionsScreenViewModel.setDebugOffload(it)
      }
      developerCheckedSetting(
        transformationSpec = transformationSpec,
        value = uiState.podcastControls,
        textId = R.string.podcast_controls,
        enabled = uiState.writable,
      ) {
        developerOptionsScreenViewModel.setPodcastControls(it)
      }
      developerCheckedSetting(
        transformationSpec = transformationSpec,
        value = uiState.loadItemsAtStartup,
        textId = R.string.load_items,
        enabled = uiState.writable,
      ) {
        developerOptionsScreenViewModel.setLoadItemsAtStartup(it)
      }
      developerCheckedSetting(
        transformationSpec = transformationSpec,
        value = uiState.streamingMode,
        textId = R.string.streaming_mode,
        enabled = uiState.writable,
      ) {
        developerOptionsScreenViewModel.setStreamingMode(it)
      }
      developerCheckedSetting(
        transformationSpec = transformationSpec,
        value = uiState.animated,
        textId = R.string.animated,
        enabled = uiState.writable,
      ) {
        developerOptionsScreenViewModel.setAnimated(it)
      }
      developerActionSetting(
        transformationSpec = transformationSpec,
        textId = R.string.force_stop,
      ) {
        developerOptionsScreenViewModel.forceStop()
      }
      item {
        val message = stringResource(id = R.string.sample_error)
        ActionSetting(
          text = stringResource(id = R.string.show_test_dialog),
          transformation = SurfaceTransformation(transformationSpec),
          modifier =
            Modifier.fillMaxWidth()
              .transformedHeight(this, transformationSpec)
              .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
        ) {
          developerOptionsScreenViewModel.showDialog(message)
        }
      }
    }
  }
}

private fun TransformingLazyColumnScope.developerActionSetting(
  transformationSpec: TransformationSpec,
  @StringRes textId: Int? = null,
  text: String? = null,
  onClick: () -> Unit,
) {
  item {
    ActionSetting(
      text = textId?.let { stringResource(id = it) } ?: text ?: "",
      transformation = SurfaceTransformation(transformationSpec),
      modifier =
        Modifier.fillMaxWidth()
          .transformedHeight(this, transformationSpec)
          .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
      onClick = onClick,
    )
  }
}

private fun TransformingLazyColumnScope.developerCheckedSetting(
  transformationSpec: TransformationSpec,
  value: Boolean,
  @StringRes textId: Int,
  enabled: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  item {
    CheckedSetting(
      value = value,
      text = stringResource(id = textId),
      enabled = enabled,
      transformation = SurfaceTransformation(transformationSpec),
      modifier =
        Modifier.fillMaxWidth()
          .transformedHeight(this, transformationSpec)
          .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
      onCheckedChange = onCheckedChange,
    )
  }
}

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

package com.google.android.horologist.media3.navigation

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class NavDeepLinkIntentBuilderTest {
  private val context = ApplicationProvider.getApplicationContext<Context>()
  private val builder =
    NavDeepLinkIntentBuilder(
      application = context,
      downloadUri = "test://test/player?page=1",
      playerUri = "test://test/player?page=0",
    )

  @Test
  fun playerIntentIsRestrictedToOwnPackage() {
    val pendingIntent = builder.buildPlayerIntent()

    assertRestrictedToOwnPackage(pendingIntent, "test://test/player?page=0")
  }

  @Test
  fun downloadIntentIsRestrictedToOwnPackage() {
    val pendingIntent = builder.buildDownloadIntent()

    assertRestrictedToOwnPackage(pendingIntent, "test://test/player?page=1")
  }

  private fun assertRestrictedToOwnPackage(pendingIntent: PendingIntent, uri: String) {
    val shadow = shadowOf(pendingIntent)
    assertThat(shadow.isImmutable).isTrue()

    val intent = shadow.savedIntents.last()
    assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
    assertThat(intent.data.toString()).isEqualTo(uri)
    assertThat(intent.`package` ?: intent.component?.packageName).isEqualTo(context.packageName)
  }
}

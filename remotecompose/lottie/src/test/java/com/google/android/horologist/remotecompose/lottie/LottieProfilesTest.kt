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

@file:Suppress("RestrictedApi")

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.remote.core.Operations
import androidx.compose.remote.core.operations.ShaderData
import androidx.compose.remote.creation.CreationDisplayInfo
import androidx.compose.remote.creation.profile.RcPlatformProfiles
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LottieProfilesTest {
  private val display = CreationDisplayInfo(64, 64, 160)

  @Test
  fun retainsAndroidxOperationsExceptRuntimeShaders() {
    val base = RcPlatformProfiles.ANDROIDX
    val profile = LottieProfiles.NoRuntimeShaders
    assertThat(profile.apiLevel).isEqualTo(base.apiLevel)
    assertThat(profile.operationsProfiles).isEqualTo(base.operationsProfiles)
    assertThat(profile.supportedOperations)
      .containsExactlyElementsIn(base.supportedOperations - Operations.DATA_SHADER)
    // Creating the custom profile must not mutate the shared AndroidX profile.
    assertThat(base.supportedOperations).contains(Operations.DATA_SHADER)
  }

  @Test
  fun rejectsLuminanceShaderDuringGeneration() {
    val source =
      """
      uniform shader matte;
      half4 main(float2 p) {
        half4 color = matte.eval(p);
        half alpha = dot(color.rgb, half3(0.2126, 0.7152, 0.0722));
        return half4(alpha);
      }
      """
        .trimIndent()
    // Control: shader serialization is supported by the ordinary AndroidX profile.
    val ordinary = RcPlatformProfiles.ANDROIDX.create(display, null)
    ordinary.createShader(source).commit()
    assertThat(ordinary.encodeToByteArray()).isNotEmpty()

    val checked = LottieProfiles.NoRuntimeShaders.create(display, null)
    val failure =
      assertThrows(RuntimeException::class.java) { checked.createShader(source).commit() }
    assertThat(failure).hasMessageThat().contains("Operation ${Operations.DATA_SHADER}")
  }

  @Test
  fun rejectsDirectShaderOperationsToo() {
    val writer = LottieProfiles.NoRuntimeShaders.create(display, null)
    val failure =
      assertThrows(RuntimeException::class.java) {
        ShaderData.apply(writer.buffer.buffer, 42, 43, null, null, null)
      }
    assertThat(failure).hasMessageThat().contains("Operation ${Operations.DATA_SHADER}")
  }

  @Test
  fun stillRecordsOrdinaryDrawing() {
    val writer = LottieProfiles.NoRuntimeShaders.create(display, null)
    writer.drawRect(0f, 0f, 64f, 64f)
    assertThat(writer.encodeToByteArray()).isNotEmpty()
  }
}

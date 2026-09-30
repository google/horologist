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

@file:Suppress("RestrictedApi", "INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.google.android.horologist.remotecompose.lottie

import androidx.compose.remote.core.operations.Utils
import androidx.compose.remote.core.operations.utilities.AnimatedFloatExpression
import androidx.compose.remote.creation.compose.capture.RemoteComposeCreationState
import androidx.compose.remote.creation.compose.state.RemoteFloatExpression
import androidx.compose.remote.creation.compose.state.RemoteStateInstanceKey
import androidx.compose.ui.geometry.Size
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.horologist.remotecompose.lottie.renderer.properties.rawReference
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteExpressionCacheTest {
  @Test
  fun refusesGeometryBeyondPlayerStateCapacity() {
    val state = RemoteComposeCreationState(Size(64f, 64f), LottieProfiles.NoRuntimeShaders)
    repeat(10000) { state.document.addFloatConstant(it.toFloat()) }
    val expression =
      RemoteFloatExpression(null, RemoteStateInstanceKey()) {
        floatArrayOf(Utils.asNan(100), 1f, AnimatedFloatExpression.ADD)
      }
    val failure =
      assertThrows(IllegalStateException::class.java) {
        expression.rawReference().getIdForCreationState(state)
      }
    assertThat(failure).hasMessageThat().contains("float-state limit")
  }

  @Test
  fun collidingNanPayloadsMustNotAliasExpressions() {
    val state = RemoteComposeCreationState(Size(64f, 64f), LottieProfiles.NoRuntimeShaders)
    // Same polynomial hash: incrementing the first word offsets 31 decrements of the second.
    val a = floatArrayOf(Utils.asNan(100), Utils.asNan(200), AnimatedFloatExpression.ADD)
    val b = floatArrayOf(Utils.asNan(101), Utils.asNan(169), AnimatedFloatExpression.ADD)
    fun hash(array: FloatArray) = array.fold(0) { h, f -> h * 31 + f.toRawBits() }
    assertThat(hash(a)).isEqualTo(hash(b))
    assertThat(a.contentEquals(b)).isTrue() // Kotlin loses NaN payload identity here.
    val first = RemoteFloatExpression(null, RemoteStateInstanceKey()) { a }
    val second = RemoteFloatExpression(null, RemoteStateInstanceKey()) { b }
    assertThat(first.rawReference().getIdForCreationState(state))
      .isNotEqualTo(second.rawReference().getIdForCreationState(state))
  }
}

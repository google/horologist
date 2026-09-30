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

package com.google.android.horologist.remotecompose.lottie.renderer.properties

import androidx.compose.remote.core.Limits
import androidx.compose.remote.core.operations.Utils
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemoteFloatExpression
import androidx.compose.remote.creation.compose.state.RemoteOperationCacheKey

private enum class LottieExpressionOperation {
  RawReference
}

/**
 * Share an expression without RC alpha19's NaN-insensitive expression-cache comparison. Variable
 * ids and operators live in NaN payload bits, so distinct arrays with the same polynomial hash must
 * not be treated as equivalent. The structural state key still shares repeated references. This
 * protects this allocation only: share intermediate expressions before their parents grow past RC's
 * inline-expression limit, or RC can still auto-materialize them through its own cache.
 */
internal fun RemoteFloat.rawReference(): RemoteFloat {
  if (constantValueOrNull != null) return this
  return RemoteFloatExpression(
    null,
    RemoteOperationCacheKey.create(LottieExpressionOperation.RawReference, this),
  ) { state ->
    val array = arrayForCreationState(state)
    if (array.size == 1) array
    else {
      val reference = state.document.floatExpression(*array)
      check(Utils.idFromNan(reference) < Limits.MAX_STATE_DATA) {
        "Lottie geometry exceeds RC's ${Limits.MAX_STATE_DATA}-entry float-state limit"
      }
      floatArrayOf(reference)
    }
  }
}

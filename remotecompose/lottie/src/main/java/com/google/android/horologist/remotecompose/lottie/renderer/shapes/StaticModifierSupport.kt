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

package com.google.android.horologist.remotecompose.lottie.renderer.shapes

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import com.google.android.horologist.remotecompose.lottie.renderer.properties.RemoteBezierValue

/** Never silently turn live expressions into zero while running a CPU-only modifier. */
@SuppressLint("RestrictedApi")
internal fun RemoteFloat.requireStaticModifierValue(feature: String): Float =
  requireNotNull(constantValueOrNull) { "$feature does not yet support animated values" }
    .also { require(it.isFinite()) { "$feature requires finite values" } }

@SuppressLint("RestrictedApi")
internal fun RemoteBezierValue.requireStaticModifierGeometry(feature: String) {
  require(
    (vertices + inTangents + outTangents).flatten().all {
      it.constantValueOrNull?.isFinite() == true
    }
  ) {
    "$feature does not yet support animated geometry"
  }
}

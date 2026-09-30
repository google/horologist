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
import androidx.compose.remote.creation.profile.Profile
import androidx.compose.remote.creation.profile.RcPlatformProfiles
import java.util.Collections

/** Recording profiles for Lottie documents and their previews. */
public object LottieProfiles {
  /**
   * AndroidX recording with runtime shaders disabled at generation time.
   *
   * The operation whitelist rejects shader data instead of silently dropping an effect. Ordinary
   * linear/radial gradients remain supported. In particular, alpha19 cannot execute luminance
   * runtime shaders on its software offscreen canvases. This profile does not bake a screenshot or
   * freeze animated content. Hosts should also deny shaders in their player policy.
   *
   * Pass this profile to `rememberRemoteDocument(profile = NoRuntimeShaders)` when recording.
   */
  public val NoRuntimeShaders: Profile =
    RcPlatformProfiles.ANDROIDX.let { base ->
      val operations =
        Collections.unmodifiableSet(base.supportedOperations - Operations.DATA_SHADER)
      Profile(
        base.apiLevel,
        base.operationsProfiles,
        base.platform,
        { operations },
        base.profileFactory,
      )
    }
}

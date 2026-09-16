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

package com.google.android.horologist.lottie

import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import com.google.android.horologist.sample.R

/** Active navigation / presentation mode within the Lottie showcase screen. */
sealed interface LottieViewMode {
  data object Gallery : LottieViewMode

  data class Detail(val index: Int) : LottieViewMode

  data class Demo(val index: Int) : LottieViewMode
}

/** Playback regime for the interactive detail player. */
enum class PlaybackRegime {
  TIME,
  CROWN,
}

/** Lightweight metadata describing a Lottie animation showcase entry. */
data class LottieDemoItem(
  val title: String,
  val subtitle: String,
  val category: String,
  @param:RawRes val rawRes: Int,
  val color: Color? = null,
)

/** Canonical catalog of all test and feature showcase animations in the sample app. */
val LottieDemoCatalog: List<LottieDemoItem> =
  listOf(
    LottieDemoItem(
      title = "Clear weather",
      subtitle = "Weather forecast",
      category = "Weather widget",
      rawRes = R.raw.prod,
      color = Color(0xff3C6D9A),
    ),
    LottieDemoItem(
      title = "Snow",
      subtitle = "Weather forecast",
      category = "Weather widget",
      rawRes = R.raw.prod2,
      color = Color(0xFF314560),
    ),
    // 1. Media & System Controls
    LottieDemoItem(
      title = "Geometry",
      subtitle = "Multi-shape morphing",
      category = "Media & Controls",
      rawRes = R.raw.geometry,
    ),
    LottieDemoItem(
      title = "Play / Pause",
      subtitle = "Playback state transition",
      category = "Media & Controls",
      rawRes = R.raw.play_pause,
    ),
    LottieDemoItem(
      title = "Next Track",
      subtitle = "Directional chevron motion",
      category = "Media & Controls",
      rawRes = R.raw.next,
    ),
    LottieDemoItem(
      title = "M3 Play / Pause",
      subtitle = "Material 3 expressive easing",
      category = "Media & Controls",
      rawRes = R.raw.m3_play_pause,
    ),
    LottieDemoItem(
      title = "M3 Next Track",
      subtitle = "Material 3 expressive arrow",
      category = "Media & Controls",
      rawRes = R.raw.m3_next,
    ),
    LottieDemoItem(
      title = "Volume Up",
      subtitle = "Speaker waves expanding",
      category = "Media & Controls",
      rawRes = R.raw.volume_up,
    ),
    LottieDemoItem(
      title = "Volume Down",
      subtitle = "Speaker waves contracting",
      category = "Media & Controls",
      rawRes = R.raw.volume_down,
    ),
    LottieDemoItem(
      title = "Mute to Unmute",
      subtitle = "Slash dismiss transition",
      category = "Media & Controls",
      rawRes = R.raw.mute_to_unmute,
    ),
    LottieDemoItem(
      title = "Unmute to Mute",
      subtitle = "Slash draw transition",
      category = "Media & Controls",
      rawRes = R.raw.unmute_to_mute,
    ),

    // 2. Parametric Shapes & Hierarchies
    LottieDemoItem(
      title = "Position Animated",
      subtitle = "Spatial Bézier 2D curve",
      category = "Shapes & Hierarchies",
      rawRes = R.raw.position_animated,
    ),
    LottieDemoItem(
      title = "PolyStar",
      subtitle = "5-point star with roundness",
      category = "Shapes & Hierarchies",
      rawRes = R.raw.polystar,
    ),
    LottieDemoItem(
      title = "Rect & Ellipse",
      subtitle = "Rounded rect and oval primitives",
      category = "Shapes & Hierarchies",
      rawRes = R.raw.rect_ellipse,
    ),
    LottieDemoItem(
      title = "Parent Chain",
      subtitle = "20-deep ancestor transform chain",
      category = "Shapes & Hierarchies",
      rawRes = R.raw.parent_chain,
    ),
    LottieDemoItem(
      title = "Transform Skew",
      subtitle = "2D skew and skew-axis shear",
      category = "Shapes & Hierarchies",
      rawRes = R.raw.transform_skew,
    ),

    // 3. Gradients, Strokes & Fills
    LottieDemoItem(
      title = "Linear Gradient Fill",
      subtitle = "3-color stop linear shader",
      category = "Gradients & Strokes",
      rawRes = R.raw.gradient_linear_fill,
    ),
    LottieDemoItem(
      title = "Radial Gradient Fill",
      subtitle = "Center-origin radial shader",
      category = "Gradients & Strokes",
      rawRes = R.raw.gradient_radial_fill,
    ),
    LottieDemoItem(
      title = "Gradient Stroke",
      subtitle = "Linear gradient on stroke line",
      category = "Gradients & Strokes",
      rawRes = R.raw.gradient_stroke,
    ),
    LottieDemoItem(
      title = "Stroke Dash Pattern",
      subtitle = "Animated dash & gap sequence",
      category = "Gradients & Strokes",
      rawRes = R.raw.stroke_dash_pattern,
    ),
    LottieDemoItem(
      title = "EvenOdd Fill Rule",
      subtitle = "Self-intersecting star cutout",
      category = "Gradients & Strokes",
      rawRes = R.raw.fill_rule_even_odd,
    ),

    // 4. Modifiers & Path Operations
    LottieDemoItem(
      title = "Trim Path Primitives",
      subtitle = "Parametric start/end trimming",
      category = "Modifiers & Paths",
      rawRes = R.raw.trim_path_primitives,
    ),
    LottieDemoItem(
      title = "Repeater Linear",
      subtitle = "5 copies with opacity fade",
      category = "Modifiers & Paths",
      rawRes = R.raw.repeater_linear_copies,
    ),
    LottieDemoItem(
      title = "Repeater Radial",
      subtitle = "60° rotational flower petals",
      category = "Modifiers & Paths",
      rawRes = R.raw.repeater_radial_distribution,
    ),
    LottieDemoItem(
      title = "Rounded Corners",
      subtitle = "Filleted star polygon vertices",
      category = "Modifiers & Paths",
      rawRes = R.raw.rounded_corners_star,
    ),
    LottieDemoItem(
      title = "Merge Paths",
      subtitle = "Boolean union of circles",
      category = "Modifiers & Paths",
      rawRes = R.raw.merge_paths_overlapping_circles,
    ),
  )

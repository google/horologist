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

package com.google.android.horologist.remotecompose.lottie

import org.junit.Ignore
import org.junit.Test

class RemainingCompositingRegressionTest : MotionPixelHarness() {
  // [SP_COMPOSITING_R05_01] Opaque alpha coverage exposes the target only inside the matte.
  @Test
  fun exposesRedTargetInsideOpaqueAlphaMatte() {
    val progress = show(matteAnimation(matteShapes(rectangle(fixed("[16,32]")))))
    assertPixels(Probe(16, 32, 1f), Probe(40, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(16, 32, 1f), Probe(40, 32, 0f))
  }

  // [SP_COMPOSITING_R05_02] Disjoint rendered shapes contribute their union to alpha coverage.
  @Test
  fun exposesBothDisjointMatteShapesAndKeepsTheirGapTransparent() {
    val shapes = "${rectangle(fixed("[16,32]"))},${rectangle(fixed("[48,32]"))}"
    val progress = show(matteAnimation(matteShapes(shapes)))
    assertPixels(Probe(16, 32, 1f), Probe(48, 32, 1f), Probe(32, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(16, 32, 1f), Probe(48, 32, 1f), Probe(32, 32, 0f))
  }

  // [SP_COMPOSITING_R05_03] Matte layer opacity modulates target alpha on the recorded document.
  @Test
  fun fadesTargetThroughHalfAlphaWhenMatteOpacityAnimatesToZero() {
    val matte = matteShapes(rectangle(fixed("[16,32]")), opacity = animated("[100]", "[0]"))
    val progress = show(matteAnimation(matte))
    assertPixels(Probe(16, 32, 1f), Probe(40, 32, 0f))
    advance(progress, 5f)
    assertPixels(Probe(16, 32, 0.5f), Probe(40, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(16, 32, 0f), Probe(40, 32, 0f))
  }

  // [SP_COMPOSITING_R05_04] White and black luminance boundaries produce full and zero coverage.
  @Test
  @Ignore(
    "R05: RC alpha19 has no software luminance-to-alpha compositor; runtime shaders reject bitmap canvases"
  )
  fun hidesTargetWhenLuminanceMatteChangesFromWhiteToBlack() {
    val matte = matteShapes(rectangle(fixed("[16,32]")), color = animated("[1,1,1,1]", "[0,0,0,1]"))
    val progress = show(matteAnimation(matte, mode = 3))
    assertPixels(Probe(16, 32, 1f), Probe(40, 32, 0f))
    advance(progress, 5f)
    assertPixels(Probe(16, 32, 0.5f), Probe(40, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(16, 32, 0f), Probe(40, 32, 0f))
  }

  // [SP_COMPOSITING_R05_05] A matte contributes no alpha after its own visibility interval.
  @Test
  fun hidesTargetAfterMatteOutPointWhileTargetRemainsInRange() {
    val progress = show(matteAnimation(matteShapes(rectangle(fixed("[16,32]")), outPoint = 10)))
    assertPixels(Probe(16, 32, 1f), Probe(40, 32, 0f))
    // Frame 12 is beyond existing out-point padding, isolating matte visibility from endpoint
    // policy.
    advance(progress, 12f)
    assertPixels(Probe(16, 32, 0f), Probe(40, 32, 0f))
  }

  // [SP_COMPOSITING_R05_06] Inverted alpha retains target pixels outside matte coverage.
  @Test
  fun removesMatteInteriorAndPreservesTargetExteriorForInvertedAlpha() {
    val progress = show(matteAnimation(matteShapes(rectangle(fixed("[16,32]"))), mode = 2))
    assertPixels(Probe(16, 32, 0f), Probe(40, 32, 1f))
    advance(progress, 10f)
    assertPixels(Probe(16, 32, 0f), Probe(40, 32, 1f))
  }

  // [SP_COMPOSITING_R05_07] A precomp target's children share the target's track matte.
  @Test
  fun appliesAlphaMatteToEveryChildOfPrecompTarget() {
    val matte = matteShapes(rectangle(fixed("[16,32]"), fixed("[16,64]")))
    val children = "${solid(index = 1, height = 32)},${solid(index = 2, height = 32, y = 32)}"
    val progress = show(document("$matte,${precomp(extra = "\"tt\":1,")}", children))
    assertPixels(Probe(16, 16, 1f), Probe(16, 48, 1f), Probe(40, 16, 0f), Probe(40, 48, 0f))
    advance(progress, 10f)
    assertPixels(Probe(16, 16, 1f), Probe(16, 48, 1f), Probe(40, 16, 0f), Probe(40, 48, 0f))
  }

  // [SP_COMPOSITING_R07_01] Precomp masks and transforms use containing time, not child time.
  @Test
  fun movesMaskAcrossEntireShiftedPrecompUsingContainingTimeline() {
    val path =
      """{"a":1,"k":[{"t":0,"s":[${boxPath(0, 16)}],$linear},
        {"t":20,"s":[${boxPath(32, 48)}]}]}"""
    val position = """{"a":1,"k":[{"t":0,"s":[0,0],$linear},{"t":20,"s":[8,0]}]}"""
    val children = "${solid(index = 1, height = 32)},${solid(index = 2, height = 32, y = 32)}"
    val layer = precomp(start = 10, stretch = 2, position = position, extra = mask(path))
    val progress = show(document(layer, children))
    advance(progress, 10f)
    assertPixels(Probe(28, 16, 1f), Probe(28, 48, 1f), Probe(12, 16, 0f), Probe(44, 48, 0f))
    advance(progress, 20f)
    assertPixels(Probe(48, 16, 1f), Probe(48, 48, 1f), Probe(24, 16, 0f), Probe(60, 48, 0f))
  }

  // [SP_COMPOSITING_R07_02] Declared precomp bounds clip overflow in child-local coordinates.
  @Test
  fun clipsOverflowingChildrenToTranslatedPrecompWidthAndHeight() {
    val children = "${solid(index = 1, height = 16, y = 8)},${solid(index = 2, width = 16, x = 8)}"
    val layer = precomp(width = 32, height = 32, position = animated("[8,8]", "[16,16]"))
    val progress = show(document(layer, children))
    assertPixels(Probe(24, 24, 1f), Probe(48, 24, 0f), Probe(24, 48, 0f), Probe(4, 24, 0f))
    advance(progress, 10f)
    // Stay inside the cross; (32, 32) is exactly on both child rectangles' outer edges.
    assertPixels(Probe(28, 28, 1f), Probe(56, 32, 0f), Probe(32, 56, 0f), Probe(12, 32, 0f))
  }

  // [SP_COMPOSITING_R07_03] A half-opacity precomp mask applies once to the completed composite.
  @Test
  fun keepsOverlappingChildrenAtHalfRedUnderHalfOpacityPrecompMask() {
    val children = "${solid(index = 1, width = 40)},${solid(index = 2, width = 40, x = 24)}"
    val layer = precomp(extra = mask(fixed(boxPath(0, 64)), opacity = 50))
    val progress = show(document(layer, children))
    assertPixels(Probe(8, 32, 0.5f), Probe(32, 32, 0.5f), Probe(56, 32, 0.5f))
    advance(progress, 10f)
    assertPixels(Probe(8, 32, 0.5f), Probe(32, 32, 0.5f), Probe(56, 32, 0.5f))
  }

  private fun matteAnimation(matte: String, mode: Int = 1): String =
    document("$matte,${solid(index = 2, extra = "\"tt\":$mode,")}")

  @Test
  fun expandsAnAddMaskOutsideItsOriginalOutline() {
    val properties =
      mask(fixed(boxPath(16, 32))).replace("\"x\":${fixed("0")}", "\"x\":${fixed("8")}")
    show(document(solid(1, extra = properties)))
    assertPixels(Probe(12, 32, 1f), Probe(36, 32, 1f), Probe(4, 32, 0f), Probe(44, 32, 0f))
  }

  @Test
  fun makesZeroOpacityMaskTransparent() {
    show(document(solid(1, extra = mask(fixed(boxPath(0, 64)), opacity = 0))))
    assertPixels(Probe(32, 32, 0f))
  }

  @Test
  fun appliesSubtractThenAddInAuthoredOrder() {
    val properties =
      """"masksProperties":[
      {"mode":"s","pt":${fixed(boxPath(0, 32))},"o":${fixed("100")}},
      {"mode":"a","pt":${fixed(boxPath(16, 48))},"o":${fixed("100")}}],"""
    show(document(solid(1, extra = properties)))
    assertPixels(Probe(8, 32, 0f), Probe(24, 32, 1f), Probe(56, 32, 1f))
  }

  @Test
  fun preservesParentSurfaceWhenChildAlsoHasAMask() {
    val child = solid(index = 1, extra = mask(fixed(boxPath(8, 40))))
    val progress = show(document(precomp(extra = mask(fixed(boxPath(0, 64)), opacity = 50)), child))
    assertPixels(Probe(16, 32, 0.5f), Probe(4, 32, 0f), Probe(48, 32, 0f))
    advance(progress, 10f)
    assertPixels(Probe(16, 32, 0.5f), Probe(4, 32, 0f), Probe(48, 32, 0f))
  }

  @Test
  fun multipliesTheCompletedLayerAgainstItsBackdrop() {
    val foreground = solid(index = 1, extra = "\"bm\":1,")
    val background = solid(index = 2).replace("#FF0000", "#800000")
    val progress = show(document("$foreground,$background"))
    assertPixels(Probe(32, 32, 0.5f))
    advance(progress, 10f)
    assertPixels(Probe(32, 32, 0.5f))
  }

  private fun matteShapes(
    shapes: String,
    opacity: String = fixed("100"),
    color: String = fixed("[1,1,1,1]"),
    outPoint: Int = 40,
  ): String =
    """{"ty":4,"ind":1,"td":1,"ip":0,"op":$outPoint,"st":0,"sr":1,
      "ks":${transform(opacity = opacity)},"shapes":[$shapes,
      {"ty":"fl","r":1,"c":$color,"o":${fixed("100")}}]}"""

  private fun solid(
    index: Int,
    width: Int = 64,
    height: Int = 64,
    x: Int = 0,
    y: Int = 0,
    extra: String = "",
  ): String =
    """{"ty":1,"ind":$index,"ip":0,"op":40,"st":0,"sr":1,$extra
      "sw":$width,"sh":$height,"sc":"#FF0000","ks":${transform(fixed("[$x,$y]"))}}"""

  private fun precomp(
    width: Int = 64,
    height: Int = 64,
    start: Int = 0,
    stretch: Int = 1,
    position: String = fixed("[0,0]"),
    extra: String = "",
  ): String =
    """{"ty":0,"ind":2,"refId":"children","w":$width,"h":$height,
      "ip":0,"op":40,"st":$start,"sr":$stretch,$extra"ks":${transform(position)}}"""

  private fun mask(path: String, opacity: Int = 100): String =
    """"hasMask":true,"masksProperties":[{"mode":"a","inv":false,
      "pt":$path,"o":${fixed("$opacity")},"x":${fixed("0")}}],"""

  private fun boxPath(left: Int, right: Int): String =
    """{"c":true,"v":[[$left,0],[$right,0],[$right,64],[$left,64]],
      "i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}"""

  private fun transform(position: String = fixed("[0,0]"), opacity: String = fixed("100")): String =
    """{"a":${fixed("[0,0]")},"p":$position,"r":${fixed("0")},
      "s":${fixed("[100,100]")},"o":$opacity}"""

  private fun document(layers: String, children: String = ""): String =
    """{"v":"5.7.4","fr":20,"ip":0,"op":40,"w":64,"h":64,"ddd":0,
      "assets":[{"id":"children","layers":[$children]}],"layers":[$layers]}"""
}

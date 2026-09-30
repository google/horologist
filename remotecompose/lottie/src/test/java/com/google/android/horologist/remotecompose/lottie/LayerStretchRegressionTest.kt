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

import org.junit.Test

/** Ordinary-layer keyframes are already authored in their containing composition's time. */
class LayerStretchRegressionTest : FixtureParityHarness() {
  @Test fun slowShape() = checkShape(2f, "slow")

  @Test fun fastShape() = checkShape(0.5f, "fast")

  @Test fun reversedShape() = checkShape(-1f, "reverse")

  @Test
  fun solidTransform() =
    check(
      "solid",
      root(
        """{"ty":1,"ind":1,"ip":0,"op":40,"st":9,"sr":2,
          "sw":12,"sh":12,"sc":"#ff0000","ks":${transform(motion("[6,26]", "[42,26]"))}}"""
      ),
    )

  @Test
  fun parentTransform() {
    val parent =
      """{"ty":3,"ind":2,"ip":0,"op":40,"st":9,"sr":0.5,
        "ks":${transform(motion("[0,-6]", "[0,6]"))}}"""
    check("parent", root("$parent,${shape(2f, extra = "\"parent\":2,")}"))
  }

  private fun maskedShape(stretch: Float): String {
    val mask =
      """"masksProperties":[{"mode":"a","inv":false,
        "pt":${fixed("""{"c":true,"v":[[0,0],[64,0],[64,64],[0,64]],"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]]}""")},
        "o":${motion("[30]", "[100]")},"x":${fixed("0")}}],"""
    return root(shape(stretch, extra = mask))
  }

  @Test
  fun maskOpacityAtNativeSurfaceResolution() =
    // This 64px surface must be compared at 64px (32dp in this test environment).
    // Enlarged offscreen edges remain the separately tracked R21 limitation.
    compare(maskedShape(2f), "layerstretch_mask", maxForegroundError = 0.01, renderSizeDp = 32)

  @Test
  fun maskOpacityControlAtNativeSurfaceResolution() =
    compare(
      maskedShape(1f),
      "layerstretch_mask-control",
      maxForegroundError = 0.01,
      renderSizeDp = 32,
    )

  @Test
  fun childOfStretchedPrecomp() {
    val precomp =
      """{"ty":0,"ind":1,"refId":"child","ip":0,"op":40,"st":0,"sr":2,
        "w":64,"h":64,"ks":${transform()}}"""
    check("precomp", root(precomp, """{"id":"child","layers":[${shape(0.5f)}]}"""))
  }

  @Test
  fun authoredVisibilityAndBackwardSeeks() {
    val progress = show(root(shape(2f, start = 5, end = 30)))
    assertPixels(Probe(12, 32, 0f))
    advance(progress, 5f)
    assertPixels(Probe(16, 32, 1f), Probe(44, 32, 0f))
    advance(progress, 20f)
    assertPixels(Probe(28, 32, 1f), Probe(12, 32, 0f))
    advance(progress, 30f)
    assertPixels(Probe(36, 32, 0f))
    advance(progress, 20f)
    assertPixels(Probe(28, 32, 1f))
    advance(progress, 20f)
    assertPixels(Probe(28, 32, 1f))
  }

  private fun checkShape(stretch: Float, name: String) = check(name, root(shape(stretch)))

  private fun check(name: String, json: String) =
    compare(json, "layerstretch_$name", maxForegroundError = 0.01)

  private fun root(layers: String, assets: String = ""): String =
    """{"v":"5.7.4","fr":20,"ip":0,"op":40,"w":64,"h":64,
      "assets":[$assets],"layers":[$layers]}"""

  private fun shape(stretch: Float, extra: String = "", start: Int = 0, end: Int = 40): String =
    """{"ty":4,"ind":1,"ip":$start,"op":$end,"st":9,"sr":$stretch,
      $extra"ks":${transform()},"shapes":[
      {"ty":"rc","p":${motion("[12,32]", "[44,32]")},"s":${fixed("[12,12]")},"r":${fixed("0")}},$redFill]}"""

  private fun transform(position: String = fixed("[0,0]")): String =
    """{"a":${fixed("[0,0]")},"p":$position,"r":${fixed("0")},
      "s":${fixed("[100,100]")},"o":${fixed("100")}}"""

  private fun motion(start: String, end: String): String =
    """{"a":1,"k":[{"t":0,"s":$start,"o":{"x":0,"y":0},"i":{"x":1,"y":1}},
      {"t":40,"s":$end}]}"""
}

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

/**
 * Screenshot tests comparing RemoteCompose Lottie rendering with `lottie-android` reference output.
 *
 * Test cases for parametric shapes (`rect_ellipse`, `polystar`) are sourced from the
 * [Lottie Format Feature Support & Sample Test Suite](https://docs.google.com/document/d/1jXj3kbXL57kxjRc0soUqst2poa2-Lrc2qZAIzEmbB8w/edit).
 */
class LottieFeatureDiffScreenshotTest : LottieDiffScreenshotTest() {

  @Test
  fun positionStatic() {
    runLottieDiffTest(R.raw.position_static)
  }

  @Test
  fun positionAnimated() {
    runLottieDiffTest(R.raw.position_animated) {
      captureFrame(frame = 0f)
      captureFrame(frame = 20f)
      captureFrame(frame = 40f)
      captureFrame(frame = 60f)
    }
  }

  /** Tests parametric rectangle, rounded rectangle, ellipse, and circle shapes. */
  @Test
  fun rectEllipse() {
    runLottieDiffTest(R.raw.rect_ellipse)
  }

  /** Tests parametric star, rounded star, polygon, and rounded polygon shapes. */
  @Test
  fun polystar() {
    runLottieDiffTest(R.raw.polystar)
  }

  /**
   * Tests layer parenting more than one level deep.
   *
   * `parent_chain` is 20 dot layers chained child -> parent -> grandparent -> ..., each applying
   * the same relative delta (translate 30px, rotate 25 degrees, scale 93%). Accumulating those
   * deltas down the chain draws a shrinking, fading spiral.
   *
   * Only the layer's immediate parent transform is applied, so every layer from the third down
   * loses its ancestors' transforms and collapses onto a single point.
   *
   * See https://github.com/google/horologist/issues/2795.
   */
  @Test
  fun parentChain() {
    runLottieDiffTest(R.raw.parent_chain)
  }

  /** Tests 2D skew and skew axis transformation matrix rendering. */
  @Test
  fun transformSkew() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "SkewedLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 },
              "sk": { "a": 0, "k": 30.0 },
              "sa": { "a": 0, "k": 45.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "RectGroup",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [40.0, 40.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.2, 0.6, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests precomposition recursive sub-composition rendering engine. */
  @Test
  fun precompSubcompositionRendering() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "assets": [
          {
            "id": "comp_circle_group",
            "nm": "CircleSubcomp",
            "layers": [
              {
                "ty": 4,
                "nm": "SubcompShape",
                "ind": 1,
                "ip": 0,
                "op": 30,
                "ks": {
                  "p": { "a": 0, "k": [0.0, 0.0, 0.0] },
                  "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
                  "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
                  "r": { "a": 0, "k": 0.0 },
                  "o": { "a": 0, "k": 100.0 }
                },
                "shapes": [
                  {
                    "ty": "gr",
                    "nm": "ShapeGroup",
                    "it": [
                      {
                        "ty": "el",
                        "p": { "a": 0, "k": [0.0, 0.0] },
                        "s": { "a": 0, "k": [36.0, 36.0] }
                      },
                      {
                        "ty": "fl",
                        "c": { "a": 0, "k": [0.9, 0.2, 0.3, 1.0] },
                        "o": { "a": 0, "k": 100.0 }
                      },
                      {
                        "ty": "tr",
                        "p": { "a": 0, "k": [0.0, 0.0] },
                        "a": { "a": 0, "k": [0.0, 0.0] },
                        "s": { "a": 0, "k": [100.0, 100.0] },
                        "r": { "a": 0, "k": 0.0 },
                        "o": { "a": 0, "k": 100.0 }
                      }
                    ]
                  }
                ]
              }
            ]
          }
        ],
        "layers": [
          {
            "ty": 0,
            "nm": "PrecompInstance",
            "refId": "comp_circle_group",
            "ind": 10,
            "ip": 0,
            "op": 30,
            "w": 100,
            "h": 100,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            }
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests linear gradient fill with multi-color stops on parametric shapes. */
  @Test
  fun gradientLinearFill() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "LinearGradientLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "RectWithLinearGrad",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [70.0, 70.0] },
                    "r": { "a": 0, "k": 10.0 }
                  },
                  {
                    "ty": "gf",
                    "nm": "LinearGradFill",
                    "t": 1,
                    "o": { "a": 0, "k": 100.0 },
                    "r": 1,
                    "s": { "a": 0, "k": [-35.0, -35.0] },
                    "e": { "a": 0, "k": [35.0, 35.0] },
                    "g": {
                      "p": 3,
                      "k": [
                        0.0, 1.0, 0.2, 0.2,
                        0.5, 1.0, 0.8, 0.0,
                        1.0, 0.2, 0.4, 1.0
                      ]
                    }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests radial gradient fill with color and opacity/alpha stops. */
  @Test
  fun gradientRadialFill() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "RadialGradientLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "CircleWithRadialGrad",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [70.0, 70.0] }
                  },
                  {
                    "ty": "gf",
                    "nm": "RadialGradFill",
                    "t": 2,
                    "o": { "a": 0, "k": 100.0 },
                    "r": 1,
                    "s": { "a": 0, "k": [0.0, 0.0] },
                    "e": { "a": 0, "k": [35.0, 0.0] },
                    "g": {
                      "p": 2,
                      "k": [
                        0.0, 0.0, 1.0, 0.8,
                        1.0, 0.8, 0.0, 1.0,
                        0.0, 1.0,
                        1.0, 0.3
                      ]
                    }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests gradient stroke with multi-color stops, stroke width, and rounded caps/joins. */
  @Test
  fun gradientStroke() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "GradientStrokeLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "StrokeGradGroup",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [64.0, 64.0] },
                    "r": { "a": 0, "k": 12.0 }
                  },
                  {
                    "ty": "gs",
                    "nm": "LinearGradStroke",
                    "t": 1,
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 8.0 },
                    "s": { "a": 0, "k": [-32.0, 0.0] },
                    "e": { "a": 0, "k": [32.0, 0.0] },
                    "g": {
                      "p": 2,
                      "k": [
                        0.0, 1.0, 0.6, 0.0,
                        1.0, 0.0, 0.8, 1.0
                      ]
                    },
                    "lc": 2,
                    "lj": 2
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests stroke dash patterns and dash offsets. */
  @Test
  fun strokeDashPattern() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "DashLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "DashedRectGroup",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [64.0, 64.0] },
                    "r": { "a": 0, "k": 12.0 }
                  },
                  {
                    "ty": "st",
                    "nm": "DashStroke",
                    "c": { "a": 0, "k": [0.2, 0.7, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 6.0 },
                    "lc": 2,
                    "lj": 2,
                    "d": [
                      { "n": "d", "nm": "dash", "v": { "a": 0, "k": 10.0 } },
                      { "n": "g", "nm": "gap", "v": { "a": 0, "k": 5.0 } },
                      { "n": "d", "nm": "dash2", "v": { "a": 0, "k": 2.0 } },
                      { "n": "g", "nm": "gap2", "v": { "a": 0, "k": 5.0 } },
                      { "n": "o", "nm": "offset", "v": { "a": 0, "k": 0.0 } }
                    ]
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests acute miter limit clipping on sharp polystar stroke corners. */
  @Test
  fun strokeMiterLimit() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "MiterLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "MiterStarGroup",
                "it": [
                  {
                    "ty": "sr",
                    "sy": 1,
                    "pt": { "a": 0, "k": 5.0 },
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "or": { "a": 0, "k": 38.0 },
                    "os": { "a": 0, "k": 0.0 },
                    "ir": { "a": 0, "k": 16.0 },
                    "is": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "st",
                    "nm": "MiterStroke",
                    "c": { "a": 0, "k": [1.0, 0.5, 0.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 6.0 },
                    "lc": 1,
                    "lj": 1,
                    "ml": 4.0
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests EvenOdd fill rule on self-intersecting star polygon contours. */
  @Test
  fun fillRuleEvenOdd() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "EvenOddLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "EvenOddGroup",
                "it": [
                  {
                    "ty": "sh",
                    "nm": "StarPath",
                    "ks": {
                      "a": 0,
                      "k": {
                        "c": true,
                        "v": [
                          [0.0, -40.0],
                          [23.51, 32.36],
                          [-38.04, -12.36],
                          [38.04, -12.36],
                          [-23.51, 32.36]
                        ],
                        "i": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]],
                        "o": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]]
                      }
                    }
                  },
                  {
                    "ty": "fl",
                    "nm": "EvenOddFill",
                    "c": { "a": 0, "k": [0.2, 0.8, 0.5, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "r": 2
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests static trim path modifiers on primitive parametric shapes. */
  @Test
  fun trimPathPrimitives() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "TrimPrimitivesLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "TrimRectGroup",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [60.0, 60.0] },
                    "r": { "a": 0, "k": 10.0 }
                  },
                  {
                    "ty": "tm",
                    "nm": "Trim",
                    "s": { "a": 0, "k": 20.0 },
                    "e": { "a": 0, "k": 80.0 },
                    "o": { "a": 0, "k": 15.0 }
                  },
                  {
                    "ty": "st",
                    "nm": "Stroke",
                    "c": { "a": 0, "k": [0.9, 0.3, 0.2, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 6.0 },
                    "lc": 2,
                    "lj": 2
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests dynamic animated trim paths with keyframed start, end, and offset. */
  @Test
  fun trimPathAnimated() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "AnimatedTrimLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "TrimEllipseGroup",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [64.0, 64.0] }
                  },
                  {
                    "ty": "tm",
                    "nm": "AnimatedTrim",
                    "s": {
                      "a": 1,
                      "k": [
                        { "t": 0, "s": [0.0], "e": [40.0] },
                        { "t": 30, "s": [40.0] }
                      ]
                    },
                    "e": {
                      "a": 1,
                      "k": [
                        { "t": 0, "s": [60.0], "e": [100.0] },
                        { "t": 30, "s": [100.0] }
                      ]
                    },
                    "o": {
                      "a": 1,
                      "k": [
                        { "t": 0, "s": [0.0], "e": [90.0] },
                        { "t": 30, "s": [90.0] }
                      ]
                    }
                  },
                  {
                    "ty": "st",
                    "nm": "Stroke",
                    "c": { "a": 0, "k": [0.2, 0.5, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 6.0 },
                    "lc": 2,
                    "lj": 2
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json) {
      captureFrame(frame = 0f)
      captureFrame(frame = 15f)
      captureFrame(frame = 30f)
    }
  }

  /** Tests Inverted Alpha track matte (tt: 2) cutout clipping. */
  @Test
  fun trackMatteInvertedAlpha() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "MatteMaskLayer",
            "ind": 1,
            "td": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "CircleShape",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [40.0, 40.0] }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [1.0, 1.0, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          },
          {
            "ty": 4,
            "nm": "TargetLayer",
            "ind": 2,
            "tt": 2,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "RectShape",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [72.0, 72.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.2, 0.6, 0.9, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests non-adjacent matte parent referencing (tp: 10) with alpha clipping. */
  @Test
  fun trackMatteNonAdjacentParent() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "MatteSourceLayer",
            "ind": 10,
            "td": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "MatteCircle",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [50.0, 50.0] }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [1.0, 1.0, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          },
          {
            "ty": 4,
            "nm": "BackgroundLayer",
            "ind": 20,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "BgRect",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [80.0, 80.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.15, 0.15, 0.2, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          },
          {
            "ty": 4,
            "nm": "MaskedStarLayer",
            "ind": 30,
            "tt": 1,
            "tp": 10,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "StarShape",
                "it": [
                  {
                    "ty": "sr",
                    "sy": 1,
                    "pt": { "a": 0, "k": 5.0 },
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "or": { "a": 0, "k": 38.0 },
                    "os": { "a": 0, "k": 0.0 },
                    "ir": { "a": 0, "k": 18.0 },
                    "is": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [1.0, 0.8, 0.1, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests track matte where the matte source layer has hd: true (hidden flag). */
  @Test
  fun trackMatteHiddenSourceLayer() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "HiddenMatteSource",
            "ind": 1,
            "td": 1,
            "hd": true,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "CircleShape",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [40.0, 40.0] }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [1.0, 1.0, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          },
          {
            "ty": 4,
            "nm": "TargetLayer",
            "ind": 2,
            "tt": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "RectShape",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [72.0, 72.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.9, 0.2, 0.3, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests SolidColorLayer with Subtract mask (mode: "s") cutting out a hole. */
  @Test
  fun layerMaskSolidSubtract() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 1,
            "nm": "SolidWithMask",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "sw": 100,
            "sh": 100,
            "sc": "#3F51B5",
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "masksProperties": [
              {
                "nm": "SubtractMask",
                "mode": "s",
                "inv": false,
                "pt": {
                  "a": 0,
                  "k": {
                    "c": true,
                    "v": [
                      [50.0, 20.0],
                      [80.0, 50.0],
                      [50.0, 80.0],
                      [20.0, 50.0]
                    ],
                    "i": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]],
                    "o": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]]
                  }
                },
                "o": { "a": 0, "k": 100.0 }
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests ShapeLayer with Add / Intersect masks clipping vector shapes. */
  @Test
  fun layerMaskShapeIntersect() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "MaskedShapeLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "masksProperties": [
              {
                "nm": "IntersectMask",
                "mode": "a",
                "inv": false,
                "pt": {
                  "a": 0,
                  "k": {
                    "c": true,
                    "v": [
                      [-30.0, -30.0],
                      [30.0, -30.0],
                      [30.0, 10.0],
                      [-30.0, 10.0]
                    ],
                    "i": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]],
                    "o": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]]
                  }
                },
                "o": { "a": 0, "k": 100.0 }
              }
            ],
            "shapes": [
              {
                "ty": "gr",
                "nm": "StarGroup",
                "it": [
                  {
                    "ty": "sr",
                    "sy": 1,
                    "pt": { "a": 0, "k": 5.0 },
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "or": { "a": 0, "k": 40.0 },
                    "os": { "a": 0, "k": 0.0 },
                    "ir": { "a": 0, "k": 18.0 },
                    "is": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.95, 0.6, 0.1, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests ShapeLayer with multiple Add masks merged into a composite union path. */
  @Test
  fun layerMultipleAddMasks() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "MultiAddMaskLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "masksProperties": [
              {
                "nm": "AddMaskLeft",
                "mode": "a",
                "inv": false,
                "pt": {
                  "a": 0,
                  "k": {
                    "c": true,
                    "v": [[-40.0, -20.0], [-10.0, -20.0], [-10.0, 20.0], [-40.0, 20.0]],
                    "i": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]],
                    "o": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]]
                  }
                },
                "o": { "a": 0, "k": 100.0 }
              },
              {
                "nm": "AddMaskRight",
                "mode": "a",
                "inv": false,
                "pt": {
                  "a": 0,
                  "k": {
                    "c": true,
                    "v": [[10.0, -20.0], [40.0, -20.0], [40.0, 20.0], [10.0, 20.0]],
                    "i": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]],
                    "o": [[0.0, 0.0], [0.0, 0.0], [0.0, 0.0], [0.0, 0.0]]
                  }
                },
                "o": { "a": 0, "k": 100.0 }
              }
            ],
            "shapes": [
              {
                "ty": "gr",
                "nm": "CircleGroup",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [80.0, 80.0] }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.2, 0.6, 0.9, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests Repeater (rp) linear duplication with start/end opacity interpolation. */
  @Test
  fun repeaterLinearCopies() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "RepeaterLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [18.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "RepeatedCircle",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [12.0, 12.0] }
                  },
                  {
                    "ty": "rp",
                    "nm": "Repeater 1",
                    "c": { "a": 0, "k": 5.0 },
                    "o": { "a": 0, "k": 0.0 },
                    "m": 1,
                    "tr": {
                      "p": { "a": 0, "k": [16.0, 0.0] },
                      "a": { "a": 0, "k": [0.0, 0.0] },
                      "s": { "a": 0, "k": [100.0, 100.0] },
                      "r": { "a": 0, "k": 0.0 },
                      "so": { "a": 0, "k": 100.0 },
                      "eo": { "a": 0, "k": 20.0 }
                    }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.1, 0.7, 0.4, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests Repeater (rp) radial distribution with rotational stepping. */
  @Test
  fun repeaterRadialDistribution() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "RadialRepeaterLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "PetalsGroup",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, -28.0] },
                    "s": { "a": 0, "k": [10.0, 18.0] }
                  },
                  {
                    "ty": "rp",
                    "nm": "RadialRepeater",
                    "c": { "a": 0, "k": 6.0 },
                    "o": { "a": 0, "k": 0.0 },
                    "m": 1,
                    "tr": {
                      "p": { "a": 0, "k": [0.0, 0.0] },
                      "a": { "a": 0, "k": [0.0, 0.0] },
                      "s": { "a": 0, "k": [100.0, 100.0] },
                      "r": { "a": 0, "k": 60.0 },
                      "so": { "a": 0, "k": 100.0 },
                      "eo": { "a": 0, "k": 100.0 }
                    }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.85, 0.2, 0.5, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests RoundedCorners (rd) modifier filleting sharp polygon vertices. */
  @Test
  fun roundedCornersStar() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "RoundedStarLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "StarGroup",
                "it": [
                  {
                    "ty": "sr",
                    "sy": 1,
                    "pt": { "a": 0, "k": 5.0 },
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "or": { "a": 0, "k": 38.0 },
                    "os": { "a": 0, "k": 0.0 },
                    "ir": { "a": 0, "k": 18.0 },
                    "is": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "rd",
                    "nm": "RoundCorners",
                    "r": { "a": 0, "k": 8.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.95, 0.45, 0.2, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "st",
                    "c": { "a": 0, "k": [0.9, 0.9, 0.9, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 2.0 },
                    "lc": 2,
                    "lj": 2
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests MergePaths (mm) modifier merging overlapping subpaths. */
  @Test
  fun mergePathsOverlappingCircles() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "MergePathsLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "MergedGroup",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [-14.0, 0.0] },
                    "s": { "a": 0, "k": [36.0, 36.0] }
                  },
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [14.0, 0.0] },
                    "s": { "a": 0, "k": [36.0, 36.0] }
                  },
                  {
                    "ty": "mm",
                    "nm": "Merge",
                    "mm": 1
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.2, 0.5, 0.9, 1.0] },
                    "o": { "a": 0, "k": 80.0 }
                  },
                  {
                    "ty": "st",
                    "c": { "a": 0, "k": [1.0, 1.0, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 2.5 },
                    "lc": 2,
                    "lj": 2
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests 3 levels of nested Precompositions with compound transform propagation. */
  @Test
  fun nestedPrecompositions() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "assets": [
          {
            "id": "comp_level2",
            "layers": [
              {
                "ty": 4,
                "nm": "InnerLeafShape",
                "ind": 1,
                "ip": 0,
                "op": 30,
                "ks": {
                  "p": { "a": 0, "k": [10.0, 10.0, 0.0] },
                  "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
                  "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
                  "r": { "a": 0, "k": 0.0 },
                  "o": { "a": 0, "k": 100.0 }
                },
                "shapes": [
                  {
                    "ty": "gr",
                    "nm": "InnerRect",
                    "it": [
                      {
                        "ty": "rc",
                        "p": { "a": 0, "k": [0.0, 0.0] },
                        "s": { "a": 0, "k": [30.0, 30.0] },
                        "r": { "a": 0, "k": 4.0 }
                      },
                      {
                        "ty": "fl",
                        "c": { "a": 0, "k": [0.2, 0.7, 0.8, 1.0] },
                        "o": { "a": 0, "k": 100.0 }
                      },
                      {
                        "ty": "tr",
                        "p": { "a": 0, "k": [0.0, 0.0] },
                        "a": { "a": 0, "k": [0.0, 0.0] },
                        "s": { "a": 0, "k": [100.0, 100.0] },
                        "r": { "a": 0, "k": 0.0 },
                        "o": { "a": 0, "k": 100.0 }
                      }
                    ]
                  }
                ]
              }
            ]
          },
          {
            "id": "comp_level1",
            "layers": [
              {
                "ty": 0,
                "nm": "Level2Ref",
                "refId": "comp_level2",
                "ind": 1,
                "ip": 0,
                "op": 30,
                "ks": {
                  "p": { "a": 0, "k": [15.0, 15.0, 0.0] },
                  "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
                  "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
                  "r": { "a": 0, "k": 0.0 },
                  "o": { "a": 0, "k": 100.0 }
                },
                "w": 100,
                "h": 100
              }
            ]
          }
        ],
        "layers": [
          {
            "ty": 0,
            "nm": "Level1Ref",
            "refId": "comp_level1",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [25.0, 25.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "w": 100,
            "h": 100
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests PrecompLayer with animated Time Remapping (tm) driving internal keyframes. */
  @Test
  fun precompTimeRemapping() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "assets": [
          {
            "id": "comp_rotating_bar",
            "layers": [
              {
                "ty": 4,
                "nm": "RotatingBar",
                "ind": 1,
                "ip": 0,
                "op": 30,
                "ks": {
                  "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
                  "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
                  "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
                  "r": {
                    "a": 1,
                    "k": [
                      { "t": 0, "s": [0.0] },
                      { "t": 30, "s": [180.0] }
                    ]
                  },
                  "o": { "a": 0, "k": 100.0 }
                },
                "shapes": [
                  {
                    "ty": "gr",
                    "nm": "BarShape",
                    "it": [
                      {
                        "ty": "rc",
                        "p": { "a": 0, "k": [0.0, 0.0] },
                        "s": { "a": 0, "k": [60.0, 14.0] },
                        "r": { "a": 0, "k": 0.0 }
                      },
                      {
                        "ty": "fl",
                        "c": { "a": 0, "k": [0.9, 0.3, 0.4, 1.0] },
                        "o": { "a": 0, "k": 100.0 }
                      },
                      {
                        "ty": "tr",
                        "p": { "a": 0, "k": [0.0, 0.0] },
                        "a": { "a": 0, "k": [0.0, 0.0] },
                        "s": { "a": 0, "k": [100.0, 100.0] },
                        "r": { "a": 0, "k": 0.0 },
                        "o": { "a": 0, "k": 100.0 }
                      }
                    ]
                  }
                ]
              }
            ]
          }
        ],
        "layers": [
          {
            "ty": 0,
            "nm": "TimeRemappedPrecomp",
            "refId": "comp_rotating_bar",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "tm": {
              "a": 1,
              "k": [
                { "t": 0, "s": [0.0] },
                { "t": 15, "s": [0.5] },
                { "t": 30, "s": [1.0] }
              ]
            },
            "ks": {
              "p": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "w": 100,
            "h": 100
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json) {
      captureFrame(frame = 0f)
      captureFrame(frame = 15f)
      captureFrame(frame = 30f)
    }
  }

  /** Tests ImageLayer (ty: 2) rendering an embedded Base64 bitmap. */
  @Test
  fun imageLayerBase64() {
    val pngDataUrl =
      "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="

    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "assets": [
          {
            "id": "image_1x1",
            "w": 60,
            "h": 60,
            "p": "$pngDataUrl",
            "e": 1
          }
        ],
        "layers": [
          {
            "ty": 2,
            "nm": "ImageLayer",
            "refId": "image_1x1",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [20.0, 20.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 85.0 }
            }
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests TextLayer (ty: 5) rendering vector character glyphs and document styling. */
  @Test
  fun textLayerVectorGlyphs() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "fonts": {
          "list": [
            {
              "fName": "CustomGlyphFont",
              "fFamily": "CustomGlyphFont",
              "fStyle": "Regular",
              "ascent": 75.0
            }
          ]
        },
        "chars": [
          {
            "ch": "H",
            "fFamily": "CustomGlyphFont",
            "style": "Regular",
            "size": 100.0,
            "w": 60.0,
            "data": {
              "shapes": [
                {
                  "ty": "gr",
                  "nm": "Glyph_H",
                  "it": [
                    {
                      "ty": "rc",
                      "p": { "a": 0, "k": [10.0, 30.0] },
                      "s": { "a": 0, "k": [10.0, 60.0] },
                      "r": { "a": 0, "k": 0.0 }
                    },
                    {
                      "ty": "rc",
                      "p": { "a": 0, "k": [50.0, 30.0] },
                      "s": { "a": 0, "k": [10.0, 60.0] },
                      "r": { "a": 0, "k": 0.0 }
                    },
                    {
                      "ty": "rc",
                      "p": { "a": 0, "k": [30.0, 30.0] },
                      "s": { "a": 0, "k": [30.0, 10.0] },
                      "r": { "a": 0, "k": 0.0 }
                    },
                    {
                      "ty": "tr",
                      "p": { "a": 0, "k": [0.0, 0.0] },
                      "a": { "a": 0, "k": [0.0, 0.0] },
                      "s": { "a": 0, "k": [100.0, 100.0] },
                      "r": { "a": 0, "k": 0.0 },
                      "o": { "a": 0, "k": 100.0 }
                    }
                  ]
                }
              ]
            }
          }
        ],
        "layers": [
          {
            "ty": 5,
            "nm": "TextLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "t": {
              "d": {
                "k": [
                  {
                    "s": {
                      "t": "H",
                      "s": 70.0,
                      "f": "CustomGlyphFont",
                      "j": 2,
                      "tr": 0.0,
                      "lh": 70.0,
                      "fc": [0.1, 0.7, 0.9, 1.0]
                    },
                    "t": 0.0
                  }
                ]
              }
            }
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  /** Tests TextLayer with multiline text and custom line height. */
  @Test
  fun textLayerMultiline() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "fonts": {
          "list": [
            {
              "fName": "CustomGlyphFont",
              "fFamily": "CustomGlyphFont",
              "fStyle": "Regular",
              "ascent": 75.0
            }
          ]
        },
        "chars": [
          {
            "ch": "H",
            "fFamily": "CustomGlyphFont",
            "style": "Regular",
            "size": 100.0,
            "w": 60.0,
            "data": {
              "shapes": [
                {
                  "ty": "gr",
                  "nm": "Glyph_H",
                  "it": [
                    {
                      "ty": "rc",
                      "p": { "a": 0, "k": [10.0, 30.0] },
                      "s": { "a": 0, "k": [10.0, 60.0] },
                      "r": { "a": 0, "k": 0.0 }
                    },
                    {
                      "ty": "rc",
                      "p": { "a": 0, "k": [50.0, 30.0] },
                      "s": { "a": 0, "k": [10.0, 60.0] },
                      "r": { "a": 0, "k": 0.0 }
                    },
                    {
                      "ty": "rc",
                      "p": { "a": 0, "k": [30.0, 30.0] },
                      "s": { "a": 0, "k": [30.0, 10.0] },
                      "r": { "a": 0, "k": 0.0 }
                    },
                    {
                      "ty": "tr",
                      "p": { "a": 0, "k": [0.0, 0.0] },
                      "a": { "a": 0, "k": [0.0, 0.0] },
                      "s": { "a": 0, "k": [100.0, 100.0] },
                      "r": { "a": 0, "k": 0.0 },
                      "o": { "a": 0, "k": 100.0 }
                    }
                  ]
                }
              ]
            }
          }
        ],
        "layers": [
          {
            "ty": 5,
            "nm": "MultilineTextLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 25.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "t": {
              "d": {
                "k": [
                  {
                    "s": {
                      "t": "H\nH",
                      "s": 35.0,
                      "f": "CustomGlyphFont",
                      "j": 2,
                      "tr": 0.0,
                      "lh": 40.0,
                      "fc": [0.2, 0.8, 0.4, 1.0]
                    },
                    "t": 0.0
                  }
                ]
              }
            }
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  // [SP_LOTTIE_DIFF_03_02] [SP_LOTTIE_MATTE_01_01]
  @Test
  fun trackMatteAlpha() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "MatteSourceCircle",
            "ind": 1,
            "td": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "CircleShape",
                "it": [
                  {
                    "ty": "el",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [44.0, 44.0] }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [1.0, 1.0, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          },
          {
            "ty": 4,
            "nm": "TargetLayer",
            "ind": 2,
            "tt": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "RectShape",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [72.0, 72.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.9, 0.2, 0.3, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  // [SP_LOTTIE_DIFF_03_02] [SP_LOTTIE_MATTE_01_01]
  @Test
  fun trackMatteLuma() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "LumaMaskSource",
            "ind": 1,
            "td": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "GradientRect",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [80.0, 80.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "gf",
                    "t": 1,
                    "o": { "a": 0, "k": 100.0 },
                    "r": 1,
                    "s": { "a": 0, "k": [-40.0, 0.0] },
                    "e": { "a": 0, "k": [40.0, 0.0] },
                    "g": {
                      "p": 2,
                      "k": [
                        0.0, 0.0, 0.0, 0.0,
                        1.0, 1.0, 1.0, 1.0
                      ]
                    }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          },
          {
            "ty": 4,
            "nm": "TargetLayer",
            "ind": 2,
            "tt": 3,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "SolidRect",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [80.0, 80.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [0.2, 0.6, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json, expectedFailure = true)
  }

  // [SP_LOTTIE_DIFF_03_02] [SP_LOTTIE_SHAPES_03_01]
  @Test
  fun extendedModifiersZigZagPuckerBloat() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "ModifiersLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "DeformedRectGroup",
                "it": [
                  {
                    "ty": "rc",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [54.0, 54.0] },
                    "r": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "zz",
                    "nm": "ZigZag",
                    "s": { "a": 0, "k": 6.0 },
                    "r": { "a": 0, "k": 2.0 },
                    "pt": 1
                  },
                  {
                    "ty": "pb",
                    "nm": "PuckerBloat",
                    "a": { "a": 0, "k": 40.0 }
                  },
                  {
                    "ty": "fl",
                    "c": { "a": 0, "k": [1.0, 0.25, 0.5, 1.0] },
                    "o": { "a": 0, "k": 100.0 }
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }

  // [SP_LOTTIE_DIFF_03_02] [SP_LOTTIE_SHAPES_03_01]
  @Test
  fun extendedModifiersTwistOffsetPath() {
    val json =
      """
      {
        "v": "5.7.0",
        "fr": 30,
        "ip": 0,
        "op": 30,
        "w": 100,
        "h": 100,
        "layers": [
          {
            "ty": 4,
            "nm": "TwistOffsetLayer",
            "ind": 1,
            "ip": 0,
            "op": 30,
            "ks": {
              "p": { "a": 0, "k": [50.0, 50.0, 0.0] },
              "a": { "a": 0, "k": [0.0, 0.0, 0.0] },
              "s": { "a": 0, "k": [100.0, 100.0, 100.0] },
              "r": { "a": 0, "k": 0.0 },
              "o": { "a": 0, "k": 100.0 }
            },
            "shapes": [
              {
                "ty": "gr",
                "nm": "TwistedStarGroup",
                "it": [
                  {
                    "ty": "sr",
                    "sy": 1,
                    "pt": { "a": 0, "k": 5.0 },
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "or": { "a": 0, "k": 32.0 },
                    "os": { "a": 0, "k": 0.0 },
                    "ir": { "a": 0, "k": 16.0 },
                    "is": { "a": 0, "k": 0.0 }
                  },
                  {
                    "ty": "tw",
                    "nm": "Twist",
                    "a": { "a": 0, "k": 90.0 },
                    "c": { "a": 0, "k": [0.0, 0.0] }
                  },
                  {
                    "ty": "op",
                    "nm": "OffsetPath",
                    "a": { "a": 0, "k": 6.0 },
                    "lj": 1
                  },
                  {
                    "ty": "st",
                    "c": { "a": 0, "k": [0.0, 0.9, 1.0, 1.0] },
                    "o": { "a": 0, "k": 100.0 },
                    "w": { "a": 0, "k": 2.0 },
                    "lc": 2,
                    "lj": 2
                  },
                  {
                    "ty": "tr",
                    "p": { "a": 0, "k": [0.0, 0.0] },
                    "a": { "a": 0, "k": [0.0, 0.0] },
                    "s": { "a": 0, "k": [100.0, 100.0] },
                    "r": { "a": 0, "k": 0.0 },
                    "o": { "a": 0, "k": 100.0 }
                  }
                ]
              }
            ]
          }
        ]
      }
      """
        .trimIndent()

    runLottieDiffTest(json = json)
  }
}

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

package com.google.android.horologist.remotecompose.lottie.renderer.layers

import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemotePaint
import androidx.compose.remote.creation.compose.state.RemotePaintingStyle
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.remote.creation.compose.text.RemoteTypeface
import androidx.compose.runtime.Composable
import com.google.android.horologist.remotecompose.lottie.LocalAnimationSettings
import com.google.android.horologist.remotecompose.lottie.format.FontChar
import com.google.android.horologist.remotecompose.lottie.format.graphicelement.grouping.Transform
import com.google.android.horologist.remotecompose.lottie.format.layer.TextDocument
import com.google.android.horologist.remotecompose.lottie.format.layer.TextJustify
import com.google.android.horologist.remotecompose.lottie.format.layer.TextLayer
import com.google.android.horologist.remotecompose.lottie.format.mask.MaskMode
import com.google.android.horologist.remotecompose.lottie.renderer.NoopStyle
import com.google.android.horologist.remotecompose.lottie.renderer.applyLayerMasks
import com.google.android.horologist.remotecompose.lottie.renderer.applyMatteClip
import com.google.android.horologist.remotecompose.lottie.renderer.createTextMeasurer
import com.google.android.horologist.remotecompose.lottie.renderer.gatherShapes
import com.google.android.horologist.remotecompose.lottie.renderer.inverseTransform
import com.google.android.horologist.remotecompose.lottie.renderer.mergeCompoundPaths
import com.google.android.horologist.remotecompose.lottie.renderer.properties.animateScalar
import com.google.android.horologist.remotecompose.lottie.renderer.splitCodePoints
import com.google.android.horologist.remotecompose.lottie.renderer.transform
import kotlin.math.roundToInt

/** Parses RGB/RGBA float lists `[r, g, b]` or `[r, g, b, a]` into a [RemoteColor]. */
internal fun parseColorFromList(list: List<Float>): RemoteColor {
  if (list.size < 3) return 0xFF000000.toInt().rc
  val r = (list[0].coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
  val g = (list[1].coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
  val b = (list[2].coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
  val a =
    if (list.size >= 4) (list[3].coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255) else 255
  return ((a shl 24) or (r shl 16) or (g shl 8) or b).rc
}

/** A Layer rendering vector typographic text or glyph shapes. */
@Suppress("RestrictedApi") // selectIfLt
@Composable
@RemoteComposable
internal fun TextLayer(
  layer: TextLayer,
  transformStack: List<Transform> = emptyList(),
  matteContext: MatteContext? = null,
  layerVisibility: RemoteFloat = 1f.rf,
) {
  if (layer.hidden?.constantValue == true) {
    return
  }

  val textData = layer.text ?: return
  val docProperty = textData.document ?: return
  val animationSettings = LocalAnimationSettings.current

  // Documents are discrete keyframes. Record each once and select it on the player's timeline.
  val documents = docProperty.keyframes.filter { it.start != null }
  for ((index, keyframe) in documents.withIndex()) {
    val start =
      if (index == 0) 1f.rf
      else animationSettings.currentFrame.isLessThan((keyframe.time ?: 0f).rf).select(0f.rf, 1f.rf)
    val end =
      documents.getOrNull(index + 1)?.let {
        animationSettings.currentFrame.isLessThan((it.time ?: 0f).rf).select(1f.rf, 0f.rf)
      } ?: 1f.rf
    RenderTextDocument(
      layer,
      keyframe.start!!,
      transformStack,
      matteContext,
      layerVisibility * start * end,
    )
  }
}

@Suppress("RestrictedApi") // RemoteCanvas, RemoteDrawScope.remoteCanvas, RemoteFloat.div(Float)
@Composable
@RemoteComposable
private fun RenderTextDocument(
  layer: TextLayer,
  currentDoc: TextDocument,
  transformStack: List<Transform>,
  matteContext: MatteContext?,
  layerVisibility: RemoteFloat,
) {
  val animationSettings = LocalAnimationSettings.current
  val text = currentDoc.text
  if (text.isEmpty()) {
    return
  }

  val updatedTransformStack =
    if (layer.transform != null) transformStack + layer.transform else transformStack

  val layerOpacity =
    (updatedTransformStack.lastOrNull()?.opacity?.let {
      animateScalar(it, animationSettings) / 100f.rf
    } ?: 1f.rf) * layerVisibility

  val fontSize = currentDoc.fontSize
  val tracking = currentDoc.tracking
  val justification = currentDoc.justification

  val fillColor = currentDoc.fillColor?.let { parseColorFromList(it) } ?: 0xFF000000.toInt().rc
  val strokeColor = currentDoc.strokeColor?.let { parseColorFromList(it) }
  val strokeWidth = currentDoc.strokeWidth ?: 0f
  val font = animationSettings.fonts?.list?.firstOrNull { it.name == currentDoc.fontName }
  val fontFamily = font?.family?.takeIf { it.isNotEmpty() } ?: currentDoc.fontName
  val weight = if (font?.style?.contains("bold", ignoreCase = true) == true) 700 else 400
  val isItalic = font?.style?.contains("italic", ignoreCase = true) == true
  val typeface = RemoteTypeface.Named(fontFamily, weight = weight, isItalic = isItalic)

  val fillPaint = RemotePaint {
    this.color = fillColor.copy(alpha = fillColor.alpha * layerOpacity)
    this.textSize = fontSize.rf
    this.typeface = typeface
  }
  val strokePaint =
    if (strokeColor != null && strokeWidth > 0f) {
      RemotePaint {
        this.color = strokeColor.copy(alpha = strokeColor.alpha * layerOpacity)
        this.style = RemotePaintingStyle.Stroke
        this.strokeWidth = strokeWidth.rf
        this.textSize = fontSize.rf
        this.typeface = typeface
      }
    } else {
      null
    }

  val charsMap = animationSettings.chars
  val hasMasks = layer.masksProperties.any { it.mode != MaskMode.None && it.path != null }
  val needsSave = matteContext != null || hasMasks
  val effectSurface = LocalEffectSurface.current

  RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
    remoteCanvas.withContainingSurface(effectSurface) {
      if (needsSave) {
        remoteCanvas.save()
      }

      if (matteContext != null) {
        applyMatteClip(matteContext, animationSettings, remoteCanvas)
      }

      if (hasMasks) {
        for (transform in updatedTransformStack) {
          transform(transform, null, animationSettings, remoteCanvas)
        }
        applyLayerMasks(layer.masksProperties, animationSettings, remoteCanvas)
        for (transform in updatedTransformStack.reversed()) {
          inverseTransform(transform, animationSettings, remoteCanvas)
        }
      }

      for (transform in updatedTransformStack) {
        remoteCanvas.save()
        transform(transform, null, animationSettings, remoteCanvas)
      }

      // Render glyph vector shapes if chars are available in animation
      run {
        val lines = text.split(Regex("\r\n|\r|\n"))
        val effLineHeight = currentDoc.lineHeight ?: (fontSize * 1.2f)
        val baselineShift = currentDoc.baselineShift ?: 0f
        val strokeOverFill = currentDoc.strokeOverFill ?: true

        for ((lineIndex, lineText) in lines.withIndex()) {
          var totalLineWidth = 0f
          val glyphs = mutableListOf<Triple<String, FontChar?, Float>>()
          val measurer = createTextMeasurer(fontFamily, weight, isItalic, fontSize)
          val characters = splitCodePoints(lineText)
          for ((index, ch) in characters.withIndex()) {
            val fontChar = charsMap.firstOrNull {
              it.character == ch &&
                (it.family == fontFamily || it.family.isEmpty()) &&
                (it.style.isEmpty() || font == null || it.style == font.style)
            }
            val width =
              if (fontChar != null) {
                fontChar.width * (fontSize / 100f)
              } else measurer(ch)
            val advance =
              width + if (index < characters.lastIndex) tracking * (fontSize / 1000f) else 0f
            glyphs.add(Triple(ch, fontChar, advance))
            totalLineWidth += advance
          }

          var currentX =
            when (justification) {
              TextJustify.Right,
              TextJustify.JustifyWithLastLineRight -> -totalLineWidth
              TextJustify.Center,
              TextJustify.JustifyWithLastLineCenter -> -totalLineWidth / 2f
              else -> 0f
            }
          val currentY = (lineIndex * effLineHeight) - baselineShift

          for ((character, fontChar, advance) in glyphs) {
            if (fontChar == null) {
              if (!strokeOverFill && strokePaint != null)
                remoteCanvas.drawText(character.rs, currentX.rf, currentY.rf, strokePaint)
              remoteCanvas.drawText(character.rs, currentX.rf, currentY.rf, fillPaint)
              if (strokeOverFill && strokePaint != null)
                remoteCanvas.drawText(character.rs, currentX.rf, currentY.rf, strokePaint)
              currentX += advance
              continue
            }
            val fontScale = fontSize / 100f
            val shapes = fontChar.shapeData?.shapes.orEmpty()
            if (shapes.isNotEmpty()) {
              val glyphGroups =
                gatherShapes(shapes, animationSettings, inheritedStyle = NoopStyle())
              remoteCanvas.save()
              remoteCanvas.translate(currentX.rf, currentY.rf)
              remoteCanvas.scale(fontScale.rf, fontScale.rf)

              for (group in glyphGroups) {
                if (group.style is NoopStyle) {
                  val mergedShapes = mergeCompoundPaths(group.shapes)
                  if (strokeOverFill) {
                    usePaint(fillPaint) {
                      for (shape in mergedShapes) {
                        shape.draw(this, remoteCanvas, layerOpacity)
                      }
                    }
                    if (strokePaint != null) {
                      usePaint(strokePaint) {
                        for (shape in mergedShapes) {
                          shape.draw(this, remoteCanvas, layerOpacity)
                        }
                      }
                    }
                  } else {
                    if (strokePaint != null) {
                      usePaint(strokePaint) {
                        for (shape in mergedShapes) {
                          shape.draw(this, remoteCanvas, layerOpacity)
                        }
                      }
                    }
                    usePaint(fillPaint) {
                      for (shape in mergedShapes) {
                        shape.draw(this, remoteCanvas, layerOpacity)
                      }
                    }
                  }
                } else {
                  val groupPaint = group.style.getPaint(layerOpacity)
                  usePaint(groupPaint) {
                    for (shape in group.shapes) {
                      shape.draw(this, remoteCanvas, layerOpacity)
                    }
                  }
                }
              }
              remoteCanvas.restore()
            }
            currentX += advance
          }
        }
      }

      for (transform in updatedTransformStack) {
        remoteCanvas.restore()
      }

      if (needsSave) {
        remoteCanvas.restore()
      }
    }
  }
}

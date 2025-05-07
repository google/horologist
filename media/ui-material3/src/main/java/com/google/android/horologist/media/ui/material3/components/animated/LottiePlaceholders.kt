/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.android.horologist.media.ui.material3.components.animated

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal object LottiePlaceholders {
    public val Next: ImageVector
        get() {
            if (_next != null) {
                return _next!!
            }
            _next = materialIcon(name = "Next") {
                materialPath {
                    moveTo(91f, 202.8f)
                    curveToRelative(-1.70f, 00f, -3.30f, -0.40f, -4.80f, -1.30f)
                    curveToRelative(-3.10f, -1.70f, -4.90f, -50f, -4.90f, -8.50f)
                    verticalLineToRelative(-86f)
                    curveToRelative(00f, -3.50f, 1.90f, -6.80f, 4.90f, -8.50f)
                    curveToRelative(3.10f, -1.70f, 6.80f, -1.70f, 9.80f, 0.10f)
                    lineToRelative(71.7f, 43f)
                    curveToRelative(2.90f, 1.80f, 4.70f, 4.90f, 4.70f, 8.40f)
                    reflectiveCurveToRelative(-1.8f, 6.6f, -4.7f, 8.4f)
                    lineToRelative(-71.7f, 43f)
                    curveToRelative(-1.50f, 0.90f, -3.30f, 1.40f, -50f, 1.40f)
                    close()
                    moveTo(100.7f, 124.2f)
                    verticalLineToRelative(51.6f)
                    lineToRelative(43f, -25.8f)
                    lineToRelative(-43f, -25.8f)
                    close()
                }
                materialPath {
                    moveTo(195f, 197.5f)
                    verticalLineToRelative(-95f)
                    curveToRelative(00f, -4.40f, 3.60f, -80f, 80f, -80f)
                    horizontalLineToRelative(4f)
                    curveToRelative(4.40f, 00f, 80f, 3.60f, 80f, 80f)
                    verticalLineToRelative(95f)
                    curveToRelative(00f, 4.40f, -3.60f, 80f, -80f, 80f)
                    horizontalLineToRelative(-4f)
                    curveToRelative(-4.40f, 00f, -80f, -3.60f, -80f, -80f)
                    close()
                }
            }
            return _next!!
        }

    private var _next: ImageVector? = null

    public val Play: ImageVector
        get() {
            if (_play != null) {
                return _play!!
            }
            _play = materialIcon(name = "Play") {
                materialPath {
                    moveTo(225.9f, 163f)
                    lineToRelative(-104.2f, 65.9f)
                    curveToRelative(-17.20f, 10.20f, -26.30f, 2.20f, -26.10f, -9.60f)
                    curveToRelative(00f, -60f, 00f, -141.30f, 00f, -141.30f)
                    curveToRelative(00f, -15.90f, 8.50f, -20.40f, 230f, -12.60f)
                    curveToRelative(20f, 0.80f, 220f, 13.70f, 44.60f, 27.70f)
                    reflectiveCurveToRelative(56.3f, 34.4f, 62.3f, 38.9f)
                    curveToRelative(14.40f, 9.90f, 14.80f, 20.80f, 0.40f, 31.20f)
                    close()
                }
            }
            return _play!!
        }

    private var _play: ImageVector? = null

    public val Pause: ImageVector
        get() {
            if (_pause != null) {
                return _pause!!
            }
            _pause = materialIcon(name = "Pause") {
                materialPath {
                    moveTo(95.9f, 55.2f)
                    horizontalLineTo(96f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 123f, 82.2f)
                    verticalLineTo(217.2f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 96f, 244.2f)
                    horizontalLineTo(95.9f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 68.9f, 217.2f)
                    verticalLineTo(82.2f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 95.9f, 55.2f)
                    close()
                }
                materialPath {
                    moveTo(204f, 55.2f)
                    horizontalLineTo(204.1f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 231.1f, 82.2f)
                    verticalLineTo(217.2f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 204.1f, 244.2f)
                    horizontalLineTo(204f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 177f, 217.2f)
                    verticalLineTo(82.2f)
                    arcTo(27f, 27f, 0f, isMoreThanHalf = false, isPositiveArc = true, 204f, 55.2f)
                    close()
                }
            }
            return _pause!!
        }

    private var _pause: ImageVector? = null
}

internal fun Color.opacityForLottieAnimation() = (this.alpha * 100).toInt()

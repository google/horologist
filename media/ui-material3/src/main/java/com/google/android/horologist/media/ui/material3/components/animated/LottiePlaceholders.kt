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
                    moveTo(7.28f, 16.224f)
                    arcToRelative(0.73f, 0.73f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.384f, -0.104f)
                    arcToRelative(0.77f, 0.77f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.392f, -0.68f)
                    verticalLineTo(8.56f)
                    curveToRelative(00f, -0.280f, 0.1520f, -0.5440f, 0.3920f, -0.680f)
                    curveToRelative(0.2480f, -0.1360f, 0.5440f, -0.1360f, 0.7840f, 0.0080f)
                    lineToRelative(5.736f, 3.44f)
                    curveToRelative(0.2320f, 0.1440f, 0.3760f, 0.3920f, 0.3760f, 0.6720f)
                    reflectiveCurveToRelative(-0.144f, 0.528f, -0.376f, 0.672f)
                    lineToRelative(-5.736f, 3.44f)
                    curveToRelative(-0.120f, 0.0720f, -0.2640f, 0.1120f, -0.40f, 0.1120f)
                    close()
                    moveToRelative(0.776f, -6.288f)
                    verticalLineToRelative(4.128f)
                    lineTo(11.496f, 12f)
                    lineToRelative(-3.44f, -2.064f)
                    close()
                    moveTo(15.6f, 15.8f)
                    verticalLineTo(8.2f)
                    arcToRelative(0.64f, 0.64f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.64f, -0.64f)
                    horizontalLineToRelative(0.32f)
                    arcToRelative(0.64f, 0.64f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.64f, 0.64f)
                    verticalLineToRelative(7.6f)
                    arcToRelative(0.64f, 0.64f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.64f, 0.64f)
                    horizontalLineToRelative(-0.32f)
                    arcToRelative(0.64f, 0.64f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.64f, -0.64f)
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
                    moveTo(18.072f, 13.04f)
                    lineToRelative(-8.336f, 5.272f)
                    curveToRelative(-1.3760f, 0.8160f, -2.1040f, 0.1760f, -2.0880f, -0.7680f)
                    verticalLineTo(6.24f)
                    curveToRelative(00f, -1.2720f, 0.680f, -1.6320f, 1.840f, -1.0080f)
                    curveToRelative(0.160f, 0.0640f, 1.760f, 1.0960f, 3.5680f, 2.2160f)
                    lineToRelative(4.984f, 3.112f)
                    curveToRelative(1.1520f, 0.7920f, 1.1840f, 1.6640f, 0.0320f, 2.4960f)
                    verticalLineToRelative(-0.016f)
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
                    moveTo(7.68f, 4.416f)
                    horizontalLineToRelative(-0.008f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, -2.16f, 2.16f)
                    verticalLineToRelative(10.8f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, 2.16f, 2.16f)
                    horizontalLineToRelative(0.008f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, 2.16f, -2.16f)
                    verticalLineToRelative(-10.8f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, -2.16f, -2.16f)
                    close()
                    moveTo(16.328f, 4.416f)
                    horizontalLineToRelative(-0.008f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, -2.16f, 2.16f)
                    verticalLineToRelative(10.8f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, 2.16f, 2.16f)
                    horizontalLineToRelative(0.008f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, 2.16f, -2.16f)
                    verticalLineToRelative(-10.8f)
                    arcToRelative(2.16f, 2.16f, 0.0f, false, false, -2.16f, -2.16f)
                    close()
                }
            }
            return _pause!!
        }

    private var _pause: ImageVector? = null
}

internal fun Color.opacityForLottieAnimation() = (this.alpha * 100).toInt()

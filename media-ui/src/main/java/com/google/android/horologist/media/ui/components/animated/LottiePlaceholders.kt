/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.android.horologist.media.ui.components.animated

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

internal object LottiePlaceholders {
    public val Next: ImageVector
        get() {
            if (_next != null) {
                return _next!!
            }
            _next = materialIcon(name = "Next") {
                materialPath {
                    moveTo(5f, 6f)
                    curveTo(5f, 6f, 15f, 12f, 15f, 12f)
                    curveTo(15f, 12f, 5f, 18f, 5f, 18f)
                    curveTo(5f, 18f, 5f, 6f, 5f, 6f)
                    close()
                }
                materialPath {
                    moveTo(17f, 6f)
                    curveTo(17f, 6f, 19f, 6f, 19f, 6f)
                    curveTo(19f, 6f, 19f, 10f, 19f, 12f)
                    curveTo(19f, 14f, 19f, 18f, 19f, 18f)
                    curveTo(19f, 18f, 17f, 18f, 17f, 18f)
                    curveTo(17f, 18f, 17f, 14.02664f, 17f, 12.04f)
                    curveTo(17f, 10.02664f, 17f, 6f, 17f, 6f)
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
                    moveToRelative(12.644f, 16.648f)
                    curveToRelative(0.0f, 0.0f, 7.242f, -4.637f, 7.242f, -4.637f)
                    curveToRelative(0.0f, 0.0f, 0.0f, -0.005f, 0.0f, -0.005f)
                    curveToRelative(0.0f, 0.0f, -7.265f, -4.669f, -7.265f, -4.669f)
                    curveToRelative(0.0f, 0.0f, 0.022f, 9.311f, 0.022f, 9.311f)
                    close()
                }
                materialPath {
                    moveToRelative(7.455f, 4.124f)
                    curveToRelative(0.0f, 0.0f, 0.0f, 15.753f, 0.0f, 15.753f)
                    curveToRelative(0.0f, 0.0f, 5.492f, -3.421f, 5.492f, -3.421f)
                    curveToRelative(0.0f, 0.0f, 0.001f, -8.909f, 0.001f, -8.909f)
                    curveToRelative(0.0f, 0.0f, -5.493f, -3.422f, -5.493f, -3.422f)
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
                    moveToRelative(13.931f, 18.993f)
                    curveToRelative(0.0f, 0.0f, 1.889f, 0.001f, 4.019f, 0.001f)
                    curveToRelative(0.0f, -2.033f, 0.003f, -12.666f, 0.003f, -13.989f)
                    curveToRelative(-1.692f, 0.0f, -4.02f, 0.006f, -4.02f, 0.006f)
                    curveToRelative(0.0f, 0.0f, -0.001f, 13.982f, -0.001f, 13.982f)
                    close()
                }
                materialPath {
                    moveToRelative(6.047f, 5.009f)
                    curveToRelative(0.0f, 0.0f, 0.0f, 13.984f, 0.0f, 13.984f)
                    curveToRelative(0.0f, 0.0f, 3.968f, 0.001f, 3.968f, 0.001f)
                    curveToRelative(0.0f, 0.0f, 0.001f, -13.983f, 0.001f, -13.983f)
                    curveToRelative(0.0f, 0.0f, -3.969f, -0.002f, -3.969f, -0.002f)
                    close()
                }
            }
            return _pause!!
        }

    private var _pause: ImageVector? = null
}
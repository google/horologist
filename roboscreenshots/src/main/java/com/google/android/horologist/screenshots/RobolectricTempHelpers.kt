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

package com.google.android.horologist.screenshots

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderNode
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.compose.ui.window.DialogWindowProvider
import androidx.test.platform.graphics.HardwareRendererCompat
import org.robolectric.util.ReflectionHelpers
import java.nio.ByteOrder
import java.nio.IntBuffer

// From https://cs.android.com/android/platform/superproject/main/+/main:external/robolectric/shadows/framework/src/main/java/org/robolectric/shadows/ShadowPixelCopy.java;bpv=1
internal object RobolectricTempHelpers {
    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun capture(view: View, bitmap: Bitmap) {
        withDrawingEnabled {
            val dialogWindow = findDialogWindowProviderInParent(view)?.window
            val windowToUse = dialogWindow ?: view.context.getActivityWindow()

            generateBitmapUsingHardwareRenderNative(windowToUse.decorView, bitmap)
        }
    }

    internal fun findDialogWindowProviderInParent(view: View): DialogWindowProvider? {
        if (view is DialogWindowProvider) {
            return view
        }
        val parent = view.parent ?: return null
        if (parent is View) {
            return findDialogWindowProviderInParent(parent)
        }
        return null
    }

    private fun Context.getActivityWindow(): Window {
        fun Context.getActivity(): Activity {
            return when (this) {
                is Activity -> this
                is ContextWrapper -> this.baseContext.getActivity()
                else -> throw IllegalStateException(
                    "Context is not an Activity context, but a ${javaClass.simpleName} context. " +
                        "An Activity context is required to get a Window instance"
                )
            }
        }
        return getActivity().window
    }

    private fun <R> withDrawingEnabled(block: () -> R): R {
        val wasDrawingEnabled = HardwareRendererCompat.isDrawingEnabled()
        try {
            if (!wasDrawingEnabled) {
                HardwareRendererCompat.setDrawingEnabled(true)
            }
            return block.invoke()
        } finally {
            if (!wasDrawingEnabled) {
                HardwareRendererCompat.setDrawingEnabled(false)
            }
        }
    }

    /**
     * Generates a bitmap given the current view using HardwareRenderer with native graphics calls.
     * Requires API 31+ (S).
     *
     *
     * This code mirrors the behavior of LayoutLib's RenderSessionImpl.renderAndBuildResult(); see
     * https://googleplex-android.googlesource.com/platform/frameworks/layoutlib/+/refs/heads/master-layoutlib-native/bridge/src/com/android/layoutlib/bridge/impl/RenderSessionImpl.java#573
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun generateBitmapUsingHardwareRenderNative(view: View, destBitmap: Bitmap) {
        val width = view.width
        val height = view.height
        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1)
        val renderer = HardwareRenderer()
        renderer.setSurface(imageReader.surface)
        val nativeImage: Image = imageReader.acquireNextImage()

//        setupRendererShadowProperties(renderer, view)

        val node = getRenderNode(view)
        renderer.setContentRoot(node)
        renderer.createRenderRequest().syncAndDraw()
        val renderPixels = IntArray(width * height)
        val planes: Array<Image.Plane> = nativeImage.getPlanes()
        val srcBuff = planes[0].buffer.order(ByteOrder.BIG_ENDIAN).asIntBuffer()
        val dstBuff = IntBuffer.wrap(renderPixels)
        val len = srcBuff.remaining()
        // Read source RGBA and write dest as ARGB.
        for (j in 0 until len) {
            val s = srcBuff.get()
            val a = s shl 24
            val rgb = s ushr 8
            dstBuff.put(a + rgb)
        }
        destBitmap.setPixels(
            renderPixels,  /* offset= */
            0,  /* stride= */
            width,  /* x= */
            0,  /* y= */
            0,
            width,
            height
        )
    }

    private fun getRenderNode(view: View): RenderNode {
        return ReflectionHelpers.callInstanceMethod(view, "updateDisplayListIfDirty")
    }
}
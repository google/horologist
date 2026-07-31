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

package com.google.android.horologist.media.ui

/**
 * A coil model these previews can actually load.
 *
 * The previews used to pass unresolvable models — the literal string `"artworkUri"`, or
 * `https://www.example.com/album1.png` — and rely on the `placeholder` painter for their pixels.
 * That worked only because coil's `LocalInspectionMode` branch painted the placeholder and returned
 * without loading anything. An offscreen renderer that runs the real request instead draws nothing
 * at all for those models, and a URL is the wrong answer anyway: previews whose pixels depend on
 * live egress aren't reproducible, and the renderer refuses `http(s)://` models on purpose.
 *
 * An asset ships with the module, resolves through the same `AsyncImagePainter` path production
 * uses, and renders identically on every machine. Previews that are *about* the artwork-less state
 * (`…NoArtwork`) still pass `null` — that case is the one their placeholder exists for.
 */
internal const val SampleArtworkUri: String = "file:///android_asset/sample_artwork.png"

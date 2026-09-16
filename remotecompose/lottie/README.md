# Horologist RemoteCompose Lottie (`:remotecompose:lottie`)

`:remotecompose:lottie` compiles Lottie (`Bodymovin`) JSON animations directly into AndroidX **RemoteCompose** wire documents (`RemoteCanvas` drawing operations and `RemoteFloat` expression graphs). Unlike traditional CPU-bound Lottie players that re-evaluate the animation AST on the host UI thread every frame, `:remotecompose:lottie` records the animation **once** at creation time so the RemoteCompose player can evaluate keyframes, easing curves, transforms, and path deformations directly on the remote surface.

---

## Usage

```kotlin
val animation = remember { Animation.decodeFromString(lottieJsonString) }

RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
    with(animation) {
        draw(
            animationSettings = LottieSettings(
                progress = { remoteProgressFloat },
            )
        )
    }
}
```

---

## Supported Lottie Features

### Layers & Compositing
- **Precomposition Layers (`ty = 0`)**: Nested `assets` lookup by `refId`, time remapping (`tm`), time stretch (`sr`), start time (`st`), `ip`/`op` frame clipping, and cycle detection.
- **Solid Color Layers (`ty = 1`)**: Hex color (`sc`) and dimensions (`sw`, `sh`).
- **Image Layers (`ty = 2`)**: Embedded Base64 data URIs (`data:image/png;base64,...`, `data:image/jpeg;base64,...`) and asset scaling.
- **Null Layers (`ty = 3`)**: Non-rendering parent transform anchors (`parent` / `ind` hierarchy).
- **Shape Layers (`ty = 4`)**: Full group hierarchy (`gr`), paths (`sh`), rectangles (`rc`), ellipses (`el`), polystars (`sr`), fills (`fl`), strokes (`st`, dashes `d`), and linear/radial gradients (`gf`, `gs`).
- **Text Layers (`ty = 5`)**: Keyframed text documents (`t.d.k`), justification, tracking (`tr`), line height (`lh`), baseline shift (`ls`), vector glyph shapes (`chars`), and system font fallback.
- **Track Mattes (`tt` / `td` / `tp`)**: `Alpha` (`1`) and `InvertedAlpha` (`2`) masking.
- **Blend Modes (`bm`)**: Standard Porter-Duff and separable blend modes (`0..15`).

### Shape Modifiers
- **Trim Paths (`tm`)**: Simultaneous (`m = 1`) and individual (`m = 2`) path trimming with animated `start`, `end`, and `offset`.
- **Rounded Corners (`rd`)**: Static and animated corner rounding on polygonal and Bezier contours.
- **Repeater (`rp`)**: Static and animated copy counts (`c`), offset (`o`), cumulative transform (`tr`), and start/end opacity ramps (`so`/`eo`).
- **Merge Paths (`mm`)**: Boolean path combinations (`Merge`, `Add`, `Subtract`, `Intersect`, `ExcludeIntersections`) via RemoteCompose `combinePath`.
- **Pucker / Bloat (`pb`)**: Static and animated inward/outward vertex and tangent bowing.
- **Twist (`tw`)**: Static and animated spiral deformation around a center point.
- **Zig Zag (`zz`)**: Corner (`pt = 1`) and smooth (`pt = 2`) ridge subdivision.
- **Offset Path (`op`)**: Miter (`lj = 1`), Round (`lj = 2`), and Bevel (`lj = 3`) contour offsetting for static and fixed-topology animated paths.

---

## Unsupported Features & Fail-Fast Limitations

Because RemoteCompose compiles animations into a fixed-structure wire document (`PathData`, `PathCreate`, `PathAppend`, `PathCombine`, and `RemoteFloat` RPN expressions) during a single recording pass without a general-purpose runtime CPU geometry kernel, certain features are unsupported and **fail noisily at recording time** (`IllegalArgumentException` / `IllegalStateException`) rather than silently misrendering:

1. **Dynamic Self-Intersection Removal / Variable-Topology Offsetting on Live-Animated Curves (`OffsetPath`)**:
   - **Why it is unsupported in full analytical mode**: When an `OffsetPath` (`"ty": "op"`) modifier is applied to a contour whose vertex/segment topology varies across playback frames (for example, a `PolyStar` with an animated point count `pt`, a `RoundedCorners` modifier whose corner splits activate/deactivate dynamically, or a variable-topology padded curve with live-animated control points or offset amount), the number of resulting offset sub-contours, inflection splits, and self-intersection loops changes dynamically every frame. Resolving arbitrary planar-graph self-intersections (`Path.op` / winding-number loop pruning) on variable-topology curves at playback time is not expressible without a dedicated native `PathOffset` / `PathSimplify` wire opcode in `androidx.compose.remote.core`, and running 3-piece inflection splitting plus 10-step cubic-cubic bisection across all padded slots exceeds the per-frame `Limits.MAX_OP_COUNT = 20_000` RPN instruction budget.
   - **Default Graceful Degradation (`strictOffsetTopology = false`)**: By default, `OffsetPath` automatically degrades these contours to a single-piece Tiller-Hanson hodograph offset with closed-form 2×2 tangent-ray joins and active-edge scanning (~23× fewer RPN operations per segment). This renders convex and moderate offsets accurately within the player's instruction budget, while deep inward self-intersections retain unpruned corner loops rather than clipping interior cubic crossings.
   - **Strict Fail-Fast Mode (`strictOffsetTopology = true`)**: When `LottieSettings(strictOffsetTopology = true)` is enabled (or `requireSupportedOffsetTopology` is invoked directly), the renderer fails noisily at recording time with `IllegalArgumentException("OffsetPath does not support dynamic variable-topology or self-intersection removal on live-animated curves")`.
2. **Modifiers Downstream of Live `MergePaths` (`RemoteBooleanPath`)**:
   - Applying geometry-rewriting modifiers (`OffsetPath`, `PuckerBloat`, `Twist`, `ZigZag`, `RoundedCorners`) after a live `MergePaths` boolean operation throws `IllegalStateException`, because `combinePath` produces an opaque device-side path ID whose control points cannot be read back into `RemoteFloat` expressions on the wire.
3. **Pre-Recording Validation (`Animation.validateForRecording()`)**:
   - **3D Layers (`ddd = 1`)**: Throws `IllegalArgumentException` (RemoteCompose `RemoteCanvas` is a 2D affine surface).
   - **Auto-Orientation (`ao = 1`)**: Throws `IllegalArgumentException` when combined with unsupported motion paths.
   - **Luminance Track Mattes (`tt = 3`, `tt = 4`)**: Throws `IllegalArgumentException` (only `Alpha` and `InvertedAlpha` mattes are supported).
   - **Hard-Mix Blend Mode (`bm = 17`)**: Throws `IllegalArgumentException`.

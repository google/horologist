# Lottie AST Serialization Layer & Type Mapping

This document describes the design, conventions, and type mappings used in the `remotecompose/lottie` serialization layer (`format/`).

## Architectural Principle: Two-Tier Type System

The Lottie engine is structured into two distinct layers:
1. **AST / Format Layer (`com.google.android.horologist.remotecompose.lottie.format`)**:
   - Represents the parsed Lottie JSON document conforming to the [Lottie 1.0.1 Specification](https://lottie.github.io/lottie-spec/1.0.1/).
   - Uses `kotlinx.serialization` for decoding JSON directly into immutable data structures.
   - Types are optimized for direct mapping to the specification wire format, resilience against variations across exporters (Bodymovin, After Effects, Synfig, Haiku), and slot mapping support (`sid`).
2. **Evaluation / Renderer Layer (`com.google.android.horologist.remotecompose.lottie.renderer`)**:
   - Evaluates keyframes, Bézier curves, layer transforms, time remapping, and clipping hierarchies.
   - Binds and evaluates expressions into Remote Compose primitives (`RemoteFloat`, `RemoteColor`, `RemoteBoolean`, `RemoteInt`).

---

## Wire Format Primitive Type Mapping

The Lottie specification defines distinct primitive types. To ensure zero-allocation streaming and avoid unneeded conversions, the following serialization types and typealiases must be used:

| Lottie Spec Primitive | Specification Reference | Format Type / Typealias | Underlying Type | Serializer | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Float / Number** | [Number](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#number) | `Float` or `SerializableRemoteFloat` | `Float` or `RemoteFloat` | `RemoteFloatSerializer` | Use `Float` for document headers (`fr`, `ip`, `op`, `w`, `h`), asset dimensions, and keyframe frame timestamps (`t`). Use `SerializableRemoteFloat` for animatable property values. |
| **Integer** | [Number](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#number) | `Int` or `SerializableRemoteInt` | `Int` or `RemoteInt` | `RemoteIntSerializer` | Used for layer indices (`ind`, `parent`), blend modes (`bm`), and dimension integers. |
| **Boolean (JSON)** | [Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#boolean) | `Boolean` or `SerializableBoolean` | `Boolean` or `RemoteBoolean` | `BooleanRemoteSerializer` | Standard JSON boolean (`true` / `false`), e.g., layer hidden (`hd`). |
| **Integer Boolean** | [Int Boolean](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#int-boolean) | `SerializableRemoteBoolean` | `RemoteBoolean` | `IntBooleanRemoteSerializer` | Wire integer (`0` = false, `1` = true), e.g., animated flag (`a`), hold keyframe (`h`), auto-orient (`ao`). |
| **Color** | [Color](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#color) | `SerializableRemoteColor` | `RemoteColor` | `RemoteColorSerializer` | 3 (RGB) or 4 (RGBA) component float array in `[0.0, 1.0]`. Auto-normalizes legacy 0-255 values if > 1.0. |
| **2D Point / Vector** | [Vector](https://lottie.github.io/lottie-spec/1.0.1/specs/values/#vector) | `Point` | `Point(x: RemoteFloat, y: RemoteFloat)` | `PointSerializer` | Array of at least 2 floats `[x, y]`. Extra components (e.g. z) are ignored per 2D canvas model. |
| **Keyframe Easing** | [Easing Handle](https://lottie.github.io/lottie-spec/1.0.1/specs/properties/#easing-handle) | `KeyframeEasing` | `KeyframeEasing(x: RemoteFloat, y: RemoteFloat)` | `ScalarKeyframeEasingSerializer` | Temporal cubic Bézier handles (`i` and `o`). Serialized as `{x: ..., y: ...}` with numbers or single-element arrays. |

---

## Animatable Properties (`format/properties/`)

Lottie animatable properties are split into two variants determined by the integer-boolean discriminator `"a"`:
- **`"a": 0` (Static)**: Constant value across all frames.
- **`"a": 1` (Animated)**: Keyframed sequence over time (`k: [...]`), containing frames (`t`), values (`s`), hold flags (`h`), and easing handles (`i`, `o`).

### Supported Properties

1. **Scalar Property (`BaseScalarProperty`)**:
   - `StaticScalarProperty(value: SerializableRemoteFloat)`
   - `AnimatedScalarProperty(keyframes: List<ScalarPropertyKeyframe>)`
2. **Vector Property (`BaseVectorProperty`)**:
   - `StaticVectorProperty(value: List<SerializableRemoteFloat>)`
   - `AnimatedVectorProperty(keyframes: List<VectorPropertyKeyframe>)`
3. **Position Property (`BasePositionProperty`)**:
   - `StaticPositionProperty(value: Point)`
   - `AnimatedPositionProperty(keyframes: List<PositionPropertyKeyframe>)`
   - Keyframes optionally include spatial tangent handles `ti` and `to` for curved motion paths.
4. **Color Property (`BaseColorProperty`)**:
   - `StaticColorProperty(value: SerializableRemoteColor)`
   - `AnimatedColorProperty(keyframes: List<ColorPropertyKeyframe>)`
5. **Shape / Bezier Property (`BaseBezierProperty`)**:
   - `StaticBezierProperty(value: BezierValue)`
   - `AnimatedBezierProperty(keyframes: List<BezierPropertyKeyframe>)`

---

## Assets (`format/asset/Asset.kt`)

Assets defined in the composition's root `assets` array are decoded polymorphically:
- **`PrecompAsset`**: Nested sub-composition identified by `"layers": [...]`. Contains sub-layers and independent timeline frame rate (`fr`).
- **`ImageAsset`**: Image asset with width/height (`w`, `h`), path (`p`), directory (`u`), and embedded flag (`e`). Supports both external references and inline Base64 data URIs (`data:image/png;base64,...`).
- **`AudioAsset`**: Audio clip asset (`"p"`, `"u"`).
- **`UnknownAsset`**: Safe fallback retaining asset `id` for unrecognized asset types.

---

## Markers (`format/Marker.kt`)

Markers define named temporal cues or regions on the animation timeline:
- `name` (`cm`): Cue point name or comment.
- `time` (`tm`): Start frame index.
- `duration` (`dr`): Segment duration in frames (0 for instantaneous cues).

---

## Best Practices & Guidelines

1. **Strict Spec Compliance**: Property and field names must mirror Lottie schema names via `@SerialName("...")` (e.g. `nm`, `hd`, `ty`, `ks`, `k`, `a`, `s`, `i`, `o`, `t`, `h`).
2. **Resilience**: Optional fields should default to spec defaults (`emptyList()`, `null`, `0f`, etc.) to tolerate truncated or exported variants from differing design tools.
3. **No UI Imports in Format Layer**: The `format/` package must never import Android UI or Compose UI types (`androidx.compose.ui.*`). It interacts only with `kotlinx.serialization` and Remote Compose primitives (`androidx.compose.remote.creation.compose.state.*`).
4. **Clean Imports**: Never use fully qualified types in source code; declare explicit imports.

---

## Layers & Compositing (`format/layer/`)

Lottie visual layers form an ordered compositing stack rendered bottom-up (reversed layer index):
- **`PrecompLayer` (`ty`: 0)**: References a `PrecompAsset` via `refId`. Instantiates nested compositions with isolated frame bounds, recursive cycle detection (`activePrecomps`), and nested transform hierarchies.
- **`SolidColorLayer` (`ty`: 1)**: Renders a solid color rectangle with integer dimensions (`sw`, `sh`) filled with `sc`.
- **`NullLayer` (`ty`: 3)**: Non-rendering invisible anchor node used to establish parent-child transformation chains.
- **`ShapeLayer` (`ty`: 4)**: Hosts vector paths, shapes, styling attributes, and group modifiers.

### Layer Timing & Stretch
- **`ip` / `op`**: Start and end frame defining the temporal visibility interval $[ip, op)$.
- **`st`**: Layer start time offset on the parent timeline.
- **`sr`**: Time stretch factor. Local layer frame is computed as:
  $$t_{\text{local}} = \frac{t - st}{sr}$$
  When $sr = 0$, a fallback factor of $1.0$ is enforced to prevent division by zero.
- **Boundary Padding**: Layers whose `op` reaches or exceeds the composition end frame are extended by $0.01$ frames to ensure visibility at $progress = 1.0f$.

### Track Mattes (`MatteMode`, `MatteContext`)
Track mattes define masking between adjacent layers or explicitly paired layers (`tp` / `td`):
- `MatteMode.Alpha` (1): Masks using source alpha channel.
- `MatteMode.InvertedAlpha` (2): Masks using inverted source alpha channel.
- `MatteMode.Luma` (3): Masks using source luminance.
- `MatteMode.InvertedLuma` (4): Masks using inverted source luminance.
- Source matte layers (`td = 1` or referenced as matte parent) are suppressed from direct drawing and routed via `MatteContext` to their target layer.

---

## Shape Modifiers (`format/graphicelement/modifiers/`)

Shape modifiers transform or combine sibling graphic elements within a shape group:
- **`TrimPath` (`ty`: `"tm"`)**: Trims open/closed curves using start (`s`), end (`e`), offset (`o`), and trim mode (`m`: `Simultaneously` = 1, `Individually` = 2).
- **`RoundedCorners` (`ty`: `"rd"`)**: Rounds sharp vertices of preceding shapes by radius (`r`).
- **`MergePaths` (`ty`: `"mm"`)**: Applies boolean path operations (`mm`: `Merge` = 1, `Add` = 2, `Subtract` = 3, `Intersect` = 4, `ExcludeIntersections` = 5).
- **`Repeater` (`ty`: `"rp"`)**: Duplicates preceding shapes by copies (`c`), offset (`o`), composite order (`m`: `Above` = 1, `Below` = 2), and cumulative transform (`tr`).
- **`OffsetPath` (`ty`: `"op"`)**: Expands or contracts contours by amount (`a`), line join (`lj`), and miter limit (`ml`). Note: dynamic variable-topology contours (e.g., animated `PolyStar` point count or animated `RoundedCorners` activation) and dynamic self-intersection removal on live-animated variable-topology curves are unsupported without a native `PathOffset`/`PathSimplify` wire opcode and fail fast with `IllegalArgumentException`.
- **`PuckerBloat` (`ty`: `"pb"`)**: Pulls vertices inward and tangents outward (or vice versa) by amount (`a`).
- **`Twist` (`ty`: `"tw"`)**: Spirals vertices around center (`c`) by angle (`a`).
- **`ZigZag` (`ty`: `"zz"`)**: Subdivides contours into ridges with size (`s`), ridges per segment (`r`), and point type (`pt`: `Corner` = 1, `Smooth` = 2).
- **`NoStyle` (`ty`: `"no"`) & `UnknownElement`**: Safe placeholders for explicit empty styles or unrecognized custom exporter shapes.

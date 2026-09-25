# Lottie Side-by-Side Render Comparison Report (`lottie-android` vs `rc lottie`)

## Executive Summary
- **Total External Lottie Assets Evaluated**: 24
- **Sampled Progress Frames**: `0.0, 0.5, 1.0`
- **Raw Strict Schema Pass (`Animation.decodeFromString`)**: 21 / 24 (88%)
- **Post-Normalization Visual Parity (`PASS`, FG MAE < 0.06)**: 20 / 24 (83%)
- **Partial Visual Parity (`PARTIAL_PARITY`, FG MAE < 0.18)**: 3 / 24 (13%)
- **Visual Mismatch (`VISUAL_MISMATCH`)**: 1
- **Unrecoverable Parse / Render Errors**: 0

## Asset Comparison Matrix

| # | Asset Name | Resolution | Raw Schema | Visual Status | Mean MAE | FG MAE | Min PSNR (dB) | Detected Features | Schema Cluster | Visual Cluster | Side-by-Side Image |
|---|------------|------------|------------|---------------|----------|--------|---------------|-------------------|----------------|----------------|--------------------|
| 1 | **hamburger_arrow** | `800x600` | `PARSE_ERROR` | `PASS` | `0.0000` | `0.0023` | `68.8` | Expressions, NullLayer, ShapeLayer, TrimPath | Cluster S2 | — | [View PNG](hamburger_arrow_side_by_side.png) |
| 2 | **heart** | `100x100` | `PASS` | `PASS` | `0.0000` | `0.0000` | `91.6` | Expressions, ShapeLayer, TrimPath | — | — | [View PNG](heart_side_by_side.png) |
| 3 | **bullseye** | `1334x1334` | `PASS` | `PASS` | `0.0000` | `0.0008` | `49.3` | Assets/Precomps, Expressions, PrecompLayer, ShapeLayer | — | — | [View PNG](bullseye_side_by_side.png) |
| 4 | **lottielogo** | `750x1334` | `PARSE_ERROR` | `PASS` | `0.0076` | `0.0134` | `23.2` | Expressions, ShapeLayer, SolidLayer, TrimPath | Cluster S2 | — | [View PNG](lottielogo_side_by_side.png) |
| 5 | **AndroidWave** | `800x800` | `PASS` | `PASS` | `0.0001` | `0.0015` | `57.3` | Assets/Precomps, Expressions, MergePaths, NullLayer, PrecompLayer, ShapeLayer | — | — | [View PNG](AndroidWave_side_by_side.png) |
| 6 | **CheckSwitch** | `200x200` | `PARSE_ERROR` | `PASS` | `0.0004` | `0.0021` | `55.5` | Expressions, ShapeLayer, TrimPath | Cluster S2 | — | [View PNG](CheckSwitch_side_by_side.png) |
| 7 | **TrimPaths** | `200x200` | `PASS` | `PARTIAL_PARITY` | `0.0001` | `0.0917` | `39.4` | Expressions, ShapeLayer, SolidLayer, TrimPath | — | Cluster R5 | [View PNG](TrimPaths_side_by_side.png) |
| 8 | **TrimPathWrapAround** | `200x200` | `PASS` | `VISUAL_MISMATCH` | `0.0000` | `0.0000` | `99.9` | DashStroke, Expressions, NullLayer, ShapeLayer, TrimPath | — | Cluster R5 | [View PNG](TrimPathWrapAround_side_by_side.png) |
| 9 | **Repeater** | `600x600` | `PASS` | `PASS` | `0.0011` | `0.0064` | `44.7` | Expressions, Repeater, ShapeLayer | — | — | [View PNG](Repeater_side_by_side.png) |
| 10 | **RoundedCorners** | `2000x2000` | `PASS` | `PASS` | `0.0111` | `0.0225` | `20.3` | RoundedCorners, ShapeLayer | — | — | [View PNG](RoundedCorners_side_by_side.png) |
| 11 | **Shapes** | `300x500` | `PASS` | `PASS` | `0.0063` | `0.0407` | `25.6` | Assets/Precomps, Expressions, PrecompLayer, Repeater, ShapeLayer | — | — | [View PNG](Shapes_side_by_side.png) |
| 12 | **ShapeTypes** | `200x200` | `PASS` | `PASS` | `0.0001` | `0.0047` | `53.2` | Expressions, ShapeLayer, SolidLayer | — | — | [View PNG](ShapeTypes_side_by_side.png) |
| 13 | **GradientFill** | `200x200` | `PASS` | `PASS` | `0.0005` | `0.0020` | `53.7` | Expressions, GradientFill, ShapeLayer, SolidLayer | — | — | [View PNG](GradientFill_side_by_side.png) |
| 14 | **GradientOneColor** | `1080x720` | `PASS` | `PASS` | `0.0001` | `0.0102` | `47.3` | Expressions, GradientFill, ShapeLayer | — | — | [View PNG](GradientOneColor_side_by_side.png) |
| 15 | **Masks** | `400x600` | `PASS` | `PASS` | `0.0018` | `0.0136` | `37.1` | Expressions, Masks, ShapeLayer | — | — | [View PNG](Masks_side_by_side.png) |
| 16 | **MaskInv** | `800x800` | `PASS` | `PASS` | `0.0004` | `0.0048` | `42.5` | Expressions, Masks, SolidLayer | — | — | [View PNG](MaskInv_side_by_side.png) |
| 17 | **TrackMattes** | `200x200` | `PASS` | `PASS` | `0.0005` | `0.0138` | `40.0` | Expressions, ShapeLayer, SolidLayer, TrackMatte | — | — | [View PNG](TrackMattes_side_by_side.png) |
| 18 | **2ParentsMatte** | `600x600` | `PASS` | `PASS` | `0.0002` | `0.0024` | `46.7` | NullLayer, ShapeLayer, TrackMatte | — | — | [View PNG](2ParentsMatte_side_by_side.png) |
| 19 | **TimeStretch** | `940x940` | `PASS` | `PASS` | `0.0001` | `0.0013` | `56.2` | Assets/Precomps, Expressions, PrecompLayer, TimeStretch | — | — | [View PNG](TimeStretch_side_by_side.png) |
| 20 | **TimeRemapAndStartOffset** | `1500x2668` | `PASS` | `PARTIAL_PARITY` | `0.0018` | `0.0807` | `31.3` | Assets/Precomps, Expressions, PrecompLayer, ShapeLayer | — | Cluster R8 | [View PNG](TimeRemapAndStartOffset_side_by_side.png) |
| 21 | **Skew** | `400x400` | `PASS` | `PASS` | `0.0007` | `0.0034` | `40.8` | ShapeLayer | — | — | [View PNG](Skew_side_by_side.png) |
| 22 | **SplitDimensions** | `200x200` | `PASS` | `PASS` | `0.0000` | `0.0012` | `72.9` | Expressions, NullLayer, ShapeLayer, SolidLayer, SplitDimensions | — | — | [View PNG](SplitDimensions_side_by_side.png) |
| 23 | **MiterLimit** | `600x600` | `PASS` | `PASS` | `0.0002` | `0.0022` | `61.7` | GradientStroke, ShapeLayer | — | — | [View PNG](MiterLimit_side_by_side.png) |
| 24 | **Text** | `600x600` | `PASS` | `PARTIAL_PARITY` | `0.0105` | `0.0742` | `24.9` | Fonts, TextLayer, VectorGlyphs | — | Cluster R3 | [View PNG](Text_side_by_side.png) |

## Part I: AST Schema & Deserialization Bug Clusters (Strict Raw JSON)
### Cluster S2: Split-Dimension Position Property Omitting 'a' Discriminator (3 asset(s))
- **Affected Assets**: `hamburger_arrow`, `lottielogo`, `CheckSwitch`
- **Root Cause & Diagnostics**: Position properties with split dimensions ('s': true, 'x'/'y') or un-animated defaults omit top-level 'a' field in BasePositionPropertySerializer (SerializationException: Position property missing required 'a' field per Lottie schema)

## Part II: Visual Rendering Bug Clusters (Side-by-Side `rc lottie` vs `lottie-android`)
### Cluster R5: Repeater Transform Order & TrimPath Wrap-Around / Multi-Shape Scope (2 asset(s))
- **Affected Assets**: `TrimPaths`, `TrimPathWrapAround`
- **Root Cause & Diagnostics**: Repeater copy transform accumulation or TrimPath wrap-around / multi-path trim scope diverges from lottie-android (FG MAE=0.092)

### Cluster R8: Precomposition Time Stretch, Remapping & Start Offset Timing (1 asset(s))
- **Affected Assets**: `TimeRemapAndStartOffset`
- **Root Cause & Diagnostics**: Nested precomposition frame mapping or start offset boundary differs at fractional progress steps (FG MAE=0.081)

### Cluster R3: Text Layer Font Metrics, Baseline & Vector Glyph Alignment (1 asset(s))
- **Affected Assets**: `Text`
- **Root Cause & Diagnostics**: TextLayer rendering differs in font metrics, justification, or vector glyph paths (FG MAE=0.074)


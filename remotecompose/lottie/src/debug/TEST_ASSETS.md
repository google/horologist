# Lottie Test Resources

This directory contains test animations used by unit tests and Roborazzi diff screenshot tests in `remotecompose/lottie`.

## Asset Catalog

| File | Description | Source / Reference |
|------|-------------|-------------------|
| `polystar.json` | Parametric Polystar shapes: Star, Rounded Star, Polygon, Rounded Polygon (`sr`). | [airbnb/lottie-web](https://github.com/airbnb/lottie-web) (`test/`) via [Lottie Format Feature Support & Sample Test Suite](https://docs.google.com/document/d/1jXj3kbXL57kxjRc0soUqst2poa2-Lrc2qZAIzEmbB8w/edit) |
| `rect_ellipse.json` | Parametric Rectangle, Rounded Rectangle (`rc`), Ellipse, Circle (`el`). | [airbnb/lottie-web](https://github.com/airbnb/lottie-web) (`test/`) via [Lottie Format Feature Support & Sample Test Suite](https://docs.google.com/document/d/1jXj3kbXL57kxjRc0soUqst2poa2-Lrc2qZAIzEmbB8w/edit) |
| `geometry.json` | Basic bezier path geometry with multi-keyframe animations. | Internal test suite |
| `position_animated.json` | Animated position properties with keyframe easing. | Internal test suite |
| `position_static.json` | Static position properties. | Internal test suite |
| `play_pause.json`, `m3_play_pause.json` | Play/Pause toggle animation. | Material Design / Media |
| `next.json`, `m3_next.json` | Next track media transition animation. | Material Design / Media |
| `mute_to_unmute.json`, `unmute_to_mute.json` | Audio mute state transition animations. | Material Design / Audio |
| `volume_up.json`, `volume_down.json` | Volume step adjustment animations. | Material Design / Audio |

## License & Attribution

- `polystar.json` and `rect_ellipse.json` are from the [airbnb/lottie-web](https://github.com/airbnb/lottie-web) test suite (MIT License).
- Other test assets are part of the Android Open Source Project (AOSP) / Google Horologist project (Apache 2.0 License).

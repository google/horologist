Horologist is a group of libraries that aim to supplement Wear OS developers with features that are commonly required by developers but not yet available.

---

## 🎵 Media UI

UI and Data layers for media player. PlayScreen and individual UI components.

- [horologist-media-ui](./media-ui)
- [horologist-media-data](./media-data)

| PlayScreen |
|---|
| <img src="https://raw.githubusercontent.com/google/horologist/main/docs/media-ui/playscreen.png" height="120" width="120" > |

## 📅 Composables

High quality prebuilt composables, such as Time and Date pickers.

- [horologist-composables](./composables)

DatePicker             |  TimePickerWith12HourClock |  TimePicker
:-------------------------:|:-------------------------:|:-------------------------:
<img src="https://raw.githubusercontent.com/google/horologist/main/docs/composables/date_picker.png" height="120" width="120" >  |  <img src="https://raw.githubusercontent.com/google/horologist/main/docs/composables/time_12h_picker.png" height="120" width="120"> | <img src="https://raw.githubusercontent.com/google/horologist/main/docs/composables/time_24h_picker.png" height="120" width="120">

## 📐 Compose Layout

Layout related functionality such as a Navigation Aware Scaffold.

- [horologist-compose-layout](./compose-layout)

fillMaxRectangle()             |  fadeAway()
:-------------------------:|:-------------------------:
<img src="https://raw.githubusercontent.com/google/horologist/main/docs/compose-layout/fill_max_rectangle.png" height="120" width="120" >  |  <img src="https://raw.githubusercontent.com/google/horologist/main/docs/compose-layout/fade_away.png" height="120" width="120" >

## 🔊 Audio and UI

Domain model for Audio related functionality. Volume Control, Output switching.
Subscribing to a Flow of changes in audio or output.

- [horologist-audio](./audio)
- [horologist-audio-ui](./audio-ui)

VolumeScreen            |  
:-------------------------:|
<img src="https://raw.githubusercontent.com/google/horologist/main/docs/audio-ui/volume_screen.png" height="120" width="120" > |

## 🖽 Tiles

Kotlin coroutines flavoured TileService.

[horologist-tiles](./tiles)

---

## Why the name?

The name mirrors the [Accompanist](https://github.com/google/accompanist) name, and is also Watch related.

https://en.wiktionary.org/wiki/horologist

> horologist (Noun)
>    Someone who makes or repairs timepieces, watches or clocks.

## Contributions

Please contribute! We will gladly review any pull requests.
Make sure to read the [Contributing](CONTRIBUTING.md) page first though.

## License

```
Copyright 2022 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

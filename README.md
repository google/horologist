<div align="center">
  <img src="./logo/logo-large.svg" width="343" height="100" alt="Horologist logo">
</div>

Horologist is a group of libraries that aim to supplement Wear OS developers with features that are
commonly required by developers but not yet available.

## Maintained Versions

The currently maintained branches of Horologist are.

| Version | Branch        | Min SDK (Wear) | Kotlin/JDK  | Description                                                                         |
| ------- |---------------| -------------- | ----------  |-------------------------------------------------------------------------------------|
| 0.7.x   | main          | 26             | 2.1.20 / 17 | Wear Compose 1.5.x, Compose 1.8.x and generally latest stable Androidx.             |
| 0.8.x   | planned       | 26             | 2.x / 17    | Wear Compose 1.6.x, Compose 1.8.x and generally latest relevant alphas of Androidx. |

Maintenance branches will not delete existing APIs and they should remain stable. However
the main branch will actively update to incorporate new API guidance, removing or changing
APIs.

[![Build & test (nightly release)](https://github.com/google/horologist/actions/workflows/build_nightly.yml/badge.svg?branch=main)](https://github.com/google/horologist/actions/workflows/build_nightly.yml)
[![Build & test](https://github.com/google/horologist/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/google/horologist/actions/workflows/build.yml)

---

## 🎵 Media

Horologist provides the Media Toolkit: a set of libraries to build Media apps on Wear OS and a
sample app that you can run to see the toolkit in action.

The toolkit includes:

- [horologist-media-ui](https://google.github.io/horologist/media-ui): common media UI components
  and screens like `PlayerScreen`.
- [horologist-media](https://google.github.io/horologist/media): domain model for Media related
  functionality. Provides an
  abstraction
  to the UI module (`horologist-media-ui`) that is agnostic to the Player implementation.
- [horologist-media-data](https://google.github.io/horologist/media-data): implementation of the
  domain module (`horologist-media`)
  using [Media3](https://developer.android.com/jetpack/androidx/releases/media3).
- [horologist-media3-backend](https://google.github.io/horologist/media3-backend): Player on top of
  Media3 including
  functionalities
  such as avoiding playing music on the watch speaker.
- [media sample](https://google.github.io/horologist/media-sample): sample app to listen to
  downloaded music.
- [audio](https://google.github.io/horologist/audio): domain model for Audio related functionality,
  such as Volume Control and Output switching, subscribing to a Flow of changes in audio or output.
- [audio-ui](https://google.github.io/horologist/audio-ui): UI components for Audio related functionality,
  such as Volume Control and Output switching

Player Screen | Browse Screen | Entity Screen | Volume Screen
:------------:|:-------------:|:-------------:|:-------------:
<img src="https://media.githubusercontent.com/media/google/horologist/main/docs/media-ui/playerscreen.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/media-ui/browse.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/media-ui/detail.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/audio-ui/volume_screen.png" height="120" width="120" > |


## 📅 Composables

High quality prebuilt composables, such as Time and Date pickers.

- [horologist-composables](https://google.github.io/horologist/composables)

DatePicker             |  TimePickerWith12HourClock |  TimePicker
:-------------------------:|:-------------------------:|:-------------------------:
<img src="https://media.githubusercontent.com/media/google/horologist/main/docs/composables/date_picker.png" height="120" width="120" >  |  <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/composables/time_12h_picker.png" height="120" width="120"> | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/composables/time_24h_picker.png" height="120" width="120">

| SegmentedProgressIndicator |
| :-------------------------: |
| <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/composables/segmented_progress_indicator.png" height="120" width="120"> |

## 📐 Compose Layout

Layout related functionality such as a Navigation Aware Scaffold.

- [horologist-compose-layout](https://google.github.io/horologist/compose-layout)

|                                                                fillMaxRectangle()                                                                 |
|:-------------------------------------------------------------------------------------------------------------------------------------------------:|
| <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/compose-layout/fill_max_rectangle.png" height="120" width="120" > |

## 🔲 Compose Material

Opinionated implementation of the components of
the [Wear Material Compose library](https://developer.android.com/jetpack/androidx/releases/wear-compose)
, based on the specifications
of [Wear Material Design Kit](https://developer.android.com/design/ui/wear/guides/foundations/download)
.

- [horologist-compose-material](https://google.github.io/horologist/compose-material)

## 🔐 Auth

Libraries to help developers to build apps following
the [Sign-In guidelines for Wear OS](https://developer.android.com/training/wearables/design/sign-in)
.

- [horologist-auth-composables](https://google.github.io/horologist/auth-composables): composable
  screens for authentication use
  cases, with no dependency on the `auth-data` library.
- [horologist-auth-ui](https://google.github.io/horologist/auth-ui): composable screens for
  authentication use cases, with integration
  with the `auth-data` library
- [horologist-auth-data](https://google.github.io/horologist/auth-data): implementation for Wear
  apps for most of the authentication
  methods listed in
  the [Authentication on wearables](https://developer.android.com/training/wearables/apps/auth-wear)
  guide.
- [horologist-auth-data-phone](https://google.github.io/horologist/auth-data-phone): implementation
  for Mobile apps for some of the
  authentication methods provided by the `auth-data` library.
- [sample wear](https://google.github.io/horologist/auth-sample-apps/#wear-sample): sample wear app
  to authenticate using different methods.
- [sample phone](https://google.github.io/horologist/auth-sample-apps/#phone-sample): sample phone
  app to authenticate using different methods.

## DataLayer

The Horologist DataLayer library, provide common abstractions on top of the
[Wearable DataLayer](https://developer.android.com/training/wearables/data/data-layer).
It includes libraries to build
[prompts on the phone](https://google.github.io/horologist/datalayer-phone-ui/) to improve
engagement with the correspondent Wear app and a
[sample](https://google.github.io/horologist/datalayer-sample/) to see the prompts in actions.
Find guidance in the project [documentation](https://google.github.io/horologist/datalayer/).

## ☰ Tiles

Kotlin coroutines flavoured TileService.

[horologist-tiles](https://google.github.io/horologist/tiles)

---

## Why the name?

The name mirrors the [Accompanist](https://github.com/google/accompanist) name, and is also Watch
related.

https://en.wiktionary.org/wiki/horologist

> horologist (Noun)
> Someone who makes or repairs timepieces, watches or clocks.

## Contributions

Please contribute! We will gladly review any pull requests submitted.
Make sure to read the [Contributing](CONTRIBUTING.md) page to know what our expectations of
contributions are.

## License

```
Copyright 2023 The Android Open Source Project

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

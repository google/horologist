Horologist is a group of libraries that aim to supplement Wear OS developers with features that are commonly required by developers but not yet available.

Horologist is a labs like environment for new Wear APIs. We use it to help fill known gaps in the Horologist toolkit, experiment with new APIs and to gather insight into the development experience of developing these library. The goal of these libraries is to upstream them into Jetpack, at which point they will be deprecated and removed from Horologist. Some libraries may be useful but specific enough that they do not ever graduate.

---

## Tiles

Kotlin favoured coroutines.

[horologist-tiles](./tiles)

## Compose Layout

Layout related functionality such as a Navigation Aware Scaffold.

[horologist-compose-layout](./compose-layout)

## Audio and UI

Domain model for Audio related functionality.  Volume Control, Output switching.
Subscribing to a Flow of changes in audio or output.

[horologist-audio](./audio)
[horologist-audio-ui](./audio-ui)

### Why the name?

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

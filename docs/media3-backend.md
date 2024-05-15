# Wear Media3 Backend library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-media3-backend)](https://search.maven.org/search?q=g:com.google.android.horologist)

## Features

The media3-backend module implements many of the suggested approaches at
https://developer.android.com/training/wearables/overlays/audio. These enforce the best practices
that will make you app work well for users on a range of Wear OS devices.

These build on top of the [Media3](https://developer.android.com/jetpack/androidx/releases/media3)
player featuring ExoPlayer which is optimised for Wear Playback, and is the standard playback
engine for Wear OS media apps.

All functionality is demonstrated in the `media-sample` app, and as such is not described here
with extensive code samples.

### Audio Offload

In line with https://exoplayer.dev/battery-consumption.html#audio-playback, Audio Offload
allows your app to playback audio while in the background without waking up. This
dramatically improves the users battery life, as well as decreasing the occurrences of
Audio Underruns.

The `AudioOffloadManager` configures and controls Audio Offload, enabling sleeping while your app
is in the background and disabling while in the foreground.

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-media3-backend:<version>"
}
```

# Wear Media3 Backend library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-media3-backend)](https://search.maven.org/search?q=g:com.google.android.horologist)

For more information, visit the documentation: https://google.github.io/horologist/media3-backend

## Features

The media3-backend module implements many of the suggested approaches at
https://developer.android.com/training/wearables/overlays/audio. These enforce the best practices
that will make you app work well for users on a range of Wear OS devices.

These build on top of the [Media3](https://developer.android.com/jetpack/androidx/releases/media3)
player featuring ExoPlayer which is optimised for Wear Playback, and is the standard playback
engine for Wear media apps.

All functionality is demonstrated in the `media-sample` app, and as such is not described here
with extensive code samples.

### Bluetooth Headphone Connections

Any extended playback such as music, podcasts, or radio should only use a connected bluetooth
speaker.  When not connected, the default Watch Speaker will be used and so this must be actively 
avoided.

Horologist provides the `PlaybackRules` abstraction that allows you to intercept playback requests
through your UI, a system media Tile, pressing a bluetooth headphone, or other services such as 
assistant.

```kotlin
public object Normal : PlaybackRules {
    /**
     * Can the given item be played with it's given state.
     */
    override suspend fun canPlayItem(mediaItem: MediaItem): Boolean = true

    /**
     * Can Media be played with the given audio target.
     */
    override fun canPlayWithOutput(audioOutput: AudioOutput): Boolean =
        audioOutput is AudioOutput.BluetoothHeadset
}
```

The `WearConfiguredPlayer` wraps the ExoPlayer to avoid starting playback and also pause immediately
if the headset becomes disconnected. It will prompt the user to connect a headset at this point.

The `AudioOutputSelector` and default implementation `BluetoothSettingsOutputSelector` are used
to prompt the user to connect a Bluetooth headset and then continue playback once connected.

```kotlin
public interface AudioOutputSelector {
    /**
     * Change from the current audio output, according to some sensible logic,
     * and return when either the user has selected a new audio output or returning null
     * if timed out.
     */
    public suspend fun selectNewOutput(currentAudioOutput: AudioOutput): AudioOutput?
}
```

### Audio Offload

In line with https://exoplayer.dev/battery-consumption.html#audio-playback, Audio Offload
allows your app to playback audio while in the background without waking up. This
dramatically improves the users battery life, as well as decreasing the occurrences of
Audio Underruns.

The `AudioOffloadManager` configures and controls Audio Offload, enabling sleeping while your app
is in the background and disabling while in the foreground.

### Logging

The `media3-backend` module interacts with `ExoPlayer` instance, but many events may be required
for error handling, logging or metrics.  Your can register your own `Player.Listener` with the
`ExoPlayer` instance, but to receive generally useful events you can implement `ErrorReporter`
to receive events and report with Android `Log` or write to a database.

Other things in the Horologist media libs will report events, and they all consistently use
`ErrorReporter` to allow you to understand all activity in your app.

```kotlin
public interface ErrorReporter {
    public fun logMessage(
        message: String,
        category: Category = Category.Unknown,
        level: Level = Level.Info
    )
}
```

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-media3-backend:<version>"
}
```

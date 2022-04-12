# Audio Settings Library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-compose-layout)](https://search.maven.org/search?q=g:com.google.android.horologist)

For more information, visit the documentation: https://google.github.io/horologist/audio

Domain model for Volume and Audio Output.

```kotlin
val audioRepository = SystemAudioRepository.fromContext(application)

audioRepository.increaseVolume()

val volumeState: StateFlow<VolumeState> = audioRepository.volumeState

val audioOutput: StateFlow<AudioOutput> = audioRepository.audioOutput

val output = audioOutput.value
if (output is AudioOutput.BluetoothHeadset) {
  println(output.name)
}
```

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-audio:<version>"
}
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap]. These are updated on every commit.

  [snap]: https://oss.sonatype.org/content/repositories/snapshots/com/google/android/horologist/horologist-audio/

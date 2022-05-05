# Audio Settings Library

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

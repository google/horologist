# Audio Settings UI library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-compose-layout)](https://search.maven.org/search?q=g:com.google.android.horologist)

For more information, visit the documentation: https://google.github.io/horologist/compose-layout

A volume screen, showing the current audio output (headphones, speakers) and
allowing to change the button with a stepper or bezel.

```kotlin
VolumeScreen(focusRequester = focusRequester)
```

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-audio-ui:<version>"
}
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap]. These are updated on every commit.

  [snap]: https://oss.sonatype.org/content/repositories/snapshots/com/google/android/horologist/horologist-audio-ui/

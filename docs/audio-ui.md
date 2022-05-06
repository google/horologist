# Audio Settings UI library

## Volume Screen

A volume screen, showing the current audio output (headphones, speakers) and
allowing to change the button with a stepper or bezel.

```kotlin
VolumeScreen(focusRequester = focusRequester)
```

    ![](volume_screen.png){: loading=lazy width=70% align=center }

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-audio-ui:<version>"
}
```

# Media UI library

This library contains a set of composables for media player apps.

- [Controls][controls package]: individual controls
- [Components][components package]: composables that might combine multiple controls
- [Screens][screens package]: full screen composables

## Previews

The previews of the composables can be found in the [debug][debug folder] folder of the module.

## State

This library also provides a [state][state package] package with a [PlayerUiState][playeruistate] and its own UI [models][state models package] to represent the state of common components that are displayed on a player screen. 
They can be used with your own ViewModel implementation or with the [PlayerViewModel][playerviewmodel] provided.

There is a guide on the usage of the stateful `PlayerScreen` [here][stateful playerscreen guide].

## Demo app

A sample usage of [PlayerScreen][player screen] can be found in the [sample][sample app package] app in this project, in the [media][media screen sample package] package.

![](playerscreen.png){: loading=lazy align=center }

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-media-ui:<version>"
}
```
 
 [components package]: https://github.com/google/horologist/tree/main/media-ui/src/main/java/com/google/android/horologist/media/ui/components
 [controls package]: https://github.com/google/horologist/tree/main/media-ui/src/main/java/com/google/android/horologist/media/ui/components/controls
 [screens package]: https://github.com/google/horologist/tree/main/media-ui/src/main/java/com/google/android/horologist/media/ui/screens
 [debug folder]: https://github.com/google/horologist/tree/main/media-ui/src/debug/java/com/google/android/horologist/media/ui
 [state package]: https://github.com/google/horologist/tree/main/media-ui/src/main/java/com/google/android/horologist/media/ui/state
 [playeruistate]: https://github.com/google/horologist/blob/main/media-ui/src/main/java/com/google/android/horologist/media/ui/state/PlayerUiState.kt
 [state models package]: https://github.com/google/horologist/tree/main/media-ui/src/main/java/com/google/android/horologist/media/ui/state/model
 [playerviewmodel]: https://github.com/google/horologist/blob/main/media-ui/src/main/java/com/google/android/horologist/media/ui/state/PlayerViewModel.kt
 [stateful playerscreen guide]: https://google.github.io/horologist/media-playerscreen/
 [player screen]: https://github.com/google/horologist/blob/main/media-ui/src/main/java/com/google/android/horologist/media/ui/screens/PlayerScreen.kt
 [sample app package]: https://github.com/google/horologist/tree/main/sample
 [media screen sample package]: https://github.com/google/horologist/tree/main/sample/src/main/java/com/google/android/horologist/sample/media

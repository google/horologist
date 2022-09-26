# Build a simple player app

This guide will walk you through on how to build a very simple media player app for Wear OS, capable
of playing a media which is hosted on the internet.

This guide assumes that you are familiar with:

- How to create Wear OS projects in Android Studio;
- Kotlin programming language;
- Jetpack Compose;

## Display a PlayerScreen

1. Add dependency

Add dependency on `media-ui` to your project’s `build.gradle`:

```groovy
implementation "com.google.android.horologist:horologist-media-ui:$horologist_version"
```

2. Add `PlayerScreen`

Add the following code to your `Activity`’s `onCreate` function:

```kotlin
setContent {
    PlayerScreen(
        mediaDisplay = {
            TextMediaDisplay(
                title = "Song name",
                artist = "Artist name"
            )
        },
        controlButtons = {
            PodcastControlButtons(
                onPlayButtonClick = { },
                onPauseButtonClick = { },
                playPauseButtonEnabled = true,
                playing = false,
                percent = 0f,
                onSeekBackButtonClick = { },
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = { },
                seekForwardButtonEnabled = true,
            )
        },
        buttons = { }
    )
}
```

This code is displaying `PlayerScreen` on the app. `PlayerScreen` is a full screen composable that
contains [slots](https://developer.android.com/jetpack/compose/layouts/basics#slot-based-layouts)
parameters to pass the contents to be displayed for media display, control buttons and more.

In this sample, we are using the UI components `TextMediaDisplay` and `PodcastControlButtons`,
provided by the UI library, as values to parameters of `PlayerScreen`.

### Result

Run the app and you should see the following screen:

![](simple_media_app_not_functional.png){: loading=lazy align=center }

None of the controls are working, as they were not implemented yet.

## Make the screen functional

1. Add dependencies

Add dependency on `media-data`, `compose-layout` and `media3-exoplayer` to your project’s
build.gradle:

```groovy
implementation "com.google.android.horologist:horologist-media-data:$horologist_version"
implementation("androidx.media3:media3-exoplayer:$media3_version")
```

2. Add `ViewModel`

Add a `ViewModel` extending `PlayerViewModel`, providing an instance of `PlayerRepositoryImpl`:

```kotlin
class MyViewModel(
    player: Player,
    playerRepository: PlayerRepositoryImpl = PlayerRepositoryImpl()
) : PlayerViewModel(playerRepository) {}
```

3. Add init block

Add the following init block to the `ViewModel` to connect the `Player` to the `PlayerRepository`,
set a media and update the position of the player every second:

```kotlin
init {
    viewModelScope.launch {
        playerRepository.connect(player) {}

        playerRepository.setMedia(
            Media(
                id = "wake_up_02",
                uri = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",
                title = "Geisha",
                artist = "The Kyoto Connection"
            )
        )

        // update the track position while app is in foreground
        while (isActive) {
            delay(1000)
            playerRepository.updatePosition()
        }
    }
}
```

4. Create an instance of the `ViewModel`

Change your `Activity`’s `onCreate` function to:

```kotlin
val player = ExoPlayer.Builder(this).build()
// ViewModels should NOT be created here like this
val viewModel = MyViewModel(player)

PlayerScreen(
    mediaDisplay = { playerUiState ->
        DefaultMediaDisplay(media = playerUiState.media)
    },
    controlButtons = { playerUiState ->
        PodcastControlButtons(
            playerViewModel = viewModel,
            playerUiState = playerUiState,
        )
    },
    buttons = { }
)
```

We are creating an instance of `ExoPlayer`, passing it to the `ViewModel`.

Then for the `PlayerScreen` slots we are using:

- the `DefaultMediaDisplay` component, which accepts a `MediaUiModel` instance as parameter;
-
the [stateful](https://docs.google.com/document/d/1UZBMLPbkuHbGDf_BiGU673uCJPjLy8laDeHAyfbIPCY/edit#heading=h.6a12zpdsph5c)
version of `PodcastControlButtons`, which accepts instances of `PlayerViewModel` and `PlayerUiState`
as parameters to hook the controls with the `ViewModel`;

### Result

Run the app again and this time, play with the screen controls as the app should be able to play,
pause, and seek the media now:

![](simple_media_app_functional.png){: loading=lazy align=center }

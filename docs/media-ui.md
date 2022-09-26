# Media UI library

This library contains a set of composables for media player apps:

Individual controls like Play, Pause and Seek buttons;
Components that might combine multiple controls, like `PlayPauseButton` and `MediaControlButtons`;
Screens, like `PlayerScreen`, `BrowseScreen` and `EntityScreen`.

The previews of the composables can be found in the `debug` folder of the module source code.

This library is not dependent on any specific player implementation as
per [architecture overview](media-toolkit.md#architecture-overview).

## Stateful components

Most of the components available in this library contain an overloaded version of themselves which
accept either a UI model (`MediaUiModel`, `PlaylistUiModel`) or `PlayerUiState` or `PlayerViewModel`
as parameters. We call those versions “stateful components”, which is a different definition from
the [compose documentation](https://developer.android.com/jetpack/compose/state#stateful-vs-stateless)
.

While the stateless components provide full customization, the stateful components provide
convenience (if the default implementation suits your project requirements), as can be seen in the
example below.

Stateless `PodcastControlButtons` usage:

```kotlin
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
```

Stateful `PodcastControlButtons` usage:

```kotlin
PodcastControlButtons(
    playerViewModel = viewModel,
    playerUiState = playerUiState,
)
```

Further examples on how to use these components can be found in
the [Stateful PlayerScreen guide](https://google.github.io/horologist/media-playerscreen/).

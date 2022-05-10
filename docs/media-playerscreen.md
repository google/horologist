# Stateful PlayerScreen guide

This is a guide on how to use the stateful `PlayerScreen` with your own implementation of `PlayerRepository`.

## Basic usage

### 1. Implement PlayerRepository

```kotlin
class PlayerRepositoryImpl : PlayerRepository {
    // implement required properties and functions
}
```

In the sample implementation below, the repository listens to the events of [Media3][media3 player]'s 
`Player` and update its property values accordingly (see `onIsPlayingChanged`). Its operations are 
also called on the `Player` (see `setPlaybackSpeed`).

```kotlin
class PlayerRepositoryImpl(
    private val player: Player
) : PlayerRepository {
    
    private var _playing = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _playing

    private val listener = object : Player.Listener {
        
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playing.value = isPlaying
        }
    }
    
    init {
        player.add(listener)
    }

    fun setPlaybackSpeed(speed: Float) { 
        player.setPlaybackSpeed(speed) 
    }
}

```

### 2. Extend PlayerViewModel

Pass your implementation of `PlayerRepository` as constructor parameter.

```kotlin
class MyCustomViewModel(
    playerRepository: PlayerRepositoryImpl
): PlayerViewModel(playerRepository) {
    // add custom implementation
}
```

### 3. Add PlayerScreen

Pass your `PlayerViewModel` extension as value to the constructor parameter.

```kotlin
PlayerScreen(playerViewModel = myCustomViewModel)
```

## Class diagram

The following diagram shows the interactions between the classes.

![](media_class_diagram.png){: loading=lazy align=center }

 [media3 player]: https://developer.android.com/jetpack/androidx/releases/media3

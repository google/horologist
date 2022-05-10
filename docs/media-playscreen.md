# Stateful PlayScreen guide

This is a guide on how to use the stateful `PlayScreen` with your own implementation of `PlayerRepository`.

## Basic usage

1. Implement PlayerRepository

```kotlin
class PlayerRepositoryImpl() : PlayerRepository {
    // implement required properties and functions
}
```

2. Extend PlayerViewModel

Pass your implementation of `PlayerRepository` as constructor parameter.

```kotlin
class MyCustomViewModel(
    playerRepository: PlayerRepositoryImpl
): PlayerViewModel(playerRepository) {
    // add custom implementation
}
```

3. Add PlayScreen

Pass your `PlayerViewModel` extension as value to the constructor parameter.

```kotlin
PlayScreen(playerViewModel = myCustomViewModel)
```

## Class diagram

The following diagram shows the interactions between the classes.

<img src="https://github.com/google/horologist/blob/main/docs/media/media_class_diagram.png" height="420" width="300" >

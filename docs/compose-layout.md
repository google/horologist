# Compose Layout library

## Navigation Scaffold.

Syncs the TimeText, PositionIndicator and Scaffold to the current navigation destination
state. The TimeText will scroll out of the way of content automatically.

```kotlin
WearNavScaffold(
    startDestination = "home",
    navController = navController
) {
    scalingLazyColumnComposable(
        "home",
        scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
    ) {
        MenuScreen(
            scrollState = it.scrollableState,
            focusRequester = it.viewModel.focusRequester
        )
    }

    scalingLazyColumnComposable(
        "items",
        scrollStateBuilder = { ScalingLazyListState() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollableColumn(it.viewModel.focusRequester, it.scrollableState),
            state = it.scrollableState
        ) {
            items(100) {
                Text("i = $it")
            }
        }
    }

    scrollStateComposable(
        "settings",
        scrollStateBuilder = { ScrollState(0) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = it.scrollableState)
                .scrollableColumn(focusRequester = it.viewModel.focusRequester, scrollableState = it.scrollableState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            (1..100).forEach {
                Text("i = $it")
            }
        }
    }
}
```

## Box Inset Layout.

Use as a break glass for simple layout to fit within a safe square.

```kotlin
Box(
    modifier = Modifier
        .fillMaxRectangle()
) {
    // App Content here        
}
```

![](fill_max_rectangle.png){: loading=lazy width=70% align=center }

## Fade Away Modifier

![](fade_away.png){: loading=lazy width=70% align=center }

## AmbientAware composable

`AmbientAware` allows your UI to react to ambient mode changes. For more information on how Ambient
mode and Always-on work on Wear OS, see the [developer guidance][always-on].

You should place this composable high up in your design, as it alters the behavior of the Activity.

```kotlin
@Composable
fun WearApp() {
    AmbientAware { ambientStateUpdate ->
        // App Content here
    }
}
```

If you need some screens to use always-on, and others not to, then you can use the additional
argument supplied to `AmbientAware`.

For example, in a workout app, it is desirable that the main  workout screen uses always-on, but the
workout summary at the end does not. See the [`ExerciseClient`][exercise-client]
guide and [samples][health-samples] for more information on building a workout app.

```kotlin
@Composable
fun WearApp() {
    // Hoist state here for your current screen logic
    
    AmbientAware(isAlwaysOnScreen = currentScreen.useAlwaysOn) { ambientStateUpdate ->
        // App Content here
    }
}
```

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-compose-layout:<version>"
}
```


[always-on]: https://developer.android.com/training/wearables/views/always-on
[exercise-client]: https://developer.android.com/training/wearables/health-services/active-data#work-with-data
[health-samples]: https://github.com/android/health-samples

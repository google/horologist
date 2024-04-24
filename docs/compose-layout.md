# Compose Layout library

> [!IMPORTANT]
> All the examples on this page make use of imports from Horologist's `compose-layout` and `compose-material`.
> Add these dependencies and imports to ensure you are using the Horologist version of components with the same
> name in Wear Compose, for example `Chip`.

## ScalingLazyColumn Responsive layout.

The `rememberResponsiveColumnState()` method will ensure that your ScalingLazyColumn is positioned 
correctly on all screen sizes.

Pass in a padding configuration based on your item types.

The overloaded `ScalingLazyColumn` composable with `ScalingLazyColumnState` param, when combined
with `rememberResponsiveColumnState()` will handle all the following:

- Position the first item near the top on all screen sizes.
- Ensure the last item can be scrolled into view.
- Handle RSB/Bezel scrolling with Fling.
- Size side margins based on a percentage, adapting to different screen sizes.

```kotlin
val columnState = rememberResponsiveColumnState(
    contentPadding = ScalingLazyColumnDefaults.padding(
        first = ItemType.Text,
        last = ItemType.Chip
    )
)

ScalingLazyColumn(
    modifier = Modifier.fillMaxSize(),
    columnState = columnState,
) {
    item {
        ResponsiveListHeader(contentPadding = firstItemPadding()) {
            Text(text = "Main")
        }
    }
    items(10) {
        Chip("Item $it", onClick = {})
    }
}
```

## App/Screen Scaffold.

Syncs the TimeText, PositionIndicator and Scaffold to the current navigation destination
state. The TimeText will scroll out of the way of content automatically.

```kotlin
AppScaffold {
    SwipeDismissableNavHost(
        startDestination = "home",
        navController = navController
    ) {
        composable(
            "home",
        ) {
            val columnState = rememberResponsiveColumnState()
            ScreenScaffold(scrollState = columnState) {
                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    columnState = columnState
                ) {
                    items(100) {
                        Text("i = $it")
                    }
                }
            }
        }

        composable(
            "settings"
        ) {
            val scrollState = rememberScrollState()
            ScreenScaffold(scrollState = scrollState) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotaryWithScroll(scrollState, rememberActiveFocusRequester())
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    (1..100).forEach {
                        Text("i = $it")
                    }
                }
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

## AmbientAware composable

`AmbientAware` allows your UI to react to ambient mode changes. For more information on how Ambient
mode and Always-on work on Wear OS, see the [developer guidance][always-on].

You should place this composable high up in your design, as it alters the behavior of the Activity.

```kotlin
@Composable
fun WearApp() {
    AmbientAware { ambientStateUpdate ->
        when (val state = ambientStateUpdate.ambientState) {
            is AmbientState.Ambient -> {
                val ambientDetails = state.ambientDetails
                val burnInProtectionRequired = ambientDetails?.burnInProtectionRequired
                val deviceHasLowBitAmbient = ambientDetails?.deviceHasLowBitAmbient
                // Device is in ambient (low power) mode
            }
            is AmbientState.Interactive -> {
                // Device is in interactive (high power) mode
            }
        }
    }
}
```

If you need some screens to use always-on, and others not to, then you can use
the additional argument supplied to `AmbientAware` to indicate whether a
recomposition should be triggered when the system goes into ambient mode (i.e.
whether the composable wants to handle ambient mode or not).

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

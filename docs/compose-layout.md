# Compose Layout library

> [!IMPORTANT]
> All the examples on this page make use of imports from Horologist's `compose-layout` and `compose-material`.
> Add these dependencies and imports to ensure you are using the Horologist version of components with the same
> name in Wear Compose, for example `Chip`.

## ScalingLazyColumn Responsive layout.

The `rememberResponsiveColumnState()` method ensures that your ScalingLazyColumn is positioned 
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

## Typesafe Navigation.

This provides an implementation of typesafe navigation for Wear Compose.
See https://developer.android.com/guide/navigation/design/type-safety for more information.

```kotlin
import com.google.android.horologist.compose.nav.SwipeDismissableNavHost
import com.google.android.horologist.compose.nav.composable

@kotlinx.serialization.Serializable
object Prompt

@kotlinx.serialization.Serializable
object Settings

@Composable
fun WearApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    AppScaffold(modifier = modifier) {
        SwipeDismissableNavHost(
            startDestination = Prompt,
            navController = navController,
        ) {
            composable<Prompt> {
                SamplePromptScreen(
                    onSettingsClick = { navController.navigate(Settings) },
                )
            }
            composable<Settings> {
                SettingsScreen()
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

You should place this composable high up in your screen, but within navigation routes so that 
different screens can handle ambient mode differently.

```kotlin
@Composable
fun MyScreen() {
    AmbientAware { ambientState ->
        if (ambientState.isAmbient) {
            val ambientDetails = state.ambientDetails
            val burnInProtectionRequired = ambientDetails?.burnInProtectionRequired
            val deviceHasLowBitAmbient = ambientDetails?.deviceHasLowBitAmbient
            // Device is in ambient (low power) mode
        } else {
            // Device is in interactive (high power) mode
        }
    }
}
```

For example, in a workout app, it is desirable that the main  workout screen uses always-on, but the
workout summary at the end does not. See the [`ExerciseClient`][exercise-client]
guide and [samples][health-samples] for more information on building a workout app.

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

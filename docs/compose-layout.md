# Compose Layout library

Navigation Scaffold.

Syncs the TimeText, PositionIndicator and Scaffold to the current navigation destination
state. The TimeText will scroll out of the way of content automatically.

```
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

Box Inset Layout.

Use as a break glass for simple layout to fit within a safe square.

```kotlin
Box(
    modifier = Modifier
        .fillMaxRectangle()
) {
    // App Content here        
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

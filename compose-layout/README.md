# Compose Layout library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-compose-layout)](https://search.maven.org/search?q=g:com.google.android.horologist)

For more information, visit the documentation: https://google.github.io/horologist/compose-layout

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

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap]. These are updated on every commit.

```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

dependencies {
    implementation "com.google.android.horologist:horologist-compose-layout:<version>-SNAPSHOT"
}
```

  [snap]: https://oss.sonatype.org/content/repositories/snapshots/com/google/android/horologist/horologist-compose-layout/

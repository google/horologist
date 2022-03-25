# Tiles Library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-tiles)](https://search.maven.org/search?q=g:com.google.android.horologist)

For more information, visit the documentation: https://google.github.io/horologist/horologist-tiles

Provides a CoroutinesTileService, which also acts as a LifecycleService.

```kotlin
class ExampleTileService : CoroutinesTileService() {
    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): Tile {
        return Tile.Builder()
            // create your tile here
            .build()
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources = ResourceBuilders.Resources.Builder().setVersion("1").build()
}
```

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-tiles:<version>"
}
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].
These are updated on every commit.

  [snap]: https://oss.sonatype.org/content/repositories/snapshots/com/google/android/horologist/horologist-tiles/

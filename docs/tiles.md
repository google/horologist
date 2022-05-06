# Tiles Library

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

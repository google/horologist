# Tiles Library

## CoroutinesTileService

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

## Coil Image Helpers

Provides a suspending method to load an image from the network, convert to a RGB_565
bitmap, and encode as a Tiles InlineImageResource.

```kotlin
val imageResource = imageLoader.loadImageResource(applicationContext, "https://raw.githubusercontent.com/google/horologist/main/docs/media-ui/playerscreen.png")
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

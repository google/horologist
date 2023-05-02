# Tiles Library

## SuspendingTileService

Provides a SuspendingTileService, which also acts as a LifecycleService.

```kotlin
class ExampleTileService : SuspendingTileService() {
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

Provides a suspending method to load an image from the network, convert to an RGB_565
bitmap, and encode as a Tiles InlineImageResource.

```kotlin
val imageResource = imageLoader.loadImageResource(applicationContext, 
    "https://media.githubusercontent.com/media/google/horologist/main/docs/media-ui/playerscreen.png") {
    // Show a local error image if missing
    error(R.drawable.missingImage)
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

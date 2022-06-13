# Compose Tools library

## Tile Previews.

Android Studio Preview support for tiles, using the TilesRenderer inside and AndroidView.
Uses either raw Tiles proto, or the TilesLayoutRenderer abstraction to define a predictable
process for generating a Tile for a given state.


```kotlin
@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun SampleTilePreview() {
    val context = LocalContext.current

    val tileState = remember { SampleTileRenderer.TileState(0) }

    val resourceState = remember {
        val image =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_uamp).toImageResource()
        SampleTileRenderer.ResourceState(image)
    }

    val renderer = remember {
        SampleTileRenderer(context)
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer
    )
}
```

![](tiles_preview.png){: loading=lazy width=70% align=center }

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-compose-tools:<version>"
}
```

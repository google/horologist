# Rubberband

This library provides an easy means to detect and install your app across both watch and phone.

However, it is not intended to cover complex use cases, or complex interactions between watch and
phone.

## Getting started

Include the necessary dependency:

```
dependencies {
    implementation "com.google.android.horologist:horologist-rubberband:<version>"
}
```

On both watch and phone, using the library is the same:

```kotlin
val rubberband = Rubberband(context)
```

## Typical use cases:

1.  **Connection and installation status**

    This is something that your app may do from time to time, or on start up.
    
    ```kotlin
    val connectedNodes = rubberband.connectedNodes()
    ```
    
    The resulting list might will contain entries such as:
    
    ```
    NodeStatus(
        id=1e56d2,
        displayName=Pixel Watch,
        isAppInstalled=true,
        installedTiles=[SummaryTile]
    )
    ```
    
1.  **Responding to availability change**

    Once you've established the app on both devices, you may wish to respond to when the partner 
    device connects or disconnects. For example, you may only want to show a "launch workout" button
    on the phone when the watch is connected.

    ```kotlin
    val nodes by rubberband.connectedAndInstalledNodes
        .collectAsStateWithLifecycle()
    ````
    
1.  **Installing the app on the other device**
    
    Where the app isn't installed on the other device - be that phone or watch - then the library offers
    a one step option to launch installation:
    
    ```kotlin
    rubberband.installOnNode(node.id)
    ```

1.  **Launching the app on the other device**

    If the app is installed on the other device, you can launch it remotely:
    
    ```kotlin
    val result = rubberband.startRemoteApp(node.id)
    ```

1.  **Launching the companion app**

    In some cases, it can be useful to launch the companion app, either from the watch or the phone.
    
    For example, if the connected device does not have your Tile installed, you may wish to offer the
    user the option to navigate to the companion app to install it:
    
    ```kotlin
    if (node.installedTiles.isEmpty() && askUserAttempts < MAX_ATTEMPTS) {
        // Show guidance to the user and then launch companion
        // to allow the to install the Tile.
        val result = rubberband.startCompanion(nodeStatus.id)
    }
    ```

1.  **Tracking Tile installation**

    To determine whether your Tile(s) are installed, add the following to your `TileService`:

    In `onTileAddEvent`:

    ```kotlin
    rubberband.markTileAsInstalled("SummaryTile")
    ```

    In `onTileRemoveEvent`:

    ```kotlin
    rubberband.markTileAsRemoved("SummaryTile")
    ```
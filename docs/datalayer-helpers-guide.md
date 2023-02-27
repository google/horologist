# DataLayer helpers libraries

These libraries provides an easy means to detect and install your app across both watch and phone.

However, they are not intended to cover complex use cases, or complex interactions between watch and
phone.

## Getting started

1.  Include the necessary dependency:

    ```
    dependencies {
        implementation "com.google.android.horologist:horologist-datalayer-watch:<version>"
    }
    ```

    and

    ```
    dependencies {
        implementation "com.google.android.horologist:horologist-datalayer-phone:<version>"
    }
    ```

    For your watch and phone projects respectively.

1.  Initialize the client

    ```kotlin
    val appHelper = WearDataLayerAppHelper(context)

    // or
    val appHelper = PhoneDataLayerAppHelper(context)
    ```

## Typical use cases:

1.  **Connection and installation status**

    This is something that your app may do from time to time, or on start up.

    ```kotlin
    val connectedNodes = appHelper.connectedNodes()
    ```

    The resulting list might will contain entries such as:

    ```
    AppHelperNodeStatus(
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
    val nodes by appHelper.connectedAndInstalledNodes
        .collectAsStateWithLifecycle()
    ````

1.  **Installing the app on the other device**

    Where the app isn't installed on the other device - be that phone or watch - then the library offers
    a one step option to launch installation:

    ```kotlin
    appHelper.installOnNode(node.id)
    ```

1.  **Launching the app on the other device**

    If the app is installed on the other device, you can launch it remotely:

    ```kotlin
    val result = appHelper.startRemoteOwnApp(node.id)
    ```

1.  **Launching a specific activity on the other device**

    In addition to launching your own app, you may wish to launch a different
    activity as part of the user journey:

    ```kotlin
    val config = activityConfig { 
        packageName = "com.example.myapp"
        classFullName = "com.example.myapp.MyActivity"
    }
    appHelper.startRemoteActivity(node.id, config)
    ```

1.  **Launching the companion app**

    In some cases, it can be useful to launch the companion app, either from the watch or the phone.

    For example, if the connected device does not have your Tile installed, you may wish to offer the
    user the option to navigate to the companion app to install it:

    ```kotlin
    if (node.installedTiles.isEmpty() && askUserAttempts < MAX_ATTEMPTS) {
        // Show guidance to the user and then launch companion
        // to allow the to install the Tile.
        val result = appHelper.startCompanion(nodeStatus.id)
    }
    ```

1.  **Tracking Tile installation** (Wear-only)

    To determine whether your Tile(s) are installed, add the following to your `TileService`:

    In `onTileAddEvent`:

    ```kotlin
    wearAppHelper.markTileAsInstalled("SummaryTile")
    ```

    In `onTileRemoveEvent`:

    ```kotlin
    wearAppHelper.markTileAsRemoved("SummaryTile")
    ```
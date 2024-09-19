# DataLayer helpers libraries

The DataLayer helpers libraries help tracking the connection between phone and watch and
start flows from the such as installing the app

However, they are not intended to cover complex use cases, or complex interactions between watch and
phone.

## Getting started

1. Include the necessary dependency:

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

2. Add the capabilities

   Add a `wear.xml` file in the `res/values` folder with the following content.

    ```xml
    <resources xmlns:tools="http://schemas.android.com/tools"
        tools:keep="@array/android_wear_capabilities">
        <string-array name="android_wear_capabilities">
            <!-- Used to indicate that the app is installed on this device -->
            <item>data_layer_app_helper_device_watch</item>
            <!-- Used to indicate the device is a watch -->
            <item>horologist_watch</item>
        </string-array>
    </resources>
    ```

   and

    ```xml
    <resources xmlns:tools="http://schemas.android.com/tools"
        tools:keep="@array/android_wear_capabilities">
        <string-array name="android_wear_capabilities" translatable="false" tools:ignore="UnusedResources">
            <!-- Used to indicate that the app is installed on this device -->
            <item>data_layer_app_helper_device_phone</item>
            <!-- Used to indicate the device is a phone -->
            <item>horologist_phone</item>
    </string-array>
    </resources>
    ```

   on your wear and phone projects respectively.

   After this you will need to add a `wear_keep.xml` file in your `res/raw` folder with the
   following content:

      ```xml
    <resources xmlns:tools="http://schemas.android.com/tools" tools:keep="@array/android_wear_capabilities"/>
   ```
   NB! Notice that this is a different folder than the first two. `res/raw/wear_keep.xml`. It needs
   to be `res/raw` folder due to https://issuetracker.google.com/issues/348688201.

   For more details, see
   [Specify capability names for detecting your apps](https://developer.android.com/training/wearables/apps/standalone-apps#capability-names).

3. Initialize the client including passing a `WearDataLayerRegistry`.

    ```kotlin
    // on your watch project
    val appHelper = WearDataLayerAppHelper(context, wearDataLayerRegistry, scope)

    // on your phone project
    val appHelper = PhoneDataLayerAppHelper(context, wearDataLayerRegistry)
    ```

## Check API availability

This API relies on Google Play services to be available and up-to-date on the device. You can check
the availability with:

```kotlin
if (!phoneDataLayerAppHelper.isAvailable()) {
    // display error 
}
```

Please refer
to [GoogleApiAvailability](https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#makeGooglePlayServicesAvailable(android.app.Activity))
if you would like to display an UI to request the user to install or update Google Play.

## Connection and installation status

The `DataLayerAppHelper.connectedNodes()` returns information about connected devices. You could
invoke this method at startup or while the app is running.

```kotlin
val connectedNodes = appHelper.connectedNodes()
```

The resulting list might will contain entries such as:

```
 AppHelperNodeStatus(
     id=7cd1c38a,
     displayName=Google Pixel Watch,
     appInstallationStatus=Installed(nodeType=WATCH),
     surfacesInfo=# SurfacesInfo@125fcbff
         complications {
             instance_id: 1234
             name: "MyComplication"
             timestamp {
                 nanos: 738000000
                 seconds: 1680015523
             }
             type: "SHORT_TEXT"
         }
         tiles {
             name: "MyTile"
             timestamp {
                 nanos: 364000000
                 seconds: 1680016845
             }
         }
         usage_info {
             timestamp {
                 nanos: 669000000
                 seconds: 1701708501
             }
             usage_status: USAGE_STATUS_LAUNCHED_ONCE
             usage_status_value: 1
         }
 )
```

## Responding to availability change

Once you've established the app on both devices, you may wish to respond to when the partner
device connects or disconnects. For example, you may only want to show a "launch workout" button
on the phone when the watch is connected.

```kotlin
val nodes by appHelper.connectedAndInstalledNodes
    .collectAsStateWithLifecycle()
```

## Installing the app on the other device

Where the app isn't installed on the other device - be that phone or watch - then the library offers
a one step option to launch installation:

```kotlin
appHelper.installOnNode(node.id)
```

## Launching the app on the other device

If the app is installed on the other device, you can launch it remotely:

```kotlin
val result = appHelper.startRemoteOwnApp(node.id)
```

## Launching a specific activity on the other device

In addition to launching your own app, you may wish to launch a different
activity as part of the user journey:

```kotlin
val config = activityConfig {
    classFullName = "com.example.myapp.MyActivity"
}
appHelper.startRemoteActivity(node.id, config)
```

## Launching the companion app

In some cases, it can be useful to launch the companion app, either from the watch or the phone.

For example, if the connected device does not have your Tile installed, you may wish to offer the
user the option to navigate to the companion app to install it:

```kotlin
if (node.surfacesInfo.tilesList.isEmpty() && askUserAttempts < MAX_ATTEMPTS) {
    // Show guidance to the user and then launch companion
    // to allow the to install the Tile.
    val result = appHelper.startCompanion(node.id)
}
```

## Tracking Tile installation (Wear-only)

To determine whether your Tile(s) are installed, add the following to your `TileService`:

In `onTileAddEvent`:

```kotlin
wearAppHelper.markTileAsInstalled("SummaryTile")
```

In `onTileRemoveEvent`:

```kotlin
wearAppHelper.markTileAsRemoved("SummaryTile")
```

## Deeplink into Tiles settings editor to install a Tile (phone only)

If you want to open the Tile Settings editor on your phone, you can use
`phoneDataLayerAppHelper.checkCompanionVersionSupportTileEditing` to check that the Companion version
supports deeplink into Tiles Settings editor and `phoneDataLayerAppHelper.findWatchToInstallTile`
to check if there is a connected watch where the Tile can be installed.

## Tracking Complication installation (Wear-only)

To determine whether your Complication(s) are in-use, add the following to
your `ComplicationDataSourceService`:

In `onComplicationActivated`:

```kotlin
wearAppHelper.markComplicationAsActivated(complicationName = "GoalsComplication",
        complicationInstanceId = 1234,
        complicationType = ComplicationType.SHORT_TEXT,
)
```

In `onComplicationDeactivated`:

```kotlin
wearAppHelper.markComplicationAsDeactivated(complicationInstanceId = 1234)
```

## Tracking the main activity has been launched at least once (Wear-only)

To mark that your main activity on the watch app has been launched once, use:

```kotlin
wearAppHelper.markActivityLaunchedOnce()
```

To check it on the phone side, use:

```kotlin
val connectedNodes = appHelper.connectedNodes()
// after picking a node, check if value is USAGE_STATUS_LAUNCHED_ONCE
node.surfacesInfo.usageInfo.usageStatus
```

## Tracking the app has been set up (Wear-only)

To mark that the user has completed in the app the necessary setup steps such that it is ready
for use, use the following:

```kotlin
wearAppHelper.markSetupComplete()
```

And when the app is no longer considered in a fully setup state, use the following:

```kotlin
wearAppHelper.markSetupNoLongerComplete()
```

To check it on the phone side, use:

```kotlin
val connectedNodes = appHelper.connectedNodes()
// after picking a node, check if value is either USAGE_STATUS_LAUNCHED_ONCE
// or USAGE_STATUS_SETUP_COMPLETE
node.surfacesInfo.usageInfo.usageStatus
```

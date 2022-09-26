# Media Data library

This library contains the implementation of the repositories defined in
the [media domain library](../media), using [Media3][media3] and
an internal database as data sources.

It also exposes its data sources classes so they can be used by your custom repositories.

## MediaDownloadService

An implementation of [Media3][media3]’s `DownloadService` that, in conjunction with auxiliary
classes, will monitor the state and progress of media downloads, and update the information in the
internal database.

### Usage

1. Add your own implementation of the service, extending `MediaDownloadService`;

2. Add your service implementation to your app’s `AndroidManifest.xml`:
```xml
<service android:name="MediaDownloadServiceImpl"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.android.exoplayer.downloadService.action.RESTART"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
</service>
```

[media3]: https://developer.android.com/jetpack/androidx/releases/media3

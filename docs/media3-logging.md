# Wear Media3 Logging library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-media3-logging)](https://search.maven.org/search?q=g:com.google.android.horologist)

## Features
The `media3-logging` module implements logging of events.

### Logging

The `media3-logging` module interacts with `ExoPlayer` instance, but many events may be required
for error handling, logging or metrics.  Your can register your own `Player.Listener` with the
`ExoPlayer` instance, but to receive generally useful events you can implement `ErrorReporter`
to receive events and report with Android `Log` or write to a database.

Other things in the Horologist media libs will report events, and they all consistently use
`ErrorReporter` to allow you to understand all activity in your app.

```kotlin
public interface ErrorReporter {
    public fun logMessage(
        message: String,
        category: Category = Category.Unknown,
        level: Level = Level.Info
    )
}
```

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-media3-logging:<version>"
}
```
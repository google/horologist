# DataLayer library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-datalayer)](https://search.maven.org/search?q=g:com.google.android.horologist)

For more information, visit the documentation: https://google.github.io/horologist/datalayer

DataStore documentation https://developer.android.com/topic/libraries/architecture/datastore

Direct DataLayer sample code https://github.com/android/wear-os-samples

TODO

- Use CapabilityClient instead of NodeClient
- Consider switching local/remote to owner/reader
- Support WearableListenerService to handle changes to data.

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-datalayer:<version>"
}
```

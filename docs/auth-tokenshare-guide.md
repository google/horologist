# Token sharing guide

This guide will walk you through on how to
securely [transfer authentication data](https://developer.android.com/training/wearables/apps/auth-wear#tokens)
from the phone app to the watch app using Horologist's Auth libraries.

## Requirements

Horologist Auth library is built on top
of [Wearable Data Layer API](https://developer.android.com/training/wearables/data/data-layer), so
your phone and watch apps must:

- have APK signatures and the signature schemes identical;
- the same package name;

## Getting started

1. Add dependencies

   Add the following dependencies to your project’s build.gradle.

   For the phone app project:

   ```groovy
   dependencies {
       implementation "com.google.android.horologist:horologist-auth-data-phone:<version>"
       implementation "com.google.android.horologist:horologist-datalayer:<version>"
   }
   ```

   For the watch app project:

   ```groovy
   dependencies {
       implementation "com.google.android.horologist:horologist-auth-data:<version>"
       implementation "com.google.android.horologist:horologist-datalayer:<version>"
   }
   ```

2. Create a `WearDataLayerRegistry`

   In both projects, create an instance
   of [WearDataLayerRegistry](https://google.github.io/horologist/api/datalayer/com.google.android.horologist.data/-wear-data-layer-registry/index.html)
   from the [datalayer](datalayer.md):

   ```kotlin
   val registry = WearDataLayerRegistry.fromContext(
       application = // application context,
       coroutineScope = // a coroutine scope
   )
   ```

3. Define the data to be transferred

   Define which authentication data that should be transferred from the phone to the watch. It can
   be a data class with many properties. For this guide, we will pass a simple `String` instance.

4. Create a `Serializer` for the data

   Create a `Serializer` class for the class defined to be transferred from the phone to
   the watch (`String` for this guide):

   ```kotlin
   public object TokenSerializer : Serializer<String> {
       override val defaultValue: String = ""
   
       override suspend fun readFrom(input: InputStream): String =
           InputStreamReader(input).readText()
   
       override suspend fun writeTo(t: String, output: OutputStream) {
           withContext(Dispatchers.IO) {
               output.write(t.toByteArray())
           }
       }
   }   
   ```
   
   This class should preferable be placed in a shared module between the phone and watch projects,
   but could also be duplicated in both projects.

   More information about this serialization
   in [this blog post](https://medium.com/androiddevelopers/datastore-and-kotlin-serialization-8b25bf0be66c).

5. Create a `TokenBundleRepository` on the phone project

   Create an instance
   of [TokenBundleRepository](https://google.github.io/horologist/api/auth-data-phone/com.google.android.horologist.auth.data.phone.tokenshare/-token-bundle-repository/index.html)
   on the phone app project:

   ```kotlin
   val tokenBundleRepository = TokenBundleRepositoryImpl.create(
       registry = registry,
       coroutineScope = // a coroutine scope,
       serializer = TokenSerializer
   )   
   ```

6. Send authentication data from the phone

   The authentication data can be sent from the phone calling `update`:

   ```kotlin
   tokenBundleRepositoryDefaultKey.update("token")
   ```

7. Create a `TokenBundleRepository` on the watch project

   Create an instance
   of [TokenBundleRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.tokenshare/-token-bundle-repository/index.html)
   on the watch app project:

   ```kotlin
   val tokenBundleRepository = TokenBundleRepositoryImpl.create(
       registry = registry,
       serializer = TokenSerializer
   )
   ```

8. Receive authentication data on the watch

   The authentication data can be listened from the watch via the `flow` property:
   ```kotlin
   tokenBundleRepository.flow
   ```   

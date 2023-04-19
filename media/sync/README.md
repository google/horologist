# Sync module

This is an adaptation of the code from Now in Android [repository][1].
The changes made to the original code was to make it more generic given their [model][2] is not open
for extension.

## Usage

### Implement Syncable

Repositories that should be synchronized with a remote source should implement interface `Syncable`,
using helper function `changeListSync` in the `syncWith` function.

The diff between local and remote data should be provided via `NetworkChangeList` based on the 
current and remote change list versions. If your database does not provide such capability, both the
diff and `NetworkChangeList` should be computed and built locally.

### Provide configurations via Dagger

In order to compile, the module will be expecting the following configurations to be provided via
DI using Dagger.

#### Syncables

An array of repositories that implement `Syncable`.

```kotlin
    @Singleton
    @Provides
    fun syncables(
        repositoryImpl: RepositoryImpl,
        repositoryImpl2: RepositoryImpl2,
    ): Array<Syncable> = arrayOf(
        repositoryImpl1,
        repositoryImpl2,
    )
```

#### ChangeListVersionRepository

An implementation of `ChangeListVersionRepository` which can provide a way of retrieving and 
updating the local change list version for a specific model. A typical implementation will use 
[Android Jetpack DataStore][3] to store this information. 

```kotlin
    @Provides
    fun changeListVersionRepository(): ChangeListVersionRepository =
        object : ChangeListVersionRepository {
            override suspend fun getChangeListVersion(model: String): Int {
                // implementation 
            }

            override suspend fun updateChangeListVersion(model: String, newVersion: Int) {
                // implementation
            }
        }
```

#### CoroutineDispatcherProvider

An implementation of `CoroutineDispatcherProvider` in order to provide dispatchers of your choice to
be executing this module's work.

```kotlin
    @Provides
    fun coroutineDispatcherProvider(): CoroutineDispatcherProvider =
        object : CoroutineDispatcherProvider {
            override fun getIODispatcher(): CoroutineDispatcher = Dispatchers.IO
        }
```

#### NotificationConfigurationProvider

An implementation of `NotificationConfigurationProvider` in order to provide the notifications
configurations, like title, name, description and icon.

```kotlin
    @Provides
    fun notificationConfigurationProvider(): NotificationConfigurationProvider =
        object : NotificationConfigurationProvider {
            override fun getNotificationTitle(): String = "title"

            override fun getNotificationIcon(): Int = R.drawable.ic_notification_icon

            override fun getChannelName(): String = "channel name"

            override fun getChannelDescription(): String = "channel description"
        }
```

### Initialization

In `onCreate` lifecycle function of `Application`, initialize the module:

```kotlin
    Sync.initialize(context = this)
```

[1]: https://github.com/android/nowinandroid/tree/4a0fbf99b2518384df1960e0045facd456a51539/sync/src/main/java/com/google/samples/apps/nowinandroid/sync

[2]: https://github.com/android/nowinandroid/blob/4a0fbf99b2518384df1960e0045facd456a51539/core-datastore/src/main/java/com/google/samples/apps/nowinandroid/core/datastore/ChangeListVersions.kt#L23-L26

[3]: https://developer.android.com/topic/libraries/architecture/datastore
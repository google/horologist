# DataLayer library

[![Maven Central](https://img.shields.io/maven-central/v/com.google.android.horologist/horologist-datalayer)](https://search.maven.org/search?q=g:com.google.android.horologist)

For more information, visit the documentation: https://google.github.io/horologist/datalayer

DataStore documentation https://developer.android.com/topic/libraries/architecture/datastore

Direct DataLayer sample code https://github.com/android/wear-os-samples

## DataLayer approach.

The Horologist DataLayer libraries, provide common abstractions on top of the Wearable DataLayer.
These build upon a common assumption of Google Protobug and gRPC, which allows sharing data
definitions throughout your Wear and Mobile apps.

See https://github.com/google/horologist/blob/main/auth/sample/shared/build.gradle.kts
for an example of configuring a build to use proto definitions.

```protobuf
syntax = "proto3";

message CounterValue {
  int64 value = 1;
  .google.protobuf.Timestamp updated = 2;
}

message CounterDelta {
  int64 delta = 1;
}

service CounterService {
  rpc getCounter(.google.protobuf.Empty) returns (CounterValue);
  rpc increment(CounterDelta) returns (CounterValue);
}
```

## Registering Serializers.

The WearDataLayerRegistry is an application singleton to register the Serializers.

```kotlin
object CounterValueSerializer : Serializer<CounterValue> {
    override val defaultValue: CounterValue
        get() = CounterValue.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CounterValue =
        try {
            CounterValue.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: CounterValue, output: OutputStream) {
        t.writeTo(output)
    }
}

val registry = WearDataLayerRegistry.fromContext(
    application = sampleApplication,
    coroutineScope = coroutineScope,
).apply {
    registerSerializer(CounterValueSerializer)
}
```

## Use Androidx DataStore

This library provides a new implementation of Androidx DataStore, in addition to the local
Proto and Preferences implementations.  The implementation uses the Wearable DataClient
with a single owner and multiple readers.

See https://developer.android.com/topic/libraries/architecture/datastore

Publishing a DataStore.

```kotlin
    private val dataStore: DataStore<CounterValue> by lazy {
        registry.protoDataStore<CounterValue>(lifecycleScope)
    }
```

Reading a remote DataStore.

```kotlin
val counterFlow = registry.protoFlow<CounterValue>(TargetNodeId.PairedPhone)
```

## Using gRPC

This library implements the gRPC transport over the Wearable MessageClient using the RPC request
feature.

Implementing a service.

```kotlin
class CounterService(val dataStore: DataStore<GrpcDemoProto.CounterValue>) :
    CounterServiceGrpcKt.CounterServiceCoroutineImplBase() {
        override suspend fun getCounter(request: Empty): GrpcDemoProto.CounterValue {
            return dataStore.data.first()
        }

        override suspend fun increment(request: GrpcDemoProto.CounterDelta): GrpcDemoProto.CounterValue {
            return dataStore.updateData {
                it.copy {
                    this.value = this.value + request.delta
                    this.updated = System.currentTimeMillis().toProtoTimestamp()
                }
            }
        }
    }

class WearCounterDataService : BaseGrpcDataService<CounterServiceGrpcKt.CounterServiceCoroutineImplBase>() {

    private val dataStore: DataStore<CounterValue> by lazy {
        registry.protoDataStore<CounterValue>(lifecycleScope)
    }

    override val registry: WearDataLayerRegistry by lazy {
        WearDataLayerRegistry.fromContext(
            application = applicationContext,
            coroutineScope = lifecycleScope,
        ).apply {
            registerSerializer(CounterValueSerializer)
        }
    }

    override fun buildService(): CounterServiceGrpcKt.CounterServiceCoroutineImplBase {
        return CounterService(dataStore)
    }
}
```

Calling a remote service.

```kotlin
val client = registry.grpcClient(
    nodeId = TargetNodeId.PairedPhone,
    coroutineScope = sampleApplication.servicesCoroutineScope,
) {
    CounterServiceGrpcKt.CounterServiceCoroutineStub(it)
}

// Call the increment method from the proto service definition
val newValue: CounterValue =
    counterService.increment(counterDelta { delta = i.toLong() })
```

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-datalayer:<version>"
}
```

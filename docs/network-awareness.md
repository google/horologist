# Network Awareness library

## Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.google.android.horologist:horologist-network-awareness:<version>"
}
```

## Network access on Wear OS

On Wear OS, the choice of network is critical to build efficient applications. See
[network documentation](https://developer.android.com/training/wearables/data/network-access) for more information.

The default behaviour is:

- Use paired Bluetooth connection when it is available.
- Use Wi-Fi when available and watch is charging.
- Use LTE if no other network is available.
- Wi-Fi and LTE can be requested by the application.

Without intentional network strategy, following situations may happen:

- Downloading large downloads over the slow and shared bluetooth connection. This may overload the
  connection and starve other applications. Instead, Wi-Fi should be used to 
  quickly download and then close the connection. 
- Using LTE for requests that could be postponed until watch is connected to Wi-Fi.
- Using a single current network for all traffic regardless of length or important of the request.

## Functionality

This library allows defining rules based on the RequestType and NetworkType, currently integrated
into OkHttp.

```kotlin
public interface NetworkingRules {
    /**
     * Is this request considered high bandwidth and should activate LTE or Wifi.
     */
    public fun isHighBandwidthRequest(requestType: RequestType): Boolean

    /**
     * Checks whether this request is allowed on the current network type.
     */
    public fun checkValidRequest(
        requestType: RequestType,
        currentNetworkInfo: NetworkInfo
    ): RequestCheck

    /**
     * Returns the preferred network for a request.
     *
     * Null means no suitable network.
     */
    public fun getPreferredNetwork(
        networks: Networks,
        requestType: RequestType
    ): NetworkStatus?
}
```

The library also allows logging network usage and provides information about network status, 
for example for media apps could be used to signal that the watch is offline so only downloaded 
content is available to play.

See [media-sample](media-sample.md) for an example how Network Awareness is used. Key classes to observe the usage are:

- [UampNetworkingRules](https://github.com/google/horologist/blob/main/media/sample/src/main/java/com/google/android/horologist/mediasample/di/config/UampNetworkingRules.kt) - defining the app specific rules for the network.
- [MediaInfoTimeText](https://github.com/google/horologist/blob/main/media/sample/src/main/java/com/google/android/horologist/mediasample/ui/debug/MediaInfoTimeText.kt) - An example of displaying network status and usage to the user.
- [NetworkAwareCallFactory](https://github.com/google/horologist/blob/main/network-awareness/okhttp/src/main/java/com/google/android/horologist/networks/okhttp/NetworkAwareCallFactory.kt) - Used to wrap an OkHttp Call. Factory when passed to a library such as Coil.
- [NetworkSelectingCallFactory](https://github.com/google/horologist/blob/main/network-awareness/okhttp/src/main/java/com/google/android/horologist/networks/okhttp/NetworkSelectingCallFactory.kt) - Used to apply the networking rules to OkHttp.
- [NetworkRepository](https://github.com/google/horologist/blob/main/network-awareness/core/src/main/java/com/google/android/horologist/networks/status/NetworkRepository.kt) - The Repository for the current network state.
- [HighBandwidthNetworkMediator](https://github.com/google/horologist/blob/main/network-awareness/core/src/main/java/com/google/android/horologist/networks/highbandwidth/HighBandwidthNetworkMediator.kt) - a mediator for requesting high bandwidth networks.

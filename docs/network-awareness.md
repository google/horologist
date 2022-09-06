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

## Problem Statement

On Wear choice of network is critical to efficient applications. See
https://developer.android.com/training/wearables/data/network-access for more information.

The default behaviour is roughly

- Use Paired Bluetooth connection when it is available.
- Use Wifi when available and Charging.
- Use LTE if no other network is available.
- Wifi and Cell may be requested specifically by the application.

This leads to some suboptimal decisions

- Downloading large downloads over the slow and shared bluetooth connection.  This may overload the
  connection and starve other applications. Instead, Wifi or Cellular would be better used to 
  quickly download and then close the connection.
- Using LTE for trivial requests when not really required.
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

It allow allows logging network usage and visibility into network status.

See media-sample for an example. Key classes to observe usage of

- UampNetworkingRules - defining the app specific rules for the network.
- MediaInfoTimeText - An example of displaying network status and usage to the user.
- NetworkAwareCallFactory - Used to wrap an OkHttp Call.Factory when passed to a library such as Coil.
- NetworkSelectingCallFactory - Used to apply the networking rules to OkHttp.
- NetworkRepository - The Repository for the current network state.
- HighBandwidthNetworkMediator - a mediator for requesting high bandwidth networks.

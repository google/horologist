# Auth Data

This library contains implementation for Wear apps for most of the authentication methods listed in
the [Authentication on wearables](https://developer.android.com/training/wearables/apps/auth-wear)
guide.

The repositories of this library are built mainly to support the components
from [auth-ui](auth-ui.md) library, but can be used with your own UI components.

## [Token sharing](https://developer.android.com/training/wearables/apps/auth-wear#tokens)

- [TokenBundleRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.tokenshare/-token-bundle-repository/index.html)
    - [TokenBundleRepositoryImpl](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.tokenshare.impl/-token-bundle-repository-impl/index.html)

## [Google Sign-In](https://developer.android.com/training/wearables/apps/auth-wear#Google-Sign-in)

- [GoogleSignInAuthUserRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.googlesignin/-google-sign-in-auth-user-repository/index.html)

## [OAuth (PKCE)](https://developer.android.com/training/wearables/apps/auth-wear#pkce)

- [PKCEConfigRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.oauth.pkce/-p-k-c-e-config-repository/index.html)
    - [PKCEConfigRepositoryGoogleImpl](https://google.github.io/horologist/api/auth-data-watch-oauth/com.google.android.horologist.auth.data.watch.oauth.pkce.impl.google/-p-k-c-e-config-repository-google-impl/index.html)
- [PKCEOAuthCodeRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.oauth.pkce/-p-k-c-e-o-auth-code-repository/index.html)
    - [PKCEOAuthCodeRepositoryImpl](https://google.github.io/horologist/api/auth-data-watch-oauth/com.google.android.horologist.auth.data.watch.oauth.pkce.impl/-p-k-c-e-o-auth-code-repository-impl/index.html)
- [PKCETokenRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.oauth.pkce/-p-k-c-e-token-repository/index.html)
    - [PKCETokenRepositoryGoogleImpl](https://google.github.io/horologist/api/auth-data-watch-oauth/com.google.android.horologist.auth.data.watch.oauth.pkce.impl.google/-p-k-c-e-token-repository-google-impl/index.html)

## [OAuth (Device Grant)](https://developer.android.com/training/wearables/apps/auth-wear#DAG)

- [DeviceGrantConfigRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.oauth.devicegrant/-device-grant-config-repository/index.html)
    - [DeviceGrantConfigRepositoryDefaultImpl](https://google.github.io/horologist/api/auth-data-watch-oauth/com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl/-device-grant-config-repository-default-impl/index.html)
- [DeviceGrantTokenRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.oauth.devicegrant/-device-grant-token-repository/index.html)
    - [DeviceGrantTokenRepositoryGoogleImpl](https://google.github.io/horologist/api/auth-data-watch-oauth/com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.google/-device-grant-token-repository-google-impl/index.html)
- [DeviceGrantVerificationInfoRepository](https://google.github.io/horologist/api/auth-data/com.google.android.horologist.auth.data.oauth.devicegrant/-device-grant-verification-info-repository/index.html)
    - [DeviceGrantVerificationInfoRepositoryGoogleImpl](https://google.github.io/horologist/api/auth-data-watch-oauth/com.google.android.horologist.auth.data.watch.oauth.devicegrant.impl.google/-device-grant-verification-info-repository-google-impl/index.html)

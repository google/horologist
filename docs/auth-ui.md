# Auth UI

This library contains a set of composables screens and components related to authentication.

The previews of the composables can be found in the `debug` folder of the module source code.

The composables of this module might depend on repository interfaces defined
in [auth-data](auth-data.md)
library. The implementation of these repositories does not necessarily need to be from `auth-data`,
they can be your own implementation. Some of the composables might depend on an external library.

## Screens

### Common

#### [SignInPromptScreen](https://google.github.io/horologist/api/auth/ui/com.google.android.horologist.auth.ui.common.screens.prompt/-sign-in-prompt-screen.html)

A screen to prompt users to sign in.

It helps achieve to the following [best practices][best_practices]:

- [Explain sign-in benefits][explain_benefits]
- [Provide alternatives][provide_alternatives]

[best_practices]: https://developer.android.com/training/wearables/apps/auth-wear

[explain_benefits]: https://developer.android.com/training/wearables/design/sign-in#benefits

[provide_alternatives]: https://developer.android.com/training/wearables/design/sign-in#alternatives

### Google Sign-In

#### [GoogleSignInScreen](https://google.github.io/horologist/api/auth/ui/com.google.android.horologist.auth.ui.googlesignin.signin/-google-sign-in-screen.html)

A screen for
the [Google Sign-In](https://developer.android.com/training/wearables/apps/auth-wear#Google-Sign-in)
authentication method.

It uses different screens from [auth-composables](auth-composables.md) to display the full
authentication flow.

| <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/sign_in_placeholder_screen.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/select_account_screen.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120" > |
|:---------------------------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------:|

It relies on
the [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android/start)
library for authentication and account selection. So an instance
of [GoogleSignInClient](https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInClient)
has to be provided to `GoogleSignInViewModel`.

### OAuth

#### [PKCESignInScreen](https://google.github.io/horologist/api/auth/ui/com.google.android.horologist.auth.ui.oauth.pkce.signin/-p-k-c-e-sign-in-screen.html)

A screen for
the [OAuth (PKCE)](https://developer.android.com/training/wearables/apps/auth-wear#pkce)
authentication method.

It uses different screens from [auth-composables](auth-composables.md) to display the full
authentication flow.

| <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/sign_in_placeholder_screen.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/check_your_phone_screen.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120" > |
|:---------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------:|

A implementation for the following repositories are required to be provided:

- [PKCEConfigRepository](https://google.github.io/horologist/api/auth/data/com.google.android.horologist.auth.data.oauth.pkce/-p-k-c-e-config-repository/index.html)
- [PKCEOAuthCodeRepository](https://google.github.io/horologist/api/auth/data/com.google.android.horologist.auth.data.oauth.pkce/-p-k-c-e-o-auth-code-repository/index.html)
- [PKCETokenRepository](https://google.github.io/horologist/api/auth/data/com.google.android.horologist.auth.data.oauth.pkce/-p-k-c-e-token-repository/index.html)

#### [DeviceGrantSignInScreen](https://google.github.io/horologist/api/auth/ui/com.google.android.horologist.auth.ui.oauth.devicegrant.signin/-device-grant-sign-in-screen.html)

A screen for
the [OAuth (Device Grant)](https://developer.android.com/training/wearables/apps/auth-wear#DAG)
authentication method.

It uses different screens from [auth-composables](auth-composables.md) to display the full
authentication flow.

| <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/sign_in_placeholder_screen.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/check_your_phone_screen_code.png" height="120" width="120" > | <img src="https://media.githubusercontent.com/media/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120" > |
|:---------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------:|

A implementation for the following repositories are required to be provided:

- [DeviceGrantConfigRepository](https://google.github.io/horologist/api/auth/data/com.google.android.horologist.auth.data.oauth.devicegrant/-device-grant-config-repository/index.html)
- [DeviceGrantVerificationInfoRepository](https://google.github.io/horologist/api/auth/data/com.google.android.horologist.auth.data.oauth.devicegrant/-device-grant-verification-info-repository/index.html)
- [DeviceGrantTokenRepository](https://google.github.io/horologist/api/auth/data/com.google.android.horologist.auth.data.oauth.devicegrant/-device-grant-token-repository/index.html)

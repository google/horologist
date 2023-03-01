# Auth UI

This library contains a set of composables screens and components related to authentication.

The previews of the composables can be found in the `debug` folder of the module source code.

The composables of this module might depend on an implementation of the repositories defined
in [auth-data](auth-data.md) library. Some of them might depend on an external library.

## Screens

### Common

#### SignInPromptScreen

A screen to prompt users to sign in.

It helps achieve to the following [best practices][best_practices]:

- [Explain sign-in benefits][explain_benefits]
- [Provide alternatives][provide_alternatives]

[best_practices]: https://developer.android.com/training/wearables/apps/auth-wear

[explain_benefits]: https://developer.android.com/training/wearables/design/sign-in#benefits

[provide_alternatives]: https://developer.android.com/training/wearables/design/sign-in#alternatives

### Google Sign-In

#### GoogleSignInScreen

A screen for
the [Google Sign-In](https://developer.android.com/training/wearables/apps/auth-wear#Google-Sign-in)
authentication method.

It uses different screens from [auth-composables](auth-composables.md) to display the full
authentication flow.

| <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/sign_in_placeholder_screen.png" height="120" width="120" > | <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/select_account_screen.png" height="120" width="120" > | <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120" > |
|:---------------------------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------:|

It relies on
the [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android/start)
library for authentication and account selection. So an instance
of [GoogleSignInClient](https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInClient)
has to be provided to `GoogleSignInViewModel`.

### OAuth

#### PKCESignInScreen

A screen for
the [OAuth (PKCE)](https://developer.android.com/training/wearables/apps/auth-wear#pkce)
authentication method.

It uses different screens from [auth-composables](auth-composables.md) to display the full
authentication flow.

| <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/sign_in_placeholder_screen.png" height="120" width="120" > | <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/check_your_phone_screen.png" height="120" width="120" > | <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120" > |
|:---------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------:|

A implementation for the following repositories are required to be provided:

- PKCEConfigRepository
- PKCEOAuthCodeRepository
- PKCETokenRepository

#### DeviceGrantSignInScreen

A screen for
the [OAuth (Device Grant)](https://developer.android.com/training/wearables/apps/auth-wear#DAG)
authentication method.

It uses different screens from [auth-composables](auth-composables.md) to display the full
authentication flow.

| <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/sign_in_placeholder_screen.png" height="120" width="120" > | <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/check_your_phone_screen_code.png" height="120" width="120" > | <img src="https://raw.githubusercontent.com/google/horologist/main/docs/auth-composables/signed_in_confirmation_dialog.png" height="120" width="120" > |
|:---------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------:|

A implementation for the following repositories are required to be provided:

- DeviceGrantConfigRepository
- DeviceGrantVerificationInfoRepository
- DeviceGrantTokenRepository

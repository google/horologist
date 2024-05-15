# Auth sample Wear app

For more information, visit the
documentation: https://google.github.io/horologist/auth-sample-apps#wear-sample

## Google Sign-in

Note: Google Sign-in is now  deprecated. Do not integrate with Google Sign-in if creating a new app.

In  order to successfully sign-in, follow the instructions for registering your app within the
Google API Console:

https://developers.google.com/identity/sign-in/android/start-integrating

- debug app id: `com.google.android.horologist.auth.sample.debug`

- SHA-1: grab from key tool output below, likely using "android" as password.

    ```
    keytool -keystore ~/.android/debug.keystore -list -v
    ```

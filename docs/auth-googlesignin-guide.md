# Google Sign-In guide

This guide will walk you through on how to display a screen on your watch app so that users can
select their Google account to sign-in to your app.

## Requirements

Follow the setup instructions for integrating Google Sign-in into an Android app
from [this link](https://developers.google.com/identity/sign-in/android/start-integrating).

## Getting started

1.  Add dependencies

   Add the following dependencies to your project’s build.gradle:

   ```groovy
   dependencies {
       implementation "com.google.android.horologist:horologist-auth-composables:<version>"
       implementation "com.google.android.horologist:horologist-auth-ui:<version>"
       implementation "com.google.android.horologist:horologist-base-ui:<version>"
   }
   ```

2.  Create an instance of `GoogleSignInClient`

   Create an instance
   of [GoogleSignInClient](https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInClient),
   according to your requirements, for example:

   ```kotlin
   val googleSignInClient = GoogleSignIn.getClient(
       applicationContext,
       GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestEmail()
           .build()
   )
   ```

## Display the screen

1.  Create a ViewModel

   Create your implementation of `GoogleSignInViewModel`, passing the `GoogleSignInClient` created:
   ```kotlin
   class MyGoogleSignInViewModel(
       googleSignInClient: GoogleSignInClient,
   ) : GoogleSignInViewModel(googleSignInClient)
   ```   

2.  Display the screen

   Display the `GoogleSignInScreen` passing an instance of the `GoogleSignInViewModel` created:

   ```koltin
   GoogleSignInScreen(
      onAuthCancelled = { /* code to navigate to another screen on this event */ },
      onAuthSucceed = { /* code to navigate to another screen on this event */ },
      viewModel = hiltViewModel<MyGoogleSignInViewModel>()
   )
   ```

   This sample uses Hilt to retrieve an instance of the ViewModel, but you should use what suits
   your project best,
   see [this link](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-cheatsheet)
   for more info.

## Retrieve the signed in account

In order to have access an instance of
the [GoogleSignInAccount](https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInAccount)
selected by the user, follow the steps:

1.  Implement `GoogleSignInEventListener`

   ```kotlin
   class GoogleSignInEventListenerImpl : GoogleSignInEventListener {
       override suspend fun onSignedIn(account: GoogleSignInAccount) {
           // your implementation using the account parameter
       }
   }
   ```

2.  Pass the listener to the ViewModel

   Pass an instance of `GoogleSignInEventListener` to `GoogleSignInViewModel`:

   ```kotlin
   class MyGoogleSignInViewModel(
       googleSignInClient: GoogleSignInClient,
       googleSignInEventListener: GoogleSignInEventListener,
   ) : GoogleSignInViewModel(googleSignInClient, googleSignInEventListener)
   ```

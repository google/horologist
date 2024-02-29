# DataLayer phone UI

The DataLayer phone UI library provides an implementation for phone prompts UI that can be used to
bring more users to the Wear app.

## Getting started

Please follow the "Getting started" from the [Datalayer App Helper](datalayer-helpers-guide.md)
guide in order to set up the apps and create an instance
of [PhoneDataLayerAppHelper](https://google.github.io/horologist/api/datalayer/phone/com.google.android.horologist.datalayer.phone/-phone-data-layer-app-helper/index.html).

Also refer to "Check API availability" from the [Datalayer App Helper](datalayer-helpers-guide.md)
guide to check if the required APIs are available.

## Basic usage

```kotlin
val installPrompt = InstallAppPrompt(phoneDataLayerAppHelper = phoneDataLayerAppHelper)

if (installPrompt.shouldDisplayPrompt()) {
    val intent = installAppPrompt.getIntent(
        context = context,
        appPackageName = context.packageName,
        image = R.drawable.image_drawable,
        topMessage = "top message",
        bottomMessage = "bottom message",
    )
    startActivity(intent)
}
```

The code above uses the install prompt as example, but the usage of other prompts are similar.

## Check user's action

In order to check what was the action taken by the user, refer
to [Get a result from an activity](https://developer.android.com/training/basics/intents/result).

For example, if using Compose:

```kotlin
val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
) { result ->
  if (result.resultCode == RESULT_OK) {
    // user pushed install!
  } else {
    // user dismissed the prompt.
  }
}

if (installPrompt.shouldDisplayPrompt()) {
  val intent = installAppPrompt.getIntent(
      context = context,
      appPackageName = context.packageName,
      image = R.drawable.image_drawable,
      topMessage = "top message",
      bottomMessage = "bottom message",
  )
  
  launcher.launch(intent)
}
```

The code above uses the install prompt as example, but the usage of other prompts are similar.

## Install app

Use
the [InstallAppPrompt](https://google.github.io/horologist/api/datalayer/phone-ui/com.google.android.horologist.datalayer.phone.ui.prompt.installapp/-install-app-prompt/index.html)
to build the UI of a prompt to ask the user to install the app on their watch.

The `shouldDisplayPrompt` method allows to check if the prompt should be displayed, based on the
following conditions:

- there is a watch connected
- the app is not installed

The `shouldDisplayPrompt` method is relying on the capability defined in the wear.xml of the Wear
app, if the Wear app is installed
but hasn't been updated with the capability definition, this method may still return
a `AppHelperNodeStatus`
indicating that the Wear app is not installed.

## ReEngage prompt

Use
the [ReEngagePrompt](https://google.github.io/horologist/api/datalayer/phone-ui/com.google.android.horologist.datalayer.phone.ui.prompt.reengage/-re-engage-prompt/index.html)
to build the UI of a prompt to ask the user to install the app on their
watch.

The `shouldDisplayPrompt` method allows to check if the prompt should be displayed, based on the
following conditions:

- there is a watch connected
- the app is installed

## SignIn prompt

Use
the [SignInPrompt](https://google.github.io/horologist/api/datalayer/phone-ui/com.google.android.horologist.datalayer.phone.ui.prompt.signin/-sign-in-prompt/index.html)
to build the UI of a prompt to ask the user to finish the sign in on the
watch.

The `shouldDisplayPrompt` method allows to check if the prompt should be displayed, based on the
following conditions:

- there is a watch connected
- the app is installed
- the app was not marked as "setup complete" (see "Tracking the app has been set up"
  in [Datalayer App Helper](datalayer-helpers-guide.md) guide)

## Custom prompts

If desired to display your own custom prompt, this API can still be helpful. 

Use `shouldDisplayPrompt` to check if there conditions to display the prompt are met: 

```kotlin
val installPrompt = InstallAppPrompt(phoneDataLayerAppHelper = phoneDataLayerAppHelper)

if (installPrompt.shouldDisplayPrompt()) {
    // display your own prompt
}
```

When the user tap on the positive button of your prompt, call `performAction`:

```kotlin
installPrompt.performAction(context, context.packageName)
```

The code above uses the install prompt as example, but the usage of other prompts are similar.

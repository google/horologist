# DataLayer phone UI

The DataLayer phone UI library provides an implementation for phone prompts UI that can be used to
bring more users to the Wear app.

## Install app
Use the `InstallAppPrompt` to build the UI of a prompt to ask the user to install the app on their watch.

The `shouldDisplayPrompt` method allows to check if the prompt should be displayed, based on the following conditions:

- there is a watch connected

- the app is not installed

The `shouldDisplayPrompt` method is relying on the capability defined in the wear.xml of the Wear app, if the Wear app is installed 
but hasn't been updated with the capability definition, this method may still return a `AppHelperNodeStatus`
indicating that the Wear app is not installed.

## ReEngage prompt
Use the `ReEngagePrompt` to build the UI of a prompt to ask the user to install the app on their watch.
The `shouldDisplayPrompt` method allows to check if the prompt should be displayed, based on
the criteria that the app is already installed.

## SignIn prompt
Use the `SignInPrompt` to build the UI of a prompt to ask the user to finish the sign in on the watch.
The `shouldDisplayPrompt` method allows to check if the prompt should be displayed, if any of the 
following conditions for the `UsageStatus` is true: 

- `UsageStatus.UNRECOGNIZED`
- `UsageStatus.USAGE_STATUS_UNSPECIFIED`
- `UsageStatus.USAGE_STATUS_LAUNCHED_ONCE`

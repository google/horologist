# Auth libraries

## Overview

The purpose of the auth libraries is to:

- help developers to build apps following
  the [Sign-In guidelines for Wear OS](https://developer.android.com/training/wearables/design/sign-in);
- provide implementation (for Wear and Mobile) for most of the authentication methods listed in
  the [Authentication on wearables](https://developer.android.com/training/wearables/apps/auth-wear)
  guide;

The following libraries are provided:

- [auth-composables](auth-composables.md): composable screens for Authentication use cases, with no
  dependency on the `auth-data` library.
- [auth-ui](auth-ui.md): composable screens for Authentication use cases, with integration with
  the `auth-data` library.
- [auth-data](auth-data.md): implementation for Wear apps for most of the authentication methods
  listed in
  the [Authentication on wearables](https://developer.android.com/training/wearables/apps/auth-wear)
  guide.
- [auth-data-phone](auth-data-phone.md): implementation for Mobile apps for some of the
  authentication methods provided by the `auth-data` library.

The following sample apps are also provided:

- [auth-sample-wear](auth-sample-apps.md#wear-sample): sample wear app to authenticate using
  different methods.
- [auth-sample-phone](auth-sample-apps.md#phone-sample): sample phone app to authenticate using
  different methods.

## Architecture overview

The auth libraries are separated by layers (UI and data), following
the [recommended app architecture](https://developer.android.com/topic/architecture#recommended-app-arch).
The reason for including an extra UI library (`auth-composables`) is to provide flexibility to
projects that would like to only use the UI components that are not dependent on `auth-data`.

## Getting started

The usage of the auth libraries will vary according to the requirements of your project.

As per [architecture overview](auth-overview.md#architecture-overview), your project might not need
to add all the auth libraries as dependency. If that’s the case, refer to the documentation of each
library required to your project for a guide on how to get started.

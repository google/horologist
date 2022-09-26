# Media Toolkit

## Overview

Horologist provides what it is called the "Media Toolkit": a set of libraries to build media apps on
Wear OS and a sample app that you can run to see the toolkit in action.

The following modules in the Horologist project are part of the toolkit:

- [media-ui](./media-ui): common media UI components and screens like `PlayerScreen`.
- [media](./media): domain model for media related functionality. Provides an abstraction to the UI
  module (`media-ui`) that is agnostic to the `Player` implementation.
- [media-data](./media-data): implementation of the domain module (`media`) using Media3.
- [media3-backend](./media3-backend): `Player` on top of Media3 including functionalities such as
  avoiding playing music on the watch speaker.
- [media-sample](./media-sample): sample app to listen to downloaded music.

## Architecture overview

The Media Toolkit libraries are separated by layers (UI, domain and data) following
the [recommended app architecture](https://developer.android.com/topic/architecture#recommended-app-arch)
.

The reason for including a domain layer is to provide flexibility to projects to use
the [UI library][ui library] or the [data library][data library] independently.

For example, if your project already contains an implementation for the player and you are only
interested in using the media screens provided by the toolkit, then only
the [UI library][ui library] needs to be added as a dependency. Thus, no extra dependencies (
e.g. [Media3][media3]) will be added to your project.

On the other hand, if your project does not need any of the media screens or media UI components
provided by the [UI library][ui library], and you are only interested in the player implementation,
then only the [data library][data library] needs to be added as a dependency to your project.

## Getting started

The usage of the toolkit will vary according to the requirements of your project.

As per [architecture overview](#architecture-overview), your project might not need to add all the
libraries of the toolkit as dependency. If that’s the case, refer to the documentation of each
library required to your project for a guide on how to get started.

For a walkthrough on how to build a very simple media application using some libraries of the
toolkit, refer to this [guide](simple-media-app-guide.md).

For good reference on how to use all the libraries available in the toolkit, refer to the code of
the [media-sample][media sample] app.

[ui library]: https://google.github.io/horologist/media-ui/

[data library]: https://google.github.io/horologist/media-data/

[media3]: https://developer.android.com/jetpack/androidx/releases/media3

[media sample]: https://google.github.io/horologist/media-sample/

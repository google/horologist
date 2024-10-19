# Media UI library - hard-linked to media3-common

**Warning: This is a prototype and not meant for consumption!**

Builds on top of the media-ui library to demonstrate a different architectural
approach that is meant to be more composable by nature.

Each UI component observes and interacts with the `Player` instance individually,
thereby preventing a big PlayerUiState that needs to know about everything.

It assumes that the consumer of this library uses media3-common and implements
the `Player` interface, although the implementation is left up to them. It could
be an Exoplayer instance, but just as wel a `MediaSession` or `CastPlayer`.
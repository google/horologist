# Media UI Material 3 Library

This library contains a set of high-quality Wear OS Jetpack Compose components and screens for media player applications, fully built on top of **Wear Compose Material 3**.

It includes:

*   **Individual Controls**: Specialized buttons for play/pause, skip, shuffle, and volume.
*   **Media Scaffolds**: The modern `MediaPlayerScaffold` which handles key-based navigation, volume indicators, and responsive page layouts.
*   **Ready-to-use Screens**: Full-screen components like `PlaylistStreamingScreen`, `PlaylistDownloadScreen`, and `PlaylistsScreen`.

This library is not dependent on any specific player implementation as per the [architecture overview](media-toolkit.md#architecture-overview).

---

## Modern Material 3 Scaffolding & Type-Safe Navigation

The centerpiece of the Material 3 Media library is the **`MediaPlayerScaffold`**. 

Unlike legacy scaffolds, it integrates natively with **Jetpack Navigation 3** and its key-based backstack (`NavBackStack<MediaRoute>`). This enables **100% type-safe routing** and deep-link parsing. Destinations are defined as `@Serializable` objects and classes that implement a sealed `MediaRoute` interface, completely eliminating manual string routing, query parameter parsing, and URL encoding.

### Type-Safe Route Setup Example:

```kotlin
// Define your type-safe routes
@Serializable
public sealed interface MediaRoute : NavKey

@Serializable
public data class PlayerRoute(val page: Int = 0) : MediaRoute

@Serializable
public data class CollectionRoute(val id: String, val name: String) : MediaRoute
```

---

## Component Best Practices & Implementation Rules

When building or extending components in this library, please adhere strictly to the following Material 3 design and implementation guidelines:

### 1. Rendering Full-Color Artwork
For media artwork (like album covers, playlist covers, or podcast avatars), **do not use the `Icon` component**. By default, `Icon` applies the `LocalContentColor` tint, turning full-color images into solid single-color blocks. 

Instead, use the `Image` component, clipped to a circle and cropped using `ContentScale.Crop` to match the Wear OS design guidelines:



---

## Stateful vs. Stateless Components

Like the Material 2 version, most of the components in this library provide both a **stateless** and a **stateful** version:

*   **Stateless Components**: Provide maximum customizability. They accept raw state values and lambdas for event handling.
*   **Stateful Components**: Provide maximum convenience. They accept a `PlayerViewModel` or a `PlayerUiState` and handle all player state wiring and event routing automatically.

For more details on connecting view models and UI states, refer to the [Stateful PlayerScreen guide](media-playerscreen.md).

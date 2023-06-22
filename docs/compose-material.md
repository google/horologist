# Compose Material

A library providing opinionated implementation of the components of
the [Wear Material Compose library][1], based on the specifications of
[Wear Material Design Kit][2].

## Motivation

In order to display a `Chip` component in a Wear OS app, using [Wear Material Compose library][1],
the code would look like this:

```kotlin
Chip(
    label = { Text("Primary label") },
    onClick = { },
    secondaryLabel = { Text("Secondary label") },
    icon = { Icon(imageVector = Icons.Default.Image, contentDescription = null) }
)
```

In comparison, using Horologist's Compose Material library, the code would look simpler:

```kotlin
Chip(
    label = "Primary label",
    onClick = { },
    secondaryLabel = "Secondary label",
    icon = Icons.Default.Image
)
```

As seen above, Horologist's Compose Material provides convenient ways of passing parameters to the
components. Furthermore, for this particular component, it will also take care of:

- Define the maximum number of lines for each label, truncating them appropriately, even when the
  user has changed the font size in the OS settings;
- Change the text alignment and maximum number of lines of the primary label when the secondary
  label is not present;
- Adjust the content padding based on the icon size;

The list is not exhaustive and it varies for each individual component.

## When this library should not be used?

If the specifications for the component needed in your app does not match the specifications of the
components listed in [Wear Material Design Kit][2], then [Wear Material Compose library][1] should
be used instead.

[1]: https://developer.android.com/jetpack/androidx/releases/wear-compose

[2]: https://developer.android.com/design/ui/wear/guides/foundations/download

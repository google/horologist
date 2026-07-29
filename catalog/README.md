# Horologist preview catalog

One Gradle module, `:catalog`, holding curated `@Preview`s for Horologist's UI surfaces across both
form factors. Rendered outside Android Studio by the
[`ee.schimke.composeai.preview`](https://github.com/yschimke/compose-ai-tools) plugin:

```
./gradlew :catalog:composePreviewRenderAll
# PNGs land in catalog/build/compose-previews/renders/
```

## Why one module

Wear and mobile share a module rather than splitting into `:catalog:wear` and `:catalog:mobile`.
The device is a per-preview property — `@Preview(device = "id:pixel_7")` versus
`@Preview(device = "id:wearos_large_round")` — not a per-module one, so a split would buy nothing
and cost a second module, a second render invocation, and a duplicated dependency block. Verified
end to end: a single `composePreviewRenderAll` renders the Wear stickers at 227×227dp and the phone
stickers at 411×914dp in the same pass.

## How sections are declared

Sections are annotations, not configuration. There is no catalog JSON, no manifest, and no registry
to keep in sync — [`CatalogPreviews.kt`](src/main/java/com/google/android/horologist/catalog/CatalogPreviews.kt)
declares one multipreview annotation per (area, form factor) pair, each fixing the device, the
background, and the `group`:

```kotlin
@Preview(device = "id:wearos_large_round", backgroundColor = 0xFF000000, showBackground = true, group = "Media")
public annotation class MediaCatalog
```

and a preview is then just:

```kotlin
@MediaCatalog
@Composable
internal fun MediaNothingPlayingDisplay() { … }
```

The group reaches `previews.json` and the rendered filenames
(`MediaNothingPlayingDisplay_Media.png`), so the grouping is visible to every downstream consumer
without anything else being declared anywhere.

The form factor is only spelled out when an area spans both — `Auth Wear` / `Auth Mobile`. An area
that only exists on the watch is just `Media`, `Material`, `Composables`, `Health`, and reads as
Wear by default, matching how Horologist itself is described.

Adding an area means adding an annotation and a file. Nothing in the build wiring is per-area.

| Section | Form factor | Source library |
| --- | --- | --- |
| Auth Wear | Wear | `:auth:composables-material3`, `:auth:ui-material3` |
| Media | Wear | `:media:ui-material3` |
| Material | Wear | `:compose-material` |
| Composables | Wear | `:composables` |
| Health | Wear | `:health:composables` |
| Auth Mobile | Phone | `:datalayer:phone-ui` |
| DataLayer Mobile | Phone | `:datalayer:phone-ui` |

## Relationship to the per-library previews

Several libraries already apply the same plugin and render whatever previews happen to live in
their own `src/debug` (`:auth:composables`, `:compose-material`, `:media:ui`, …). Those stay as
they are — they're the library author's working previews. This module is the curated counterpart:
one place where a surface is previewed with realistic data, on the device it ships to, grouped so a
reader can navigate by area rather than by Gradle path.

## Known gap: dialogs and bottom sheets render blank

`Dialog`- and `ModalBottomSheet`-based composables compose into their own window, and the renderer
captures the host activity's root view. Those previews are discovered, sized, and grouped
correctly, but the PNG comes out empty. It affects both form factors:

- `AuthWearSignedInConfirmationDialog` / `…Truncated` (Wear `Dialog`)
- every `Auth Mobile` and `DataLayer Mobile` sticker (`ModalBottomSheet`)

This is a renderer-side limitation, not a catalog one — the same previews are blank whether they
live here or in a library's `src/debug`. The fix belongs in the compose-preview renderer (capture
the topmost window when one is present); the previews are kept here so they start working the day
that lands.

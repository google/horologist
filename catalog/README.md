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

| Section | Form factor | Previews | Source library |
| --- | --- | --- | --- |
| Material | Wear | 16 | `:compose-material` |
| Media | Wear | 16 | `:media:ui-material3` |
| Auth Wear | Wear | 13 | `:auth:composables-material3`, `:auth:ui-material3` |
| Composables | Wear | 10 | `:composables` |
| Health | Wear | 7 | `:health:composables` |
| Audio | Wear | 5 | `:media:audio-ui-material3` |
| AI | Wear | 5 | `:ai:ui` |
| Layout | Wear | 3 | `:compose-layout` |
| DataLayer Mobile | Phone | 3 | `:datalayer:phone-ui` |
| Auth Mobile | Phone | 2 | `:datalayer:phone-ui` |

80 curated component previews are published, plus three generated dark-theme specimen sheets.
Sections cover a component's *states*, not just its happy path — disabled
seek buttons at a queue end, an account row with no display name, a five-digit metric, an empty and
a complete segmented indicator — because those are the cases that break and the ones a static
screenshot is good at catching.

Previews that would otherwise read the wall clock (the date/time pickers, the AI prompt timestamp,
exercise durations) take fixed values. A preview whose output depends on `now()` renders
differently on every run and reports as a spurious diff on every PR.

The same rule governs artwork. The Media section's artwork-bearing previews (`MediaArtwork`,
`MediaDetailsButtonWithArtwork`, `MediaEntityScreenWithArtwork`) load a local drawable through
`CoilPaintable`, not a remote URL — the renderer resolves coil requests inline but refuses
`http(s)://` models on purpose, since pixels that depend on live egress aren't reproducible.

They are new, because until compose-ai-tools 0.19.11 coil-backed images captured blank: the load
never started under the renderer's inspection mode, and an unresolved `AsyncImagePainter` reports no
intrinsic size, so it also collapsed the layout around it. That is what the top row below is —
the same three previews, same code, one plugin version earlier
([compose-ai-tools#2952](https://github.com/yschimke/compose-ai-tools/issues/2952)):

![Artwork previews before and after the coil fix](images/artwork-before-after.png)

## Relationship to the per-library previews

Several libraries already apply the same plugin and render whatever previews happen to live in
their own `src/debug` (`:auth:composables`, `:compose-material`, `:media:ui`, …). Those stay as
they are — they're the library author's working previews. This module is the curated counterpart:
one place where a surface is previewed with realistic data, on the device it ships to, grouped so a
reader can navigate by area rather than by Gradle path.

## Dialogs and bottom sheets

All 80 previews render with real content. The 8 dialog-shaped ones —
`AuthWearSignedInConfirmationDialog` and its two variants (Wear `Dialog`), plus every `Auth Mobile`
and `DataLayer Mobile` sticker (`ModalBottomSheet`) — were withheld from
[`catalog.spec.json`](../catalog.spec.json) until compose-ai-tools 0.19.13, and are back in it now.

They compose into their own window with their own `ViewRootImpl` and Compose root, rather than into
the host activity's content view. The renderer used to prefer the activity's root unconditionally,
which for these is present but empty: the previews were discovered, sized (411×914dp for the phone
sheets) and grouped correctly, but the exported tree had nothing in it — so they reported under
`no semantics for: …` and the sticker was the whole activity window with the component floating in
it. Fixed upstream in
[yschimke/compose-ai-tools#3048](https://github.com/yschimke/compose-ai-tools/issues/3048): the
renderer now picks the dialog's root and crops the capture to the dialog's own window.

Two things worth knowing when reading these stickers:

- A Wear `Dialog` is centred in its window, so its sticker is cropped to the dialog itself rather
  than to the 227dp round screen the other Wear stickers show.
- A `ModalBottomSheet`'s window fills the screen, so its sticker keeps the full 411×914dp phone
  frame — the crop is a no-op there. What changed for those is the semantics, which is what the
  completeness gate was failing on.

`design-artifacts.yml` runs with `allow-incomplete: false`, so the completeness gate is on for
everything the catalog declares.

## Custom themes

The catalog exposes three dark themes: **Blue**, **Lilac**, and **Green**. They reuse palettes that
Horologist already carries in `:compose-tools` for preview and screenshot coverage rather than
inventing catalog-only colours.

Each theme is a `@WearThemeCatalog` `PreviewWrapperProvider` whose wrapper installs the selected
palette into both Wear Material 3 and Wear Material 2. The default Blue provider is inherited from
the Wear section annotations through `@PreviewWrapperClass`. A live theme selection replaces that
provider, so preview-local Wear theme wrappers deliberately remain pass-through and cannot shadow
the selected theme. The five phone bottom-sheet previews keep their existing fixed Material 3
presentation outside the Wear theme axis; wrapping their separate dialog window with a preview
provider produces an empty capture.

Discovery therefore reports 83 entries: the 80 curated previews from `catalog.spec.json` and the
three generated theme specimen sheets. All custom themes use dark backgrounds and dark colour
schemes; the catalog does not advertise a synthetic Wear light mode.

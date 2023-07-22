# How to Contribute

We'd love to accept your patches and contributions to this project. There are
just a few small guidelines you need to follow.

If you find a common problem that you think would help other Wear developers
please consider submitting a PR. Please avoid significant work before raising
an issue https://github.com/google/horologist/issues with the label "Feature Request"

## Development

The project should work immediately from a fresh checkout in Android Studio ([Stable](https://developer.android.com/studio/releases) or newer) or Gradle (./gradlew).

When submitting a PR, please check API compatibility and lint rules first.

A good first step is

```
$ ./gradlew spotlessApply spotlessCheck compileDebugSources compileReleaseSources metalavaGenerateSignature metalavaGenerateSignatureDebug lintDebug
```

Also make sure you have ([Git LFS](https://git-lfs.github.com/)) installed.

If you change any code affecting screenshot tests, then run the following and check the failures in the `out` directory.

```
$ ./gradlew verifyPaparazziDebug
```

To record the new golden images, run the following and check in the specific files that failed. Paparazzi has some tolerance for minor changes,
so not all diffs need be committed.

```
$ ./gradlew recordPaparazziDebug
```

## Contributor License Agreement

Contributions to this project must be accompanied by a Contributor License
Agreement. You (or your employer) retain the copyright to your contribution,
this simply gives us permission to use and redistribute your contributions as
part of the project. Head over to <https://cla.developers.google.com/> to see
your current agreements on file or to sign a new one.

You generally only need to submit a CLA once, so if you've already submitted one
(even if it was for a different project), you probably don't need to do it
again.

## Code reviews

All submissions, including submissions by project members, require review. We
use GitHub pull requests for this purpose. Consult
[GitHub Help](https://help.github.com/articles/about-pull-requests/) for more
information on using pull requests.

## Translation and localization

This project uses a semi-automatic pipeline to translate strings. When new or
updated localized strings are ready, a PR is generated (example:
google/horologist#692). Only the files configured via [localization.bzl](https://github.com/google/horologist/blob/main/localization.bzl)
are sent for translation.

If you see a problem with translated text, don't edit localized resource files
(e.g. `res/values-en/strings.xml`) manually, as they'll be overwritten. Instead,
file an issue and use the [l10n](https://github.com/google/horologist/labels/l10n)
label. This will then be forwarded to the relevant teams.

## Project Direction and Ownership

There are a couple of reasons we may not accept an otherwise valuable
contribution.

- Where the internal framework feature team, thinks the contribution is against the
long term direction of the library.
- Where long term ownership is unclear, such as a large contribution that likely involves
ongoing maintenance.

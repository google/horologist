# Media Domain library

This library currently contains a set of models and repositories that are common to media apps. But
it can be expanded in the future to also include common use cases, as
per [domain layer guide](https://developer.android.com/topic/architecture/domain-layer).

The [data](https://developer.android.com/topic/architecture/data-layer)
and [domain](https://developer.android.com/topic/architecture/domain-layer) layer guides seem to
imply that the definitions of the repositories should belong to the data layer. This would make the
domain library dependent on a specific data library. In this project, the repositories are defined (
not implemented) in the domain layer. This makes the domain layer independent of external layers,
and any implementation of the repositories can be used: the ones provided by the toolkit, or custom
implementations provided by your project.

The reason for having a domain library is described in
the [architecture overview](media-toolkit.md#architecture-overview).

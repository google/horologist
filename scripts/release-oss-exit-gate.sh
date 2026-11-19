#!/usr/bin/env bash
# Copyright 2026 Google LLC
# SPDX-License-Identifier: Apache-2.0
#
# Stages Horologist Maven artifacts to OSS Exit Gate Artifact Registry and
# uploads the release manifest to trigger OSS Exit Gate publication.
set -euo pipefail

REPOSITORY="horologist--mavencentral"
VERSION="${RELEASE_VERSION:-$(awk -F= '/^VERSION_NAME=/{print $2; exit}' gradle.properties | tr -d '[:space:]')}"

if [ -n "${VERSION}" ]; then
  while IFS= read -r pkg; do
    pkg_name="${pkg##*/}"
    [ -n "${pkg_name}" ] || continue
    if gcloud artifacts versions describe "${VERSION}" \
      --package="${pkg_name}" \
      --project="oss-exit-gate-prod" \
      --location="us" \
      --repository="${REPOSITORY}" >/dev/null 2>&1; then
      gcloud artifacts versions delete "${VERSION}" \
        --package="${pkg_name}" \
        --project="oss-exit-gate-prod" \
        --location="us" \
        --repository="${REPOSITORY}" \
        --quiet
    fi
  done < <(gcloud artifacts packages list \
    --project="oss-exit-gate-prod" \
    --location="us" \
    --repository="${REPOSITORY}" \
    --format="value(name)" 2>/dev/null || true)
fi

OSS_EXIT_GATE_TOKEN="$(gcloud auth print-access-token)"
export OSS_EXIT_GATE_TOKEN

./gradlew publishAllPublicationsToOssExitGateRepository

MANIFEST_FILE="$(mktemp)"
echo '{"publish_all": true}' > "${MANIFEST_FILE}"
gcloud storage cp "${MANIFEST_FILE}" \
    "gs://oss-exit-gate-prod-projects-bucket/horologist/mavencentral/manifests/manifest-${VERSION:-release}-$(date +%s).json"
rm -f "${MANIFEST_FILE}"

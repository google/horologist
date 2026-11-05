#!/usr/bin/env bash
# Copyright 2026 Google LLC
# SPDX-License-Identifier: Apache-2.0
#
# Stages Horologist Maven artifacts to OSS Exit Gate Artifact Registry and
# uploads the release manifest to trigger OSS Exit Gate publication.
set -euo pipefail

REPOSITORY="horologist--mavencentral"

# clean up stale releases from staging repo
packages=$(gcloud artifacts packages list \
    --project="oss-exit-gate-prod" \
    --location="us" \
    --repository="${REPOSITORY}" \
    --format="value(name)" 2>/dev/null || true)

echo "${packages}" | xargs -r -I {} sh -c '
    package_name=$(basename "{}")
    echo "Deleting package: $package_name..."
    gcloud artifacts packages delete "{}" \
        --project="oss-exit-gate-prod" \
        --location="us" \
        --repository="'"${REPOSITORY}"'" \
        --quiet
'

OSS_EXIT_GATE_TOKEN="$(gcloud auth print-access-token)"
export OSS_EXIT_GATE_TOKEN

./gradlew publishAllPublicationsToOssExitGateRepository

MANIFEST_FILE="$(mktemp)"
echo '{"publish_all": true}' > "${MANIFEST_FILE}"
gcloud storage cp "${MANIFEST_FILE}" \
    "gs://oss-exit-gate-prod-projects-bucket/horologist/mavencentral/manifests/manifest-$(date +%s).json"
rm -f "${MANIFEST_FILE}"

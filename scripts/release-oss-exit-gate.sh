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
if [ -n "${GITHUB_ACTIONS:-}" ]; then
  echo "::add-mask::${OSS_EXIT_GATE_TOKEN}"
fi
export OSS_EXIT_GATE_TOKEN

./gradlew publishAllPublicationsToOssExitGateRepository

MANIFEST_FILE="$(mktemp)"
if [ -z "${VERSION}" ]; then
  echo "ERROR: Could not determine release version for manifest generation." >&2
  exit 1
fi
python3 - "${VERSION}" "${MANIFEST_FILE}" <<'PY'
import collections, json, subprocess, sys
version, out_file = sys.argv[1], sys.argv[2]
repo = "horologist--mavencentral"
pkgs_out = subprocess.check_output([
    "gcloud", "artifacts", "packages", "list",
    "--project=oss-exit-gate-prod", "--location=us",
    f"--repository={repo}", "--format=value(name)",
], text=True).splitlines()
groups = collections.defaultdict(list)
for pkg_path in sorted(pkgs_out):
  pkg_id = pkg_path.strip().split("/")[-1]
  if ":" not in pkg_id:
    continue
  rc = subprocess.run([
      "gcloud", "artifacts", "versions", "describe", version,
      f"--package={pkg_id}", "--project=oss-exit-gate-prod",
      "--location=us", f"--repository={repo}",
  ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode
  if rc == 0:
    ns, name = pkg_id.split(":", 1)
    groups[ns].append({"name": name, "version": version})
if not groups:
  raise SystemExit(f"ERROR: No staged Maven packages found in {repo} for version {version}")
manifest = {
    "publish_all": False,
    "publishing_groups": [
        {"namespace": ns, "packages": pkgs}
        for ns, pkgs in sorted(groups.items())
    ],
}
with open(out_file, "w", encoding="utf-8") as fh:
  json.dump(manifest, fh, indent=2)
  fh.write("\n")
PY
gcloud storage cp "${MANIFEST_FILE}" \
    "gs://oss-exit-gate-prod-projects-bucket/horologist/mavencentral/manifests/manifest-${VERSION:-release}-$(date +%s).json"
rm -f "${MANIFEST_FILE}"

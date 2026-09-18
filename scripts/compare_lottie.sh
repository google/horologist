#!/usr/bin/env bash
# Copyright 2026 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -euo pipefail

# Command-line tool that given a URL (or file) to a Lottie asset, renders side-by-side
# in lottie-android (Reference) and rc lottie (RemoteCompose), producing visual
# comparison PNGs and clustered bug reports.
#
# Usage:
#   ./scripts/compare_lottie.sh <lottie_url_or_file> [output_dir]
#   ./scripts/compare_lottie.sh --batch <urls_file> [output_dir]
#   ./scripts/compare_lottie.sh --default-suite [output_dir]

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GRADLEW="${REPO_ROOT}/gradlew"

if [[ $# -eq 0 ]]; then
  echo "Usage:"
  echo "  $0 <url_or_file> [output_dir]"
  echo "  $0 --batch <urls_file> [output_dir]"
  echo "  $0 --default-suite [output_dir]"
  exit 1
fi

ARGS=()
if [[ "$1" == "--batch" ]]; then
  URLS_FILE="$2"
  OUT_DIR="${3:-build/outputs/lottie-comparison-cli}"
  ARGS+=("-PlottieUrlsFile=${URLS_FILE}" "-PlottieOutput=${OUT_DIR}")
elif [[ "$1" == "--default-suite" ]]; then
  OUT_DIR="${2:-build/outputs/lottie-comparison-cli}"
  ARGS+=("-PlottieOutput=${OUT_DIR}")
else
  URL="$1"
  OUT_DIR="${2:-build/outputs/lottie-comparison-cli}"
  ARGS+=("-PlottieUrl=${URL}" "-PlottieOutput=${OUT_DIR}")
fi

exec "${GRADLEW}" :remotecompose:lottie:testDebugUnitTest \
  --tests "com.google.android.horologist.remotecompose.lottie.cli.LottieSideBySideCliTest" \
  --rerun-tasks \
  --info \
  -PlottieCliRun=true \
  "${ARGS[@]}"

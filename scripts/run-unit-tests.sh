#!/bin/bash

# Copyright 2022 The Android Open Source Project
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

# Fail on error and print out commands
set -ex

# Parse parameters
for i in "$@"; do
  case $i in
  --run-affected)
    RUN_AFFECTED=true
    shift
    ;;
  --affected-base-ref=*)
    BASE_REF="${i#*=}"
    shift
    ;;
  --run-flaky-tests)
    RUN_FLAKY_TESTS=true
    shift
    ;;
  *)
    echo "Unknown option"
    exit 1
    ;;
  esac
done

FILTER_OPTS=""
# Filter out flaky tests if we're not set to run them
if [[ -z "$RUN_FLAKY_TESTS" ]]; then
  FILTER_OPTS="$FILTER_OPTS -Pandroid.testInstrumentationRunnerArguments.notAnnotation=androidx.test.filters.FlakyTest"
fi

# If we're set to only run affected test, update the Gradle task
if [[ ! -z "$RUN_AFFECTED" ]]; then
  TASK="runAffectedUnitTests"
  TASK="$TASK -Paffected_module_detector.enable"

  # If we have a base branch set, add the Gradle property
  if [[ ! -z "$BASE_REF" ]]; then
    TASK="$TASK -Paffected_base_ref=$BASE_REF"
  fi
fi

# If we don't have a task yet, use the defaults
if [[ -z "$TASK" ]]; then
  TASK="testDebug"
fi

./gradlew --scan --continue --no-configuration-cache --stacktrace $TASK $FILTER_OPTS

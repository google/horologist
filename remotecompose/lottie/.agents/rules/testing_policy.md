---
trigger: model_decision
description: "Testing policy, module-specific Gradle execution order, and verification tasks for :remotecompose:lottie."
---

# Testing Policy & Verification (:remotecompose:lottie)

## 1. Test-Driven Approach
- **ALWAYS** follow a Test-Driven Development (TDD) approach.
- **First Step of Every Plan:** The very first step of every plan or task **MUST** be to design and write representative test cases to cover the bug (reproduction) or the new feature requirements before implementing the solution.
- Ensure tests fail or capture the missing functionality before writing the implementation.
- Delegate test writing to the isolated `dev-test` subagent (`.agents/agents/dev-test.md`).

## 2. Module Execution Order
Execute in this exact sequence for `:remotecompose:lottie`:

1. **Format Code:** `./gradlew :remotecompose:lottie:ktfmtFormat`
2. **Update Metalava Signatures:** `./gradlew :remotecompose:lottie:metalavaGenerateSignatureDebug`
3. **Compile Kotlin:** `./gradlew :remotecompose:lottie:compileDebugKotlin`
4. **Assemble Build:** `./gradlew :remotecompose:lottie:assembleDebug`
5. **Run Unit Tests:** `./gradlew :remotecompose:lottie:testDebugUnitTest`
6. **Run All Checks:** `./gradlew :remotecompose:lottie:check`

## 3. Screenshot & Roborazzi Tasks
- Record: `./gradlew :remotecompose:lottie:recordRoborazziDebug`
- Verify: `./gradlew :remotecompose:lottie:verifyRoborazziDebug`
- Fallback to global only if needed: `./gradlew verifyRoborazziDebug`

## 4. Verification Scope
- **ALWAYS** prioritize running checks on `:remotecompose:lottie`.
- If changes affect downstream modules (e.g. `:sample`), run tests for downstream modules as well.

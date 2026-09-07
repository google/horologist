---
trigger: model_decision
description: "Verification tasks before commit."
---

# Verification tasks before commit (:remotecompose:lottie)

## 1. Module Execution Order
Execute in this exact sequence for `:remotecompose:lottie`:

1. **Format Code:** `./gradlew :remotecompose:lottie:ktfmtFormat`
2. **Update Metalava Signatures:** `./gradlew :remotecompose:lottie:metalavaGenerateSignatureDebug`
3. **Compile Kotlin:** `./gradlew :remotecompose:lottie:compileDebugKotlin`
4. **Assemble Build:** `./gradlew :remotecompose:lottie:assembleDebug`
5. **Run Unit Tests:** `./gradlew :remotecompose:lottie:testDebugUnitTest`
6. **Run All Checks:** `./gradlew :remotecompose:lottie:check`

## 2. Screenshot & Roborazzi Tasks
- Record: `./gradlew :remotecompose:lottie:recordRoborazziDebug`
- Verify: `./gradlew :remotecompose:lottie:verifyRoborazziDebug`
- Fallback to global only if needed: `./gradlew verifyRoborazziDebug`

## 3. Verification Scope
- **ALWAYS** prioritize running checks on `:remotecompose:lottie`.
- If changes affect downstream modules (e.g. `:sample`), run tests for downstream modules as well.

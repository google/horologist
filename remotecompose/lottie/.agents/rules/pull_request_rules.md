---
trigger: model_decision
description: "Guidelines and template for drafting Pull Request descriptions and checklists."
---

# Pull Request Guidelines

When drafting Pull Requests (distinct from commit messages), you **MUST** follow this template:

#### WHAT
(Briefly explain what this PR does)

#### WHY
(Explain the motivation and context)

#### HOW
(Explain the technical approach and justify complex or non-obvious design choices)

#### Checklist :clipboard:
- [ ] **Diff Audit:** I have reviewed my own diff and ensured no debugging code, unused imports, or unrelated changes accidentally slipped in.
- [ ] Add explicit visibility modifier and explicit return types for public declarations
- [ ] Run spotless / ktfmt check (`./gradlew :remotecompose:lottie:ktfmtFormat`)
- [ ] Run tests (`./gradlew :remotecompose:lottie:check`)
- [ ] Update metalava's signature text files (`./gradlew :remotecompose:lottie:metalavaGenerateSignatureDebug`)

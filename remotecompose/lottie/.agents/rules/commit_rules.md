---
trigger: model_decision
description: "Guidelines and inverted-pyramid format for writing Git commit messages."
---

# Commit Message Guidelines

When generating or suggesting a commit message, analyze the git commit history and current diff. Simplify code review and long-term maintenance. Never generate generic messages like "Fix bug" or "Update UI".

## 1. Core Structure & Inverted Pyramid
- **The Inverted Pyramid:** Place the most critical information at the very top so the reader gets full context immediately.
- **Use Headings:** For complex changes requiring longer descriptions, structure using Markdown headings.

## 2. Title Line (Imperative & Effect-Focused)
- **Describe the effect, not the implementation:** Focus on what the change actually does to the application ("what"), not the implementation details ("how" or "why").
  - *Bad:* "Add a mutex to guard the database handle"
  - *Good:* "Prevent database corruption during simultaneous sign-ups"

## 3. Body: Impact & Motivation
- **Impact Summary:** Summarize how the change affects clients and end-users with sufficient detail for non-code readers.
- **Motivation ("Why"):** Explain why the change is necessary, the constraints that guided your decision, and how this change fits into any larger architectural designs or team goals.

## 4. Specific Context (When Applicable)
- **Breaking Changes:** Explicitly flag breaking changes under a `### Breaking Changes` heading with migration steps.
- **Cross-References:** Use auto-closing keywords for related issues (e.g., `Fixes #1234`) or reference PR IDs and commit hashes.
- **New Dependencies:** Explicitly flag and justify third-party dependencies and justify *why* it was added and how it was selected.
- **External References:** Link to relevant non-obvious resources, documentation, or design posts.
- **Rich Context:** Document alternatives considered, bug summaries, test coverage/limitations, or what was learned.

## 5. Antipatterns (Strictly Prohibited)
**NEVER** include the following in commit descriptions:
- Information that is obvious from reading the code.
- Code maintenance instructions (place these in code comments instead).
- Short-term discussions.
- Preview URLs and build artifacts.
- Comments or tags like "build with AI".

## 6. Scope and Isolation
- **DO NOT** mix functional and non-functional changes in the same commit. If you are asked to add a feature, do not simultaneously reformat surrounding code, reorganize imports, or refactor unrelated methods in the same commit.
- Keep commits atomic (narrowly scoped): Split into **Commit A (Refactor/Cleanup)** and **Commit B (Feature Logic)** when preparing code for a new feature.

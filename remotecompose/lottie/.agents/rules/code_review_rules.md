---
trigger: model_decision
description: "Guidelines for responding to code review comments and feedback."
---

# Handling Code Review & Feedback

When receiving user feedback, change requests, or clarification questions:

1. **Answer with code, not just chat:** If the user expresses confusion or asks "Why did you do this?", the code is not self-documenting. **Refactor the code for clarity or add explicit explanatory code comments directly in the codebase**, rather than only replying in chat.
2. **Explicitly communicate resolutions:** Clearly specify what changed (e.g., "Extracted parsing logic into `UserParser.kt` and added unit tests") instead of generic confirmations like "Fixed".

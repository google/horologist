---
trigger: model_decision
description: "Design document and writing style guidelines for concepts, specs, and plans."
---

# Design Document & Writing Style Guidelines

1. **Accessibility:** Easy to read. Assume the reader does not know internal terminology and is not fluent in English. Avoid unexplained acronyms.
2. **Tone:** Dry, practical language. No figurative expressions, idioms, or anthropomorphism (components do not "look", "see", or "watch").
3. **Structure:** Important information first. Follow the sequence: *what* → *how* → *details*.
4. **Conciseness:** Fewer words win. Cut whole concepts when possible, not just words.
5. **Ubiquitous Language:** Exactly one term per concept, used consistently. Maintain a glossary in the document.
6. **Alternatives Considered:** Rejected options **ALWAYS** belong in an "Alternatives considered" section at the end, detailing pros, cons, and rationale.
7. **Quantifiers:** Use universal quantifiers (*all, every, never, always*) only when literally true by design; otherwise specify exact sets.
8. **Referents:** Avoid ambiguous pronouns (*it, them*); repeat the explicit noun.
9. **Precision:** Avoid subjective words (*small, big, fast, slow, cheap, expensive*); provide concrete numbers, sizes, and frequencies.
10. **Ownership:** Be explicit about ownership and audience: explicitly state where values are stored, access permissions, and target audiences.

*Feedback Rule:* Treat user comments as alternatives to evaluate (pros/cons), adopt the selected approach in the main text, and document rejected alternatives under "Alternatives considered".

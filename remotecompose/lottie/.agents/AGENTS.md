# RemoteCompose Lottie Guidelines

<lottie_specifications>
## Canonical Lottie Specifications
Always consult these authoritative references when developing or verifying Lottie format, values, properties, and animations:
- **One-page Lottie 1.0.1 Specification:** https://lottie.github.io/lottie-spec/1.0.1/single-page/
- **Lottie JSON Schema:** https://lottie.github.io/lottie-spec/1.0.1/lottie.schema.json
- **Modular Topic Specs:** `https://lottie.github.io/lottie-spec/1.0.1/specs/{section}/`
  - (Replace `{section}` with the target domain: `values`, `properties`, `composition`, `layers`, `shapes`, `assets`, `constants`, `helpers`, `glossary`, `format`)
</lottie_specifications>

<workflow_routers>
## Workflow Routers (Phase Guidelines)
To prevent prompt noise, detailed phase rules are loaded just-in-time from `.agents/rules/` and specialized subagents:
- **Verification tasks before commit:** Follow `.agents/rules/pre_commit_verification.md` (code formating, running unit and screenshot tests, final verification before commit).
- **Commit Messages:** Follow `.agents/rules/commit_rules.md` (inverted pyramid, imperative effects, headings).
- **Pull Requests:** Follow `.agents/rules/pull_request_rules.md` when drafting PR descriptions.
</workflow_routers>

---
trigger: model_decision
description: "Rules and principles for writing rigorous code comments, function contracts, and data structure invariants based on Rigorous Software Engineering standards."
---

# Code Documentation and Function Commenting Guidelines

These guidelines are based on Rigorous Software Engineering principles (ETH Zürich). They ensure that documentation clarifies contractual guarantees, prevents subtle bugs during software evolution, and distinguishes essential behavior from incidental implementation details.

---

## 1. The Core Purpose: Essential vs. Incidental Properties

Source code expresses *what the code currently does*, but it does not tell maintainers which properties are **stable and guaranteed** across software evolution.
- **Essential Properties:** Contractual guarantees that clients can safely rely on (e.g., whether `find()` is guaranteed to return the *first/smallest* index or any arbitrary match).
- **Incidental Properties:** Accidents of the current implementation (e.g., linear search order that might change when parallelized).
- **Rule:** Explicitly document all essential properties so future refactorings (such as parallelization, caching, or lazy evaluation) do not break client expectations.

---

## 2. Separate Documentation by Target Audience

Always maintain a clear separation between the two audiences:

| Target Audience | Question to Answer | Focus |
| :--- | :--- | :--- |
| **Clients (Callers)** | *How to use the code?* | **Interface Documentation:** Contracts, preconditions, postconditions, exceptions, invariants. |
| **Implementors (Maintainers)** | *How does the code work?* | **Implementation Documentation:** Algorithms, data structure invariants, aliasing, assumption justifications. |

---

## 3. Focus on WHAT, Not HOW

Documentation must declare the **state invariants and guarantees**, not narrate execution steps.

* **Incorrect (narrating execution steps):**
  > *"When creating a new List object with an existing ListRep object, the shared-field is set to true."*
* **Correct (stating the invariant / essential property):**
  > *"Whenever a ListRep object's shared-field is false, it is used as the representation of at most one List object."*

---

## 4. Method and Function Documentation (The 3 Dimensions)

Every non-trivial public or internal method must document three critical dimensions:

### A. The Call (Preconditions & Input State)
Document all constraints required for the caller to invoke the method safely:
- **Nullability:** Explicitly declare whether arguments can be null (`@NonNull`, `@Nullable`).
- **Value Bounds:** Specify ranges (e.g., `offset >= 0`, `len >= 0`).
- **Relational Constraints:** Specify cross-parameter relationships (e.g., `offset + len <= cbuf.length`).

### B. The Results (Postconditions & Output State)
Document the exact meaning of all return values:
- **Return Value Meanings:** Document boundary returns and sentinels (e.g., `returns -1 if EOF reached before any characters read; otherwise returns number of characters read [0, len]`).
- **Object Identity:** State whether the returned object is a **fresh object** (safe to mutate), a cached singleton, or an internal view.
- **Copy Semantics:** Explicitly specify **shallow copy** (collection copied, elements shared) vs. **deep copy**.

### C. The Effects (Side Effects, Exceptions, and Invariants)
Document all changes to the system and execution environment:
- **Heap / State Mutations:** What data is modified or consumed (e.g., *"characters are consumed from the stream and written to `cbuf` from `offset` onwards"*). State when data is left untouched (e.g., *"if result is -1, no characters are consumed and `cbuf` is unchanged"*).
- **Exceptions:** Document all thrown exceptions and the exact conditions that trigger them (e.g., `@throws IOException if the stream is closed or an I/O error occurs`).
- **Concurrency & Blocking:** Document whether the function blocks, is thread-safe, reentrant, or requires external synchronization.
- **Complexity Guarantees:** Document time and space complexity when significant (e.g., *"requires constant O(1) time and space"*).

---

## 5. Invariant Documentation

### A. Data Structure / One-State Invariants (Consistency)
Document properties of states that are maintained by all methods together:
- Structural bounds (e.g., `0 <= len <= rep.elems.length`).
- Value ordering (e.g., `list is sorted: a <= b <= c`).
- Field relationships and nullability (e.g., `rep is non-null and referenced only by List objects`).

### B. Temporal Invariants (Evolution & Sequences of States)
Document rules governing how objects change over time:
- **Immutability:** State whether the object or its underlying data is immutable across arbitrary operations.
- **Safe Lazy Initialization:** Ensure that lazy evaluation or caching does not mutate states observed by identity/equality methods (e.g., `hashCode` must not change after lazy loading).

### C. Aliasing and Internal Sharing
Document ownership and sharing of underlying data:
- Specify whether internal buffers are shared across instances.
- State copy-on-write policies (e.g., *"ensures a shared array is never modified in-place; copies before mutation"*).

---

## 6. Algorithm & Inline Code Comments

Do not paraphrase obvious code lines (e.g., avoid `// increment i`). Use inline comments to:
1. **Explain Control Flow:** Clarify non-obvious branches or loop exit conditions.
2. **Justify Assumptions:** Document why an invariant or precondition is guaranteed to hold at that point in the code.
3. **Explain Optimization Rationale:** Document why an optimization is safe (e.g., `// perform array copy only if capacity can be reduced by >= 50% to prevent re-allocation thrashing`).

---

## 7. The Golden Rule of Code Documentation

> *"It is better to simplify than to describe complexity!"* — Alan J. Perlis

1. If a procedure requires extensive prose to explain 10 interdependent parameters, refactor the design before documenting.
2. Use strong types, modifiers (`val`, `final`), annotations (`@NonNull`), and assertions (`require`, `check`, `assert`) to enforce properties statically and at runtime rather than relying purely on comments.

---

## 8. Formatting Constraints in Doc Comments (KDoc / Javadoc)

- **No Bold Text:** Do not use bold formatting (`**text**`) within doc comments. Use plain text, structural lists, or code spans (`` `code` ``) instead. Bold markers create visual clutter in IDE tooltips and rendered documentation.
- **Normal Markdown Links for Specifications:** When referencing external specifications (such as Lottie specification anchors), always format them as standard markdown links with human-readable anchor text and canonical URLs (e.g. `[Integer Boolean](https://lottie.github.io/lottie-spec/dev/specs/values/#int-boolean)`). Do not leave raw URLs or JSON schema pointers (e.g. avoid `` `int-boolean` (https://...) `` or `#/$defs/...`).


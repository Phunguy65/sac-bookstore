## [2026-03-28] Round 1 (from spx-apply auto-verify)

### spx-verifier
- Fixed: Added broader purchase service and page-bean tests covering inactive-book validation, stock failure paths, address fallback rules, multi-item totals, and order-detail success handling.

### spx-arch-verifier
- Fixed: Moved order-detail web flow off the domain aggregate by introducing application-facing order detail view DTOs.
- Fixed: Removed duplicated cart summary assembly by introducing a shared cart view assembler.
- Fixed: Added batch book loading support via `BookRepository.findByIds(...)` and used it in cart and checkout projections to avoid repeated per-item lookups.

### spx-uiux-verifier
- Fixed: Made checkout default-address fields read-only when the flow is using the saved default address.
- Fixed: Added pending state and live status feedback for cart item removal actions.
- Fixed: Updated account landing copy to reflect real purchase navigation instead of a placeholder protected-route message.

### spx-test-verifier
- Fixed: Expanded purchase tests with additional cart and checkout failure-path coverage plus order-detail happy-path verification.

## [2026-03-28] Round 2 (from spx-apply auto-verify)

### spx-verifier
- Fixed: Added tests for missing-book cart and checkout failure paths.

### spx-arch-verifier
- Fixed: Stopped swallowing infrastructure/runtime exceptions in cart write operations so validation failures remain distinct from persistence faults.
- Fixed: Batched checkout book loading with `BookRepository.findByIds(...)` during stock validation and decrement preparation.
- Fixed: Slimmed order history/detail repository fetch joins to avoid pulling joined book entities when only order item snapshots are needed.

### spx-uiux-verifier
- Fixed: Added visible disabled-button styling to purchase actions during pending states.
- Fixed: Upgraded the order-detail back link to a full-size tappable control and aligned its focus tokens with the purchase pages.
- Fixed: Replaced repeated inline toolbar/empty-state layout styles with reusable page classes and added recovery CTAs to empty states.

## [2026-03-28] Round 3 (from spx-apply auto-verify)

### spx-arch-verifier
- Fixed: Removed unnecessary `BookEntity` fetch-joins from cart repository reads now that cart and checkout views load current books in batch through `BookRepository.findByIds(...)`.

### spx-uiux-verifier
- Fixed: Gave cart primary checkout links the same guaranteed touch-target sizing as other interactive controls.
- Fixed: Added responsive data labels for stacked cart rows on narrow screens.
- Fixed: Added field-associated inline validation messaging and invalid-state wiring for cart add/update forms and checkout address fields.
- Fixed: Updated home page messaging to describe the implemented purchase experience instead of a placeholder auth route.

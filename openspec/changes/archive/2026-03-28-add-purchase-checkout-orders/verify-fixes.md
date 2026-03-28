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

## [2026-03-28] Round 4 (from spx-apply auto-verify)

### spx-arch-verifier
- Fixed: Removed `setMaxResults(1)` from cart and order repository methods that fetch item collections so aggregate loading no longer depends on provider-specific row limiting.
- Fixed: Kept WildFly-compatible fetch joins alias-free across cart and order repository reads.

### spx-uiux-verifier
- Fixed: Added empty-checkout feedback by redirecting back to cart with an informational message and hiding the cart header checkout CTA when the cart is empty.
- Fixed: Added screen-reader live status messaging for cart quantity update submissions.
- Fixed: Moved cart and order-detail pages onto shared purchase design tokens/components and improved order-history pagination semantics.

### spx-test-verifier
- Fixed: Added regression checks for missing-order lookup results and for purchase repository fetch-join portability constraints.

## [2026-03-28] Round 5 (from spx-apply auto-verify)

### spx-arch-verifier
- Fixed: Replaced string-parsing field binding in cart and checkout page flows with typed field-error maps returned from application-service result objects.

### spx-uiux-verifier
- Fixed: Moved checkout form panel/grid/list styling onto shared purchase CSS tokens and localized the remaining English checkout field labels.
- Fixed: Removed inline pagination list styling from order history in favor of shared utility classes.

### spx-test-verifier
- Fixed: Added zero/negative quantity boundary tests for cart mutations and explicit checkout address field validation tests.
- Fixed: Covered the page-bean parse-error path with typed field-error wiring so inline validation still works after the service-result refactor.

## [2026-03-28] Round 6 (from spx-apply auto-verify)

### spx-arch-verifier
- Fixed: Introduced `FieldValidationException` in shared validation utilities so cart and checkout field-error binding now uses stable field metadata instead of parsing human-readable message text.

### spx-uiux-verifier
- Fixed: Moved the remaining purchase page body/shell/layout/table/item/input pagination styling into `purchase-shared.css` and switched the pages to shared utility classes.

### spx-test-verifier
- Fixed: Expanded checkout validation coverage to all remaining required address fields plus invalid phone-number format handling.

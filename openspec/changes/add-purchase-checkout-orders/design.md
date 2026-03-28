# Context

The codebase already contains purchase-oriented database tables, domain records, and persistence mappers for carts and orders, but it does not yet expose any executable purchase flow. The web layer follows a JSP-first pattern where pages delegate request handling to CDI request-scoped beans, application logic lives in EJB services, and persistence adapters are implemented as JPA-backed repositories.

This change spans multiple modules: purchase, book, auth, and account web pages. It also relies on existing session-based authentication and must keep business rules out of JSP files. The current schema already supports cart item price snapshots, order shipping snapshots, and one cart per customer.

## Goals / Non-Goals

**Goals:**

- Implement an authenticated cart flow that supports add, update, remove, and cart review.
- Implement a full-cart checkout flow that uses COD/manual ordering only.
- Resolve shipping address by preferring the customer's default address and allowing checkout-time address entry when none exists.
- Place orders transactionally by validating stock, decrementing stock immediately on success, persisting order snapshots, and clearing the cart.
- Expose order history and order detail pages for the owning customer.
- Follow existing project conventions for JSP/CDI/EJB/JPA and support unit plus integration testing.

**Non-Goals:**

- Integrating a real payment gateway or asynchronous payment state machine.
- Supporting partial checkout, buy-now flows, or multi-cart ownership.
- Repricing flows, promotional discounts, shipping fee calculation, or tax calculation.
- Introducing browser-based end-to-end automation in this change.

## Decisions

### 1. Keep the existing layered web pattern

Purchase pages will follow the same shape as auth pages: JSP pages invoke CDI request-scoped page beans, page beans translate request/response handling into application-service calls, and EJB services own transactional business logic.

- Chosen because it matches the codebase's current execution model and keeps JSP files thin.
- Alternative considered: add servlets/controllers for purchase. Rejected because it would introduce a second web interaction pattern alongside the existing JSP+bean flow.

### 2. Introduce purchase-focused application services instead of pushing logic into repositories

Cart mutations, checkout placement, and order querying will be coordinated in application services. Repositories will stay focused on persistence operations for books, addresses, carts, and orders.

- Chosen because order placement crosses aggregate boundaries and needs one place to enforce stock validation, address fallback, snapshot creation, and cart clearing.
- Alternative considered: perform orchestration inside JSP beans or repositories. Rejected because that would blur layer boundaries and make testing harder.

### 3. Checkout always operates on the entire authenticated cart

The checkout flow will only place one order from all current cart items for the signed-in customer.

- Chosen because the MVP explicitly excludes partial checkout and makes success/failure rules deterministic.
- Alternative considered: selected-item checkout. Rejected as unnecessary scope expansion that complicates cart retention rules.

### 4. Address resolution prefers stored default, then creates a new default from checkout input

If the customer has a default address, checkout pre-fills and uses it. If not, checkout requires address fields, saves that address as the new default, and snapshots the same address into the order.

- Chosen because it matches the agreed product behavior and reuses existing address domain primitives.
- Alternative considered: temporary order-only addresses without persistence. Rejected because the chosen workflow explicitly promotes checkout-entered addresses to default.

### 5. Order placement is a single transactional boundary

The place-order flow will validate that the cart is not empty, reload the current books for all cart items, validate stock for every item, create the order with `PLACED` status, decrement `books.stock_quantity` immediately, persist order items with snapshots, and clear the cart only after the order succeeds.

- Chosen because the failure rule requires keeping the cart unchanged when any item cannot be fulfilled.
- Alternative considered: multi-step persistence or deferred stock decrement. Rejected because it increases inconsistency risk and does not match the agreed stock behavior.

### 6. Price snapshots remain in persistence but no reprice logic is introduced

The schema already stores unit price snapshots in cart and order items. The implementation will continue filling those fields from the book price, while the product rules assume price does not change during MVP usage.

- Chosen because it respects the existing schema without adding needless comparison or confirmation flows.
- Alternative considered: removing snapshot handling from the plan. Rejected because it would conflict with the existing data model.

### 7. Owner-only order detail uses redirect-with-error behavior

If an authenticated customer requests an order detail that does not belong to them or does not exist in their visible scope, the UI redirects back to order history and shows an inline error message.

- Chosen because this matches the agreed UX decision and avoids exposing foreign-order data.
- Alternative considered: hard 404. Rejected because the chosen user behavior is an in-app redirect with feedback.

## Risks / Trade-offs

- **Cross-aggregate transaction complexity** -> Keep order placement inside one EJB service and verify with integration tests covering stock decrement, order creation, and cart clearing.
- **Missing persistence adapters today** -> Add explicit repository implementations for book, address, cart, and order access rather than hiding data access in services.
- **Checkout fallback creates a new default address** -> Ensure address persistence updates default semantics consistently so each customer still has only one default address.
- **UI-first JSP flow can accumulate page logic** -> Keep request parsing and branching inside page beans and leave validation/orchestration in services.
- **Order detail redirect may hide authorization failures from tests** -> Add explicit scenarios and tests for foreign-order access so the behavior remains intentional and stable.
